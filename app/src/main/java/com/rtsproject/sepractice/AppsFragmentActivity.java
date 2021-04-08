package com.rtsproject.sepractice;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AppsFragmentActivity extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private int admin_num = 0;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private ImageView indicatorDatabase;
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragmentLayout = inflater.inflate(R.layout.apps_fragment, container, false);
        Button onlineButton = fragmentLayout.findViewById(R.id.online_btn);
        final Button adminButton = fragmentLayout.findViewById(R.id.admin_btn);
        final Button searchButton = fragmentLayout.findViewById(R.id.search_btn);
        ImageView iconFeature1 = fragmentLayout.findViewById(R.id.feature_icon_1);
        ImageView iconFeature2 = fragmentLayout.findViewById(R.id.feature_icon_2);
        final ImageView iconFeature3 = fragmentLayout.findViewById(R.id.feature_icon_3);
        TextView titleFeature1 = fragmentLayout.findViewById(R.id.feature_title_1);
        TextView contentFeature1 = fragmentLayout.findViewById(R.id.feature_content_1);
        TextView contentFeature2 = fragmentLayout.findViewById(R.id.feature_content_2);
        final TextView contentFeature3 = fragmentLayout.findViewById(R.id.feature_content_3);
        final TextView greetingText = fragmentLayout.findViewById(R.id.greeting_text_features);
        indicatorDatabase = fragmentLayout.findViewById(R.id.database_connection_features);


        if (mAuth.getCurrentUser() != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            titleFeature1.setText("Log Out");
            contentFeature2.setText("Look for Specific Information.");
            contentFeature1.setText("Go Offline");
            iconFeature1.setImageResource(R.drawable.ic_ion_log_out_sharp);
            String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            DocumentReference userDocRef = db.collection("users").document(userID);
            userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    indicatorDatabase.setColorFilter(getResources().getColor(android.R.color.holo_green_light), PorterDuff.Mode.SRC_IN);
                    greetingText.setText("Welcome "+ documentSnapshot.getString("Name")+" !");
                    admin_num = Objects.requireNonNull(documentSnapshot.getLong("Admin")).intValue();
                    if(admin_num == 1){
                        contentFeature3.setText("Enables you to add or modified information data.");
                        adminButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), AdministratorActivity.class);
                                startActivity(intent);
                            }
                        });
                    }else{
                        iconFeature3.setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_IN);
                        contentFeature3.setText("You are not allow to access this feature");
                        adminButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(),
                                        "You are not an admin", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }else{
            indicatorDatabase.setVisibility(View.GONE);
            titleFeature1.setText("Online Version");
            contentFeature1.setText("Online features are using Firebase Database by Google,  VPN may be needed in China");
            iconFeature1.setImageResource(R.drawable.ic_firebase_logo);
            iconFeature2.setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_IN);
            iconFeature3.setColorFilter(getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_IN);
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // User is signed in (getCurrentUser() will be null if not signed in)
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(),
                            "You need to go online first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),
                        "You need to go online first", Toast.LENGTH_SHORT).show();
            }
        });
        onlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // User is signed in (getCurrentUser() will be null if not signed in)
                    new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                            .setTitle("Log Out Confirmation")
                            .setMessage("Are you sure want to Log Out?")
                            .setIcon(R.drawable.ic_ion_log_out_sharp)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mAuth.signOut();
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        return fragmentLayout;
    }
}
