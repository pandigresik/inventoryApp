package com.asligresik.inventoryApp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.activity.StockActivity;
import com.asligresik.inventoryApp.adapter.StockTableViewAdapter;
import com.asligresik.inventoryApp.model.ResponseStock;
import com.asligresik.inventoryApp.model.StockItem;
import com.asligresik.inventoryApp.util.AppUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

;

public class HomeStockFragment extends Fragment {
    View view;
    @BindView(R.id.kodeBarangText)
    EditText kodeBarangText;
    @BindView(R.id.kodeRakText)
    EditText kodeRakText;
    @BindView(R.id.cbLot)
    CheckBox cbLot;
    @BindView(R.id.rvResultTable)
    RecyclerView rvResultTable;

    ProgressDialog loading;

    List<StockItem> items = new ArrayList<>();
    StockTableViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_stock, container, false);

        ButterKnife.bind(this, view);
        String kodeBarang = ((StockActivity)getActivity()).getKodeBarang();
        if(kodeBarang != null){
            HashMap<String,String> hashMap;
            hashMap = (HashMap<String, String>) AppUtility.extractBarcode(kodeBarang);
            kodeBarangText.setText(hashMap.get("kodeBarang"));
            //kodeBarangText.setText(kodeBarang);
        }
        String kodeRak = ((StockActivity)getActivity()).getKodeRak();
        if(kodeRak != null){
            kodeRakText.setText(((StockActivity)getActivity()).getKodeRak());
        }

        adapter = new StockTableViewAdapter(items);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvResultTable.setLayoutManager(mLayoutManager);
        rvResultTable.setItemAnimator(new DefaultItemAnimator());
        rvResultTable.setAdapter(adapter);

        kodeBarangText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String kodeBarang = kodeBarangText.getText().toString();
                    HashMap<String,String> hashMap;
                    hashMap = (HashMap<String, String>) AppUtility.extractBarcode(kodeBarang);
                    kodeBarangText.setText(hashMap.get("kodeBarang"));
                }
            }
        });
        return view;
    }

    @OnClick(R.id.qrBarangBtn)
    public void scanBarang(){
        ((StockActivity)getActivity()).scan("kodeBarang");
    }

    @OnClick(R.id.qrRakBtn)
    public void scanRak(){
        ((StockActivity)getActivity()).scan("kodeRak");
    }

    @OnClick(R.id.btnCari)
    public void loadDataStock(){
        loading = ProgressDialog.show(getContext(), null, "Harap Tunggu...", true, false);
        String kodebarang = kodeBarangText.getText().toString();
        String koderak = kodeRakText.getText().toString();
        int lot = cbLot.isChecked() ? 1 : 0;
        Boolean error = false;
        Integer jumlah = 0;
        List<String> pesanError;
        pesanError = new ArrayList<>();

        if(kodebarang.trim().isEmpty() && koderak.trim().isEmpty()){
            pesanError.add("kode barang atau kode rak minimal harus diisi salah satu");
            error = true;
        }

        if(error){
            Toast.makeText(getContext(), pesanError.toString(), Toast.LENGTH_SHORT).show();
            loading.dismiss();
            return;
        }

        ((StockActivity)getActivity()).getmApiService().listStock(kodebarang,koderak,lot).enqueue(new Callback<ResponseStock>() {
            @Override
            public void onResponse(Call<ResponseStock> call, Response<ResponseStock> response) {
                if (response.isSuccessful()){
                    loading.dismiss();

                        String message = response.body().getMessage();
                        Integer status = response.body().getStatus();
                        if (status == 1){
                            adapter.setItems(response.body().getItems());
                            adapter.notifyDataSetChanged();
                            loading.dismiss();
                        }

                } else {
                    loading.dismiss();
                    Toast.makeText(getContext(), "Gagal mengambil data stock", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseStock> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getContext(), "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
