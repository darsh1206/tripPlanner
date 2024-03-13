package com.example.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Objects;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private EditText personName;
    private EditText adults;
    private EditText children;
    private TextView total;
    private TextView error;
    private Button tripMaker;
    private int adultsNum =0;
    private int childrenNum=0;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Application created");
        setContentView(R.layout.homepage);

        personName = findViewById(R.id.name);
        adults = findViewById(R.id.adults);
        children = findViewById(R.id.children);
        total = findViewById(R.id.total);
        error = findViewById(R.id.error);
        tripMaker = findViewById(R.id.tripPlanner);

        TextWatcher numAdder = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                calculateAndShowSum();
            }
        };
        adults.addTextChangedListener(numAdder);
        children.addTextChangedListener(numAdder);

        tripMaker.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                personName = findViewById(R.id.name);

                String[] nameParts = personName.getText().toString().split(" ");
                checkForErrors(personName.getText().toString(), nameParts);
            }
        });
    }

    private void calculateAndShowSum(){

        if(adults.getText().toString().equals("") || adults.getText().toString().equals("0")){
            adultsNum =0;
            error.setText("Adults field cannot be zero");
        }
        else {
            adultsNum = Integer.parseInt(adults.getText().toString());
            error.setText("");

        }
        if(children.getText().toString().equals("")){
            childrenNum=0;
        }
        else {
            childrenNum = Integer.parseInt(children.getText().toString());
        }
        int sum = adultsNum + childrenNum;
        total.setText(String.valueOf(sum));
    }

    private void checkForErrors(String name, String[] nameParts){
        if(name.length()==0){
            error.setText("Name cannot be empty");
        }
        else if(nameParts.length<2 || name.length()<6){
            error.setText("Invalid Input Name.");
        }

        else{
            error.setText("");

            // Create an Intent to start SecondActivity
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);

            adultsNum = Integer.parseInt(Objects.requireNonNull(adults.getText().toString()));
            childrenNum = Integer.parseInt(Objects.requireNonNull(children.getText().toString()));
            // Optionally, add data to pass to the second activity
            intent.putExtra("name", name);
            intent.putExtra("adults",String.valueOf(adultsNum));
            intent.putExtra("children", String.valueOf(childrenNum));
            // Start SecondActivity
            startActivity(intent);

        }
    }
}
