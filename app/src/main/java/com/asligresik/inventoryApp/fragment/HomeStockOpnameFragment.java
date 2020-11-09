package com.asligresik.inventoryApp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.activity.StockActivity;
import com.asligresik.inventoryApp.activity.StockOpnameActivity;
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

public class HomeStockOpnameFragment extends Fragment {
    View view;
    ProgressDialog loading;

    @BindView(R.id.kodeBarangText)
    EditText kodeBarangText;
    @BindView(R.id.kodeRakText)
    EditText kodeRakText;
    @BindView(R.id.jumlahText)
    EditText jumlahText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_stockopname, container, false);
        ButterKnife.bind(this, view);

        String kodeBarang = ((StockActivity)getActivity()).getKodeBarang();
        if(kodeBarang != null){
            HashMap<String,String> hashMap;
            hashMap = (HashMap<String, String>) AppUtility.extractBarcode(kodeBarang);
            kodeBarangText.setText(hashMap.get("kodeBarang"));
            jumlahText.setText(hashMap.get("qty"));
            //kodeBarangText.setText(kodeBarang);

        }

        String kodeRak = ((StockActivity)getActivity()).getKodeRak();
        if(kodeRak != null){
            kodeRakText.setText(((StockActivity)getActivity()).getKodeRak());
        }

        kodeBarangText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String kodeBarang = kodeBarangText.getText().toString();
                    HashMap<String,String> hashMap;
                    hashMap = (HashMap<String, String>) AppUtility.extractBarcode(kodeBarang);
                    kodeBarangText.setText(hashMap.get("kodeBarang"));
                    jumlahText.setText(hashMap.get("qty"));
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

    @OnClick(R.id.simpanBtn)
    protected void simpan(){
        loading = ProgressDialog.show(getContext(), null, "Harap Tunggu...", true, false);
        String kodebarang = kodeBarangText.getText().toString();
        String koderak = kodeRakText.getText().toString();
        String jml = jumlahText.getText().toString();
        Boolean error = false;
        Float jumlah = 0.2f;
        List<String> pesanError;
        pesanError = new ArrayList<>();
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


        ((StockOpnameActivity)getActivity()).getmApiService().saveStockOpname(kodebarang,koderak,jumlah).enqueue(new Callback<ResponseBody>() {
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
                        Toast.makeText(getContext(), "Gagal menyimpan data stock in", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(getContext(), "Koneksi Internet Bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetInputan(){
        kodeBarangText.setText("");
        kodeRakText.setText("");
        jumlahText.setText("");
    }
}
