package com.example.assignment1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;


public class TripDetailsPage extends AppCompatActivity {
    public static final String TAG = TripDetailsPage.class.getSimpleName();  // For easy logging

    // UI Elements
    private String personName;
    private int adults =0;
    private int children =0;
    private TextView greeting;
    private SeekBar budgetBar;
    private TextView budget;
    private int budgetValue;
    private EditText departure, arrival;
    private TextView dateError;
    private Button backButton, nextButton;
    private Spinner mode;
    private String modeName;
    private TextView price;
    private Calendar calendar = Calendar.getInstance(); // Holds the current date and time
    private int[] modeRates = {100, 200, 1000, 2000}; // Price rates for different modes
    private TextView modeError;
    private int totalPrice =0;
    private String cityName;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Input Page");
        setContentView(R.layout.firstpage);

        // Display a welcoming Toast message
        Toast t = Toast.makeText(TripDetailsPage.this, "Lets begin planning your trip.", Toast.LENGTH_LONG);
        t.show();

        // Get trip details from the previous page (HomePage)
        personName = getIntent().getStringExtra("name");
        try {
            adults = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("adults")));
            children = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("children")));
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        cityName = getIntent().getStringExtra("cityName");

        // Greet the user by name
        greeting = findViewById(R.id.greet);
        greeting.setText(greetings(personName.split(" ")[0]));

        // Initialize Budget UI elements and listener
        budgetBar = findViewById(R.id.budgetBar);
        budget = findViewById(R.id.budget);

        // Initialize date picker UI Elements
        departure = findViewById(R.id.departureDate);
        arrival = findViewById(R.id.arrivalDate);
        dateError = findViewById(R.id.dateError);

        // Initialize back and next Buttons
        backButton = findViewById(R.id.backBtn);
        nextButton = findViewById(R.id.nextBtn);

        // Transportation Mode UI elements and listener
        mode = findViewById(R.id.mode);
        price = findViewById(R.id.price);
        modeError = findViewById(R.id.modeError);

        // Initialize the database
        TripDataBase db = new TripDataBase(this);

        // Handle actions for the "Next" button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (checkForDateErrors(departure.getText().toString(), arrival.getText().toString()) ||
//                        updateBudgetError(totalPrice, budgetValue)) {
//                    return; // Stop if there are errors
//                }

                int trip_id = db.addNewEntry(personName); // Create a new trip entry in the database
                // Add trip details to the database
                db.addNewEntry(trip_id, departure.getText().toString(), arrival.getText().toString());
                db.addNewEntry(trip_id, adults, children);
                db.addNewEntry(trip_id, budgetValue, modeName);

                // Start the next activity
                Intent intent = new Intent(TripDetailsPage.this, PreparationPage.class);
                intent.putExtra("trip_id", String.valueOf(trip_id));
                intent.putExtra("total_price", String.valueOf(totalPrice));
                intent.putExtra("cityName", cityName);
                startActivity(intent);
            }
        });

        // return to the homepage
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();// Close the current activity, returning to the previous one
            }
            });

        try {
            budgetValue = Integer.parseInt(budget.getText().toString());
        }
        catch (Exception e){
            Log.d(TAG,e.toString());
            budgetValue=0; // Handle potential errors and set a default budget value
        }

        // Changing budget value based on the progress bar
        budgetBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar budgetBar, int progress, boolean fromUser) {
                Log.d(TAG,"Changing budget value");
                budgetValue = progress*200;
                String finalBudget = String.valueOf(budgetValue);
                budget.setText(finalBudget);
                updateBudgetError(totalPrice,budgetValue);
            }
            @Override
            public void onStartTrackingTouch(SeekBar budgetBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar budgetBar) {
            }
        });

        // watch on every change of date prickers
        TextWatcher dateChecker = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG,"Date Changed");
                checkForDateErrors(departure.getText().toString(), arrival.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        // checking for date errors
        departure.addTextChangedListener(dateChecker);
        arrival.addTextChangedListener(dateChecker);

        // opening calendar dialogs to select a date
        departure.setOnClickListener(date -> showCalendar(departure));
        arrival.setOnClickListener(date -> showCalendar(arrival));

        mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the relevant price
                if(position==0){
                    return;
                }
                int priceValue = modeRates[position-1];

                switch (position){
                    case 1:
                        modeName="Bus";
                        break;
                    case 2:
                        modeName="Train";
                        break;
                    case 3:
                        modeName="Airplane";
                        break;
                    case 4:
                        modeName="Ship";
                        break;
                }
                // Update the prices
                totalPrice = priceValue*adults+(priceValue/2)*children;
                String priceCalc = "Ticket Rates:\n Adult: $"+priceValue+"  Child: $"+priceValue/2+"\nTotal Price: $"+(totalPrice);
                price.setText(priceCalc);
                try {
                    budgetValue = Integer.parseInt(budget.getText().toString());
                }
                catch (Exception e){
                    budgetValue=0;
                }
                updateBudgetError(totalPrice,budgetValue);
                Log.d(TAG,"Update Ticket Prices");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Checking budget errors
    private boolean updateBudgetError(int totalPrice, int budgetValue){
        try {
            budgetValue = Integer.parseInt(budget.getText().toString());
        }
        catch (Exception e){
            budgetValue=0;
        }
        if(totalPrice>budgetValue){
            modeError.setText("Total Price exceeds the budget value.");
            return true;
        }
        modeError.setText("");
        return false;
    }

    // Displaying Calendar
    private void showCalendar(EditText editor) {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog and set the initial date to the current date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Set the selected date in the EditText
                        editor.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

                    }
                }, year, month, dayOfMonth);

        // Show the DatePickerDialog
        datePickerDialog.show();
        Log.d(TAG,"Calendar opened");

    }

    // greet the user
    private String greetings(String personName){
        // get hour of the day
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greet;
        // greet the user according to the time
        if(hour>=4 && hour<12) {
            greet ="Good Morning";
        }
        else if (hour<18){
            greet = "Good Afternoon";
        }
        else if(hour<22){
            greet = "Good Evening";
        }
        else{
            greet = "Good Night";
        }
        Log.d(TAG,"Created the greetings");
        return greet+ " " + personName  + ",";

    }

    // checking Date errors
    private boolean checkForDateErrors(String departure, String arrival){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
        String today = formatter.format(new Date());

        Log.d(TAG,"Checking date errors");

        // Date Errors
        if(arrival.equals("") || departure.equals("")){
            dateError.setText("");
            return true;
        }
        else if(departure.compareTo(today)<=0){
            dateError.setText("Departure Date does not exist");
            return true;
        }
        else if(arrival.compareTo(today)<=0){
            dateError.setText("Arrival Date does not exist");
            return true;
        }
        else if(arrival.compareTo(departure) <= 0){
            dateError.setText("Arrival Date cannot be before departure Date");
            return true;
        }
        else{
            dateError.setText("");
        }
        return false;
    }

}

