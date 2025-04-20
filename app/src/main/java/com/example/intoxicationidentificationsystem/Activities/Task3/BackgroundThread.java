package com.example.intoxicationidentificationsystem.Activities.Task3;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.example.intoxicationidentificationsystem.Databases.Task3.MyDatabaseHelperTask3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class BackgroundThread extends Thread {



    private final Handler mainHandler= new Handler(Looper.getMainLooper());

    Random r = new Random();
    int correct = 0;
    int wrong = 0;
    int total=0;
    long pressStartTime = 0;                // The time when the user presses down on the button
    long pressEndTime = 0;                  // The time when the user releases the button
    long xSpawnTime = 0;                    // The time that the x appears
    long xDespawnTime = 0;                  // The time that the x disappears
    long clickTimeInterval = 0;             // The user reaction time for press and holding
    long releasedTimeInterval = 0;          // The user reaction time for releasing


    Boolean running = true;                 // To tell main thread to stop
    Boolean lastRound=false;                // To ensure that the last press and hold does not count multiple times
    Boolean counting=false;

    Boolean crossVisible = false;           // True means there is a cross visible on screen false is when there is none; starts invisible
    Boolean clickPerRound = false;          // Will prevent multiple correct clicks per round
    Boolean releasedPerRound = false;       // Will allow researchers to see that the user did not release for that turn
    ImageView cross1,cross2,cross3,cross4,cross5;
    ImageView[] crossArray = new ImageView[5];

    TextView correct_TV,wrong_TV;


    int user_id;
    String baseTest;
    MyDatabaseHelperTask3 myDB;
    Context context;
    public BackgroundThread(ImageView cross1, ImageView cross2, ImageView cross3, ImageView cross4, ImageView cross5, TextView correct_TV, TextView wrong_TV, int user_id, Context context, String baseTest) {
        this.cross1 = cross1;
        this.cross2 = cross2;
        this.cross3 = cross3;
        this.cross4 = cross4;
        this.cross5 = cross5;
        this.correct_TV= correct_TV;
        this.wrong_TV=wrong_TV;
        this.user_id=user_id;
        this.context=context;
        this.baseTest= baseTest;
        crossArray[0]=this.cross1;
        crossArray[1]=this.cross2;
        crossArray[2]=this.cross3;
        crossArray[3]=this.cross4;
        crossArray[4]=this.cross5;

    }

    int trials = 0;     // Will go up to 20;
    // 4 different size Xs
    // First two will be bigger to allow use to grasp concept.

    int[] sizes= new int[20];
    ArrayList<Integer> lst = new ArrayList<>(Arrays.asList(25, 25, 25, 25, 25, 50, 50, 50, 50, 50, 100, 100, 100, 100, 200, 200, 200, 200));
    int lstSize=lst.size();
    int[] crosses = new int[20];
    @Override
    public void run() {
        myDB = new MyDatabaseHelperTask3(context);  // Open a connection to the database

        sizes[0]=200;
        sizes[1]=100;
        //fill the rest of the array with random sizes still available
        for(int i =2; i<lstSize;i++){
            sizes[i]=lst.remove(r.nextInt(lst.size()));
        }
        sleepTimer(1,3,"waiting to start");
        for(int i=0;i<crosses.length;i++){
            clickPerRound=false;
            releasedPerRound=false;
            int number=r.nextInt(5);
            int j=i;
            crosses[i]=number;

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams params = crossArray[number].getLayoutParams();
                    params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizes[j], context.getResources().getDisplayMetrics());
                    params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizes[j], context.getResources().getDisplayMetrics());
                    crossArray[number].setLayoutParams(params);
                }
            });


            //Log.d("X is becoming visible","X is becoming visible");
            turnXOn(crosses[i]);
            sleepTimer(5,2,"on");
            turnXOff(crosses[i]);
            //Log.d("Waiting for next X","Waiting for next X");
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams params = crossArray[number].getLayoutParams();
                    params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, context.getResources().getDisplayMetrics());
                    params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, context.getResources().getDisplayMetrics());
                    crossArray[number].setLayoutParams(params);
                }
            });

            sleepTimer(2,2,"off");
            // Give the user 2-3 second to release the react button
            // Other wise it will count that they did not release.
            // Record the data of this round
            recordRound();

        }
        sleepTimer(2,1,"off");// end of the round
        // Sleep for 1-2 additional seconds
        running=false;





    }



    public void sleepTimer(int bound, int add, String state) {
        final int sleepTime = (r.nextInt(bound) + add) * 1000;

        counting=true;
        long timerStart = SystemClock.elapsedRealtime();
        while (SystemClock.elapsedRealtime() - timerStart < sleepTime) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        counting=false;

    }




    private void turnXOn(int boxNumber) {

        crossVisible = true;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                crossArray[boxNumber].setVisibility(View.VISIBLE);
            }
        });

        total++;
        //Log.d("x is on", "x visible: " + crossVisible);
        xSpawnTime = SystemClock.elapsedRealtime();

    }

    private void turnXOff(int boxNumber) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                //crossArray[boxNumber].clearAnimation();
                crossArray[boxNumber].setVisibility(View.INVISIBLE);
                //crossArray[boxNumber].clearAnimation();

            }
        });
        crossVisible = false;
        //Log.d("x is off", "x visible: " + crossVisible);
        xDespawnTime = SystemClock.elapsedRealtime();

    }

    public void clickReact(long time){
        if(!crossVisible&&clickPerRound&&lastRound){   //special stop for last round
            // Weird bug when holding at the end of the round caused a huge accumulation of wrong points
            // To prevent massive accumulation of points at the end of the task
            // The task should still keep track of the time of release

        }
       else if(crossVisible&&!clickPerRound){   //x is visible so user gets a correct point only works once per round
            clickPerRound = true;
            correct++;

            pressStartTime = time;// the time when the user presses the button
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    correct_TV.setText(Integer.toString(correct));
                }
            });
       }
        else{
            wrong++;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    wrong_TV.setText(Integer.toString(wrong));
                }
            });
        }

    }
    public void releaseReact(long time){
        if(!releasedPerRound&&clickPerRound){   // The first release of the button, the user must have clicked or otherwise it must be from a previous round
            releasedPerRound=true;
            pressEndTime=time;
        }
    }

    public void recordRound(){
        if(clickPerRound&&pressStartTime<xDespawnTime){ // The user clicked and it was before the x disappeared
            clickTimeInterval=pressStartTime-xSpawnTime;
        }
        else {
            clickTimeInterval=-1; // The user fail the requirement
        }
        if(releasedPerRound){   // Meaning the user properly played the round and release the button in a timely manner
            releasedTimeInterval=pressEndTime-xDespawnTime;
        }
        else{
            releasedTimeInterval=-1;
        }
        myDB.addRoundData(user_id,correct,wrong,total,clickTimeInterval,releasedTimeInterval,baseTest);
    }

    public void wrongClick() {
        wrong++;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                wrong_TV.setText(Integer.toString(wrong));
            }
        });
    }
}
