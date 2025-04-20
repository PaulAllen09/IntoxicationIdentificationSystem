package com.example.intoxicationidentificationsystem.Activities.Task4;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ThirtySecTimer extends Thread {
    //this is the 1 minute timer
    private static final String TAG= "OneMinuteTimer";

    private int timeLeft = 30;
    private Handler bgWork = new Handler();


    public ThirtySecTimer(Handler bgWork){
        super();
        this.bgWork=bgWork;
    }

    @Override
    public void run() {
        while (timeLeft > 0) {
            Log.d("ThirtySecTimer",""+timeLeft);
            if(interrupted()){
                break;
            }
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
            }
            timeLeft--;
        }
        if(timeLeft==0){
            Message msg = Message.obtain(); // Creates an new Message instance
            msg.obj = "end";
            bgWork.sendMessage(msg);
        }

        //endTask

    }

    public int getTimeLeft(){
        return timeLeft;
    }
    public void addTime(int sec){
        timeLeft+=sec;
    }
    public void resetTimer(){
        timeLeft=30;
    }


}

