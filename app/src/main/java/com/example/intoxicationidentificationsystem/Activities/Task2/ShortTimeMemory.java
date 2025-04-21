package com.example.intoxicationidentificationsystem.Activities.Task2;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.intoxicationidentificationsystem.Welcome;
import com.example.intoxicationidentificationsystem.Databases.Task2.MyDatabaseHelperTask2;
import com.example.intoxicationidentificationsystem.R;

import java.util.Locale;
import java.util.Objects;


public class ShortTimeMemory extends AppCompatActivity implements TextToSpeech.OnInitListener {
/*
This is task 2 or the Short Time Memory task.
This Task will read and display a series of numbers to the user,
the user is supposed to listen and watch as the numbers display.
Afterwards the user is supposed to enter the numbers they saw/heard in
reverse order.
So, if the app displays 1,2,3 then the user would enter 3,2,1.
The more times in a row the user gets the answer correct, the larger their number will grow
*/

    TextToSpeech tts;// Used to vocalize the numbers

    ConstraintLayout buttons;   // The constraint layout containing all the buttons
                                // Turns on and off for users to know when to enter values
    TextView displayBox;        // Where the numbers are displayed
    TextView answerBox;         // When the user types their answer, it is displayed here

    // Creates the UI of this task
    Button one;
    Button two;
    Button three;
    Button four;
    Button five;
    Button six;


    // The background thread for this task. It does a lot of the heavy lifting.
    Backgroundwork bgWork;

    int user_id;
    Intent intent;
    MyDatabaseHelperTask2 myDB;
    String baseTest;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Same set up as Task 1
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_2_activity);
        intent = new Intent(ShortTimeMemory.this, Welcome.class);
        if(getIntent().hasExtra("id")){
            user_id=Integer.valueOf(getIntent().getStringExtra("id"));
            if(getIntent().hasExtra("status")){
                baseTest=getIntent().getStringExtra("status");
            }
            Objects.requireNonNull(getSupportActionBar()).setTitle("User: "+user_id);
            intent.putExtra("id", String.valueOf(user_id));
            intent.putExtra("status",baseTest);
        }

        // Initializing the text to speech
        tts = new TextToSpeech(this, this);
        tts.setLanguage(Locale.US);

        // Pointing to xml counterparts
        displayBox= findViewById(R.id.displayBox);
        answerBox= findViewById(R.id.AnswerBox);
        buttons= findViewById(R.id.buttonsBox);

        one = findViewById(R.id.Number1);
        two = findViewById(R.id.Number2);
        three = findViewById(R.id.Number3);
        four = findViewById(R.id.Number4);
        five = findViewById(R.id.Number5);
        six = findViewById(R.id.Number6);

        // Starting background thread
        bgWork= new Backgroundwork(ShortTimeMemory.this, one,two,three,four,five,six, displayBox, answerBox, tts, intent, baseTest,user_id);


    }
    // This will activate when the tts is fully initiated and ready to use
    // Once the tts is ready, the activity will start
    @Override
    public void onInit(int status) {
        // Begin the game
        bgWork.start();

        // Handler to end the main thread once the background thread is done running
            // Perhaps a simple while loop would be better for this part?
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable checkRunning = new Runnable() {
            @Override
            public void run() {
                if (bgWork.running) {
                    // Creates a loop that checks to see if the background thread is running every 1000 millisec
                    handler.postDelayed(this, 1000);
                } else {
                    // End the task and return to main menu
                    ShortTimeMemory.this.startActivity(intent);
                }
            }
        };
        // Starts this small looping runnable
        handler.post(checkRunning);
    }




}
