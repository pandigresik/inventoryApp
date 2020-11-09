package com.asligresik.inventoryApp.activity;

import android.os.Bundle;

import com.asligresik.inventoryApp.fragment.HomeTransferFragment;

public class TransferActivity extends StockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadDefaultFragment() {
        fragment = new HomeTransferFragment();
        setFragment(fragment);
    }
}
