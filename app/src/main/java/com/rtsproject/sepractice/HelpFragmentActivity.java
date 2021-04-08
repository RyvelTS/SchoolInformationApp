package com.rtsproject.sepractice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HelpFragmentActivity extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private TextView greetingText,weblink;
    private ImageView indicatorDatabase;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.help_fragment, container, false);
        greetingText = content.findViewById(R.id.greeting_txtedit);
        indicatorDatabase=content.findViewById(R.id.database_connection_help);
        weblink=content.findViewById(R.id.web_link_tv);
        Button ryvel_fb  =content.findViewById(R.id.ryvel_fb_button);
        Button ryvel_in  =content.findViewById(R.id.ryvel_in_button);
        Button sobuj_fb  =content.findViewById(R.id.sobuj_fb_button);
        Button sobuj_in  =content.findViewById(R.id.sobuj_in_button);
        Button rased_fb  =content.findViewById(R.id.rased_fb_button);
        Button rased_in  =content.findViewById(R.id.rased_in_button);
        Button sifat_fb  =content.findViewById(R.id.sifat_fb_button);
        Button sifat_in  =content.findViewById(R.id.sifat_in_button);

        if (mAuth.getCurrentUser() != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            DocumentReference userDocRef = db.collection("users").document(userID);
            userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    greetingText.setText("Welcome "+ documentSnapshot.getString("Name")+" !");
                    indicatorDatabase.setColorFilter(getResources().getColor(android.R.color.holo_green_light), PorterDuff.Mode.SRC_IN);
                }
            });
        }else{
            indicatorDatabase.setVisibility(View.GONE);
        }
        DocumentReference appinfosDocRef = db.collection("appinfos").document("website");
        appinfosDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                weblink.setText("Web : "+ documentSnapshot.getString("url"));
                if(documentSnapshot.getString("url").contains(".")){
                    weblink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openLink(documentSnapshot.getString("url"));
                        }
                    });
                }
            }
        });

        ryvel_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.facebook.com/ryvel.timothy");
            }
        });
        ryvel_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.linkedin.com/in/ryvel-timothy-b359051a9/");
            }
        });
        sobuj_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.facebook.com/sobuj.hossen.45");
            }
        });
        sobuj_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.linkedin.com/in/sobuj-hossen-7a5309162/");

            }
        });
        rased_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.facebook.com/profile.php?id=100006751386476");
            }
        });
        rased_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        sifat_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.facebook.com/haque.sifatul");
            }
        });
        sifat_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return content;

    }
    public void openLink(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
