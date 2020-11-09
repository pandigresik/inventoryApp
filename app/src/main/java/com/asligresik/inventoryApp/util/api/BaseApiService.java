package com.asligresik.inventoryApp.util.api;

import com.asligresik.inventoryApp.model.ResponseHistory;
import com.asligresik.inventoryApp.model.ResponseKategoriMr;
import com.asligresik.inventoryApp.model.ResponseMr;
import com.asligresik.inventoryApp.model.ResponseOutstandingRmiItem;
import com.asligresik.inventoryApp.model.ResponseRmiItem;
import com.asligresik.inventoryApp.model.ResponseStock;
import com.asligresik.inventoryApp.model.ResponseStockRmiItem;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BaseApiService {

    // Fungsi ini untuk memanggil API http://10.0.2.2/mahasiswa/login.php
    @FormUrlEncoded
    @POST("auth/login")
    Call<ResponseBody> loginRequest(@Field("username") String username,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("stock/savein")
    Call<ResponseBody> saveStockIn(@Field("listbarang") String listbarang);

    @FormUrlEncoded
    @POST("stock/saveout")
    Call<ResponseBody> saveStockOut(@Field("kodebatchorder") String kodebatchorder,@Field("kodebarang") String kodebarang,
                                   @Field("koderak") String koderak, @Field("quantity") Float quantity, @Field("kodemr") String kodemr, @Field("note") String note);

    @FormUrlEncoded
    @POST("stock/saveopname")
    Call<ResponseBody> saveStockOpname(@Field("kodebarang") String kodebarang,
                                   @Field("koderak") String koderak, @Field("quantity") Float quantity);

    @FormUrlEncoded
    @POST("stock/transfer")
    Call<ResponseBody> saveTransfer(@Field("koderakasal") String kodebatchorder,@Field("kodebarang") String kodebarang,
                                    @Field("koderaktujuan") String koderak, @Field("quantity") Float quantity);


    @GET("stock/list")
    Call<ResponseStock> listStock(@Query("kodebarang") String kodebarang, @Query("koderak") String koderak, @Query("lot") int lot);

    @GET("stock/history")
    Call<ResponseHistory> historyStock(@Query("tglawal") String tglawal,@Query("tglakhir") String tglakhir,@Query("kodebarang") String kodebarang, @Query("koderak") String koderak);

    @GET("stock/kodeRak")
    Call<ResponseBody> getKodeRak(@Query("kodebarang") String kodebarang);

    @GET("stock/listMr")
    Call<ResponseMr> getListMr(@Query("kodebarang") String kodebarang);

    @GET("stock/kategoriMr")
    Call<ResponseKategoriMr> getKategoriMr();

    @GET("stock/itemRmi")
    Call<ResponseRmiItem> getItemRmi(@Query("rmi") String rmi);

    @GET("stock/stockRmi")
    Call<ResponseOutstandingRmiItem> getStockRmi(@Query("rmi") String rmi);

    @GET("stock/stockRakRmi")
    Call<ResponseStockRmiItem> getStockRakItemRmi(@Query("rmi") String rmi);

    @GET("stock/stockItemRmi")
    Call<ResponseOutstandingRmiItem> getStockItemRmi();

    // Fungsi ini untuk memanggil API http://10.0.2.2/mahasiswa/register.php
    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseBody> registerRequest(@Field("nama") String nama,
                                       @Field("email") String email,
                                       @Field("password") String password);

}
