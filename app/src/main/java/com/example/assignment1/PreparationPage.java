package com.example.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PreparationPage extends AppCompatActivity {
    public static final String TAG = PreparationPage.class.getSimpleName(); // Tag for logging

    // UI Elements
    private EditText title, description;
    private Spinner status;
    private Button reset, add, summaryPageBtn;
    private TextView error;
    private ListView listView;

    // Data Structures and Adapter
    private List<Task> taskList = new ArrayList<>();
    private TaskAdapter adapter;

    // Trip Details
    private String cityName;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preppage);
        Log.d("TAG", "On Preparation List Page");

        // Display welcome Toast
        Toast t = Toast.makeText(PreparationPage.this, "Lets add some tasks for the trip.", Toast.LENGTH_LONG);
        t.show();

        // Get trip data from the intent
        int trip_id = 0;
        int totalPrice = 0;
        cityName = getIntent().getStringExtra("cityName");
        try {
            trip_id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("trip_id")));
            totalPrice = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("total_price")));
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        // Initialize UI elements
        title = findViewById(R.id.taskTitleIn);
        status = findViewById(R.id.status);
        description = findViewById(R.id.taskDescription);
        reset = findViewById(R.id.reset);
        add = findViewById(R.id.add);
        error = findViewById(R.id.taskError);
        summaryPageBtn = findViewById(R.id.summaryPageBtn);
        listView = findViewById(R.id.task_list_view);

        // Set up the TaskAdapter and connect it to the ListView
        adapter = new TaskAdapter(this, taskList);
        listView.setAdapter(adapter);

        // Initialize the database
        TripDataBase db = new TripDataBase(this);

        Log.d("TAG", "Took input of all required variables");

        // 'Reset' button functionality
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");      // Clear the title field
                status.setSelection(0); // Reset the status spinner
                description.setText(""); // Clear the description field
                Log.d("TAG", "Reset the fields");
            }
        });

        // 'Add' button functionality
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTaskErrors()) { // Check for errors in the new task
                    error.setText("");
                    Log.d(TAG, "No Errors in inputs");

                    // Get the status value
                    int position = status.getSelectedItemPosition();
                    String statusVal = "";
                    switch (position) {
                        case 1:  statusVal = "Pending"; break;
                        case 2:  statusVal = "In Progress"; break;
                        case 3:  statusVal = "Completed"; break;
                    }

                    // Create a new task and add it to the list
                    Task newTask = new Task(title.getText().toString(), statusVal, description.getText().toString());
                    taskList.add(newTask);
                    adapter.notifyDataSetChanged(); // Update the ListView

                    Toast t = Toast.makeText(PreparationPage.this, "Task " + title.getText().toString()+ " added", Toast.LENGTH_LONG);
                    t.show();
                    Log.d("TAG", "Task added successfully");


                    title.setText("");      // Clear the title field
                    status.setSelection(0); // Reset the status spinner
                    description.setText(""); // Clear the description field

                } else {
                    Log.d(TAG, "Some Errors in inputs");
                }

            }
        });

        int finalTrip_id = trip_id;
        int finalTotalPrice = totalPrice;
        summaryPageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreparationPage.this, SummaryPage.class);

                for(Task task: taskList){
                    db.addNewEntry(finalTrip_id, task.getTitle(),task.getStatus(),task.getDescription());
                }
                intent.putExtra("trip_id", String.valueOf(finalTrip_id));
                intent.putExtra("total_price", String.valueOf(finalTotalPrice));
                intent.putExtra("cityName", cityName);
                startActivity(intent);
            }
        });
    }

    // checking task errors
    private boolean checkTaskErrors(){
        if(title.getText().toString().equals("")||description.getText().toString().equals("")||status.getSelectedItemPosition()==0){
            error.setText("Please fill out all fields of a new task");
            return true;
        }
        else if(title.getText().toString().length()>40){
            error.setText("Title should be under 40 characters");
            return true;
        }
        else if(description.getText().toString().length()>200){
            error.setText("Description should be under 200 characters");
            return true;
        }
        for(Task task:taskList){
            if(title.getText().toString().equals(task.getTitle())){
                error.setText("Titles cannot be same");
                return true;
            }
        }
        Log.d(TAG,"Checking Task Errors");
        return false;
    }
}
