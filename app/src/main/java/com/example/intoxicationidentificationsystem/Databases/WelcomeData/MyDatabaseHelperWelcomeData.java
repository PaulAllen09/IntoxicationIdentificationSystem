package com.example.intoxicationidentificationsystem.Databases.WelcomeData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Date;

public class MyDatabaseHelperWelcomeData extends SQLiteOpenHelper {
    /*
    * This is a database helper task. It is responsible for handling
    * data and putting it into an SQLite table
    * */

    private Context context;
    //database_name will be the title of the file
    private static final String DATABASE_NAME = "Used_Ids.db";
    private static final int DATABASE_VERSION = 1;

    //table name is what the title of the table will be
    private static final String TABLE_NAME="used_id_numbers";

    private static final String TABLE_TASK_DATA="used_id_numbers";

    //col_id is the col where user id will be stored
    private static final String COL_ID= "_id";
    private static final String DATE = "_date";

    public MyDatabaseHelperWelcomeData(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating the table make sure to include the proper spacing
        //last line will change color of the text in the sql language
        String query=
                "CREATE TABLE " + TABLE_NAME +
                        " ( "+ COL_ID + " INTEGER PRIMARY KEY, "+
                        DATE+ " STRING"+ "); ";
        db.execSQL(query);
        //This database is purely used to store what values have already been used
        //by users as an id value.

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //if the database version updates, delete the current table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
    public void addIdNum(int userID){
    //this function will add a user id to the database when there is a number in the enter id field that is not in the database
        SQLiteDatabase db = this.getWritableDatabase();
        //pass all the values that need to be stored through here
        ContentValues cv = new ContentValues();

        cv.put(COL_ID,userID);
        cv.put(DATE, String.valueOf(java.time.LocalDateTime.now()));


        long result= db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context,"Failed to add user. Please reload the app.", Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(context,"Added Successfully", Toast.LENGTH_SHORT).show();

        }
    }

    public Cursor readAllData(){
        //reads all data from the table
        //not used for anything
        //maybe will be used for mobile database viewing
        String query = " SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db!=null){
            cursor =db.rawQuery(query,null);
        }
        return cursor;
    }
    public int getNextUserId() {
        // Find the highest value stored in the table, add one and return it
        // If no values in table return 1001 which will be the value the first user will use.
        String query = "SELECT MAX(" + COL_ID + ") FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int nextUserId = 1001; // Default value if no rows are in the table

        if (db != null) {
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()&& !cursor.isNull(0)) {
                int maxId = cursor.getInt(0); // Get the max id from the first column
                if(maxId>1000) {
                    nextUserId = maxId + 1; // Increment the max id by 1
                }
            }

            if (cursor != null) {
                cursor.close();
            }

            db.close();
        }

        return nextUserId;
    }

    public Boolean findID(int userID){
        //will return true if userID is found in table false if not
        String query = " SELECT * FROM " + TABLE_NAME +" WHERE "+COL_ID+" is " + userID      ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db!=null){
                cursor =db.rawQuery(query,null);
        }
        if(cursor==null){
            assert db != null;
            db.close();

            return false;
        }
        if(cursor.getCount()==0){
            cursor.close();
            db.close();
            return false;
        }
        if(cursor.moveToFirst() &&cursor.getInt(0)!=userID){
            cursor.close();
            db.close();
            return false;
        }
        return true;
    }
}