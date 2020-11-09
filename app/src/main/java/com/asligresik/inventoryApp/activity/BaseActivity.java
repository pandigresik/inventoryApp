package com.asligresik.inventoryApp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.asligresik.inventoryApp.util.SharedPrefManager;
import com.asligresik.inventoryApp.util.api.BaseApiService;
import com.asligresik.inventoryApp.util.api.UtilsApi;

public class BaseActivity extends AppCompatActivity {
    private SharedPrefManager sharedPrefManager;
    private String urlServer;
    private String token;
    private BaseApiService mApiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getClass().getSimpleName());
        sharedPrefManager = new SharedPrefManager(this);
        urlServer = sharedPrefManager.getSPUrlServer();
        token = sharedPrefManager.getSpToken();
        if(urlServer.isEmpty()){
            setUrlServer(UtilsApi.BASE_URL_API);
        }

        mApiService = UtilsApi.getAPIService(getUrlServer(),token);
    }

    public void gotoActivity(BaseActivity activity){
        startActivity(new Intent(this, activity.getClass()));
    }

    public String getUrlServer() {
        return urlServer;
    }

    public void setUrlServer(String urlServer) {
        this.urlServer = urlServer;
    }

    public BaseApiService getmApiService() {
        return mApiService;
    }

    public void setmApiService(BaseApiService mApiService) {
        this.mApiService = mApiService;
    }
}
