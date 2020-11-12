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
import com.asligresik.inventoryApp.model.ResponseRmiGoodReceipt;
import com.asligresik.inventoryApp.model.RmiGoodReceipt;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoodReceiptSearchAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<RmiGoodReceipt> resultList = new ArrayList<RmiGoodReceipt>();
    public GoodReceiptSearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public RmiGoodReceipt getItem(int position) {
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
        ((TextView) convertView.findViewById(R.id.tvRmi)).setText(getItem(position).getRmi());
        return convertView;

    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    ((BaseActivity) mContext).getmApiService().getRmiSearch(constraint.toString()).enqueue(new Callback<ResponseRmiGoodReceipt>() {
                        @Override
                        public void onResponse(Call<ResponseRmiGoodReceipt> call, Response<ResponseRmiGoodReceipt> response) {
                            if (response.isSuccessful()){
                                String message = response.body().getMessage();
                                Integer status = response.body().getStatus();
                                if (status == 1) {
                                    List<RmiGoodReceipt> items = response.body().getItems();
                                    filterResults.values = items;
                                    filterResults.count = items.size();
                                    resultList = items;
                                    notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(mContext, "Gagal mengambil Good Receipt Search", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseRmiGoodReceipt> call, Throwable t) {
                            Toast.makeText(mContext, "Koneksi internet bermasalah, Good Receipt Search", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<RmiGoodReceipt>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;

    }
}
