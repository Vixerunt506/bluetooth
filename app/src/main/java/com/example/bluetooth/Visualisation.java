/**
 * Developing
 * This Visualisation class is responsible for displaying data in a graphical format using MPAndroidChart library.
 */
package com.example.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;

public class Visualisation extends Activity {
    private LineChart lineChart;
    private HashMap<Integer, String> batchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualisation);
        lineChart = findViewById(R.id.chart);

        // Get the data from Intent
        Intent intent = getIntent();
        if (intent == null) {
            Log.e("Visualisation", "Intent is null");
            return;
        }

        batchData = (HashMap<Integer, String>) intent.getSerializableExtra("batchData");

        // Update the LineChart with the batch data
        updateLineChart();
    }

    private void updateLineChart() {
        // Create an ArrayList of ILineDataSet for holding multiple LineDataSets
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        for (Integer batch : batchData.keySet()) {
            String parsedData = batchData.get(batch);
            if (parsedData == null) {
                Log.e("Visualisation", "No data found for batch " + batch);
                continue;
            }

            String[] lines = parsedData.split("\n");
            ArrayList<Entry> entries = new ArrayList<>();
            for (String line : lines) {
                String[] parts = line.split(", ");
                if (parts.length != 2) {
                    Log.e("Visualisation", "Invalid data format: " + line);
                    continue;
                }

                String[] values = parts[1].split(": ");
                if (values.length != 2) {
                    Log.e("Visualisation", "Invalid value format: " + parts[1]);
                    continue;
                }

                try {
                    float x = Float.parseFloat(values[0].replace("μA", ""));
                    float y = Float.parseFloat(values[1].replace("V", ""));
                    entries.add(new Entry(x, y));
                } catch (NumberFormatException e) {
                    Log.e("Visualisation", "NumberFormatException: " + e.getMessage());
                }
            }

            // A LineDataSet for this batch
            LineDataSet dataSet = new LineDataSet(entries, "Batch " + batch);
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);
            dataSet.setDrawValues(false); // Disable drawing values on data points

            // Add LineDataSet to the ArrayList of dataSets
            dataSets.add(dataSet);
        }

        // A LineData object from the dataSets
        LineData lineData = new LineData(dataSets);

        // Set up the LineChart
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(Color.WHITE);

        // Legend
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        legend.setTextColor(Color.BLACK);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        // X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);

        // Y-axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextSize(11f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Refresh the chart
        lineChart.invalidate();
    }
}
