package com.example.intoxicationidentificationsystem.StartActivities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.intoxicationidentificationsystem.R;



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
                    startActivity(new Intent(SplashScreen.this, com.example.intoxicationidentificationsystem.Activities.Welcome.class));
                }
            }
        };
        thread.start();
    }
}