package com.example.intoxicationidentificationsystem.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.intoxicationidentificationsystem.Databases.WelcomeData.MyDatabaseHelperWelcomeData;
import com.example.intoxicationidentificationsystem.R;
import com.example.intoxicationidentificationsystem.StartActivities.BalanceStart;
import com.example.intoxicationidentificationsystem.StartActivities.ChoiceStart;
import com.example.intoxicationidentificationsystem.StartActivities.MultitaskingStart;
import com.example.intoxicationidentificationsystem.StartActivities.ShortTimeStart;
import com.example.intoxicationidentificationsystem.StartActivities.SimpleReactionStart;

import java.util.Objects;


public class Welcome extends AppCompatActivity {
    /*
    * Created by Paul Allen 06/20/2024
    * This is the Welcome class. It serves as the main hub for this application.
    * On this page the user will find 5 buttons, one to start each task each task.
    *
    * After every task, the user will be navigated back here.
    * This class will send the user id and the baseline or testing status to every task to enter in the SQLite
    * data. It will also receive the same info from each task, and update the fields respectively.
    *
    * 08/05/24
    * Update: there will now a toggle switch on this page to allow the user to choose between
    * baseline and testing. Baseline should be completed sober and testing intoxicated
    *
    */

    EditText displayIdBox;
    Button newIdButton;
    Button task1Button, task2Button, task3Button, task4Button, task5Button;
    SwitchCompat baselineVsTesting;
    int user_id=-1;
    String testerStatus ="baseline";


    MyDatabaseHelperWelcomeData db;
    Cursor readingCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        baselineVsTesting=findViewById(R.id.baselineVsTesting);

        if(getIntent().hasExtra("id")){
            user_id=Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));
        }
        if(getIntent().hasExtra("testerStatus")){
            testerStatus =getIntent().getStringExtra("testerStatus");

            if(testerStatus!=null && testerStatus.equals("baseline")){
                baselineVsTesting.setChecked(false);
            }
            else if (testerStatus.equals("testing")){
                baselineVsTesting.setChecked(true);
            }
        }
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        db = new MyDatabaseHelperWelcomeData(Welcome.this);


        task1Button = findViewById(R.id.btn_CRT);
        task2Button = findViewById(R.id.btn_STM);
        task3Button = findViewById(R.id.btn_SRT);
        task4Button = findViewById(R.id.btn_B);
        task5Button = findViewById(R.id.btn_M);
        newIdButton = findViewById(R.id.new_id_button);

        displayIdBox = findViewById(R.id.displayIdBox);

        if(user_id!=-1){
            displayIdBox.setText(""+user_id);
        }
        String display = String.valueOf(displayIdBox.getText());
        if (display.equals("-1000")){
            displayIdBox.setText(""+db.getNextUserId());
        }
        //Log.d("testing","before check");
        if(baselineVsTesting.isPressed()){
            //Log.d("testing","after check");

            testerStatus ="testing";
            //testerStatus ="baseline";
        }
        else{
            //Log.d("testing","after check");
            //testerStatus ="testing";
            testerStatus ="baseline";
        }




        task1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayIdBox.getText().toString().isEmpty()){
                    Toast.makeText(Welcome.this, "@string/new_id_please", Toast.LENGTH_SHORT).show();
                }
                else{
                    clickedButton();
                    //Log.d("testing","create intent");
                    Intent intent = new Intent(Welcome.this, ChoiceStart.class);
                    intent.putExtra("id", displayIdBox.getText().toString().trim());
                    //Log.d("Welcome status",""+testerStatus);
                    intent.putExtra("status", testerStatus);
                    //Log.d("testing","before start");
                    Welcome.this.startActivity(intent);
                }
            }
        });
        task2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayIdBox.getText().toString().isEmpty()){
                    Toast.makeText(Welcome.this, "@string/new_id_please", Toast.LENGTH_SHORT).show();
                }
                else{
                    clickedButton();
                    Intent intent = new Intent(Welcome.this, ShortTimeStart.class);
                    intent.putExtra("id", displayIdBox.getText().toString().trim());
                    intent.putExtra("status", testerStatus);
                    Welcome.this.startActivity(intent);
                }
            }
        });
        task3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayIdBox.getText().toString().isEmpty()){
                    Toast.makeText(Welcome.this, "@string/new_id_please", Toast.LENGTH_SHORT).show();
                }
                else{
                    clickedButton();
                    Intent intent = new Intent(Welcome.this, SimpleReactionStart.class);
                    intent.putExtra("id", displayIdBox.getText().toString().trim());
                    intent.putExtra("status", testerStatus);
                    Welcome.this.startActivity(intent);
                }
            }
        });
        task4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayIdBox.getText().toString().isEmpty()){
                    Toast.makeText(Welcome.this, "@string/new_id_please", Toast.LENGTH_SHORT).show();
                }
                else{
                    clickedButton();
                    Intent intent = new Intent(Welcome.this, BalanceStart.class);
                    intent.putExtra("id", displayIdBox.getText().toString().trim());
                    intent.putExtra("status", testerStatus);
                    Welcome.this.startActivity(intent);
                }
            }
        });
        task5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayIdBox.getText().toString().isEmpty()){
                    Toast.makeText(Welcome.this, "@string/new_id_please", Toast.LENGTH_SHORT).show();
                }
                else{
                    clickedButton();
                    Intent intent = new Intent(Welcome.this, MultitaskingStart.class);
                    intent.putExtra("id", displayIdBox.getText().toString().trim());
                    intent.putExtra("status", testerStatus);
                    Welcome.this.startActivity(intent);
                }
            }
        });

        newIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayIdBox!=null){
                    displayIdBox.setText(""+db.getNextUserId());
                }
            }
        });


        baselineVsTesting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setPressed(buttonView.isChecked());
            }
        });

        db.close();
    }
    void clickedButton(){
        String temp = String.valueOf(displayIdBox.getText());
        if(!db.findID(Integer.parseInt(temp))){
            db.addIdNum(Integer.parseInt(temp));
        }

    }



}
