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

public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.InformationViewHolder>{
    private ArrayList<InformationObject> InfoItem;
    private OnItemClickListener Listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        Listener = listener;
    }
    public InformationAdapter(ArrayList<InformationObject> infoItem) {
        InfoItem = infoItem;
    }
    @NonNull
    @Override
    public InformationAdapter.InformationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_viewer_a,parent,false);
        InformationViewHolder viewHolder = new InformationViewHolder(view, Listener);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InformationAdapter.InformationViewHolder holder, int position) {
    InformationObject currentItem = InfoItem.get(position);
        if(currentItem.getInfoTitle().length() <=20){
            holder.Title.setText(currentItem.getInfoTitle());
        }else if(currentItem.getInfoTitle().length() >21){
            holder.Title.setText(currentItem.getInfoTitle().substring(0,20) + "...");
        }
        if(currentItem.getInfoContent().length() <=53){
            holder.Content.setText(currentItem.getInfoContent());
        }else if(currentItem.getInfoContent().length() >53){
            holder.Content.setText(currentItem.getInfoContent().substring(0,53) + "...");
        }
    holder.thumbnail.setImageResource(currentItem.getPreview());
    }


    @Override
    public int getItemCount() {
        return InfoItem.size();
    }


    public class InformationViewHolder extends RecyclerView.ViewHolder {
        public TextView Title;
        public TextView Content;
        public ImageView thumbnail;
        public InformationViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
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
