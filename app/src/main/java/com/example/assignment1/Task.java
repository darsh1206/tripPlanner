package com.example.assignment1;

import android.util.Log;

import com.example.assignment1.TripDetailsPage;

public class Task {
    public static final String TAG = TripDetailsPage.class.getSimpleName();
    private String title;
    private String status;
    private String description;

    public Task(String title, String status, String description){
        this.title = title;
        this.status = status;
        this.description = description;
        Log.d(TAG, "New Task created");
    }

    // Methods to get Data
    public String getTitle() {
        return title;
    }
    public String getStatus() {
        return status;
    }
    public String getDescription() {
        return description;
    }

}
