package com.asligresik.inventoryApp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.model.StockRmiItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportStockRakRMIItemViewAdapter extends RecyclerView.Adapter {
    List<StockRmiItem> items;
    List<StockRmiItem> defaultitems;
    public ReportStockRakRMIItemViewAdapter(List<StockRmiItem> items) {
        this.items = items;
        this.defaultitems = new ArrayList<>(items);
    }

    public List<StockRmiItem> getItems() {
        return items;
    }

    public void setItems(List<StockRmiItem> items) {
        this.items = items;
        this.defaultitems = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.stock_rak_rmi_item, parent, false);

        return new RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rowViewHolder = (RowViewHolder) holder;

        int rowPos = rowViewHolder.getAdapterPosition();
        if (rowPos == 0) {
            // Header Cells. Main Headings appear here
            rowViewHolder.txtRmiItem.setText("RMI");
            rowViewHolder.txtRak.setText("Rak");
            rowViewHolder.txtQty.setText("Qty");

            rowViewHolder.txtRmiItem.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtRak.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtQty.setBackgroundResource(R.drawable.table_header_cell_bg);

        } else {

            StockRmiItem modal = items.get(rowPos - 1);
            // Content Cells. Content appear here
            rowViewHolder.txtRmiItem.setText(modal.getRmi() + "");
            rowViewHolder.txtRak.setText(modal.getRak() + "");
            rowViewHolder.txtQty.setText(String.valueOf(modal.getQty()) +"");

            rowViewHolder.txtRmiItem.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtRak.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtQty.setBackgroundResource(R.drawable.table_content_cell_bg);
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1; // one more to add header row
    }

    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public void setFilter(String characterText) {
        List<StockRmiItem> itemResults = new ArrayList<>();
        characterText = characterText.toLowerCase(Locale.getDefault());
        if (characterText.length() > 0) {
            for (StockRmiItem item: defaultitems) {
                if (item.getRmi().toLowerCase(Locale.getDefault()).contains(characterText) || item.getRak().toLowerCase(Locale.getDefault()).contains(characterText)) {
                    itemResults.add(item);
                }
            }
        }else{
            itemResults.addAll(defaultitems);
        }
        items.clear(); // add this so that it will clear old data
        items.addAll(itemResults);
        notifyDataSetChanged();
    }
    public class RowViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtRmiItem;
        protected TextView txtRak;
        protected TextView txtQty;

        public RowViewHolder(View itemView) {
            super(itemView);
            txtRmiItem = itemView.findViewById(R.id.txtRmiItem);
            txtRak = itemView.findViewById(R.id.txtRak);
            txtQty = itemView.findViewById(R.id.txtQty);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(itemView,position);
                        }
                    }
                }
            });
        }
    }
}
