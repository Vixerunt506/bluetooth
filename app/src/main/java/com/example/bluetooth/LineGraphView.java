/**
 * This class extends LineChart from the MPAndroidChart library to create a customized Line Graph view.
 * It accepts a list of y-axis values, a list of x-axis values, and a boolean flag to determine the plot type.
 */
package com.example.bluetooth;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class LineGraphView extends LineChart {
    private ArrayList<Float> yList;
    private ArrayList<Float> xList;
    private boolean isVtPlot;

    public LineGraphView(Context context, ArrayList<Float> yList, ArrayList<Float> xList, boolean isVtPlot) {
        super(context);
        this.yList = yList;
        this.xList = xList;
        this.isVtPlot = isVtPlot;

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < xList.size(); i++) {
            // turn your data into Entry objects
            entries.add(new Entry(xList.get(i), yList.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "10mM ferriferro in PBS");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        this.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "Time (s): " + value;
            }
        });

        this.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (isVtPlot) {
                    return "Voltage (V): " + value;
                } else {
                    return "Current (μA): " + value;
                }
            }
        });

        this.setExtraOffsets(10f, 10f, 10f, 10f);
        this.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        this.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        this.getDescription().setEnabled(false);

        LineData lineData = new LineData(dataSet);
        setData(lineData);
        invalidate(); // refresh
    }
}

