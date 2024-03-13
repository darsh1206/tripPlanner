package com.example.assignment1;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

public class SummaryPage extends AppCompatActivity {

    public static final String TAG = SummaryPage.class.getSimpleName();
    private String departure;
    private String arrival;
    private String personName;
    private int adults=0;
    private int children =0;
    private int totalPrice;
    private String mode;
    private TextView summary;
    private List<Task> tasks;
    private LinearLayout checkboxContainer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "On Final Page");
        setContentView(R.layout.lastpage);

        // getting data from intent
        int trip_id=0;

        try {
            trip_id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("trip_id")));
            totalPrice = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("total_price")));
        }
        catch (Exception e){
            Log.d(TAG, e.toString());
        }
        TripDataBase db = new TripDataBase(this);
        // Fetching the summary Data
        String Data =  db.getData(trip_id);
        String [] dataParts = Data.split("_");

        // Fetching the tasks Data
        tasks = db.getTasks(trip_id);
        checkboxContainer = findViewById(R.id.checkboxContainer);

        try{
            personName = dataParts[0];
            departure = dataParts[1];
            arrival = dataParts[2];
            adults = Integer.parseInt(dataParts[3]);
            children = Integer.parseInt(dataParts[4]);
            mode = dataParts[6];
        }
        catch(Exception e){
            Log.d(TAG,e.toString());
        }

        // variables
        summary = findViewById(R.id.summaryPoints);
        putSummary();
        addTasks();
    }

    // This function generates the summary and displays it
    private void putSummary(){
        String finalSummary = "➡ Trip members: "+ adults +" Adults and "+children+" Children.\n➡ Departure Date: "+ departure + "\n➡ Arrival Date: "+ arrival + "\n➡ Travel Mode: "+ mode + "\n➡ Total Travelling Price: "+ totalPrice;
        summary.setText(finalSummary);
    }

    private void addTasks(){
        for (Task task : tasks) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(task.getTitle());
            checkBox.setChecked("Completed".equals(task.getStatus())); // Check the box if the task is completed
            checkboxContainer.addView(checkBox);

            // Optionally, set an OnClickListener to update the task status in your database when checked/unchecked
            checkBox.setOnClickListener(v -> {
                boolean isChecked = checkBox.isChecked();
                // Update the task status in your database
                // Example: db.updateTaskStatus(task.getId(), isChecked ? "Completed" : "Pending");
            });
        }
    }
}
