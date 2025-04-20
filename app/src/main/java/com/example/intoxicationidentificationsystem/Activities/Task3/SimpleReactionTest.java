package com.example.intoxicationidentificationsystem.Activities.Task3;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.intoxicationidentificationsystem.Activities.Task2.ShortTimeMemory;
import com.example.intoxicationidentificationsystem.Activities.Welcome;
import com.example.intoxicationidentificationsystem.R;

import java.util.Objects;

public class SimpleReactionTest extends AppCompatActivity {

    Boolean running=true;
    public ImageView cross1,cross2,cross3,cross4,cross5;            //the 5 places where x will appear
    public ImageView[] crossArray;
    Button react;               // The button users have to click during task
    TextView correctNum;        // Where it is displayed if the user gets the answer right
    TextView wrongNum;          // Where it is displayed if the user gets the answer wrong or miss
    ConstraintLayout background;

    // Data being passed between activities
    int user_id;
    String baseTest;
    Intent intent;
    private BackgroundThread backgroundThread;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard activity set up see task 1 for more details
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_reaction_test_activity);
        intent = new Intent(SimpleReactionTest.this, Welcome.class);
        if(getIntent().hasExtra("id")){
            user_id=Integer.valueOf(getIntent().getStringExtra("id"));
            if(getIntent().hasExtra("status")){
                baseTest=getIntent().getStringExtra("status");
            }

            Objects.requireNonNull(getSupportActionBar()).setTitle("User: "+user_id);
            intent.putExtra("id", String.valueOf(user_id));
            intent.putExtra("status",baseTest);
        }

        // The locations that the X will appear
        cross1 = findViewById(R.id.crossButton1);
        cross2 = findViewById(R.id.crossButton2);
        cross3 = findViewById(R.id.crossButton3);
        cross4 = findViewById(R.id.crossButton4);
        cross5 = findViewById(R.id.crossButton5);
        crossArray = new ImageView[]{cross1,cross2,cross3,cross4,cross5};

        // Pointing the variables to their xml counterparts
        react = findViewById(R.id.reactButton);
        correctNum = findViewById(R.id.numCorrect);
        wrongNum = findViewById(R.id.wrong_txt);



        // Set up an on touch listener to help with more data collection
        react.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                //pressing button
                backgroundThread.clickReact(SystemClock.elapsedRealtime());
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //finger lifted
                backgroundThread.releaseReact(SystemClock.elapsedRealtime());
            }
            return true;
        });




        backgroundThread = new BackgroundThread(cross1,cross2,cross3,cross4,cross5,correctNum,wrongNum,user_id, SimpleReactionTest.this,baseTest);
        background = findViewById(R.id.background_wrong_click);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundThread.wrongClick();
            }
        });

        beginReactionTest();
    }
    //**********************************************************************************************

    private void beginReactionTest(){
        /*
         * This method will start the game and timer
         * */
        backgroundThread.start();

        Handler handler = new Handler(Looper.getMainLooper());

        Runnable checkRunning = new Runnable() {
            @Override
            public void run() {
                if (backgroundThread.running) {
                    // Creates a loop that checks to see if the background thread is running every 1000 millisec
                    handler.postDelayed(this, 10);
                } else {
                    // End the task and return to main menu
                    SimpleReactionTest.this.startActivity(intent);
                }
            }
        };
        // Starts this small looping runnable
        handler.post(checkRunning);
    }



}


