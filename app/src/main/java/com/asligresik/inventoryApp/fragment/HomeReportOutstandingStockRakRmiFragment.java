package com.asligresik.inventoryApp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.activity.ReportStockActivity;
import com.asligresik.inventoryApp.adapter.ReportStockRakRMIItemViewAdapter;
import com.asligresik.inventoryApp.model.ResponseStockRmiItem;
import com.asligresik.inventoryApp.model.StockRmiItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

;

public class HomeReportOutstandingStockRakRmiFragment extends Fragment {
    View view;
    ProgressDialog loading;
    Context mContext;
    String rmi;

    @BindView(R.id.rvReport)
    RecyclerView rvResultTable;
    @BindView(R.id.svSstock)
    SearchView svStock;
    List<StockRmiItem> items = new ArrayList<>();
    ReportStockRakRMIItemViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stock_report_rmi_item, container, false);
        mContext = getContext();
        ButterKnife.bind(this, view);
        Bundle b = getArguments();
        rmi = b.getString("rmi");
        adapter = new ReportStockRakRMIItemViewAdapter(items);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvResultTable.setLayoutManager(mLayoutManager);
        rvResultTable.setItemAnimator(new DefaultItemAnimator());
        rvResultTable.setAdapter(adapter);

        populateRakItemRmi(rmi);
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
    private void populateRakItemRmi(String rmi){
        loading = ProgressDialog.show(getContext(), null, "Harap Tunggu...", true, false);
        ((ReportStockActivity)getActivity()).getmApiService().getStockRakItemRmi(rmi).enqueue(new Callback<ResponseStockRmiItem>() {
            @Override
            public void onResponse(Call<ResponseStockRmiItem> call, Response<ResponseStockRmiItem> response) {
                if (response.isSuccessful()){
                    loading.dismiss();
                    String message = response.body().getMessage();
                    Integer status = response.body().getStatus();
                    if (status == 1) {
                        List<StockRmiItem> items = response.body().getItems();
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
            public void onFailure(Call<ResponseStockRmiItem> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getContext(), "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }
}