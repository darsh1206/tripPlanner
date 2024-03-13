package com.example.assignment1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.app.DatePickerDialog;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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


public class SecondActivity extends AppCompatActivity {
    public static final String TAG = SecondActivity.class.getSimpleName();
    private String personName;
    private int adults;
    private int children;
    private TextView greeting;
    private SeekBar budgetBar;
    private TextView budget;
    private int budgetValue;
    private EditText departure;
    private EditText arrival;
    private TextView dateError;
    private Button backButton;
    private Button nextButton;
    private Spinner mode;
    private String modeName;
    private TextView price;
    private Calendar calendar = Calendar.getInstance();
    private int[] modeRates={100,200,1000,2000};
    private TextView modeError;
    private int totalPrice =0;
    @SuppressLint("MissingInflatedId")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Input Page");
        setContentView(R.layout.firstpage);
        Toast t = Toast.makeText(SecondActivity.this, "Lets begin planning your trip.", Toast.LENGTH_LONG);
        t.show();

        // person name
        personName = getIntent().getStringExtra("name");
        // members
        try{
            adults = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("adults")));
        }
        catch (Exception e){
            adults =0;
        }
        try{
            children = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("children")));
        }
        catch (Exception e){
            children =0;
        }

        // Greet user
        greeting = findViewById(R.id.greet);
        greeting.setText(greetings(personName.split(" ")[0]));

        // Budget values
        budgetBar = findViewById(R.id.budgetBar);
        budget = findViewById(R.id.budget);

        // Date picker values
        departure = findViewById(R.id.departureDate);
        arrival = findViewById(R.id.arrivalDate);
        dateError = findViewById(R.id.dateError);

        // Buttons
        backButton = findViewById(R.id.backBtn);
        nextButton = findViewById(R.id.nextBtn);

        // Transportation Mode
        mode=findViewById(R.id.mode);
        price = findViewById(R.id.price);
        modeError = findViewById(R.id.modeError);

        TripDataBase db = new TripDataBase(this);

        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkForDateErrors(departure.getText().toString() ,arrival.getText().toString())||updateBudgetError(totalPrice,budgetValue)){
                    return;
                }

                int trip_id = db.addNewEntry(personName);
                db.addNewEntry(trip_id, departure.getText().toString(), arrival.getText().toString());
                db.addNewEntry(trip_id, adults,children);
                db.addNewEntry(trip_id, budgetValue, modeName);

                Intent it = getIntent();
                it = new Intent(SecondActivity.this, ThirdActivity.class);
                it.putExtra("adultsNum",String.valueOf(adults));
                it.putExtra("childrenNum", String.valueOf(children));
                it.putExtra("departure", departure.getText().toString());
                it.putExtra("arrival", arrival.getText().toString());
                it.putExtra("mode", modeName);
                it.putExtra("totalPrice", String.valueOf(totalPrice));
                it.putExtra("trip_id", String.valueOf(trip_id));
                startActivity(it);
            }
        });

        // return to the homepage
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
            });

        try {
            budgetValue = Integer.parseInt(budget.getText().toString());
        }
        catch (Exception e){
            budgetValue=0;
        }

        // Changing budget value based on the progress bar
        budgetBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar budgetBar, int progress, boolean fromUser) {
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

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

    }

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
        return greet+ " " + personName  + ",";
    }

    private boolean checkForDateErrors(String departure, String arrival){

        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
        String today = formatter.format(new Date());

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

