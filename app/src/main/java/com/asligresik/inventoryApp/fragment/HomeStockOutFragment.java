package com.asligresik.inventoryApp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.activity.StockActivity;
import com.asligresik.inventoryApp.activity.StockoutActivity;
import com.asligresik.inventoryApp.model.MrItem;
import com.asligresik.inventoryApp.model.ResponseMr;
import com.asligresik.inventoryApp.util.AppUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeStockOutFragment extends Fragment {
    View view;
    ProgressDialog loading;

    @BindView(R.id.kodeBatchOrderText)
    EditText kodeBatchOrderText;
    @BindView(R.id.kodeBarangText)
    EditText kodeBarangText;
    @BindView(R.id.kodeRakText)
    EditText kodeRakText;
    @BindView(R.id.jumlahText)
    EditText jumlahText;
    @BindView(R.id.mrSpinner)
    AppCompatSpinner mrSpinner;
    @BindView(R.id.etNote)
    EditText etNote;

    protected ArrayAdapter<String> adapterMr;
    protected List<String> listMr = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_stockout, container, false);

        ButterKnife.bind(this, view);

        String kodeBarang = ((StockActivity)getActivity()).getKodeBarang();
        String kodeBarangSaja = "";
        if(kodeBarang != null){
            HashMap<String,String> hashMap;
            hashMap = (HashMap<String, String>) AppUtility.extractBarcode(kodeBarang);
            kodeBarangSaja = hashMap.get("kodeBarang");
            kodeBarangText.setText(kodeBarangSaja);
            jumlahText.setText(hashMap.get("qty"));
            //kodeBarangText.setText(kodeBarang);
        }

        String kodeRak = ((StockActivity)getActivity()).getKodeRak();
        if(kodeRak != null){
            kodeRakText.setText(kodeRak);
        }

        if(kodeBarang != null && kodeBarang.length() > 0){
            if(kodeRak == null || kodeRak.length() == 0){
                autoKodeRak(kodeBarangSaja);
            }   
        }

        String kodeBatchOrder = ((StockActivity)getActivity()).getKodeBatchOrder();
        if(kodeBatchOrder != null){
            kodeBatchOrderText.setText(kodeBatchOrder);
        }

        kodeBarangText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String kodeBarang = kodeBarangText.getText().toString();
                    if(kodeBarang.length() > 0){
                        HashMap<String,String> hashMap;
                        hashMap = (HashMap<String, String>) AppUtility.extractBarcode(kodeBarang);
                        String kodeBarangSaja = hashMap.get("kodeBarang");
                        kodeBarangText.setText(kodeBarangSaja);
                        //jumlahText.setText(hashMap.get("qty"));
                        autoKodeRak(kodeBarangSaja);
                    }
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

    @OnClick(R.id.qrBatchOrderBtn)
    public void scanBatchOrder(){
        ((StockActivity)getActivity()).scan("kodeBatchOrder");
    }

    @OnClick(R.id.simpanBtn)
    protected void simpan(){
        loading = ProgressDialog.show(getContext(), null, "Harap Tunggu...", true, false);
        String kodebatchorder = kodeBatchOrderText.getText().toString();
        String kodebarang = kodeBarangText.getText().toString();
        String koderak = kodeRakText.getText().toString();
        String kodemr = mrSpinner.getSelectedItem().toString();
        String jml = jumlahText.getText().toString();
        String note = etNote.getText().toString();
        Boolean error = false;
        Float jumlah = 0.2f;
        List<String> pesanError;
        pesanError = new ArrayList<>();
        /* pastkan semua sudah diisi
        if(kodebatchorder.trim().isEmpty()){
            error = true;
            pesanError.add("Scan batch order");
        }*/
        /* pastkan semua sudah diisi */
        if(kodebarang.trim().isEmpty()){
            error = true;
            pesanError.add("Scan barcode barang");
        }
        if(koderak.trim().isEmpty()){
            error = true;
            pesanError.add("Scan barcode rak");
        }
        if(jml.trim().isEmpty()){
            error = true;
            pesanError.add("Jumlah barang harus diisi");
        }else{
            jumlah = Float.parseFloat(jml);
            if(jumlah <= 0){
                error = true;
                pesanError.add("Jumlah barang harus > 0");
            }
        }

        if(error){
            Toast.makeText(getContext(), pesanError.toString(), Toast.LENGTH_SHORT).show();
            loading.dismiss();
            return;
        }

        ((StockoutActivity)getActivity()).getmApiService().saveStockOut(kodebatchorder,kodebarang,koderak,jumlah, kodemr, note).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    loading.dismiss();
                    try {
                        JSONObject jsonRESULTS = null;
                        try {
                            jsonRESULTS = new JSONObject(response.body().string());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String message = jsonRESULTS.getString("message");

                        if (jsonRESULTS.getString("status").equals("1")){
                            resetInputan();
                        }
                        AppUtility.showAlert(getContext(),message,"Informasi");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    loading.dismiss();
                    Toast.makeText(getContext(), "Gagal menyimpan data stock out", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getContext(), "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void autoKodeRak(String kodebarang){
        loading = ProgressDialog.show(getContext(), null, "Harap Tunggu...", true, false);
        ((StockoutActivity)getActivity()).getmApiService().getKodeRak(kodebarang).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    loading.dismiss();
                    try {
                        JSONObject jsonRESULTS = null;
                        try {
                            jsonRESULTS = new JSONObject(response.body().string());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String message = jsonRESULTS.getString("message");
                        String content = jsonRESULTS.getString("content");
                        String qty = jsonRESULTS.getString("qty");
                        if (jsonRESULTS.getString("status").equals("1")){
                            kodeRakText.setText(content);
                            jumlahText.setText(qty);
                            populateSpinnerOption(kodebarang);
                        }else{
                            AppUtility.showAlert(getContext(),message,"Informasi");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    loading.dismiss();
                    Toast.makeText(getContext(), "Gagal mengambil data rak", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getContext(), "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateSpinnerOption(String kodebarang){
        loading = ProgressDialog.show(getContext(), null, "Harap Tunggu...", true, false);
        ((StockoutActivity)getActivity()).getmApiService().getListMr(kodebarang).enqueue(new Callback<ResponseMr>() {
            @Override
            public void onResponse(Call<ResponseMr> call, Response<ResponseMr> response) {
                if (response.isSuccessful()){
                    loading.dismiss();
                    String message = response.body().getMessage();
                    Integer status = response.body().getStatus();
                    if (status == 1) {
                        List<MrItem> items = response.body().getItems();
                        listMr.add("Pilih MR__Pilih FG");
                        for(MrItem item : items){
                            listMr.add(item.getMrFg());
                        }
                        adapterMr = new ArrayAdapter<String>(getContext(), R.layout.spinner_row, listMr);
                        mrSpinner.setAdapter(adapterMr);
                        //adapterMr.notifyDataSetChanged();
                        loading.dismiss();
                    }
                } else {
                    loading.dismiss();
                    Toast.makeText(getContext(), "Gagal mengambil data MR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseMr> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getContext(), "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetInputan(){
        kodeBatchOrderText.setText("");
        kodeBarangText.setText("");
        kodeRakText.setText("");
        jumlahText.setText("");
    }
}
