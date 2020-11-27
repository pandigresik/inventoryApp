package com.asligresik.inventoryApp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.activity.BaseActivity;
import com.asligresik.inventoryApp.model.PartNameGoodReceipt;
import okhttp3.ResponseBody;

import com.asligresik.inventoryApp.model.ResponseRmiGoodReceipt;
import com.asligresik.inventoryApp.model.RmiGoodReceipt;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedHashTreeMap;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoodReceiptSearchAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private LinkedTreeMap<String,PartNameGoodReceipt> partNameList = new LinkedTreeMap<>();
    private List<String> resultList = new ArrayList<String>();
    public GoodReceiptSearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public LinkedTreeMap<String, PartNameGoodReceipt> getPartNameList() {
        return partNameList;
    }

    public void setPartNameList(LinkedTreeMap<String, PartNameGoodReceipt> partNameList) {
        this.partNameList = partNameList;
    }

    public LinkedTreeMap<String, RmiGoodReceipt> getListRmi(String partName){
        return partNameList.get(partName).getRmi();
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_rmi, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.tvRmi)).setText(getItem(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    String keySearch = constraint.toString();
                    if(partNameList.containsKey(keySearch)){
                        List<String> items = new ArrayList<String>();
                        items.add(keySearch);
                        filterResults.values = items;
                        filterResults.count = items.size();
                        return filterResults;
                    }
                    ((BaseActivity) mContext).getmApiService().getRmiSearch(constraint.toString()).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()){
                                JSONObject jsonRESULTS = null;
                                try{
                                    String json = response.body().string();
                                    try {
                                        jsonRESULTS = new JSONObject(json);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String message = jsonRESULTS.getString("message");
                                    Integer status = jsonRESULTS.getInt("status");

                                    if (status == 1) {
                                        Gson gson = new Gson();
                                        // partNameList = gson.fromJson(jsonRESULTS.getString("content"), new TypeToken<Map<String,PartNameGoodReceipt>>(){}.getType() );
                                        JsonObject jsonObject = gson.fromJson(jsonRESULTS.getString("content"), JsonObject.class);
                                        for(Map.Entry<String, JsonElement> entry: jsonObject.entrySet()){
                                            String key = entry.getKey();
                                            PartNameGoodReceipt partNameGoodReceipt = new PartNameGoodReceipt();
                                            JsonObject rmiJson = gson.fromJson(entry.getValue().toString(),JsonObject.class);
                                            LinkedTreeMap<String, RmiGoodReceipt> rmiObj = new LinkedTreeMap<String, RmiGoodReceipt>();
                                            for(Map.Entry<String, JsonElement> entryRmi: rmiJson.entrySet()){
                                                rmiObj.put(entryRmi.getKey(),gson.fromJson(entryRmi.getValue().toString(),RmiGoodReceipt.class));
                                            }
                                            partNameGoodReceipt.setRmi(rmiObj);
                                            partNameList.put(key,partNameGoodReceipt);
                                        }

                                        List<String> items = new ArrayList<String>();
                                        partNameList.forEach((k,v) -> {
                                            items.add(k);
                                        });
                                        filterResults.values = items;
                                        filterResults.count = items.size();
                                        resultList = items;
                                        notifyDataSetChanged();
                                    }
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            } else {
                                Toast.makeText(mContext, "Gagal mengambil Good Receipt Search", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("rmi search","rmi search "+ call.toString() );
                            Toast.makeText(mContext, "Koneksi internet bermasalah, Good Receipt Search", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<String>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;

    }
}
