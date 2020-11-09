package com.asligresik.inventoryApp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.util.SharedPrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.stock)
    RelativeLayout stockIcon;
    @BindView(R.id.stockIn)
    RelativeLayout stockInIcon;
    @BindView(R.id.stockOut)
    RelativeLayout stockOutIcon;
    @BindView(R.id.history)
    RelativeLayout historyIcon;
    @BindView(R.id.reporting)
    RelativeLayout reportIcon;
    @BindView(R.id.txtUserName)
    TextView username;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        sharedPrefManager = new SharedPrefManager(this);
        username.setText("Login sebagai "+sharedPrefManager.getSPNama().toString());
    }

    @OnClick(R.id.stock)
    public void stockPage(){
        gotoActivity(new StockActivity());
    }

    @OnClick(R.id.stockIn)
    public void stockInPage(){
        gotoActivity(new StockinActivity());
    }

    @OnClick(R.id.stockOut)
    public void stockoutPage(){
        gotoActivity(new StockoutActivity());
    }

    @OnClick(R.id.history)
    public void historyPage(){
        gotoActivity(new HistoryActivity());
    }

    @OnClick(R.id.stockOpname)
    public void stockOpnamePage(){
        gotoActivity(new StockOpnameActivity());
    }

    @OnClick(R.id.incoming)
    public void transferPage(){
        gotoActivity(new TransferActivity());
    }

    @OnClick(R.id.reporting)
    public void reportPage(){
        gotoActivity(new ReportActivity());
    }

    @OnClick(R.id.stockRmi)
    public void reportStockPage(){
        gotoActivity(new ReportStockActivity());
    }

    @OnClick(R.id.printLabel)
    public void printLabelPage(){ gotoActivity(new PrintLabelActivity());}

    @Override
    public void onBackPressed() {
        sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
        sharedPrefManager.saveSPString(SharedPrefManager.SP_TOKEN, "");
        startActivity(new Intent(MainActivity.this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
        super.onBackPressed();
    }
}