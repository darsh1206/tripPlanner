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
    public static final String TAG = PreparationPage.class.getSimpleName();
    private EditText title;
    private EditText description;
    private Spinner status;
    private Button reset;
    private Button add;
    private TextView error;
    private List<Task> taskList = new ArrayList<>();
    private TaskAdapter adapter;
    private ListView listView;
    private Button summaryPageBtn;
    private String cityName;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preppage);
        Log.d("TAG", "On Preparation List Page");

        // making a toast
        Toast t = Toast.makeText(PreparationPage.this, "Lets add some tasks for the trip.", Toast.LENGTH_LONG);
        t.show();

        // getting data from intent
        int trip_id =0;
        int totalPrice =0;
        cityName = getIntent().getStringExtra("cityName");
        try {

            trip_id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("trip_id")));
            totalPrice = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("total_price")));
        }
        catch (Exception e){
            Log.d(TAG,e.toString());
        }
        // taking variables values
        title = findViewById(R.id.taskTitleIn);
        status = findViewById(R.id.status);
        description = findViewById(R.id.taskDescription);
        reset = findViewById(R.id.reset);
        add = findViewById(R.id.add);
        error = findViewById(R.id.taskError);
        summaryPageBtn = findViewById(R.id.summaryPageBtn);
        listView = findViewById(R.id.task_list_view);
        adapter = new TaskAdapter(this, taskList);
        listView.setAdapter(adapter);
        TripDataBase db = new TripDataBase(this);

        Log.d("TAG", "Took input of all required variables");

        reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                title.setText("");
                status.setSelection(0);
                description.setText("");
                Log.d("TAG", "Reset the fields");
            }
        });

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // checking for input errors
                if(!checkTaskErrors()){
                    error.setText("");
                    Log.d(TAG,"No Errors in inputs");
                    // setting status value
                    int position = status.getSelectedItemPosition();
                    String statusVal="";
                    switch (position) {
                        case 1:
                            statusVal = "Pending";
                            break;
                        case 2:
                            statusVal = "In Progress";
                            break;
                        case 3:
                            statusVal = "Completed";
                            break;
                    }
                    Task newTask = new Task(title.getText().toString(), statusVal, description.getText().toString());
                    taskList.add(newTask);
                    adapter.notifyDataSetChanged();
                    Log.d("TAG", "Task added successfully");
                    return;
                }
                Log.d(TAG,"Some Errors in inputs");
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
        return false;
    }
}
