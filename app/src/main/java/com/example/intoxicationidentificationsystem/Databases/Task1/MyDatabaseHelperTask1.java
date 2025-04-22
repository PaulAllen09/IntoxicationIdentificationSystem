package com.example.intoxicationidentificationsystem.Databases.Task1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelperTask1 extends SQLiteOpenHelper {
    /*
    * This is a database helper task. It is responsible for handling
    * data and putting it into an SQLite table
    * */

    private Context context;
    //database_name will be the title of the file
    private static final String DATABASE_NAME = "Task1.db";
    private static final int DATABASE_VERSION = 1;

    //table name is what the title of the table will be
    private static final String TABLE_NAME="task_1_user_data";

    private static final String TABLE_TASK_DATA="task_1_test_data";

    //col_id is the col where user id will be stored
    private static final String COL_ID="_id";
    //col_attempt is a col that increments automatically, and hold the number test that
    //took place. If user 1 took the first test on the device, it would get one.
    //if it took it again or user two took it, they would get 2.
    private static final String COL_ATTEMPT="_attempt";
    //correct is the number of times the user got the answer correct
    private static final String COL_CORRECT="_correct";
    //wrong is the number of times the user got the answer wrong
    private static final String COL_WRONG="_wrong";
    //
    private static final String STATUS="_status";
    private static final String DATE="_date";




    public MyDatabaseHelperTask1(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating the table make sure to include the proper spacing
        //last line will change color of the text in the sql language
        String query=
                "CREATE TABLE " + TABLE_NAME +
                        " ( "+ COL_ATTEMPT + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_ID + " INTEGER, " +
                        COL_CORRECT + " INTEGER, "+
                        COL_WRONG + " INTEGER, "+
                        STATUS + " STRING, "+
                        DATE +" STRING);";
        db.execSQL(query);
        /*
        * I wan this query to store all the user data seperately from their testing data
        * so it should have their col id and current attempt number
        * */

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //if the database version updates, delete the current table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
    public void addRoundData(int userID, int correct, int wrong, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        //pass all the values that need to be stored through here
        ContentValues cv = new ContentValues();

        cv.put(COL_ID,userID);
        cv.put(COL_CORRECT, correct);
        cv.put(COL_WRONG, wrong);
        cv.put(STATUS,status);
        cv.put(DATE, String.valueOf(java.time.LocalDateTime.now()));
        long result= db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(context,"Added Successfully", Toast.LENGTH_SHORT).show();

        }
    }

    public Cursor readAllData(){
        String query = " SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db!=null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;
    }
}