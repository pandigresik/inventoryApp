package com.asligresik.inventoryApp.activity;

import android.os.Bundle;

import com.asligresik.inventoryApp.fragment.HomeStockInMultipleFragment;

import java.util.ArrayList;
import java.util.List;

public class StockinActivity extends StockActivity {
    List<String> listKodeBarang = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadDefaultFragment() {
        String kodeBarang = getKodeBarang();
        Integer urutan = getUrutan();
        if(urutan >= 0){
            if(urutan < listKodeBarang.size()){
                listKodeBarang.set(urutan, kodeBarang);
            }else{
                listKodeBarang.add(urutan, kodeBarang);
            }
        }
        //fragment = new HomeStockInFragment();
        fragment = new HomeStockInMultipleFragment();
        setFragment(fragment);
    }

    public List<String> getListKodeBarang() {
        return listKodeBarang;
    }

    public void setListKodeBarang(List<String> listKodeBarang) {
        this.listKodeBarang = listKodeBarang;
    }

    public void resetListKodeBarang() {
        listKodeBarang.clear();
    }
}
