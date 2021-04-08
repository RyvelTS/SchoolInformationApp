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

public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder> {
    private ArrayList<RegistrationObject> RegistrationItem;
    private OnItemClickListener Listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        Listener = listener;
    }
    public RegistrationAdapter(ArrayList<RegistrationObject> registrationItem) {
        RegistrationItem = registrationItem;
    }

    @NonNull
    @Override
    public RegistrationAdapter.RegistrationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_viewer_a,parent,false);
        RegistrationViewHolder viewHolder = new RegistrationViewHolder(view,Listener);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RegistrationAdapter.RegistrationViewHolder holder, int position) {
        RegistrationObject currentItem = RegistrationItem.get(position);
        if(currentItem.getRegistrationTitle().length() <=20){
            holder.Title.setText(currentItem.getRegistrationTitle());
        }else if(currentItem.getRegistrationTitle().length() >21){
            holder.Title.setText(currentItem.getRegistrationTitle().substring(0,20) + "...");
        }
        if(currentItem.getRegistrationContent().length() <=53){
            holder.Content.setText(currentItem.getRegistrationContent());
        }else if(currentItem.getRegistrationContent().length() >53){
            holder.Content.setText(currentItem.getRegistrationContent().substring(0,53) + "...");
        }
        holder.thumbnail.setImageResource(currentItem.getPreview());
    }

    @Override
    public int getItemCount() {
        return RegistrationItem.size();
    }


    public class RegistrationViewHolder extends RecyclerView.ViewHolder {
        public TextView Title;
        public TextView Content;
        public ImageView thumbnail;
        public RegistrationViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
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
