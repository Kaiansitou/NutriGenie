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
                float y2 = y[2] - value;
                float y3 = y[3];

                if (y0 > 100) {
                    y3 = y0;
                    y0 = 0f;
                    y2 = 0f;
                } else if (y[0] == 0 && y[2] == 0) {
                    y3 += value;
                    y0 = 0;
                    y2 = 0;
                }
                entries.remove(i);
                entries.add(i,new BarEntry(x, new float[]{y0, y[1], y2, y3, y[4]}));

                //Log.i("entries", String.valueOf(y0) + "/" + String.valueOf(y1) + "/" + String.valueOf(y2));
                break;
            }
        }
    }

    public void ScanEntry(String[] preferences, String nutritionName, float value) {
        for (int i = 0; i < preferences.length; i++) {
            if (preferences[i] == nutritionName) {
                BarEntry dataEntry = entries.get(i);
                float x = dataEntry.getX();
                float[] y = dataEntry.getYVals();
                float y0 = y[0];
                float y1 = value;
                float y2 = y[2] - value;
                float y3 = y[3];
                float y4 = y[4];

                if (y[0] + value > 100) {
                    y3 = y[0];
                    y0 = 0f;
                    y1 = 0f;
                    y4 = value;
                    y2 = 0f;
                }

                entries.remove(i);
                entries.add(i,new BarEntry(x, new float[]{y0, y1, y2, y3, y4}));


                break;
            }
        }
    }

    private void addInitialEntries(String[] allNutritionNames) {

        for (int i = 0; i < allNutritionNames.length; i++) {
            entries.add(new BarEntry((float)i,new float[]{0f,0f,100f,0f,0f}));
            labels.add(allNutritionNames[i]);
        }
        Log.i("data_bar", "names: "+String.valueOf(allNutritionNames.length));
        Log.i("data_bar", "labels: "+String.valueOf(labels.size()));
        Log.i("data_bar", "entries: "+String.valueOf(entries.size()));
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
        chart.setExtraBottomOffset(-1800f);
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
        yl.setDrawLabels(false);

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
        /*l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setDrawInside(false);
        l.setFormSize(15f);
        l.setTextSize(15f);
        l.setXEntrySpace(20f);
        l.setWordWrapEnabled(true);
        l.setYOffset(-100f);*/
    }
}
