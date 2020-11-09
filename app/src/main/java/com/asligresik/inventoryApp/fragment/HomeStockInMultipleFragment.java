package com.asligresik.inventoryApp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.activity.StockActivity;
import com.asligresik.inventoryApp.activity.StockinActivity;
import com.asligresik.inventoryApp.model.BarangItem;
import com.asligresik.inventoryApp.util.AppUtility;
import com.google.gson.Gson;

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

public class HomeStockInMultipleFragment extends Fragment {
    View view;
    ProgressDialog loading;
    List<EditText> listKodeBarang = new ArrayList<>();
    List<EditText> listJumlahBarang = new ArrayList<>();

    @BindView(R.id.kodeRakText)
    EditText kodeRakText;
    @BindView(R.id.blockKodeBarang)
    LinearLayout blockKodeBarang;
    @BindView(R.id.simpanBtn)
    AppCompatButton simpanBtn;
    @BindView(R.id.blockScrollKodeBarang)
    ScrollView blockScrollKodeBarang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_stockin_multiple, container, false);
        ButterKnife.bind(this, view);

        /*String kodeBarang = ((StockActivity)getActivity()).getKodeBarang();
        if(kodeBarang != null){
            HashMap<String,String> hashMap;
            hashMap = (HashMap<String, String>) AppUtility.extractBarcode(kodeBarang);
            kodeBarangText.setText(hashMap.get("kodeBarang"));
            jumlahText.setText(hashMap.get("qty"));
        }
        */
        String kodeRak = ((StockActivity)getActivity()).getKodeRak();
        if(kodeRak != null){
            kodeRakText.setText(((StockActivity)getActivity()).getKodeRak());
        }

//        kodeBarangText.setOnFocusChangeListener(new BarangListener());
//        listKodeBarang.add(kodeBarangText);
//        listJumlahBarang.add(jumlahText);
        generateListBarang();
        updateTotalData();
        //blockScrollKodeBarang.scrollTo(0,blockScrollKodeBarang.getHeight());
        return view;
    }

    @OnClick(R.id.qrRakBtn)
    public void scanRak(){
        ((StockActivity)getActivity()).scan("kodeRak");
    }

    @OnClick(R.id.simpanBtn)
    protected void simpan(){
        loading = ProgressDialog.show(getContext(), null, "Harap Tunggu...", true, false);
        //String kodebarang = kodeBarangText.getText().toString();
        String koderak = kodeRakText.getText().toString();
        //String jml = jumlahText.getText().toString();
        List<BarangItem> listBarang = new ArrayList<>();
        boolean error = false;
        ArrayMap<String,Integer> tmpKodeBarangKembar = new ArrayMap<String, Integer>();
        List<String> pesanError;
        pesanError = new ArrayList<>();
        if(listKodeBarang.size() > 0){
            for(int i = 0; i < listKodeBarang.size(); i++){
                EditText txtKodeBarang = listKodeBarang.get(i);
                EditText txtJmlBarang = listJumlahBarang.get(i);
                String tmpKodeBarang = txtKodeBarang.getText().toString();

                if(tmpKodeBarang.length() > 0){
                    if(tmpKodeBarangKembar.get(tmpKodeBarang) == null){
                        tmpKodeBarangKembar.put(tmpKodeBarang,1);
                    }else{
                        error = true;
                        pesanError.add("Kode barang "+tmpKodeBarang+" discan lebih dari 1 kali");
                    }

                    if(txtJmlBarang.getText().toString().length() > 0){
                        Float tmpJmlBarang = 0.2f;
                        tmpJmlBarang = Float.parseFloat(txtJmlBarang.getText().toString());
                        if(tmpJmlBarang > 0){
                            BarangItem brgItem = new BarangItem();
                            brgItem.setKoderak(koderak);
                            brgItem.setKodebarang(tmpKodeBarang);
                            brgItem.setQty(tmpJmlBarang);
                            listBarang.add(brgItem);
                        }else{
                            error = true;
                            pesanError.add("Jumlah barang "+tmpKodeBarang+" harus > 0");
                        }
                    }else{
                        error = true;
                        pesanError.add("Jumlah barang "+txtKodeBarang.getText().toString()+" harus diisi");
                    }
                }
            }
        }

        /* pastkan semua sudah diisi */
        if(listBarang.size() <= 0){
            error = true;
            pesanError.add("Scan barcode barang");
        }
        if(koderak.trim().isEmpty()){
            error = true;
            pesanError.add("Scan barcode rak");
        }

        if(error){
            Toast.makeText(getContext(), pesanError.toString(), Toast.LENGTH_SHORT).show();
            loading.dismiss();
            return;
        }
        //new Gson().toJson(listBarang)
        ((StockinActivity)getActivity()).getmApiService().saveStockIn(new Gson().toJson(listBarang)).enqueue(new Callback<ResponseBody>() {
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
                            AppUtility.showAlert(getContext(),message,"Informasi");
                            resetInputan();
                        }else{
                            AppUtility.showAlert(getContext(),message,"Simpan Gagal");
                        }


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
    private void generateListBarang(){
        List<String> listScanBarang = ((StockinActivity)getActivity()).getListKodeBarang();
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int currUrutan = 0;
        if(!listScanBarang.isEmpty()){
            for (String kodeBarang: listScanBarang) {
                HashMap<String,String> hashMap;
                hashMap = (HashMap<String, String>) AppUtility.extractBarcode(kodeBarang);
                /* tambahkan element baru*/
                LinearLayout layoutBaru = generateItemBarang(li, hashMap, currUrutan);
                blockKodeBarang.addView(layoutBaru);
                currUrutan++;
            }
        }
        /* tambahkan element baru*/
        HashMap<String,String> defaultHashMap = new HashMap<>();
        defaultHashMap.put("kodeBarang","");
        defaultHashMap.put("qty","0");
        LinearLayout layoutBaru = generateItemBarang(li, defaultHashMap, currUrutan);
        blockKodeBarang.addView(layoutBaru);
    }

    private LinearLayout generateItemBarang(LayoutInflater li, HashMap<String, String> hashMap, int currUrutan){
        LinearLayout layoutBaru = (LinearLayout) li.inflate(R.layout.item_kode_barang,null);
        EditText currKodeBarang = (EditText) layoutBaru.findViewById(R.id.kodeBarangText);
        EditText currJumlahBarang = (EditText) layoutBaru.findViewById(R.id.jumlahText);
        AppCompatImageButton currQrBtn = (AppCompatImageButton) layoutBaru.findViewById(R.id.qrBarangBtn);
        currKodeBarang.setOnFocusChangeListener(new BarangListener());
        currKodeBarang.setTag(currUrutan);
        currQrBtn.setOnClickListener(new MyQrClickListener(currUrutan));
        currKodeBarang.setText(hashMap.get("kodeBarang"));
        currJumlahBarang.setText(hashMap.get("qty"));
        listKodeBarang.add(currKodeBarang);
        listJumlahBarang.add(currJumlahBarang);
        return layoutBaru;
    }
    private void resetInputan(){
        ((StockinActivity)getActivity()).setKodeRak("");
        ((StockinActivity)getActivity()).setUrutan(-1);
        ((StockinActivity)getActivity()).resetListKodeBarang();
        ((StockinActivity)getActivity()).loadDefaultFragment();

    }
    private void updateTotalData(){
        simpanBtn.setText("simpan ( "+String.valueOf(listKodeBarang.size() - 1)+" ) data");
    }
    class MyQrClickListener implements View.OnClickListener{
        private int urutan;
        public MyQrClickListener(int urutan){
            this.urutan = urutan;
        }

        @Override
        public void onClick(View view) {
            ((StockActivity)getActivity()).scan("kodeBarang",urutan);
        }
    }

    class BarangListener implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                EditText currentKodeBarang = (EditText) v.findViewById(R.id.kodeBarangText);
                int urutan =  Integer.parseInt(currentKodeBarang.getTag().toString());
                int nextUrutan = urutan + 1;

                EditText currentJumlahBarang = (EditText) blockKodeBarang.getChildAt(urutan).findViewById(R.id.jumlahText);
                String kodeBarang = currentKodeBarang.getText().toString();
                HashMap<String,String> hashMap;
                hashMap = (HashMap<String, String>) AppUtility.extractBarcode(kodeBarang);

                currentKodeBarang.setText(hashMap.get("kodeBarang"));
                currentJumlahBarang.setText(hashMap.get("qty"));

                /* tambahkan element baru*/
                LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout layoutBaru = (LinearLayout) li.inflate(R.layout.item_kode_barang,null);
                EditText nextKodeBarang = (EditText) layoutBaru.findViewById(R.id.kodeBarangText);
                EditText nextJumlahBarang = (EditText) layoutBaru.findViewById(R.id.jumlahText);
                AppCompatImageButton nextQrBtn = (AppCompatImageButton) layoutBaru.findViewById(R.id.qrBarangBtn);
                nextKodeBarang.setOnFocusChangeListener(new BarangListener());
                nextKodeBarang.setTag(nextUrutan);
                nextQrBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((StockActivity)getActivity()).scan("kodeBarang",nextUrutan);
                    }
                });
                listKodeBarang.add(nextKodeBarang);
                listJumlahBarang.add(nextJumlahBarang);
                blockKodeBarang.addView(layoutBaru);
                updateTotalData();
                //blockScrollKodeBarang.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }
    }
}
