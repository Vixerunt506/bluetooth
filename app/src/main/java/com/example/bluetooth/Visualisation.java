/**
 * This Visualisation class is responsible for displaying data in a graphical format.
 * It utilizes the LineGraphView to visualize the data in either Voltage (Vt) or Current (It) plots.
 */
package com.example.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Visualisation extends Activity {
    private TextView textView;
    private HashMap<Integer, String> batchData;
    private ArrayList<Integer> timeList;
    private Spinner spinner;
    private LineGraphView lineGraphView;
    private FrameLayout chartContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualisation);
        textView = findViewById(R.id.text);
        spinner = findViewById(R.id.spinner);
        chartContainer = findViewById(R.id.chartContainer);

        Intent intent = getIntent();
        if (intent == null) {
            Log.e("Visualisation", "Intent is null");
            return;
        }

        // Retrieve batch data and time list from Intent
        batchData = (HashMap<Integer, String>) intent.getSerializableExtra("batchData");
        timeList = (ArrayList<Integer>) intent.getSerializableExtra("timeList");

        // Set up the spinner for selecting plot type
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.plot_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Spinner item selection listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Display Vt plot
                        displayData(true);
                        break;
                    case 1:
                        // Display It plot
                        displayData(false);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void displayData(boolean isVtPlot) {
        ArrayList<Float> voltageList = new ArrayList<>();
        ArrayList<Float> currentList = new ArrayList<>();

        // Parse the batch data to extract voltage and current values
        for (String data : batchData.values()) {
            Pattern pattern = Pattern.compile("V\\d+: (.*?) V, I\\d+: (.*?) μA");
            Matcher matcher = pattern.matcher(data);
            while (matcher.find()) {
                try {
                    float voltage = Float.parseFloat(matcher.group(1));
                    float current = Float.parseFloat(matcher.group(2));

                    voltageList.add(voltage);
                    currentList.add(current);

                } catch (NumberFormatException e) {
                    Log.e("Visualisation", "Error parsing voltage or current", e);
                }
            }
        }

        if (isVtPlot) {
            lineGraphView = new LineGraphView(this, voltageList, timeList, true);
        } else {
            lineGraphView = new LineGraphView(this, currentList, timeList, false);
        }
        // Clear existing views in chart container and add the LineGraphView
        // If not, the lineGraphView will override the original layout including spinner
        chartContainer.removeAllViews();
        chartContainer.addView(lineGraphView);
    }
}


