package com.asligresik.inventoryApp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.util.SharedPrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.etUrlServer) EditText etUrlServer;
    @BindView(R.id.btnSimpan) Button btnSimpan;
    ProgressDialog loading;

    Context mContext;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
        mContext = this;
        sharedPrefManager = new SharedPrefManager(this);
        etUrlServer.setText(sharedPrefManager.getSPUrlServer());
    }

    @OnClick(R.id.btnSimpan)
    public void simpanSetting(){
        String urlServer = etUrlServer.getText().toString();
        sharedPrefManager.saveSPString(SharedPrefManager.SP_URLSERVER, urlServer);
        Toast.makeText(this,"Berhasil disimpan",Toast.LENGTH_SHORT).show();
        //AppUtility.showAlert(getContext(),"Berhasil disimpan ","Info !");
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
