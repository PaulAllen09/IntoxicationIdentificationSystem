package com.example.intoxicationidentificationsystem.Databases.Task5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelperTask5 extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Task5.db";
    private static final int DATABASE_VERSION = 1;


    private static final String TABLE_NAME="task_5_data";
    private static final String COL_ATTEMPT="_attempt";
    private static final String COL_ID="_id";
    private static final String COL_CIRCLES_CLICKED="_circles_clicked";
    private static final String COL_CIRCLES_APPEARED="_circles_appeared";
    private static final String COL_BEEPS_ENTERED="_beeps_entered";
    private static final String COL_BEEPS_APPEARED="_beeps_played";
    private static final String STATUS="_status";



    public MyDatabaseHelperTask5(@Nullable Context context) {
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
                        COL_CIRCLES_CLICKED + " INTEGER, "+
                        COL_CIRCLES_APPEARED + " INTEGER, " +
                        COL_BEEPS_ENTERED + " INTEGER, " +
                        COL_BEEPS_APPEARED +" INTEGER, "+
                        STATUS + " STRING );";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //if datbase version changes delete the current table
        //not needed, but will keep for reference
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
    public void addRoundData(int userID,int clicked, int cAppear, int beep, int cBeeps, String status){
        //will add the data collected in the round to the database
        SQLiteDatabase db = this.getWritableDatabase();

        //pass all the values that need to be stored through here
        ContentValues cv = new ContentValues();


        cv.put(COL_ID, userID);
        cv.put(COL_CIRCLES_CLICKED, clicked);
        cv.put(COL_CIRCLES_APPEARED, cAppear);
        cv.put(COL_BEEPS_ENTERED,beep);
        cv.put(COL_BEEPS_APPEARED,cBeeps);
        cv.put(STATUS,status);
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