package com.example.assignment1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TripDataBase extends SQLiteOpenHelper {
    public static final String TAG = TripDetailsPage.class.getSimpleName();
    // Database versions and name
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TripPlannerDB.db";
    public TripDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {
        // SQL statements to create required tables and logging it

        String PERSON_TRIP = "CREATE TABLE person_trip ("
                + "trip_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "person_name TEXT" + ")";
        db.execSQL(PERSON_TRIP);
        Log.d(TAG, "Created a new table with person id, name and trip id");

        String TRIP_DATES = "CREATE TABLE trip_dates ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "trip_id INTEGER,"
                + "departure_date TEXT,"
                + "arrival_date TEXT,"
                + "FOREIGN KEY (trip_id) REFERENCES person_trip(trip_id)" + ")";
        db.execSQL(TRIP_DATES);
        Log.d(TAG, "Created a new table of trip dates");

        String TRIP_MEMBERS = "CREATE TABLE trip_members ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "trip_id INTEGER,"
                + "adults INTEGER,"
                + "children INTEGER,"
                + "FOREIGN KEY (trip_id) REFERENCES person_trip(trip_id)" + ")";
        db.execSQL(TRIP_MEMBERS);
        Log.d(TAG, "Created a new table of trip members");

        String TRIP_DETAILS = "CREATE TABLE trip_details ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "trip_id INTEGER,"
                + "budget INTEGER,"
                + "mode TEXT,"
                + "FOREIGN KEY (trip_id) REFERENCES person_trip(trip_id)" + ")";
        db.execSQL(TRIP_DETAILS);
        Log.d(TAG, "Created a new table of trip details");
    }

    // Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS person_trip");
        db.execSQL("DROP TABLE IF EXISTS trip_dates");
        db.execSQL("DROP TABLE IF EXISTS trip_members");
        db.execSQL("DROP TABLE IF EXISTS trip_details");
        Log.d(TAG, "Deleted all tables");

        // Create tables again
        onCreate(db);
    }

    public int addNewEntry(String name) {
        // Open Database
        SQLiteDatabase writer = this.getWritableDatabase();
        Log.d(TAG, "Opened DataBase in write mode");

        // Created a new entry
        String query = "INSERT INTO person_trip (person_name) VALUES ('" + name +"')";

        // Executed a new entry
        writer.execSQL(query);
        Log.d(TAG, "Added new Trip Entry");

        // Close Database
        writer.close();
        Log.d(TAG, "Closed the write mode database");


        // Open Database
        SQLiteDatabase reader = this.getReadableDatabase();
        Log.d(TAG, "Opened DataBase in read mode");

        // creating and executing the query
        query = "SELECT trip_id FROM person_trip WHERE person_name ='" + name + "' ORDER BY trip_id DESC";
        Cursor cursor = reader.rawQuery(query, null);

        // fetching the id
        int id =0;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("trip_id");
            id = cursor.getInt(columnIndex);
            Log.d(TAG, "Fetched the trip id");
        }
        // closing database and cursor
        cursor.close();
        reader.close();
        Log.d(TAG, "Closed read mode DataBase");
        return id;
    }

    public void addNewEntry(int id, String departure, String arrival){
        // Open Database
        SQLiteDatabase writer = this.getWritableDatabase();
        Log.d(TAG, "Opened DataBase in write mode");

        // Created a new entry
        String query = "INSERT INTO trip_dates (trip_id, departure_date, arrival_date) VALUES (" + id + ", '" + departure + "', '"+ arrival +"')";

        // Executed a new entry
        writer.execSQL(query);
        Log.d(TAG, "Added new Trip Dates Entry");

        // Close Database
        writer.close();
        Log.d(TAG, "Closed the write mode database");
    }

    public void addNewEntry(int id, int adults, int children){
        // Open Database
        SQLiteDatabase writer = this.getWritableDatabase();
        Log.d(TAG, "Opened DataBase in write mode");

        // Created a new entry
        String query = "INSERT INTO trip_members (trip_id, adults, children) VALUES (" + id + ", " + adults + ", "+ children +")";

        // Executed a new entry
        writer.execSQL(query);
        Log.d(TAG, "Added new Trip Members Entry");

        // Close Database
        writer.close();
        Log.d(TAG, "Closed the write mode database");
    }

    public void addNewEntry(int id, int budget, String mode){
        // Open Database
        SQLiteDatabase writer = this.getWritableDatabase();
        Log.d(TAG, "Opened DataBase in write mode");

        // Created a new entry
        String query = "INSERT INTO trip_details (trip_id, budget, mode) VALUES (" + id + ", " + budget + ", '"+ mode +"')";

        // Executed a new entry
        writer.execSQL(query);
        Log.d(TAG, "Added new Trip Details Entry");

        // Close Database
        writer.close();
        Log.d(TAG, "Closed the write mode database");
    }

    @SuppressLint("Range")
    public String getData(int id){
        StringBuilder data = new StringBuilder();

        // Open Database
        SQLiteDatabase reader = this.getReadableDatabase();
        Log.d(TAG, "Opened DataBase in read mode");

        // creating and executing the query
        String query = "SELECT pt.person_name, td.departure_date, td.arrival_date, "
                + "tm.adults, tm.children, tdt.budget, tdt.mode "
                + "FROM person_trip pt "
                + "JOIN trip_dates td ON pt.trip_id = td.trip_id "
                + "JOIN trip_members tm ON pt.trip_id = tm.trip_id "
                + "JOIN trip_details tdt ON pt.trip_id = tdt.trip_id "
                + "WHERE pt.trip_id = " + id + ";";
        Cursor cursor = reader.rawQuery(query, null);

        // fetching the name
        String [] columns = {"person_name", "departure_date", "arrival_date", "adults", "children", "budget", "mode"};
        if (cursor.moveToFirst()) {
            for(String c: columns){
                data.append(cursor.getString(cursor.getColumnIndex(c)));
                if(!c.equals("mode")){
                    data.append("_");
                }
            }
        }
        Log.d(TAG, "Fetched the data");
        // closing database and cursor
        cursor.close();
        reader.close();
        Log.d(TAG, "Closed read mode DataBase");

        return data.toString();

    }
}
