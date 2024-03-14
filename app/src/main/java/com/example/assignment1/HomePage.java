package com.example.assignment1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomePage extends AppCompatActivity {
    public static final String TAG = HomePage.class.getSimpleName();
    private EditText personName;
    private EditText adults;
    private EditText children;
    private TextView total;
    private TextView error;
    private Button tripMaker;
    private int adultsNum =0;
    private int childrenNum=0;
    private Spinner citySpinner;

    @SuppressLint("MissingInflatedId")
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
        citySpinner = findViewById(R.id.cities);
        new FetchCitiesTask().execute();

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
            Intent intent = new Intent(HomePage.this, TripDetailsPage.class);

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

    private class FetchCitiesTask extends AsyncTask<Void, Void, List<String>> {
        private final String CITIES_URL = "https://raw.githubusercontent.com/darsh1206/Cities/main/cities.json";

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> cityNames = new ArrayList<>();
            HttpURLConnection connection = null;
            try {
                // connecting to an url of my github directory to find file of city names
                URL url = new URL(CITIES_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Log.d(TAG, "Connection established");

                InputStream in = new BufferedInputStream(connection.getInputStream());
                String response = convertStreamToString(in);

                JSONObject jsonObject = new JSONObject(response);
                JSONArray citiesArray = jsonObject.getJSONArray("cities");
                for (int i = 0; i < citiesArray.length(); i++) {
                    cityNames.add(citiesArray.getString(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return cityNames;
        }

        private String convertStreamToString(InputStream is) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }

        @Override
        protected void onPostExecute(List<String> cityNames) {
            super.onPostExecute(cityNames);

            // creating an adapter to deploy cities from cities.json file into my dropdown menu
            ArrayAdapter<String> adapter = new ArrayAdapter<>(HomePage.this,
                    android.R.layout.simple_spinner_item, cityNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            citySpinner.setAdapter(adapter);
            Log.d(TAG,"Added cities to dropdown menu");
        }
    }

}
