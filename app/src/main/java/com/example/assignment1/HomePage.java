/*
    File			: HomePage.java
    Programmer		: Darsh Patel
    Assignment		: Assignment 2
    First Version	: 2024-03-14
    student ID		: 880657
    Description		: This is a homepage for Trip Planner application
*/
package com.example.assignment1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.core.app.ActivityCompat;

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
    private TextView link;

    private static final String[] PERMISSIONS = {
            Manifest.permission.POST_NOTIFICATIONS
    };
    @SuppressLint("MissingInflatedId")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Application created"); // Logs when the app is created
        setContentView(R.layout.homepage); // Sets the layout for the activity

        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(HomePage.this, Manifest.permission.POST_NOTIFICATIONS);

        if (!shouldShowRationale) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 100);
            Toast.makeText(this, "Permissions are accepted", Toast.LENGTH_LONG).show();
        }


        // Initialize UI elements
        personName = findViewById(R.id.name);
        adults = findViewById(R.id.adults);
        children = findViewById(R.id.children);
        total = findViewById(R.id.total);
        error = findViewById(R.id.error);
        tripMaker = findViewById(R.id.tripPlanner);
        citySpinner = findViewById(R.id.cities);
        link = findViewById(R.id.visiter);
        Log.d(TAG,"Attributes assigned successfully");

        // Fetch cities from the JSON file and load them into the spinner
        new FetchCitiesTask().execute();

        // TextWatcher to update trip calculation when "adults" or "children" fields change
        TextWatcher numAdder = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG,"Updating total sum");
                calculateAndShowSum();
            }
        };
        adults.addTextChangedListener(numAdder);
        children.addTextChangedListener(numAdder);

        // OnClickListener for the "tripMaker" button
        tripMaker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG,"Trip Maker Button Clicked");
                personName = findViewById(R.id.name);
                String[] nameParts = personName.getText().toString().split(" ");
                checkForErrors(personName.getText().toString(), nameParts);
            }
        });

        // Listener for city spinner to show/hide Wikipedia link
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    link.setVisibility(View.GONE);
                    Log.d(TAG, "Set the link invisible");
                } else {
                    link.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Set the link visible");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                link.setVisibility(View.GONE);
                Log.d(TAG, "Set the link invisible as nothing is selected");
            }
        });

        // Listener for the Wikipedia link (if visible)
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayAdapter<String> adapter = (ArrayAdapter<String>) citySpinner.getAdapter();
                String cityName = adapter.getItem(citySpinner.getSelectedItemPosition());
                cityName = cityName.split(",")[0];

                // starting the intent for google browsing
                Log.d(TAG,"Browsing to wikipedia for city: " + cityName);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/"+cityName));
                startActivity(intent);
            }
        });
    }

    // Helper function to calculate the total number of people for the trip
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

    // Helper function to validate user input and start the next activity
    private void checkForErrors(String name, String[] nameParts){
        if(name.length()==0){
            error.setText("Name cannot be empty");
        }
        else if(nameParts.length<2 || name.length()<6){
            error.setText("Invalid Input Name.");
        }
        else if(citySpinner.getSelectedItemPosition()==0){
            error.setText("Please Select a city");
        }
        else{
            error.setText("");

            // Create an Intent to start SecondActivity
            Intent intent = new Intent(HomePage.this, TripDetailsPage.class);

            try {
                adultsNum = Integer.parseInt(Objects.requireNonNull(adults.getText().toString()));
            }
            catch (Exception e){
                error.setText("Adults cannot be zero");
                return;
            }
            try {
                childrenNum = Integer.parseInt(Objects.requireNonNull(children.getText().toString()));
            }
            catch (Exception e){
                childrenNum=0;
            }
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) citySpinner.getAdapter();
            String cityName = adapter.getItem(citySpinner.getSelectedItemPosition());

            // Optionally, add data to pass to the second activity
            intent.putExtra("name", name);
            intent.putExtra("cityName", cityName);
            intent.putExtra("adults",String.valueOf(adultsNum));
            intent.putExtra("children", String.valueOf(childrenNum));
            // Start SecondActivity
            Log.d(TAG,"Moving to Trip Input Details Page");
            startActivity(intent);

        }
    }

    // AsyncTask for fetching cities from JSON and loading them into citySpinner
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

                // getting data from file
                InputStream in = new BufferedInputStream(connection.getInputStream());
                String response = convertStreamToString(in);
                Log.d(TAG, "Reading Data");

                // parsing the json data
                JSONObject jsonObject = new JSONObject(response);
                JSONArray citiesArray = jsonObject.getJSONArray("cities");
                for (int i = 0; i < citiesArray.length(); i++) {
                    cityNames.add(citiesArray.getString(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    // disconnecting the connection
                    connection.disconnect();
                    Log.d(TAG, "Disconnected");
                }
            }
            return cityNames;
        }

        // function to convert the buffer stream data into normal String
        private String convertStreamToString(InputStream is) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            Log.d(TAG,"Converting Data into String");
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
