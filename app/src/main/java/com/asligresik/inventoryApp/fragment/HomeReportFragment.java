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
import com.asligresik.inventoryApp.activity.ReportActivity;
import com.asligresik.inventoryApp.adapter.ReportRMIViewAdapter;
import com.asligresik.inventoryApp.model.MrItem;
import com.asligresik.inventoryApp.model.ResponseKategoriMr;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeReportFragment extends Fragment {
    View view;
    ProgressDialog loading;
    Context mContext;
    
    @BindView(R.id.rvReport)
    RecyclerView rvResultTable;
    @BindView(R.id.svSstock)
    SearchView svStock;
    List<MrItem> items = new ArrayList<>();
    ReportRMIViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_report_stock, container, false);
        mContext = getContext();
        ButterKnife.bind(this, view);

        adapter = new ReportRMIViewAdapter(items);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvResultTable.setLayoutManager(mLayoutManager);
        rvResultTable.setItemAnimator(new DefaultItemAnimator());
        rvResultTable.setAdapter(adapter);
        adapter.setOnItemClickListener(new ReportRMIViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                String txtMr = ((TextView) itemView.findViewById(R.id.txtMrItem)).getText().toString();
                Fragment f = new HomeReportItemRmiFragment();
                Bundle bundle = new Bundle();
                bundle.putString("rmi", txtMr);
                f.setArguments(bundle);
                ((ReportActivity) getActivity()).addFragment(f);
            }
        });

        populateKategori();
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

    private void populateKategori(){
        loading = ProgressDialog.show(getContext(), null, "Harap Tunggu...", true, false);
        ((ReportActivity)getActivity()).getmApiService().getKategoriMr().enqueue(new Callback<ResponseKategoriMr>() {
            @Override
            public void onResponse(Call<ResponseKategoriMr> call, Response<ResponseKategoriMr> response) {
                if (response.isSuccessful()){
                    loading.dismiss();
                    String message = response.body().getMessage();
                    Integer status = response.body().getStatus();
                    if (status == 1) {
                        List<MrItem> items = response.body().getItems();
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
            public void onFailure(Call<ResponseKategoriMr> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getContext(), "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }
}