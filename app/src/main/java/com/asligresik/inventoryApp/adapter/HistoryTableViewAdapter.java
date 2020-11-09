package com.asligresik.inventoryApp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.model.HistoryItem;

import java.util.List;

public class HistoryTableViewAdapter extends RecyclerView.Adapter {
    List<HistoryItem> items;

    public HistoryTableViewAdapter(List<HistoryItem> items) {
        this.items = items;
    }

    public List<HistoryItem> getItems() {
        return items;
    }

    public void setItems(List<HistoryItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.history_item, parent, false);

        return new RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rowViewHolder = (RowViewHolder) holder;

        int rowPos = rowViewHolder.getAdapterPosition();

        if (rowPos == 0) {
            // Header Cells. Main Headings appear here
            rowViewHolder.txtTglTransaksi.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtKodeRak.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtKodeBarang.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtType.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtQty.setBackgroundResource(R.drawable.table_header_cell_bg);

            rowViewHolder.txtTglTransaksi.setText("Tanggal");
            rowViewHolder.txtKodeRak.setText("Rak");
            rowViewHolder.txtKodeBarang.setText("Barang");
            rowViewHolder.txtType.setText("Tipe");
            rowViewHolder.txtQty.setText("Jumlah");
        } else {
            HistoryItem modal = items.get(rowPos-1);
            // Content Cells. Content appear here
            rowViewHolder.txtTglTransaksi.setText(modal.getTgl_transaksi()+"");
            rowViewHolder.txtKodeRak.setText(modal.getKoderak());
            rowViewHolder.txtKodeBarang.setText(modal.getKodebarang());
            rowViewHolder.txtType.setText(modal.getType()+"");
            rowViewHolder.txtQty.setText(modal.getQty()+"");
        }
    }

    @Override
    public int getItemCount() {
        return items.size()+1; // one more to add header row
    }

    public class RowViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtTglTransaksi;
        protected TextView txtKodeRak;
        protected TextView txtKodeBarang;
        protected TextView txtType;
        protected TextView txtQty;

        public RowViewHolder(View itemView) {
            super(itemView);

            txtTglTransaksi = itemView.findViewById(R.id.txtTglTransaksi);
            txtKodeRak = itemView.findViewById(R.id.txtKodeRak);
            txtKodeBarang = itemView.findViewById(R.id.txtKodeBarang);
            txtType = itemView.findViewById(R.id.txtType);
            txtQty = itemView.findViewById(R.id.txtQty);
        }
    }
}
