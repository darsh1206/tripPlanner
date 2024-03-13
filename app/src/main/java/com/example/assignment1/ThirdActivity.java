package com.example.assignment1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

public class ThirdActivity extends AppCompatActivity {

    public static final String TAG = SecondActivity.class.getSimpleName();
    private String departure;
    private String arrival;
    private String personName;
    private int adults=0;
    private int children =0;
    private int totalPrice =0;
    private String mode;
    private TextView summary;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "On Final Page");
        setContentView(R.layout.lastpage);
        int trip_id;

        trip_id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("trip_id")));
        TripDataBase db = new TripDataBase(this);
        String Data =  db.getData(trip_id);
        String [] dataParts = Data.split("_");

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

        // values
//        try {
//            adults = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("adultsNum")));
//            children = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("childrenNum")));
//            departure = getIntent().getStringExtra("departure");
//            arrival = getIntent().getStringExtra("arrival");
//            mode = getIntent().getStringExtra("mode");
//            totalPrice = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("totalPrice")));
//
//        }
//        catch (Exception e){
//
//        }


        // variables
        summary = findViewById(R.id.summaryPoints);
        putSummary();

    }

    // This function generates the summary and displays it
    private void putSummary(){
        String finalSummary = "➡ Trip members: "+ adults +" Adults and "+children+" Children.\n➡ Departure Date: "+ departure + "\n➡ Arrival Date: "+ arrival + "\n➡ Travel Mode: "+ mode + "\n➡ Total Travelling Price: "+ 0;
        summary.setText(finalSummary);
    }
}
