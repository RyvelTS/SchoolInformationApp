package com.rtsproject.sepractice;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String admin;
    private ImageView offline_sts;
    private CheckBox terms_check;
    private EditText emailET, nameET,passwordET,confirmPasswordET,adminKeyET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        admin = "123456";
        Button signinbtn = findViewById(R.id.signin_link);
        Button signupbtn = findViewById(R.id.sign_up_button);
        offline_sts = findViewById(R.id.offline_notice);
        terms_check = findViewById(R.id.terms_checkbox);
        emailET = findViewById(R.id.email_register_edittext);
        nameET = findViewById(R.id.name_register_edittext);
        passwordET = findViewById(R.id.password_register_edittext);
        confirmPasswordET = findViewById(R.id.confirmpass_register_edittext);
        adminKeyET = findViewById(R.id.adminkey_register_edittext);

        final Random rand = new Random();
        final Handler handler = new Handler();

        Runnable a =new Runnable() {
            public void run() {
                int value = rand.nextInt(10);
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    offline_sts.setVisibility(View.GONE);
                } else
                    offline_sts.setVisibility(View.VISIBLE);
                handler.postDelayed(this, 500);
            }
        };
        handler.postDelayed(a, 500);

        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeScreen =new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(changeScreen);
                finish();

            }
        });
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                final String name = nameET.getText().toString();
                String password = passwordET.getText().toString();
                String confirmPassword = confirmPasswordET.getText().toString();
                final String adminKey = adminKeyET.getText().toString();
                if(terms_check.isChecked()){
                    if(email.equals("")){
                        Toast.makeText(RegisterActivity.this,
                                "Please Fill your Email address", Toast.LENGTH_SHORT).show();
                    }else if(name.equals("")){
                        Toast.makeText(RegisterActivity.this,
                                "Please Fill your Name", Toast.LENGTH_SHORT).show();
                    }else if(password.equals("")){
                        Toast.makeText(RegisterActivity.this,
                                "Please make your new Password", Toast.LENGTH_SHORT).show();
                    }else if(confirmPassword.equals("")){
                        Toast.makeText(RegisterActivity.this,
                                "Please re-type your new Password in Confirm Password Box", Toast.LENGTH_SHORT).show();
                    }else if(password.length()< 8){
                        Toast.makeText(RegisterActivity.this,
                                "Your Password is weak (too short) , please make a new one", Toast.LENGTH_SHORT).show();
                    }else if(!password.equals(confirmPassword)){
                        Toast.makeText(RegisterActivity.this,
                                "Your Password is not match", Toast.LENGTH_SHORT).show();
                    }else if(!adminKey.equals("") && !adminKey.equals(admin)){
                        Toast.makeText(RegisterActivity.this,
                                "Your Key is Not Valid", Toast.LENGTH_SHORT).show();
                    }else{
                        mAuth.createUserWithEmailAndPassword(email,password)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            assert user != null;
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(RegisterActivity.this, "Registered successfully. Please verify your email address",
                                                                        Toast.LENGTH_SHORT).show();
                                                                CreateDB(name, adminKey);
                                                                Intent changeScreen =new Intent(RegisterActivity.this, AuthenticationActivity.class);
                                                                startActivity(changeScreen);
                                                                finish();
                                                            }else {
                                                                Toast.makeText(RegisterActivity.this, "Registered Unsuccessfully. Your email address is not valid",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(RegisterActivity.this, "Authentication failed. Check your Internet connection. User in China may need VPN",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }
                }else{
                    Toast.makeText(RegisterActivity.this,
                            "Please agree with our Terms and Conditions to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void CreateDB(String name, String key){
        int identifier = 0;
        String currentUseremail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        if(key.equals(admin)){
            identifier = 1;
        }
        DocumentReference docRef = db.collection("users").document(userID);
        Map<String, Object> user = new HashMap<>();
        user.put("Name", name);
        user.put("Admin",identifier);
        assert currentUseremail != null;
        user.put("Email",currentUseremail);
        docRef.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}
