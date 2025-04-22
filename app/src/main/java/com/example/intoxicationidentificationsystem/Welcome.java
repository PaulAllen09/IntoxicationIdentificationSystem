package com.example.intoxicationidentificationsystem;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.intoxicationidentificationsystem.Databases.WelcomeData.MyDatabaseHelperWelcomeData;

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

    Intent intent;

    MyDatabaseHelperWelcomeData db;
    Cursor readingCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("putting up task number","Start of woelcme:");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        baselineVsTesting=findViewById(R.id.baselineVsTesting);

        displayIdBox = findViewById(R.id.displayIdBox);
        db = new MyDatabaseHelperWelcomeData(Welcome.this);


        Log.d("putting up task number","before intent check:");

        if(getIntent().hasExtra("id")){
            user_id=Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));
            displayIdBox.setText(""+user_id);
        }
        if(getIntent().hasExtra("testerStatus")){
            testerStatus =getIntent().getStringExtra("testerStatus");

            if(testerStatus!=null && testerStatus.equals("baseline")){
                baselineVsTesting.setChecked(false);
            }
            else if (testerStatus!=null && testerStatus.equals("testing")){
                baselineVsTesting.setChecked(true);
            }
        }
        Log.d("putting up task number","before check taskNumber:");

        if(getIntent().hasExtra("taskNumber")){
            displayIdBox.setText(""+db.getNextUserId());
        }
        Log.d("putting up task number","after check taskNumber:");

        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        String display = String.valueOf(displayIdBox.getText());
        if (display.equals("-1000")){
            displayIdBox.setText(""+db.getNextUserId());
        }
        intent = new Intent(Welcome.this, DirectionsPage.class);

        task1Button = findViewById(R.id.btn_CRT);
        task2Button = findViewById(R.id.btn_STM);
        task3Button = findViewById(R.id.btn_SRT);
        task4Button = findViewById(R.id.btn_B);
        task5Button = findViewById(R.id.btn_M);
        newIdButton = findViewById(R.id.new_id_button);

        displayIdBox = findViewById(R.id.displayIdBox);


        testerStatus = baselineVsTesting.isPressed()? "testing":"baseline";

        task1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedButton(1);
            }
        });
        task2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedButton(2);
            }
        });
        task3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedButton(3);

            }
        });
        task4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedButton(4);

            }
        });
        task5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedButton(5);

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
    void clickedButton(int taskNumber){
        // If the user is able to press button start activity and store user data

        if(displayIdBox.getText().toString().isEmpty()){
            Toast.makeText(Welcome.this, "@string/new_id_please", Toast.LENGTH_SHORT).show();
        }
        else{


            String temp = String.valueOf(displayIdBox.getText());
            if(!db.findID(Integer.parseInt(temp))){
                db.addIdNum(Integer.parseInt(temp));
            }

            intent.putExtra("id", displayIdBox.getText().toString().trim());
            intent.putExtra("status", testerStatus);
            intent.putExtra("taskNumber", Integer.toString(taskNumber));
            Welcome.this.startActivity(intent);
        }
    }
}
