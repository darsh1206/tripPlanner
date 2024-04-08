package com.example.assignment1;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CityFetchService extends IntentService {
    private static final String TAG = "CityFetchService";

    public CityFetchService() {
        super("CityFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Fetch cities from JSON
        List<String> cityNames = fetchCities();

        // Broadcast the fetched city names
        Intent broadcastIntent = new Intent("com.example.assignment1.CITY_FETCHED");
        broadcastIntent.putStringArrayListExtra("cityNames", (ArrayList<String>) cityNames);
        sendBroadcast(broadcastIntent);
    }

    private List<String> fetchCities() {
        List<String> cityNames = new ArrayList<>();
        HttpURLConnection connection = null;
        try {
            // URL for fetching city names
            URL url = new URL("https://raw.githubusercontent.com/darsh1206/Cities/main/cities.json");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read data from InputStream
            InputStream in = new BufferedInputStream(connection.getInputStream());
            String response = convertStreamToString(in);

            // Parse JSON response
            JSONObject jsonObject = new JSONObject(response);
            JSONArray citiesArray = jsonObject.getJSONArray("cities");
            for (int i = 0; i < citiesArray.length(); i++) {
                cityNames.add(citiesArray.getString(i));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching cities: " + e.getMessage());
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
}
