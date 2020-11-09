package com.asligresik.inventoryApp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.model.RmiItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportRMIItemViewAdapter extends RecyclerView.Adapter {
    List<RmiItem> items;
    List<RmiItem> defaultitems;
    public ReportRMIItemViewAdapter(List<RmiItem> items) {
        this.items = items;
        this.defaultitems = new ArrayList<>(items);
    }

    public List<RmiItem> getItems() {
        return items;
    }

    public void setItems(List<RmiItem> items) {
        this.items = items;
        this.defaultitems = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.rmi_item, parent, false);

        return new RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rowViewHolder = (RowViewHolder) holder;

        int rowPos = rowViewHolder.getAdapterPosition();
        if (rowPos == 0) {
            // Header Cells. Main Headings appear here
            rowViewHolder.txtRmiItem.setText("NO MR");
            rowViewHolder.txtWONumber.setText("NO WO");
            rowViewHolder.txtWOTgl.setText("TANGGAL");

            rowViewHolder.txtRmiItem.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtWONumber.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtWOTgl.setBackgroundResource(R.drawable.table_header_cell_bg);
        } else {
            RmiItem modal = items.get(rowPos - 1);
            // Content Cells. Content appear here
            rowViewHolder.txtRmiItem.setText(modal.getRmi() + "");
            rowViewHolder.txtWONumber.setText(modal.getNumber() + "");
            rowViewHolder.txtWOTgl.setText(modal.getTanggal() + "");

            rowViewHolder.txtRmiItem.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtWONumber.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtWOTgl.setBackgroundResource(R.drawable.table_content_cell_bg);
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
        List<RmiItem> itemResults = new ArrayList<>();
        characterText = characterText.toLowerCase(Locale.getDefault());
        if (characterText.length() > 0) {
            for (RmiItem item: defaultitems) {
                if (item.getRmi().toLowerCase(Locale.getDefault()).contains(characterText) || item.getNumber().toLowerCase(Locale.getDefault()).contains(characterText)) {
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
        protected TextView txtWONumber;
        protected TextView txtWOTgl;
        public RowViewHolder(View itemView) {
            super(itemView);
            txtRmiItem = itemView.findViewById(R.id.txtRmi);
            txtWONumber = itemView.findViewById(R.id.txtWONumber);
            txtWOTgl = itemView.findViewById(R.id.txtWOTgl);
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
