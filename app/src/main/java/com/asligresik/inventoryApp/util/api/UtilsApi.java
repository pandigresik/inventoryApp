package com.asligresik.inventoryApp.util.api;

public class UtilsApi {

    // 10.0.2.2 ini adalah localhost.
    public static final String BASE_URL_API = "http://192.168.42.47:8000/";
    //public static final String BASE_URL_API = "http://192.168.5.1:8080/wms_p1/index.php/";

    // Mendeklarasikan Interface BaseApiService
    public static BaseApiService getAPIService(){
        RetrofitClient.resetConnection();
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }

    public static BaseApiService getAPIService(String baseUrl){
        RetrofitClient.resetConnection();
        return RetrofitClient.getClient(baseUrl).create(BaseApiService.class);
    }

    public static BaseApiService getAPIService(String baseUrl, String token){
        RetrofitClient.resetConnection();
        return RetrofitClient.getClient(baseUrl, token).create(BaseApiService.class);
    }
}
