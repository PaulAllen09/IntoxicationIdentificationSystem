package com.example.intoxicationidentificationsystem.StartActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intoxicationidentificationsystem.Activities.Task5.Multitasking;
import com.example.intoxicationidentificationsystem.R;

import java.util.Objects;

public class MultitaskingStart extends AppCompatActivity {

    Thread thread;
    int user_id;
    Intent intent;
    String baseTest;
    Button start;
    boolean running;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multitasking_start);
        intent = new Intent(MultitaskingStart.this, Multitasking.class);
        if(getIntent().hasExtra("id")){
            user_id=Integer.valueOf(getIntent().getStringExtra("id"));
            if(getIntent().hasExtra("status")){
                baseTest=getIntent().getStringExtra("status");
            }
            Log.d("Start status",""+baseTest);

            Objects.requireNonNull(getSupportActionBar()).setTitle("User: "+user_id);
            intent.putExtra("id", String.valueOf(user_id));
            intent.putExtra("status",baseTest);
        }
        start=findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running=false;
                MultitaskingStart.this.startActivity(intent);
            }
        });
    }
}
