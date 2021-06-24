package com.kptech.peps.recycler;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kptech.peps.R;
import com.kptech.peps.interfaces.RecyclerItemSelectedListener;
import com.kptech.peps.model.TopicModel;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TopicModel> itemList;
    private List<String> selectedTopics;
    public boolean multipleSelect;
    private RecyclerItemSelectedListener recyclerItemSelectedListener;

    public TopicAdapter(List<TopicModel> itemList, List<String> selectedTopics, Context context, RecyclerItemSelectedListener recyclerItemSelectedListener) {
        this.itemList = itemList;
        this.selectedTopics = selectedTopics;
        this.recyclerItemSelectedListener = recyclerItemSelectedListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(multipleSelect ? R.layout.item_overview_email : R.layout.item_overview_email, parent, false);
        return new CustomPopupMultiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final TopicModel interestModel = itemList.get(position);
        CustomPopupMultiViewHolder holder = (CustomPopupMultiViewHolder) viewHolder;
        String title = interestModel.name;
        holder.textView.setText(title);
        if (interestModel.isChecked || selectedTopics.contains(title))
            holder.tickImg.setVisibility(View.VISIBLE);
        else holder.tickImg.setVisibility(View.INVISIBLE);

        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((itemList.get(holder.getAdapterPosition()).isChecked || selectedTopics.contains(itemList.get(holder.getAdapterPosition()).name))) {
                    itemList.get(holder.getAdapterPosition()).isChecked = false;
                    selectedTopics.remove(itemList.get(holder.getAdapterPosition()).name);
                } else {
                    itemList.get(holder.getAdapterPosition()).isChecked = true;
                    selectedTopics.add(itemList.get(holder.getAdapterPosition()).name);
                }
                recyclerItemSelectedListener.onClick(holder.getAdapterPosition(), itemList.get(holder.getAdapterPosition()), holder.rootlayout);
                notifyDataSetChanged();
            }
        });


    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class CustomPopupMultiViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootlayout;
        TextView textView;
        ImageView tickImg;

        public CustomPopupMultiViewHolder(View itemView) {
            super(itemView);
            rootlayout = (LinearLayout) itemView.findViewById(R.id.layout);
            textView = itemView.findViewById(R.id.txt);
            tickImg = itemView.findViewById(R.id.img);
        }
    }

}
