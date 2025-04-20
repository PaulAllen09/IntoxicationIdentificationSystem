package com.example.intoxicationidentificationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread() {
            @Override
            public void run() {

                try{
                    sleep(3000);
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    Log.d("atempginting","attempign to start");
                    startActivity(new Intent(SplashScreen.this, Welcome.class));
                }
            }
        };
        thread.start();
    }
}