package com.example.intoxicationidentificationsystem.StartActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intoxicationidentificationsystem.Activities.Task4.BalanceTask;
import com.example.intoxicationidentificationsystem.R;

import java.util.Objects;

public class BalanceStart extends AppCompatActivity {
    Thread thread;
    Intent intent;
    int user_id;
    String baseTest;
    Button start;
    boolean running=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_start);
        intent = new Intent(BalanceStart.this, BalanceTask.class);
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
                BalanceStart.this.startActivity(intent);
            }
        });
    }
}
