package com.example.intoxicationidentificationsystem.Databases.Task4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelperTask4 extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Task4.db";
    private static final int DATABASE_VERSION = 3;


    private static final String TABLE_NAME="task_4_data";

    private static final String COl_TESTNUM ="test_number";
    private static final String COL_ID="_id";
    private static final String COL_LIVES="_lives";
    private static final String COL_SCORE="_score";
    private static final String COL_LONGEST_TIME="_user_longest_time";
    private static final String STATUS="_status";
    private static final String DATE="_date";


    public MyDatabaseHelperTask4(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating the table make sure to include the proper spacing
        //last line will change color of the text in the sql language
        String query=
                "CREATE TABLE " + TABLE_NAME +
                        " ( "+ COl_TESTNUM + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_ID + " INTEGER, " +
                        COL_LIVES + " INTEGER, "+
                        COL_SCORE + " INTEGER, " +
                        COL_LONGEST_TIME +" STRING, "+
                        STATUS + " STRING, "+
                        DATE + " STRING );";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //if datbase version changes delete the current table
        //not needed, but will keep for reference
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
    public void addRoundData(int userID,int lives, int score, String longestTime, String status){
        //will add the data collected in the round to the database
        SQLiteDatabase db = this.getWritableDatabase();

        //pass all the values that need to be stored through here
        ContentValues cv = new ContentValues();


        cv.put(COL_ID, userID);
        cv.put(COL_LIVES, lives);
        cv.put(COL_SCORE, score);
        cv.put(COL_LONGEST_TIME,longestTime);
        cv.put(STATUS,status);
        cv.put(DATE, String.valueOf(java.time.LocalDateTime.now()));

































































































































































        long result= db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context,"Data failed to add", Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(context,"Data Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }
    public Cursor readAllData(){
        //used to read all the data from the table no used yet
        //will be used to create a ui in app eventually
        String query = " SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db!=null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;
    }
}