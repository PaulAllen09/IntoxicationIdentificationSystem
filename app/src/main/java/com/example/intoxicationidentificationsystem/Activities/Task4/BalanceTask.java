package com.example.intoxicationidentificationsystem.Activities.Task4;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intoxicationidentificationsystem.Welcome;
import com.example.intoxicationidentificationsystem.R;

import java.util.Objects;


public class BalanceTask extends AppCompatActivity {


    ImageView small_ball,upArrow,downArrow,leftArrow,rightArrow;
    Sensor moveSensor;
    SensorManager SM;

    boolean running=true;
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            //Log.d("inside mhandler","start");
            super.handleMessage(msg);
            endBalanceTask();
        }
    };
    int user_id;
    Intent intent;
    String baseTest;
    int points=500; // the scoring system used in this task, starts with 500 adn losses 100 per circle exited
    BackgroundWork backgroundWork;
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_4_activity);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Task 4");
        intent = new Intent(BalanceTask.this, Welcome.class);
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

        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        small_ball = findViewById(R.id.iv_small_ball);
        upArrow = findViewById(R.id.upArrow);
        downArrow = findViewById(R.id.downArrow);
        leftArrow = findViewById(R.id.leftArrow);
        rightArrow = findViewById(R.id.rightArrow);
        upArrow.setVisibility(View.INVISIBLE);
        downArrow.setVisibility(View.INVISIBLE);
        leftArrow.setVisibility(View.INVISIBLE);
        rightArrow.setVisibility(View.INVISIBLE);

        backgroundWork= new BackgroundWork(small_ball,SM,mHandler,upArrow,downArrow,leftArrow,rightArrow,BalanceTask.this, user_id,baseTest);


        backgroundWork.start();
    }

    public void endBalanceTask() {
        if(running){
            running=false;
            BalanceTask.this.startActivity(intent);
        }

    }



}
