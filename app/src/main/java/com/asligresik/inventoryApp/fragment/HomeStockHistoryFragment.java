package com.asligresik.inventoryApp.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.activity.StockActivity;
import com.asligresik.inventoryApp.adapter.HistoryTableViewAdapter;
import com.asligresik.inventoryApp.model.HistoryItem;
import com.asligresik.inventoryApp.model.ResponseHistory;
import com.asligresik.inventoryApp.util.AppUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

;

public class HomeStockHistoryFragment extends Fragment {
    View view;
    ProgressDialog loading;
    Context mContext;
    @BindView(R.id.tglAwalText)
    EditText tglAwalText;
    @BindView(R.id.tglAkhirText)
    EditText tglAkhirText;
    @BindView(R.id.kodeBarangText)
    EditText kodeBarangText;
    @BindView(R.id.kodeRakText)
    EditText kodeRakText;
    @BindView(R.id.rvHistory)
    RecyclerView rvResultTable;

    List<HistoryItem> items = new ArrayList<>();
    HistoryTableViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_stock, container, false);
        mContext = getContext();
        ButterKnife.bind(this, view);
        String kodeBarang = ((StockActivity) getActivity()).getKodeBarang();
        if (kodeBarang != null) {
            HashMap<String,String> hashMap;
            hashMap = (HashMap<String, String>) AppUtility.extractBarcode(kodeBarang);
            kodeBarangText.setText(hashMap.get("kodeBarang"));
            //kodeBarangText.setText(kodeBarang);
        }
        String kodeRak = ((StockActivity) getActivity()).getKodeRak();
        if (kodeRak != null) {
            kodeRakText.setText(((StockActivity) getActivity()).getKodeRak());
        }

        adapter = new HistoryTableViewAdapter(items);
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

    @OnClick(R.id.tglAwalBtn)
    public void showDatePickerAwal(){
        Calendar calendar = Calendar.getInstance();
        int Year, Month, Day;
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                String monthStr = String.format("%02d",(month+1));
                String dateStr = String.format("%02d",date);
                tglAwalText.setText(""+year+"-"+monthStr+"-"+dateStr);
            }
        },Year,Month,Day);

        datePickerDialog.show();
    }

    @OnClick(R.id.tglAkhirBtn)
    public void showDatePickerAkhir(){
        Calendar calendar = Calendar.getInstance();
        int Year, Month, Day;
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                String monthStr = String.format("%02d",(month+1));
                String dateStr = String.format("%02d",date);
                tglAkhirText.setText(""+year+"-"+monthStr+"-"+dateStr);
            }
        },Year,Month,Day);

        datePickerDialog.show();
    }

    @OnClick(R.id.qrBarangBtn)
    public void scanBarang() {
        ((StockActivity) getActivity()).scan("kodeBarang");
    }

    @OnClick(R.id.qrRakBtn)
    public void scanRak() {
        ((StockActivity) getActivity()).scan("kodeRak");
    }

    @OnClick(R.id.btnCari)
    public void loadDataHistory() {
        loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
        String tglAwal = tglAwalText.getText().toString();
        String tglAkhir = tglAkhirText.getText().toString();
        String kodebarang = kodeBarangText.getText().toString();
        String koderak = kodeRakText.getText().toString();

        Boolean error = false;
        Integer jumlah = 0;
        List<String> pesanError;
        pesanError = new ArrayList<>();

        if (tglAwal.trim().isEmpty()) {
            error = true;
            pesanError.add("Tanggal awal harus diisi");
        }
        if (tglAkhir.trim().isEmpty()) {
            error = true;
            pesanError.add("Tanggal akhir harus diisi");
        }

        if (error) {
            Toast.makeText(mContext, pesanError.toString(), Toast.LENGTH_SHORT).show();
            loading.dismiss();
            return;
        }

        ((StockActivity) getActivity()).getmApiService().historyStock(tglAwal, tglAkhir, kodebarang, koderak).enqueue(new Callback<ResponseHistory>() {
            @Override
            public void onResponse(Call<ResponseHistory> call, Response<ResponseHistory> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();

                    String message = response.body().getMessage();
                    Integer status = response.body().getStatus();
                    if (status == 1) {
                        adapter.setItems(response.body().getItems());
                        adapter.notifyDataSetChanged();
                        loading.dismiss();
                    }

                } else {
                    loading.dismiss();
                    Toast.makeText(mContext, pesanError.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseHistory> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(mContext, "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }
}