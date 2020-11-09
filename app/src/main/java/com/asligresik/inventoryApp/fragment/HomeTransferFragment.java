package com.asligresik.inventoryApp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.activity.StockActivity;
import com.asligresik.inventoryApp.activity.TransferActivity;
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

public class HomeTransferFragment extends Fragment {
    View view;
    ProgressDialog loading;
    @BindView(R.id.kodeRakAsalText)
    AppCompatEditText kodeRakAsalText;
    @BindView(R.id.kodeBarangText)
    AppCompatEditText kodeBarangText;
    @BindView(R.id.jumlahText)
    AppCompatEditText jumlahText;
    @BindView(R.id.kodeRakTujuanText)
    AppCompatEditText kodeRakTujuanText;
    @BindView(R.id.simpanBtn)
    AppCompatButton simpanBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_transfer, container, false);

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

        if(kodeBarang != null && kodeBarang.length() > 0){
            autoKodeRak(kodeBarangSaja);
        }
        kodeBarangText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String kodeBarang = kodeBarangText.getText().toString();
                    if(kodeBarang.length() > 0){
                        HashMap<String, String> hashMap;
                        hashMap = (HashMap<String, String>) AppUtility.extractBarcode(kodeBarang);
                        String kodeBarangSaja = hashMap.get("kodeBarang");
                        kodeBarangText.setText(kodeBarangSaja);
                        //jumlahText.setText(hashMap.get("qty"));
                        autoKodeRak(kodeBarangSaja);
                    }
                }
            }
        });
        String kodeRakAsal = ((StockActivity) getActivity()).getKodeRakAsal();
        if (kodeRakAsal != null) {
            kodeRakAsalText.setText(((StockActivity) getActivity()).getKodeRakAsal());
        }

        String kodeRakTujuan = ((StockActivity) getActivity()).getKodeRakAsal();
        if (kodeRakTujuan != null) {
            kodeRakTujuanText.setText(((StockActivity) getActivity()).getKodeRakTujuan());
        }

        return view;
    }
    
    @OnClick(R.id.simpanBtn)
    protected void simpan() {
        loading = ProgressDialog.show(getContext(), null, "Harap Tunggu...", true, false);
        String kodeRakAsal = kodeRakAsalText.getText().toString();
        String kodeBarang = kodeBarangText.getText().toString();
        String koderakTujuan = kodeRakTujuanText.getText().toString();
        String jml = jumlahText.getText().toString();
        Boolean error = false;
        Float jumlah = 0.2f;
        List<String> pesanError;
        pesanError = new ArrayList<>();
        /* pastkan semua sudah diisi */
        if (kodeRakAsal.trim().isEmpty()) {
            error = true;
            pesanError.add("Scan barcode rak asal");
        }
        if (kodeBarang.trim().isEmpty()) {
            error = true;
            pesanError.add("Scan barcode barang");
        }
        if (koderakTujuan.trim().isEmpty()) {
            error = true;
            pesanError.add("Scan barcode rak tujuan");
        }
        if (jml.trim().isEmpty()) {
            error = true;
            pesanError.add("Jumlah barang harus diisi");
        } else {
            jumlah = Float.parseFloat(jml);
            if (jumlah <= 0) {
                error = true;
                pesanError.add("Jumlah barang harus > 0");
            }
        }


        if (error) {
            Toast.makeText(getContext(), pesanError.toString(), Toast.LENGTH_SHORT).show();
            loading.dismiss();
            return;
        }


        ((TransferActivity) getActivity()).getmApiService().saveTransfer(kodeRakAsal, kodeBarang, koderakTujuan, jumlah).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();

                    try {
                        JSONObject jsonRESULTS = null;
                        try {
                            jsonRESULTS = new JSONObject(response.body().string());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String message = jsonRESULTS.getString("message");

                        if (jsonRESULTS.getString("status").equals("1")) {
                            resetInputan();
                        }
                        AppUtility.showAlert(getContext(), message, "Informasi");

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

    @OnClick(R.id.qrBarangBtn)
    public void scanBarang() {
        ((StockActivity) getActivity()).scan("kodeBarang");
    }

    @OnClick(R.id.qrRakBtn)
    public void scanRakTujuan() {
        ((StockActivity) getActivity()).scan("kodeRakTujuan");
    }

    @OnClick(R.id.qrRakAsalBtn)
    public void scanRakAsal() {
        ((StockActivity) getActivity()).scan("kodeRakAsal");
    }

    private void resetInputan() {
        kodeRakAsalText.setText("");
        kodeBarangText.setText("");
        kodeRakTujuanText.setText("");
        jumlahText.setText("");
    }

    private void autoKodeRak(String kodebarang){
        loading = ProgressDialog.show(getContext(), null, "Harap Tunggu...", true, false);
        ((TransferActivity)getActivity()).getmApiService().getKodeRak(kodebarang).enqueue(new Callback<ResponseBody>() {
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
                            kodeRakAsalText.setText(content);
                            jumlahText.setText(qty);
                            ((StockActivity) getActivity()).setKodeRakAsal(content);
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
}
