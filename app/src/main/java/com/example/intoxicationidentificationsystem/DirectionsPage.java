package com.example.intoxicationidentificationsystem;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intoxicationidentificationsystem.Activities.Task1.ChoiceReactionTime;
import com.example.intoxicationidentificationsystem.Activities.Task2.ShortTimeMemory;
import com.example.intoxicationidentificationsystem.Activities.Task4.BalanceTask;
import com.example.intoxicationidentificationsystem.Activities.Task5.Multitasking;
import com.example.intoxicationidentificationsystem.StartActivities.SimpleReactionStart;

import java.util.Objects;

public class DirectionsPage extends AppCompatActivity {
    Intent intent;
    int user_id;
    int taskNumber;
    String baseTest;

    TextView taskHeaderTV;
    TextView taskDescriptionTV;
    ImageView taskPreviewImage;
    Button startButton;

    Intent nextIntent;

    // Should act as lock for safe threading
    boolean taskIsRunning=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directions_page);


        taskHeaderTV = findViewById(R.id.taskHeaderTV);
        taskDescriptionTV = findViewById(R.id.taskDescriptionTV);
        taskPreviewImage = findViewById(R.id.taskPreviewImage);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!taskIsRunning){
                    taskIsRunning=true;
                    startActivity(nextIntent);
                }
            }
        });

        if(getIntent().hasExtra("id")){
            user_id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));
            baseTest = getIntent().getStringExtra("status");
            taskNumber = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("taskNumber")));
            Objects.requireNonNull(getSupportActionBar()).setTitle("User: "+user_id);
            switch (taskNumber) {
                case 1:
                    nextIntent = new Intent(this, ChoiceReactionTime.class);
                    taskHeaderTV.setText(R.string.task_1);
                    taskDescriptionTV.setText(R.string.task_1_description);
                    taskPreviewImage.setVisibility(VISIBLE);
                    taskPreviewImage.setBackgroundResource(R.drawable.task_1_preview);
                    break;
                case 2:
                    nextIntent = new Intent(this, ShortTimeMemory.class);
                    taskHeaderTV.setText(R.string.task_2);
                    taskDescriptionTV.setText(R.string.task_2_description);
                    taskPreviewImage.setVisibility(VISIBLE);
                    taskPreviewImage.setBackgroundResource(R.drawable.task_2_preview);
                    break;
                case 3:
                    nextIntent = new Intent(this, SimpleReactionStart.class);
                    taskHeaderTV.setText(R.string.task_3);
                    taskDescriptionTV.setText(R.string.task_3_description);
                    taskPreviewImage.setVisibility(VISIBLE);
                    taskPreviewImage.setBackgroundResource(R.drawable.task_3_preview);
                    break;
                case 4:
                    nextIntent = new Intent(this, BalanceTask.class);
                    taskHeaderTV.setText(R.string.task_4);
                    taskDescriptionTV.setText(R.string.task_4_description);
                    taskPreviewImage.setVisibility(GONE);

                    // There is no preview image for task 4 as the description is too long
                    //taskPreviewImage.setBackgroundResource(R.drawable.task_4_preview);
                    break;
                case 5:
                    nextIntent = new Intent(this, Multitasking.class);
                    taskHeaderTV.setText(R.string.task_1);
                    taskDescriptionTV.setText(R.string.task_5_description);
                    taskPreviewImage.setVisibility(VISIBLE);
                    taskPreviewImage.setBackgroundResource(R.drawable.task_5_preview);
                    break;
            }   // End of switch statement
            intent.putExtra("id", String.valueOf(user_id));
            intent.putExtra("status",baseTest);
        }
    }
}
