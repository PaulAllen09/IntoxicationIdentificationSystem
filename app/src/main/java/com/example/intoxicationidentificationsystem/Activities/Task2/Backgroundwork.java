package com.example.intoxicationidentificationsystem.Activities.Task2;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.intoxicationidentificationsystem.Activities.Task2.ShortTimeMemory;
import com.example.intoxicationidentificationsystem.Databases.Task2.MyDatabaseHelperTask2;
import com.example.intoxicationidentificationsystem.R;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/*
This class serves as a background thread for task 2.
It will control the majority of the task, only passing UI changes back to the main thread.
Main UI changes are the changes of the text boxes that display the numbers, and the
user input textbox

 */

public class Backgroundwork extends Thread {

    TextToSpeech tts;// Used to vocalize the numbers

    //ConstraintLayout buttons;// the constraint layout containing all the buttons
    TextView displayBox;    // Where the numbers are displayed
    TextView answerBox;     // Where the user input will be displayed
    // The buttons the user will click on.
    Button one;
    Button two;
    Button three;
    Button four;
    Button five;
    Button six;

    // The handler is used to talk to the main/UI thread
    Handler handler = new Handler(Looper.getMainLooper());

    // Used to generate random numbers every round.
    Random r = new Random();

    int trials=0;          // Switched to a trial based system, they get 15 trials and move up and down
    // based on if they are right or wrong

    int max_trials =10;
    int error_repeat=0;     // Will keep track of how many times a user enters the wrong answer in a row
    //int lives=3;            // Keeps track of how many attempts the user has left
    //int roundTimeLength=30; // The number of seconds this task will run
    int roundNumber=1;      // Keeps track of what round the user is on
    // the round number correlates to the length of the number that
    // will be displayed to the user.

    String userEnteredString;   // Will be the values the user enters
    String roundString;         // Will be the wanted answer

    // Initialized and set right away to ensure it is working properly before starting activity
    //long startTaskTimer=SystemClock.elapsedRealtime();
    long startTime=SystemClock.elapsedRealtime();
    long responseTime=SystemClock.elapsedRealtime();



    // When the user clicks a button this increments
    int buttonsClicked=0;


    // This thread will be used to communicate with the main thread
    // to display the values every round
    Thread showThread;


    boolean running=true;   // Used to prevent a double stop of thread

    Context context;    // Will be the ui thread's context
    // used to put data in the SQLite database and for toast messages

    // The incoming data to be stored here and used in SQLite database
    int user_id;
    String baseTest;

    Intent intent;
    // Connection to the SQLite database
    MyDatabaseHelperTask2 myDB;

    public Backgroundwork(Context context, Button one, Button two, Button three, Button four,
                          Button five, Button six, TextView displayBox, TextView answerBox,
                          TextToSpeech tts, Intent intent, String baseTest, int user_id){
        this.context=context;
        this.one=one;
        this.two=two;
        this.three=three;
        this.four=four;
        this.five=five;
        this.six=six;
        this.baseTest=baseTest;
        this.user_id=user_id;

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEntered(1);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEntered(2);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEntered(3);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEntered(4);
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEntered(5);
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEntered(6);
            }
        });

        this.displayBox=displayBox;
        this.answerBox=answerBox;
        this.tts=tts;
        this.intent=intent;
        myDB = new MyDatabaseHelperTask2(context);
    }


    @Override
    public void run() {
        super.run();
        // Used to keep track of how long it takes the user to input their value
        //startTime=SystemClock.elapsedRealtime();
        // Will ensure the task runs for only x amount of time
        //startTaskTimer=SystemClock.elapsedRealtime();
        playRound();
    }

    private void playRound() {
        //If the running flag is true play a round
        if (running) {
            if (trials<max_trials) {
                trials++;
                // Resets the displays and values for the new round
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This function plays and controls a round
                        answerBox.setText("");
                        buttonsClicked = 0;
                        userEnteredString = ""; // Setting the user-entered string to nothing
                        roundString = ""; // Clears the previous answer if there is one
                        roundNumber++; // Increases round number: starts at 0
                        generateNumbers(); // Generates a string containing the correct answer. Stores in roundString (universal)
                        showThread = new Thread(new showNumberThread()); // Generates a thread to show the answer in reverse order
                        showThread.start(); // Starts showing the numbers
                        // toggleButtons("on");
                        // Round ends when the correct number of buttons are pressed or game timer runs out.
                    }
                }, 1500); // Delay of 1500ms
                // The delay give the thread time for the user to understand a new round is beginning
            }
            else{
                // Ends the game when time runs out.
                endShortTimeMemory();
            }
        }
    }



    public void userEntered(int num) {
        /*
        This function gets called every time a user clicks a button.
        That button has a number associated with it, and it sends that value to
        this function where it is processed
         */

        userEnteredString+=num;     // Adds the user input onto their inputted string
        // Update the user's answer box on the main/UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Log.d("Setting answerBox", "String: "+userEnteredString);
                answerBox.setText(userEnteredString);
            }
        });
        buttonsClicked++;
        if(buttonsClicked>=roundString.length()){
            endRound();
        }
    }


    private void generateNumbers() {
        //Log.d("Generating numbers", "Generating start");

        for(int i=0;i<roundNumber;i++){     // Used to generate numbers, as many as the current round.
            if(roundString.isEmpty()){      // If the answer is empty initialize it
                roundString=""+(r.nextInt(6)+1);// initializing with random number, 1-6
                //roundString+=(r.nextInt(6)+1); // Want the user to start at two

            }
            else{
                roundString+=(r.nextInt(6)+1);
            }

        }
        //Log.d("Answer generated", "Answer: "+roundString);

    }


    private void endRound() {
        //Log.d("End of round", "End of Round: "+roundNumber);
        //Log.d("Toggle buttons ", "toggle: off?");

        // Turn buttons off
        toggleButtons("off");
        // Calculates how long it took for the user to respond.
        responseTime= SystemClock.elapsedRealtime()-startTime;


        // Stores round data
        myDB.addRoundData(user_id, roundString, userEnteredString, String.valueOf(responseTime),baseTest);
        if(userEnteredString.equals(roundString)){
            // If the answer matches the round string meaning a correct answer.
            //Log.d("inside endround","Calling a new play round");
            error_repeat=0;
            playRound();
        }
        else{
            // If the user gets the answer wrong...
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            error_repeat++;
            // If the user gets the answer wrong 3 times in a row
            if(error_repeat>=3){
                error_repeat=0;
                // Go down a round number
                roundNumber--;
            }
            // This will ensure that the round stays the same
            if(roundNumber>1){
                roundNumber--;
            }
            playRound();
            // Loose a life when the user enters an incorrect input
            //looseLife();
        }

    }




    private void endShortTimeMemory() {
        // Ending the task
        //Log.d("EndShortTimeMemory", "ESTM");
        if(running){
            // Make sure it did not try to end already
            myDB.close();       // Close the connection to the database
            running=false;      // Change flag to prevent double ending
            this.interrupt();   // Stop the thread from running.
            //end thread
        }

    }



    public void toggleButtons(String toggle){
    /*
    Communicates with the main thread to toggle the buttons on and off so the user is able to
    click the buttons at the appropriate time. Also so the user does not click at an inappropriate time.
     */
        // Turns the buttons on
        if(toggle.equals("on")){
            //Log.d("toggle buttons"," toggle on");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    one.setVisibility(View.VISIBLE);
                    two.setVisibility(View.VISIBLE);
                    three.setVisibility(View.VISIBLE);
                    four.setVisibility(View.VISIBLE);
                    five.setVisibility(View.VISIBLE);
                    six.setVisibility(View.VISIBLE);

                }
            });
        }
        // Turn the buttons off
        if(toggle.equals("off")){
            Log.d("toggle buttons"," toggle off");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    one.setVisibility(View.INVISIBLE);
                    two.setVisibility(View.INVISIBLE);
                    three.setVisibility(View.INVISIBLE);
                    four.setVisibility(View.INVISIBLE);
                    five.setVisibility(View.INVISIBLE);
                    six.setVisibility(View.INVISIBLE);                }
            });

        }
    }




    class showNumberThread implements Runnable{

        /*
         * This thread should display the numbers
         */
        @Override
        public void run() {
            //Log.d("show Thread","starting");
            // Make sure the correct answer is not empty
            // Probably not needed because initialized with one value inside?
            if(!roundString.isEmpty()) {
                for (int i = 0; i<roundString.length(); i++) {
                    displayBox.setText(String.valueOf(roundString.charAt(i)));
                    //vocalize the number here
                    switch (Character.getNumericValue(roundString.charAt(i))){
                        case 1:
                            talk("One");
                            break;
                        case 2:
                            talk("Two");
                            break;
                        case 3:
                            talk("Three");
                            break;
                        case 4:
                            talk("Four");
                            break;
                        case 5:
                            talk("Five");
                            break;
                        case 6:
                            talk("Six");
                            break;
                    }



                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        if(!running){
                            break;
                        }
                        else {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            //turns buttons on
            toggleButtons("on");
            startTime= SystemClock.elapsedRealtime();
        }

        private void talk(String text) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }

    }
    //**********************************************************************************************

}


