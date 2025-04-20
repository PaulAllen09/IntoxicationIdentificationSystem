package com.example.intoxicationidentificationsystem.Activities.Task5.Threads;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;

import com.example.intoxicationidentificationsystem.Activities.Task5.ButtonData;
import com.example.intoxicationidentificationsystem.R;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class sendShapes extends Thread{
    Boolean test1=true;
    Random r = new Random();
    public boolean running=true;
    private float startTime;
    private int time;
    ButtonData[] buttonDataArr;
    public int total_circles=0;

    public sendShapes(ButtonData[] buttonDataArr, int time) {
        this.buttonDataArr=buttonDataArr;
        this.time=time*1000;
    }


    public void runShapes(ButtonData buttonData){
        //Log.d("sendShapes run", "Attemping to get into loop; "+(SystemClock.elapsedRealtime()-startTime)+" :? "+totalTime*1000);
        buttonData.playAnimation();

        while (SystemClock.elapsedRealtime()<startTime+time) {
            // Sleep while animation is underway to prevent multiple shapes from running at once
            //Log.d("sendShapes run", "Attemping to send button");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.d("thread interrupt","Thread interrupted in sleep");
                break;
            }
        }

    }
    @Override
    public void run() {
        running=true;
        for(ButtonData i:buttonDataArr){
            startTime=SystemClock.elapsedRealtime();
            runShapes(i);
        }
        running=false;
    }
}
