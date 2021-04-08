package com.rtsproject.sepractice;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class FirestorePostsAdapter extends FirestoreRecyclerAdapter<FirestorePostsObject, FirestorePostsAdapter.PostHolder> {
    private OnItemClickListener listener;

    public FirestorePostsAdapter(@NonNull FirestoreRecyclerOptions<FirestorePostsObject> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull PostHolder holder, int position, @NonNull FirestorePostsObject model) {
        if(model.getTitle().length() <=21){
            holder.TitleTV.setText(model.getTitle());
        }else if(model.getContents().length() >21){
            holder.TitleTV.setText(model.getTitle().substring(0,20) + "...");
        }
        if(model.getContents().length() <=53){
            holder.ContentTV.setText(model.getContents());
        }else if(model.getContents().length() >53){
            holder.ContentTV.setText(model.getContents().substring(0,53) + "...");
        }
        if(!model.getPicture().equals("")){
            new DownLoadImageTask(holder.ThumbnailIV).execute(model.getPicture());
        }

    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_viewer_a,
                parent,false);
        return new PostHolder(v);
    }

    class PostHolder extends RecyclerView.ViewHolder{
        TextView TitleTV,ContentTV;
        ImageView ThumbnailIV;
        public PostHolder(View itemView){
            super (itemView);
            TitleTV=itemView.findViewById(R.id.LastItemContentTitleTextView);
            ContentTV=itemView.findViewById(R.id.LastItemContentTextView);
            ThumbnailIV = itemView.findViewById(R.id.image_thumbnails_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
