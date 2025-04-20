package com.example.intoxicationidentificationsystem.Activities.Task4;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.intoxicationidentificationsystem.Databases.Task4.MyDatabaseHelperTask4;

public class BackgroundWork extends Thread implements SensorEventListener {

    ImageView small_ball, upArrow, downArrow, leftArrow, rightArrow;
    Sensor moveSensor;
    SensorManager SM;

    Handler mHandler;
    Handler BackgroundWorkHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            endBalanceTask("timer");


        }
    };

    Boolean balanced = false;
    boolean running = true;
    ThirtySecTimer thirtySecTimer = new ThirtySecTimer(BackgroundWorkHandler);
    int life = 3;
    long start;
    long longest = 0;
    boolean first = true;
    int xMulitpier = 15;
    int yMulitpier = 15;
    float startx;
    float starty;
    int points = 500; // the scoring system used in this task, starts with 500 adn losses 100 per circle exited;;; intilization value -1 to check to se
    int highestPoints = 0;
    Context context;
    int user_id;
    MyDatabaseHelperTask4 myDB;
    String baseTest;

    public BackgroundWork(ImageView small_ball, SensorManager SM, Handler mHandler, ImageView upArrow, ImageView downArrow, ImageView leftArrow, ImageView rightArrow, Context context, int user_id, String baseTest) {
        this.small_ball = small_ball;
        this.upArrow = upArrow;
        this.downArrow = downArrow;
        this.leftArrow = leftArrow;
        this.rightArrow = rightArrow;
        this.context = context;


        this.SM = SM;
        this.mHandler = mHandler;
        this.user_id = user_id;
        this.baseTest=baseTest;

    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thirtySecTimer.start();

        //accelerometer sensor
        moveSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //make sensor listener
        SM.registerListener(this, moveSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*This function gets information from the gyroscopic sensors and moves the red ball*/
        if (first) {
            if (balanced) {
                start = SystemClock.elapsedRealtime();
                thirtySecTimer.resetTimer();
                first = false;
                startx = small_ball.getX();
                starty = small_ball.getY();
                //Log.d("checkBounds","xStart:"+startx+"\t\tyStart: "+starty);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        small_ball.setX(small_ball.getX() - xMulitpier * event.values[0]);
                        //Log.d("checkBounds","x: "+event.values[0]+"\t\ty: "+event.values[1]);
                        small_ball.setY(small_ball.getY() + yMulitpier * event.values[1]);

                    }
                });
                checkBounds();
            } else {
                Balanced(event.values[0], event.values[1]);
            }

        } else {
            //Log.d("checkBounds","xStart:"+startx+"\t\tyStart: "+starty);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    small_ball.setX(small_ball.getX() - xMulitpier * event.values[0]);
                    //Log.d("checkBounds","x: "+event.values[0]+"\t\ty: "+event.values[1]);
                    small_ball.setY(small_ball.getY() + yMulitpier * event.values[1]);

                }
            });
            checkBounds();
        }

    }

    private void Balanced(double x, double y) {
        Boolean leftRightBalanace = false;

        Boolean upDownBalance = false;

        double balencedFromCenter=.25;
        //Log.d("af",""+x);
        if (x > balencedFromCenter) {
            //device is tilted to the left, so please tilt it right
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    rightArrow.setVisibility(View.VISIBLE);
                    leftArrow.setVisibility(View.INVISIBLE);
                }
            });

        } else if (x < -balencedFromCenter) {
            //device is tilted to the right, so please tilt it left

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    leftArrow.setVisibility(View.VISIBLE);
                    rightArrow.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            //device is balanced left to right
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    leftArrow.setVisibility(View.INVISIBLE);
                    rightArrow.setVisibility(View.INVISIBLE);
                }
            });
            leftRightBalanace = true;
        }
        if (y > balencedFromCenter) {
            //the device is tilted down, so please tilt it up
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    upArrow.setVisibility(View.VISIBLE);
                    downArrow.setVisibility(View.INVISIBLE);
                }
            });
        } else if (y < -balencedFromCenter) {
            //the device is tilted down, so please tilt it up
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    upArrow.setVisibility(View.INVISIBLE);
                    downArrow.setVisibility(View.VISIBLE);
                }
            });


        } else {
            //device is balanced up and down
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    upArrow.setVisibility(View.INVISIBLE);
                    downArrow.setVisibility(View.INVISIBLE);
                }
            });
            upDownBalance = true;
        }
        if (upDownBalance && leftRightBalanace) {
            balanced = true;
        }

    }


    private void checkBounds() {
        /*Check bounds is a fucntion ment to check the position of the red ball and change the score according to the distance from the center*/
        double x = Math.round(Math.pow((small_ball.getX() - startx), 2));
        double y = Math.round(Math.pow((small_ball.getY() - starty), 2));
        //int[] xY=getCords();
        double distance = Math.round(Math.sqrt(x + y));

        //Log.d("checkBounds","x: "+x+"\t\ty: "+y);
        //Log.d("checkBounds","Distance: "+distance);
        if (Math.abs(distance) >= 500) {
            Log.d("endGame", "End Game ");
            //exits the white circle 0 points
            if (!(points < 0)) {
                points = 0;
            }
            tempRestart();
            //endBalanceTask();
        } else if (Math.abs(distance) >= 400) {
            //leaving the 4th circle 100pts
            if (!(points < 100)) {
                points = 100;
            }
        } else if (Math.abs(distance) >= 300) {
            //leaving the 3rd circle 200pts
            if (!(points < 200)) {
                points = 200;
            }
        } else if (Math.abs(distance) >= 200) {
            //leaving the 2nd cirlce 300 pts
            if (!(points < 300)) {
                points = 300;
            }
        } else if (Math.abs(distance) >= 100) {
            //leaving the 1st(smallest) circle 400pts
            if (!(points < 400)) {
                points = 400;
            }

        }


    }

    private void tempRestart() {
        life--;
        Toast.makeText(context, "You lost a life, " + life + " left.", Toast.LENGTH_SHORT).show();
        //to allow time for reset to happen
        long tempLong = SystemClock.elapsedRealtime() - start;
        if (tempLong > longest) {
            longest = tempLong;
        }

        small_ball.setX(startx);
        small_ball.setY(starty);
        thirtySecTimer.resetTimer();
        start = SystemClock.elapsedRealtime();
        //if balanced==true then add data, other wise do not count the round because they failed to balance the device within the first 30 seconds.
        balanced = false;
        first = true;
        if (life < 1) {
            endBalanceTask("life");
        }
        else{
            points=500;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not used
    }


    public void endBalanceTask(String s) {
        if (running) {
            if (s.equals("life")) {

                myDB = new MyDatabaseHelperTask4(context);
                myDB.addRoundData(user_id, life, points, String.valueOf(longest),baseTest);
                myDB.close();
                running = false;
                SM.unregisterListener(this);
                Message msg = Message.obtain(); // Creates an new Message instance
                msg.obj = "end";
                mHandler.sendMessage(msg);
            } else {
                myDB = new MyDatabaseHelperTask4(context);
                myDB.addRoundData(user_id, life, points, String.valueOf(30000),baseTest);
                myDB.close();
                running = false;
                SM.unregisterListener(this);
                Message msg = Message.obtain(); // Creates an new Message instance
                msg.obj = "end";
                mHandler.sendMessage(msg);
            }

        }

    }


}
