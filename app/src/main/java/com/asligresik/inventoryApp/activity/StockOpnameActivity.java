package com.asligresik.inventoryApp.activity;

import android.os.Bundle;

import com.asligresik.inventoryApp.fragment.HomeStockOpnameFragment;

public class StockOpnameActivity extends StockActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadDefaultFragment() {
        fragment = new HomeStockOpnameFragment();
        setFragment(fragment);
    }

}
