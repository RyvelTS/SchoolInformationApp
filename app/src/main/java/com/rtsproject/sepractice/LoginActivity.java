package com.rtsproject.sepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button registerButton,signinButton,forgotPasswordButton ;
    private ImageView status_icon ;
    private EditText emailET,passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        registerButton = findViewById(R.id.signuplink);
        status_icon = findViewById(R.id.online_status_icon_login);
        emailET = findViewById(R.id.user_email_box);
        passwordET = findViewById(R.id.user_password_box);
        signinButton = findViewById(R.id.sign_in);
        forgotPasswordButton = findViewById(R.id.forgot_button);
        final Random rand = new Random();
        final Handler handler = new Handler();

        Runnable r=new Runnable() {
            public void run() {
                int value = rand.nextInt(10);
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    status_icon.setVisibility(View.GONE);
                }
                else
                    status_icon.setVisibility(View.VISIBLE);
                handler.postDelayed(this, 500);
            }
        };

        handler.postDelayed(r, 500);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                if(email.equals("")){
                    Toast.makeText(LoginActivity.this,
                            "Please enter your registered Email address", Toast.LENGTH_SHORT).show();
                }else if (password.equals("")){
                    Toast.makeText(LoginActivity.this,
                            "Please enter your registered Password", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        assert user != null;
                                        if(user.isEmailVerified()){
                                            Toast.makeText(LoginActivity.this, "Authentication Succeed.",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent changeScreen =new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(changeScreen);
                                            finish();
                                        }else{
                                            Toast.makeText(LoginActivity.this, "Please Verify Your Email",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        forgotPasswordButton.setVisibility(View.VISIBLE);
                                    }

                                    // ...
                                }
                            });
                }
            }
        });
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                if(email.equals("")){
                    Toast.makeText(LoginActivity.this, "Please Input your email",
                            Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Reset Link Has Been Sent to Your Email",
                                                Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Email is Not Recognized",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeScreen =new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(changeScreen);
                finish();
            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent changeScreen =new Intent(LoginActivity.this, MainActivity.class);
        startActivity(changeScreen);
        finish();
    }

}
