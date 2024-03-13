package com.example.assignment1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment1.ui.theme.Task;

import java.util.ArrayList;
import java.util.List;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preppage);
        Log.d("TAG", "On Preparation List Page");

        // taking variables values
        title = findViewById(R.id.taskTitleIn);
        status = findViewById(R.id.status);
        description = findViewById(R.id.taskDescription);
        reset = findViewById(R.id.reset);
        add = findViewById(R.id.add);
        error = findViewById(R.id.taskError);

        listView = findViewById(R.id.task_list_view);
        adapter = new TaskAdapter(this, taskList);
        listView.setAdapter(adapter);


        reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                title.setText("");
                status.setSelection(0);
                description.setText("");
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
                    return;
                }
                Log.d(TAG,"Some Errors in inputs");
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
        return false;
    }
}
