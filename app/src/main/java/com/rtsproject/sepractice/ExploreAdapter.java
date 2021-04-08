package com.rtsproject.sepractice;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder> {
    private ArrayList<ExploreObject> ExploreItem;
    private OnItemClickListener Listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        Listener = listener;
    }
    public ExploreAdapter(ArrayList<ExploreObject> exploreItem) {
        ExploreItem = exploreItem;
    }

    @NonNull
    @Override
    public ExploreAdapter.ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_viewer_a,parent,false);
        ExploreViewHolder viewHolder = new ExploreViewHolder(view,Listener);
        return viewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExploreAdapter.ExploreViewHolder holder, int position) {
        ExploreObject currentItem = ExploreItem.get(position);
        if(currentItem.getExploreTitle().length() <=20){
            holder.Title.setText(currentItem.getExploreTitle());
        }else if(currentItem.getExploreTitle().length() >21){
            holder.Title.setText(currentItem.getExploreTitle().substring(0,20) + "...");
        }
        if(currentItem.getExploreContent().length() <=53){
            holder.Content.setText(currentItem.getExploreContent());
        }else if(currentItem.getExploreContent().length() >53){
            holder.Content.setText(currentItem.getExploreContent().substring(0,53) + "...");
        }
        holder.thumbnail.setImageResource(currentItem.getPreview());
    }

    @Override
    public int getItemCount() {
        return ExploreItem.size();
    }


    public class ExploreViewHolder extends RecyclerView.ViewHolder {
        public TextView Title;
        public TextView Content;
        public ImageView thumbnail;
        public ExploreViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            Title = itemView.findViewById(R.id.LastItemContentTitleTextView);
            Content = itemView.findViewById(R.id.LastItemContentTextView);
            thumbnail = itemView.findViewById(R.id.image_thumbnails_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
