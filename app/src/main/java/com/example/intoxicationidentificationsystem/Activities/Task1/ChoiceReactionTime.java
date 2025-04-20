package com.example.intoxicationidentificationsystem.Activities.Task1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intoxicationidentificationsystem.Activities.Welcome;
import com.example.intoxicationidentificationsystem.Databases.Task1.MyDatabaseHelperTask1;
import com.example.intoxicationidentificationsystem.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


/*
This is Task 1 or the Choice reaction time test. The ui will name a color on the top of the screen and
the user is supposed to click one of the lower four boxes that has the background color as listed in the
upper box
 */
public class ChoiceReactionTime extends AppCompatActivity {

    int numberOfRounds=15;


    // Used to generate random numbers
    Random r = new Random();
    // Used to connect to local SQLite database
    MyDatabaseHelperTask1 myDB;

    // Initializing the colors to allow for easy data management/manipulation
    Color color1 = new Color("Red", R.color.red);
    Color color2 = new Color("Orange",R.color.orange);
    Color color3 = new Color("Yellow",R.color.yellow);
    Color color4 = new Color("Green",R.color.green);
    Color color5 = new Color("Blue",R.color.blue);
    Color color6 = new Color("Teal",R.color.teal);

    // Initializing an array for easy manipulation of colors
    Color[] colorLst= new Color[]{color1,color2,color3,color4,color5,color6};

    // Will have the name of the correct color displayed at the top of the screen
    TextView choiceColor;
    // The text views being display on the screen that the user can interact with
    TextView colorbox1;
    TextView colorbox2;
    TextView colorbox3;
    TextView colorbox4;

    // Initializing an array for easy manipulation of color boxes
    TextView[] colorBoxes;

    // This variable will represent the index of the box with the correct color as a background.
    int currentCorrectBox;
    //This variable represents the current round number
    int roundNumber=0;


    // Initializing values to store the number of correct and incorrect guesses from the user
    int correctClicks=0;
    int wrongClicks=0;


    // Used in SQLite
    // Also transferred between activities.
    int user_id;
    String baseTest;

    // Used to get information from last activity and send data to next activity
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Standard setup of the activity and ui.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_reaction_time_activity);
        // The next activity that will be loaded will be the welcome screen
        // so users can select another Task.
        intent = new Intent(ChoiceReactionTime.this, Welcome.class);

        // Reading the data from the previous activity if there is any.
        // At most there should be id and status, but could be one or neither.
        if(getIntent().hasExtra("id")){
            user_id=Integer.valueOf(getIntent().getStringExtra("id"));
            if(getIntent().hasExtra("status")){
                baseTest=getIntent().getStringExtra("status");
            }

            // Sets the text in the bar in the upper part of the applications screen to read the users id.
            Objects.requireNonNull(getSupportActionBar()).setTitle("User: "+user_id);
            // Getting ready to send data to the next activity
            intent.putExtra("id", String.valueOf(user_id));
            intent.putExtra("status",baseTest);
        }

        // Pointing the values to their xml counterpart
        choiceColor= findViewById(R.id.choiceColorText);
        colorbox1= findViewById(R.id.ColorBox1);
        colorbox2= findViewById(R.id.ColorBox2);
        colorbox3= findViewById(R.id.ColorBox3);
        colorbox4= findViewById(R.id.ColorBox4);
        // Putting color boxes into the array
        colorBoxes = new TextView[]{colorbox1, colorbox2, colorbox3, colorbox4};

        // Creating on click listeners for the color boxes
        // so the users are able to interact with them
        colorbox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRound(0);
            }
        });
        colorbox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRound(1);
            }
        });
        colorbox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRound(2);
            }
        });
        colorbox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRound(3);
            }
        });


        // Begin the game
        playround();
    }

    private void playround() {
        // Keep track of the current round.
        roundNumber++;
        // Keep track of what background colors and color names that have
        // been used in the current round.
        // Clears after each round.
        List<Integer> usedColor=new ArrayList<Integer>();
        List<Integer> usedNames=new ArrayList<Integer>();

        // Choice color refers to the color that is considered correct
        // Randomly chooses choice a color from the array
        int randColor= r.nextInt(6);
        //Log.e("Choosing currentColor",""+colorLst[randColor].getName());

        // Puts the color's name in the upper text box
        choiceColor.setText(colorLst[randColor].getName());

        // Chooses which box to hold the correct color
        // Create variable to check upon user click.
            // Perhaps another way to do this better would be to make it apart of
            // the onclicklistener
        currentCorrectBox = r.nextInt(4);
        //Log.e("Choosing currentBox",""+currentCorrectBox);

        // Puts the color as the background color for one of the four boxes.
        colorBoxes[currentCorrectBox].setBackgroundColor(getColor(colorLst[randColor].getColor()));

        // Getting a random but incorrect color name to put in the correct box.
        int randName=r.nextInt(6);;
        while (randName==randColor){
            randName=r.nextInt(6);
        }

        // Puts the incorrect name in the correct box.
        colorBoxes[currentCorrectBox].setText(colorLst[randName].getName());

        // Marks the used color and name that are in the box as used.
        // This prevents the same color or color name from appearing twice.
        usedColor.add(randColor);
        usedNames.add(randName);


        // End of initial set up of the round.
        /*
        At this point the top box has the correct name;
        The correct color is in currentCorrectBox. The currentCorrectBox also has an incorrect name.
        both the color and the name used in  have currentCorrectBox been stored in their respective usedArrays
         */


        // Filling the rest of the boxes with other colors and other names
        for(int i=0;i<4;i++){
            if ( i==currentCorrectBox){
            }
            else{
                randColor= getRandColor(usedColor); // Gets an unused color
                randName= getRandName(usedNames);   // Gets an unused name
                usedColor.add(randColor);           // Adds that color to the used list
                usedNames.add(randName);            // Adds that name to the used list
                setBoxes(randColor,i,colorLst[randName].getName());// Sets a box with those values

            }
        }

    }

    private void checkRound(int i) {
        // Checks to see if the user clicked the correct box and changes score accordingly
        // i represents the number box the user clicked on. It is recived from the ui thread when a
        // user clicks on one of the buttons.

        // If the user clicks...

        // the correct box
        //Log.e("Check Round", i +" ? "+currentCorrectBox);
        if(i==currentCorrectBox){
            correctClicks++;
        }
        // an incorrect box
        else{
            wrongClicks++;
        }
        // Checks to see if the game is over or a new round should start
        // The game should only last 6 rounds
        if(roundNumber<numberOfRounds){
            playround();//begins another round
        }
        else{
            // Add the data from the task to the database.
            myDB = new MyDatabaseHelperTask1(ChoiceReactionTime.this);
            myDB.addRoundData(user_id, correctClicks, wrongClicks,baseTest);
            myDB.close();
            ChoiceReactionTime.this.startActivity(intent);
        }
    }


    private int getRandName(List usedNames) {
        /*Gets a list of used names and returns an unused name*/
        int rand= r.nextInt(6);
        // Try to find a new name 5 times. If it cannot find a new name
        // it will just start going through the list until it finds a new one
            // Change this to work better dynamically to allow more colors
        for(int i=0;i<5;i++){
            if(!usedNames.contains(rand)){
                return rand;
            }
            else{
                rand=r.nextInt(6);
            }
        }
        if(!usedNames.contains(0)){
            return 0;
        }
        if(!usedNames.contains(1)){
            return 1;
        }
        if(!usedNames.contains(2)){
            return 2;
        }
        if(!usedNames.contains(3)){
            return 3;
        }
        if(!usedNames.contains(4)){
            return 4;
        }
        if(!usedNames.contains(5)){
            return 5;
        }
        if(!usedNames.contains(6)){
            return 6;
        }
        return rand;
    }



    private int getRandColor(List usedColors) {
        /*Gets a list of used colors and returns an unused color*/
        int rand= r.nextInt(6);
        for(int i=0;i<5;i++){
            if(!usedColors.contains(rand)){
                return rand;
            }
            else{
                rand=r.nextInt(6);
            }
        }
        if(!usedColors.contains(0)){
            return 0;
        }
        if(!usedColors.contains(1)){
            return 1;
        }
        if(!usedColors.contains(2)){
            return 2;
        }
        if(!usedColors.contains(3)){
            return 3;
        }
        if(!usedColors.contains(4)){
            return 4;
        }
        if(!usedColors.contains(5)){
            return 5;
        }
        if(!usedColors.contains(6)){
            return 6;
        }
        return rand;
    }

    private  void setBoxes(int color, int boxNumber, String colorName){
        /*
        This function will be called 4 times per round to change all the boxes
        to the current colors of the current round.
        gives a box a name and color
        * */
        colorBoxes[boxNumber].setBackgroundColor(getColor(colorLst[color].getColor()));
        colorBoxes[boxNumber].setText(colorName);

    }
}