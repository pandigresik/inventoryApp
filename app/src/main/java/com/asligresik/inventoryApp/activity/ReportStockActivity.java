package com.asligresik.inventoryApp.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import com.asligresik.inventoryApp.fragment.HomeReportOutstandingStockRmiFragment;

public class ReportStockActivity extends StockActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void loadDefaultFragment() {
        fragment = new HomeReportOutstandingStockRmiFragment();
        setFragment(fragment);
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
}
