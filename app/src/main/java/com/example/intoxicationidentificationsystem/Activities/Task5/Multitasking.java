package com.example.intoxicationidentificationsystem.Activities.Task5;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.intoxicationidentificationsystem.Activities.Welcome;
import com.example.intoxicationidentificationsystem.R;

import java.util.Objects;

public class Multitasking extends AppCompatActivity {
    /*
    * This build goal of this task is to create shapes that "fall" down the screen.
    * User is supposed to click only circles, and count beeps that play at random
    * intervals
    *     * */
    RelativeLayout screen;
    MediaPlayer ring;

    Handler mHandler= new Handler();
    Intent intent;
    int user_id;
    BackgroundWork bgWork;
    ConstraintLayout second;
    Button submit;
    EditText getBeeps;
    String baseTest;
    Handler handler= new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard set up see task 1 for more details
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multitasking_activity);
        intent = new Intent(Multitasking.this, Welcome.class);
        if(getIntent().hasExtra("id")){
            user_id=Integer.valueOf(getIntent().getStringExtra("id"));
            if(getIntent().hasExtra("status")){
                baseTest=getIntent().getStringExtra("status");
            }
            Log.d("Start status",""+baseTest);

            Objects.requireNonNull(getSupportActionBar()).setTitle("User: "+user_id);
            intent.putExtra("id", String.valueOf(user_id));
            intent.putExtra("status",baseTest);
        }
        // Get the screen size in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;



        View.OnClickListener buttonclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgWork.wrong_click(v);
            }
        };
        View.OnClickListener cirlce_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgWork.circle_clicked(v);
            }
        };

        ring = MediaPlayer.create(Multitasking.this, R.raw.short_beep);     // To play the beep noise during the task

        screen= findViewById(R.id.wholeScreen);
        second= findViewById(R.id.beepsCL);
        submit = findViewById(R.id.submitBeeps);
        getBeeps = findViewById(R.id.TV_getBeeps);
        bgWork= new BackgroundWork(buttonclick,cirlce_click,Multitasking.this,screen, width, height, ring, user_id, second,intent, baseTest);
        submit.setOnClickListener(v -> bgWork.getBeepsEntered(Integer.valueOf(getBeeps.getText().toString())));
        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgWork.screen_clicked();
            }
        });

        //starting the task
        //ring.start();
        bgWork.start();
        Runnable checkRunning = new Runnable() {
            @Override
            public void run() {
                if (bgWork.running) {
                    // Check again after 1000ms
                    handler.postDelayed(this, 1000);
                } else {
                    // Handle completion of bgWork here if needed
                    Multitasking.this.startActivity(intent);
                }
            }
        };

        handler.post(checkRunning);
    }
}
