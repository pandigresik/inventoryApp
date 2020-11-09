package com.asligresik.inventoryApp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.model.StockItem;

import java.util.List;

public class StockTableViewAdapter extends RecyclerView.Adapter {
    List<StockItem> items;

    public StockTableViewAdapter(List<StockItem> items) {
        this.items = items;
    }

    public List<StockItem> getItems() {
        return items;
    }

    public void setItems(List<StockItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.stock_item, parent, false);

        return new RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rowViewHolder = (RowViewHolder) holder;

        int rowPos = rowViewHolder.getAdapterPosition();

        if (rowPos == 0) {
            // Header Cells. Main Headings appear here
            rowViewHolder.txtKodeRak.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtKodeBarang.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtQty.setBackgroundResource(R.drawable.table_header_cell_bg);
            
            rowViewHolder.txtKodeRak.setText("Kode Rak");
            rowViewHolder.txtKodeBarang.setText("Barang");
            rowViewHolder.txtQty.setText("Jumlah");
        } else {
            StockItem modal = items.get(rowPos-1);

            // Content Cells. Content appear here
            rowViewHolder.txtKodeRak.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtKodeBarang.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtQty.setBackgroundResource(R.drawable.table_content_cell_bg);

            rowViewHolder.txtKodeRak.setText(modal.getKoderak());
            rowViewHolder.txtKodeBarang.setText(modal.getKodebarang());
            rowViewHolder.txtQty.setText(modal.getQty()+"");
        }
    }

    @Override
    public int getItemCount() {
        return items.size()+1; // one more to add header row
    }

    public class RowViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtKodeRak;
        protected TextView txtQty;
        protected TextView txtKodeBarang;

        public RowViewHolder(View itemView) {
            super(itemView);

            txtKodeRak = itemView.findViewById(R.id.txtKodeRak);
            txtKodeBarang = itemView.findViewById(R.id.txtKodeBarang);
            txtQty = itemView.findViewById(R.id.txtQty);
        }
    }
}
