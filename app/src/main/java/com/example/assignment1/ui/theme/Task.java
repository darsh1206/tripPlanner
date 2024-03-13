package com.example.assignment1.ui.theme;

import android.util.Log;

import com.example.assignment1.TripDetailsPage;

public class Task {
    public static final String TAG = TripDetailsPage.class.getSimpleName();
    private String title;
    private String status;
    private String description;
    private boolean isExpanded;

    public Task(String title, String status, String description){
        this.title = title;
        this.status = status;
        this.description = description;
        isExpanded=false;
        Log.d(TAG, "New Task created");
    }

    public String getTitle() {
        return title;
    }
    public String getStatus() {
        return status;
    }
    public String getDescription() {
        return description;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
