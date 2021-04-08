package com.rtsproject.sepractice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ContentViewerActivityA extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private TextView contentTitle,contentPreview,datePreview,sourcePreview;
    private ImageView thumbnailPreview;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_viewer_a);
        contentTitle = findViewById(R.id.content_title_textview);
        contentPreview = findViewById(R.id.content_textview);
        datePreview = findViewById(R.id.content_date_textview);
        sourcePreview=findViewById(R.id.content_source_textview);
        thumbnailPreview = findViewById(R.id.thumbnail_image_preview);

        if(mAuth.getCurrentUser()!=null){
            if(getIntent().getStringExtra("DocId") != null){
                DocumentReference postDocRef = db.collection("posts").document(""+getIntent().getStringExtra("DocId"));
                postDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        contentTitle.setText(documentSnapshot.getString("Title"));
                        contentPreview.setText(documentSnapshot.getString("Contents"));
                        datePreview.setText(documentSnapshot.getString("Date"));
                        sourcePreview.setText("Last Update by : " + documentSnapshot.getString("Writer"));
                        new DownLoadImageTask(thumbnailPreview).execute(documentSnapshot.getString("Picture"));
                    }
                });
            }

        }else{
            String title = Objects.requireNonNull(getIntent().getExtras()).getString("Title");
            String content = getIntent().getExtras().getString("Content");
            int resource = getIntent().getExtras().getInt("Image");
            trace("LOG", title + content);
            contentTitle.setText(title);
            contentPreview.setText(content);
            thumbnailPreview.setImageResource(resource);
        }
    }
    public void trace(String tag, String message){
        Log.d(tag , message);
    }
}
