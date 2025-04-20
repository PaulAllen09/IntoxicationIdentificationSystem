package com.example.intoxicationidentificationsystem.Activities.Task5;



import static com.example.intoxicationidentificationsystem.R.*;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.intoxicationidentificationsystem.Activities.Task5.Threads.sendShapes;
import com.example.intoxicationidentificationsystem.Databases.Task5.MyDatabaseHelperTask5;
import com.example.intoxicationidentificationsystem.R;

import java.util.Random;

public class BackgroundWork extends Thread{
    Random r = new Random();

    RelativeLayout relLayout;
    View.OnClickListener buttonClick;
    View.OnClickListener circle_clicked;

    ConstraintLayout beepsCl;







    Handler mHandler;

    boolean running = true;
    boolean displaying=true;
    int time=3;

    long start_time= SystemClock.elapsedRealtime();

    int screenWidth;
    int screenHeight;
    int locations = 200;    // The number of stops the each shape should have on screen
                            // Directly correlates to the speed
    int shapeArrayLength=100;
    int time_between_shapes=1000;

    int rows_of_shapes=4;
    int total_shapes=99;
    int cirlce_trials=15;
    int cirles_counted_for=0;
    int small_circles=0;
    int med_circles=0;
    int big_circles=0;
    int max_size;
    int min_size=50;


    int wrong_clicks=0;
    int correct_clicks = 0;
    public int total_circles;
    int total_beeps = 0;
    int counted_beeps;



    sendShapes sendShape1;
    ButtonData[] shapes1= new ButtonData[total_shapes/3];
    sendShapes sendShape2;
    ButtonData[] shapes2= new ButtonData[total_shapes/3];
    sendShapes sendShape3;
    ButtonData[] shapes3= new ButtonData[total_shapes/3];


    Context context;
    MediaPlayer beep;
    MyDatabaseHelperTask5 myDB;
    int user_id;
    Intent intent;
    int[] colors= new int[]{R.color.red,R.color.orange,R.color.yellow,R.color.green,R.color.blue,R.color.pink,R.color.teal};
    int[] backgroundResources= {drawable.squares, drawable.empty_oval, drawable.small_cross, drawable.triangle };

    ButtonData[] shapes= new ButtonData[total_shapes];
    int[] rows = new int[total_shapes];

    String baseTest;
    public BackgroundWork(View.OnClickListener buttonClick, View.OnClickListener circle_clicked, Context context, RelativeLayout relLayout, int width, int height, MediaPlayer beep, int user_id, ConstraintLayout beepsCl, Intent intent, String baseTest) {
        this.buttonClick=buttonClick;
        this.circle_clicked=circle_clicked;
        this.context = context;
        this.relLayout= relLayout;
        this.screenWidth=width;
        this.screenHeight =height;
        Log.d("Screen Size",""+width);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.beep = beep;
        this.user_id = user_id;
        this.beepsCl = beepsCl;
        this.intent=intent;
        this.baseTest=baseTest;
        max_size=screenWidth/rows_of_shapes;
        generateRound();
        divideShapesIntoRows();
        float start_time= SystemClock.elapsedRealtime();

    }



    private void generateRound() {
        // Filling an array with shapes will later send to the sendShapes threads
        int since_last_circle=0;        // Keep track of how many shapes since the last circle
        int button_counter=0;           // Used to make sure I dont go over the number of cirlces

        int shape_size= getNextButtonSize();      //Max size
        //int screen_play=screenWidth-shape_size;


        for(int i=0; i<(total_shapes);i++){
            shape_size= r.nextInt((max_size-min_size))+min_size;
            Button tempButton= new Button(context);                   // Create a new button
            ButtonData buttonData;
            button_counter++;
            //tempButton.setLayoutParams(new ConstraintLayout.LayoutParams(shape_size,shape_size));
            //tempButton.setX(880);

            if(since_last_circle>=5&&cirles_counted_for<cirlce_trials){

                // Preventing a circle from appearing within the first few(8) shapes
                // Also prevents many circles from appearing in a row to quickly
                int circle_chance= r.nextInt(100)+1;
                if (circle_chance<=90){
                    shape_size=getNextButtonSize();

                    if(shape_size==50){
                        tempButton.setLayoutParams(new ConstraintLayout.LayoutParams(150,150));
                        tempButton.setBackgroundResource(drawable.circle_no_background);
                    }
                    else{
                        tempButton.setLayoutParams(new ConstraintLayout.LayoutParams(shape_size,shape_size));
                        tempButton.setBackgroundResource(drawable.circle);
                    }
                    cirles_counted_for++;
                    // If a circle is allowed, it has a 70% chance of being a circle
                    // This is successful and will become a circle
                    tempButton.setOnClickListener(circle_clicked);

                    //Log.d("RedCircle","Button: "+button_counter);
                    tempButton.setBackgroundResource(drawable.circle_no_background);
                    //Log.d("CircleChance",""+since_last_circle);
                    since_last_circle=0;
                    relLayout.addView(tempButton);
                    tempButton.setY(-shape_size*4);
                    buttonData = new ButtonData(tempButton,screenHeight,true);
                }
                else{
                    tempButton.setLayoutParams(new ConstraintLayout.LayoutParams(shape_size,shape_size));
                    // There was chance for a circle, but it did not win the lottery
                    tempButton.setOnClickListener(buttonClick);

                    tempButton.setBackgroundResource(backgroundResources[r.nextInt(backgroundResources.length)]);
                    tempButton.setBackgroundTintList(ColorStateList.valueOf(context.getColor(colors[r.nextInt(colors.length)])));
                    since_last_circle++;
                    relLayout.addView(tempButton);
                    tempButton.setY(-shape_size);
                    buttonData = new ButtonData(tempButton,screenHeight,false);
                }
                shapes[i]=buttonData;
                continue;


            }
            else{
                tempButton.setLayoutParams(new ConstraintLayout.LayoutParams(shape_size,shape_size));
                // Guaranteed to not be a circle
                // Due to one happening too recently
                tempButton.setOnClickListener(buttonClick);

                tempButton.setBackgroundResource(backgroundResources[r.nextInt(backgroundResources.length)]);
                tempButton.setBackgroundTintList(ColorStateList.valueOf(context.getColor(colors[r.nextInt(colors.length)])));
                since_last_circle++;
                relLayout.addView(tempButton);
                tempButton.setY(-shape_size);
                buttonData = new ButtonData(tempButton,screenHeight,false);
                shapes[i]=buttonData;
                continue;
            }
        }
    }

    private int getNextButtonSize() {
        int rand = r.nextInt(3)+1;
        if(rand==1){
            Log.d("RAND1","Rand1");
            if(small_circles<5){
                small_circles++;
                return min_size;
            }
            if(med_circles<5){
                med_circles++;
                return max_size+min_size/2;
            }
            if(big_circles<5){
                big_circles++;
                return max_size;
            }
        }
        if(rand==2){
            Log.d("RAND2","Rand2");

            if(med_circles<5){
                med_circles++;
                return max_size+min_size/2;
            }
            if(big_circles<5){
                big_circles++;
                return max_size;
            }
            if(small_circles<5){
                small_circles++;
                return min_size;
            }
        }
        else{
            Log.d("RAND3","Rand3");

            if(big_circles<5){
                big_circles++;
                return max_size;
            }
            if(med_circles<5){
                med_circles++;
                return max_size+min_size/2;
            }
            if(small_circles<5){
                small_circles++;
                return min_size;
            }
        }
        Log.d("DEFAULT","DEFAULT");

        return 20;
    }

    private void divideShapesIntoRows() {
        int last=-1;
        int priorToLast=-1;
        int num;
        for(int i=0;i<shapes.length;i++){
            // Determine what row each will display in
            num=r.nextInt(4);
            if(num!=last&&num!=priorToLast){
                // The row was not used recently
                priorToLast=last;
                last=num;
                rows[i]=num;
            }
            else{
                priorToLast=last;

                if(num>0){
                    last=--num;
                }
                else{
                    last=++num;
                }
                rows[i]=num;
            }
            shapes[i].setCurrentX(rows[i]*(screenWidth/rows_of_shapes));
        }
        int count=0;
        int j=0;
        for(int i=0;i<shapes.length;i++){
            // Determine which sendshapes thread will be responsible for computing

            if(count==0){
                shapes1[j]=shapes[i];
            }
            else if(count==1){
                shapes2[j]=shapes[i];
            }
            else{
                shapes3[j]=shapes[i];
                count=-1;
                j++;
            }
            count++;
        }
    }

    @Override
    public void run() {
//        try {
//            Thread.sleep(2000); // Allow time for user to understand what is happening
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        Handler handler = new Handler(Looper.getMainLooper());
//        Log.d("Getting Here", "Getting here");
//        sendShape1= new sendShapes(shapes1, time);
//        sendShape1.start();
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        sendShape2= new sendShapes(shapes2, time);
//        sendShape2.start();
//
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        sendShape3= new sendShapes(shapes3, time);
//        sendShape3.start();
//
//        while(sendShape1.running || sendShape2.running ||sendShape3.running ){
//            // Do nothing but wait
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        Log.d("Getting Here", "Getting here");
        try {
            Thread.sleep(2000); // Allow time for user to understand what is happening
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Handler handler = new Handler(Looper.getMainLooper());

        Runnable startShapes = new Runnable() {
            int step = 0;
            Runnable endDelay = new Runnable() {
                @Override
                public void run() {
                    mHandler.post(() -> beepsCl.setVisibility(View.VISIBLE));
                }
            };
            Runnable checkBeepAndTime = new Runnable() {
                @Override
                public void run() {
                    if ((sendShape1.running || sendShape2.running ||sendShape3.running)) {
                        int rand = r.nextInt(1000) + 1;
                        if (total_beeps<12&&rand < 200) {
                            beep.start();
                            total_beeps++;
                        }
                        handler.postDelayed(this, 1500);
                    } else {
                        handler.postDelayed(endDelay, 2000);
                    }
                }
            };
            @Override
            public void run() {
                switch (step) {
                    case 0:
                        sendShape1= new sendShapes(shapes1, time);
                        sendShape1.start();
                        break;
                    case 1:
                        sendShape2= new sendShapes(shapes2, time);
                        sendShape2.start();
                        break;
                    case 2:
                        sendShape3= new sendShapes(shapes3, time);
                        sendShape3.start();
                        handler.postDelayed(checkBeepAndTime, 1000);
                        break;
                }
                step++;
                if(step>=3){

                }
                else{
                    handler.postDelayed(this, time_between_shapes);

                }
            }
        };

        handler.post(startShapes);




    }


    public void endMultitasking() {
        if (running) {
            myDB.close();
            running = false;
        }

    }

    public void screen_clicked(){
        wrong_clicks++;
    }

    public void getBeepsEntered(Integer integer) {
        counted_beeps = integer;
        total_circles=sendShape1.total_circles+sendShape2.total_circles+sendShape3.total_circles;
        myDB = new MyDatabaseHelperTask5(context);
        myDB.addRoundData(user_id, correct_clicks, cirles_counted_for, counted_beeps, total_beeps, baseTest);
        endMultitasking();
    }


    public void wrong_click(View v) {

        wrong_clicks++;
        relLayout.removeView(v);
    }

    public void circle_clicked(View v) {
        correct_clicks++;
        relLayout.removeView(v);
    }
}
