package com.example.intoxicationidentificationsystem.Activities.Task5;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;

import com.example.intoxicationidentificationsystem.R;

import java.util.Random;

public class ButtonData {
    Random r = new Random();
    private int xCord,yCord,locations,startX,movementY;
    public Button button;
    Handler mainHandler= new Handler(Looper.getMainLooper());
    int total_height;
    int start_height;
    int delay =10;
    public Boolean is_red_circle;


    public ButtonData(Button button, int screenHeight, Boolean is_red_circle) {
        this.button=button;


        this.locations= r.nextInt(110)+180;
        this.xCord= (int)button.getX();
        total_height= (int) ((button.getY()*-2)+screenHeight);
        start_height= (int) button.getY();
        movementY=total_height/this.locations;
        this.is_red_circle=is_red_circle;

    }



    public int getCurrentX() {
        return xCord;
    }


    public void setCurrentX(int currentX) {
        this.xCord = currentX;
        button.setX(currentX);
    }

    public int getCurrentY() {
        return yCord;
    }

    public void setCurrentY(int currentY) {
        this.yCord = currentY;
        button.setY(currentY);

    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }



    public int getMovementY() {
        return movementY;
    }

    public void setMovementY(int movementY) {
        this.movementY = movementY;
    }

    @Override
    public String toString() {
        return "ButtonData{" +
                "currentX=" + xCord +
                ", currentY=" + yCord +
                ", startX=" + startX +
                //", endX=" + endX +
                '}';
    }
//    public int getNextXlocation(){
//        int temp=getCurrentX()+getMovementX();
//        setCurrentX(temp);
//        return temp;
//    }
    public int peakNextY(){
        return getCurrentY()+getMovementY();
    }
    public int getNextYlocation(){
        int temp=getCurrentY()+getMovementY();
        setCurrentY(temp);
        if(yCord>=total_height){
            setCurrentY(start_height);
        }
        return temp;
    }
    public void playAnimation() {
        animateStep();
    }

    private void animateStep() {
        if (locations > 0 && yCord < total_height) {
            mainHandler.post(() -> {
                button.setY(getNextYlocation());
                //Log.d("Button " + button.getId(), "Moved to: " + getCurrentY());
            });

            locations--;
            mainHandler.postDelayed(this::animateStep, delay);
        } else {
            mainHandler.post(() -> button.setY(getNextYlocation()));
            //Log.d("Button ", button.getId() + " is done");
        }
    }



}
