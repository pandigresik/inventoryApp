package com.asligresik.inventoryApp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.model.MrItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportRMIViewAdapter extends RecyclerView.Adapter {
    List<MrItem> items;
    List<MrItem> defaultitems;
    public ReportRMIViewAdapter(List<MrItem> items) {
        this.items = items;
        this.defaultitems = new ArrayList<>(items);
    }

    public List<MrItem> getItems() {
        return items;
    }

    public void setItems(List<MrItem> items) {
        this.items = items;
        this.defaultitems = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.mr_item, parent, false);

        return new RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rowViewHolder = (RowViewHolder) holder;

        int rowPos = rowViewHolder.getAdapterPosition();
            MrItem modal = items.get(rowPos);
            // Content Cells. Content appear here
            rowViewHolder.txtMrItem.setText(modal.getMr()+"");

    }

    @Override
    public int getItemCount() {
        return items.size(); // one more to add header row
    }

    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public void setFilter(String characterText) {
        List<MrItem> itemResults = new ArrayList<>();
        characterText = characterText.toLowerCase(Locale.getDefault());
        if (characterText.length() > 0) {
            for (MrItem item: defaultitems) {
                if (item.getMr().toLowerCase(Locale.getDefault()).contains(characterText)) {
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
        protected TextView txtMrItem;

        public RowViewHolder(View itemView) {
            super(itemView);
            txtMrItem = itemView.findViewById(R.id.txtMrItem);
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
