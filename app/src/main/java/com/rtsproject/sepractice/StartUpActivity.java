package com.rtsproject.sepractice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class StartUpActivity  extends AppCompatActivity {
    private int time=5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent changeScreen =new Intent(StartUpActivity.this, MainActivity.class);
                startActivity(changeScreen);
                finish();

            }
        },time);
    }
}
