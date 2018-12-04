package com.fruithat.nutrigenie;


import android.util.Log;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import android.view.View;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

public class BarChartBuilder {
    //private HorizontalBarChart chart;
    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;
    private String[] allNutritionNames;

    public BarChartBuilder(HorizontalBarChart chart) {
        //chart = (HorizontalBarChart) chartView;
        entries = new ArrayList<>();
        labels = new ArrayList<>();

        allNutritionNames = new String[]{
                "Calcium",
                "Potassium",
                "Iron",
                "Folate",
                "Biotin",
                "Pantothenic Acid",
                "Niacin",
                "Riboflavin",
                "Thiamin",
                "Vitamin K",
                "Vitamin E",
                "Vitamin D",
                "Vitamin C",
                "Vitamin B12",
                "Vitamin B6",
                "Vitamin A",
                "Protein",
                "Sugar",
                "Fiber",
                "Carbohydrates",
                "Sodium",
                "Cholesterol",
                "Trans Fat",
                "Saturated Fat",
                "Total Fat"
        };

        addInitialEntries(allNutritionNames);
        setUpBarGraphDisplay(chart);
        setUpAxes(chart);
        setUpLegend(chart);
    }

    public ArrayList<BarEntry> getEntries() {
        return entries;
    }

    public void changeEntry(String[] preferences, String nutritionName, float value) {
        for (int i = 0; i < preferences.length; i++) {
            if (preferences[i] == nutritionName) {
                BarEntry dataEntry = entries.get(i);
                float x = dataEntry.getX();
                float[] y = dataEntry.getYVals();
                float y0 = y[0] + value;
                float y1 = y[1];

                if (y0 > 100) {
                    y1 = y0;
                    y0 = 0f;
                }
                Log.i("BarChart", String.valueOf(y0) + " / " + String.valueOf(y1));
                entries.remove(i);
                entries.add(i,new BarEntry(x, new float[]{y0, y1}));
                break;
            }
        }
    }

    private void addInitialEntries(String[] allNutritionNames) {

        for (int i = 0; i < allNutritionNames.length; i++) {
            entries.add(new BarEntry((float) i, new float[]{0f, 0f}));
            labels.add(allNutritionNames[i]);
        }
    }

    public void changePreferences(String[] preferences) {
        if (entries.size() > 0) {
            entries.clear();
        }
        if (labels.size() > 0) {
            labels.clear();
        }
        addInitialEntries(preferences);
    }

    private void setUpBarGraphDisplay(HorizontalBarChart chart) {
        chart.setDrawBarShadow(false); // remove bar shadow
        chart.getDescription().setEnabled(false); // remove description
        chart.setScaleEnabled(false);
        chart.setFitBars(true);
        chart.setDrawGridBackground(false); // No Grid Lines
        chart.animateY(2500); // Bars Animate on Start

        // Add Spacing Top & Bottom
        chart.setExtraTopOffset(10f);
        chart.setExtraBottomOffset((float)-(entries.size() * 200));
        chart.setExtraLeftOffset(20f);
        chart.setExtraLeftOffset(5f);
    }

    private void setUpAxes(HorizontalBarChart chart) {

        // Left X-axis
        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setTextSize(14.5f);
        xl.setGranularity(1f);

        // Replace Number Labels with String Labels
        xl.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return labels.get((int) value);
            }
        });

        // Left Y-axis
        YAxis yl = chart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f);
        yl.setTextSize(15f);

        // Right Y-Axis
        YAxis yr = chart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);
        yr.setTextSize(15f);
    }

    private void setUpLegend(HorizontalBarChart chart) {
        Legend l = chart.getLegend();
        l.setEnabled(false);
    }
}
