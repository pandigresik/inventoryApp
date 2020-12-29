package com.asligresik.inventoryApp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.activity.ReportStockActivity;
import com.asligresik.inventoryApp.adapter.ReportStockRMIItemViewAdapter;
import com.asligresik.inventoryApp.model.OutstandingRmiItem;
import com.asligresik.inventoryApp.model.ResponseOutstandingRmiItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeReportOutstandingStockRmiFragment extends Fragment {
    View view;
    ProgressDialog loading;
    Context mContext;

    @BindView(R.id.rvReport)
    RecyclerView rvResultTable;
    @BindView(R.id.svSstock) SearchView svStock;
    List<OutstandingRmiItem> items = new ArrayList<>();
    ReportStockRMIItemViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_report_outstanding_stock_rmi, container, false);
        mContext = getContext();
        ButterKnife.bind(this, view);

        adapter = new ReportStockRMIItemViewAdapter(items);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvResultTable.setLayoutManager(mLayoutManager);
        rvResultTable.setItemAnimator(new DefaultItemAnimator());
        rvResultTable.setAdapter(adapter);
        adapter.setOnItemClickListener(new ReportStockRMIItemViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                String txtMr = ((TextView) itemView.findViewById(R.id.txtRmiItem)).getText().toString();
                Fragment f = new HomeReportOutstandingStockRakRmiFragment();
                Bundle bundle = new Bundle();
                bundle.putString("rmi", txtMr);
                f.setArguments(bundle);
                ((ReportStockActivity) getActivity()).addFragment(f);
            }
        });

        populateItemRmi();
        setSearchView();
        return view;
    }
    public void setSearchView() {
        svStock.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.setFilter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.setFilter(query);
                return true;
            }
        });
    }
    private void populateItemRmi(){
        loading = ProgressDialog.show(getContext(), null, "Harap Tunggu...", true, false);
        ((ReportStockActivity)getActivity()).getmApiService().getStockItemRmi().enqueue(new Callback<ResponseOutstandingRmiItem>() {
            @Override
            public void onResponse(Call<ResponseOutstandingRmiItem> call, Response<ResponseOutstandingRmiItem> response) {
                if (response.isSuccessful()){
                    loading.dismiss();
                    String message = response.body().getMessage();
                    Integer status = response.body().getStatus();
                    if (status == 1) {
                        List<OutstandingRmiItem> items = response.body().getItems();
                        adapter.setItems(items);
                        adapter.notifyDataSetChanged();
                        loading.dismiss();
                    }
                } else {
                    loading.dismiss();
                    Toast.makeText(getContext(), "Gagal mengambil kategori MR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseOutstandingRmiItem> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getContext(), "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }
}