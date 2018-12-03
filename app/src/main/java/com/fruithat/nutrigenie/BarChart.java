package com.fruithat.nutrigenie;


import android.app.Activity;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import android.view.View;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import sharefirebasepreferences.crysxd.de.lib.SharedFirebasePreferences;

import java.util.ArrayList;

public class BarChart {
    private HorizontalBarChart chart;
    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;
    private String[] allNutritionNames;

    public BarChart(View view, Activity activity) {
        chart = (HorizontalBarChart) view;
        entries = new ArrayList<>();
        labels = new ArrayList<>();

        SharedFirebasePreferences preferences = SharedFirebasePreferences.getDefaultInstance(activity);
        ArrayList<String> names = new ArrayList<>();
        if (preferences.getBoolean("calcium", false)) names.add("Calcium");
        if (preferences.getBoolean("potassium", false)) names.add("Potassium");
        if (preferences.getBoolean("iron", false)) names.add("Iron");
        if (preferences.getBoolean("calcium", false)) names.add("Calcium");
        if (preferences.getBoolean("folate", false)) names.add("Folate");
        if (preferences.getBoolean("biotin", false)) names.add("Biotin");
        if (preferences.getBoolean("pantothenic_acid", false)) names.add("Pantothenic Acid");
        if (preferences.getBoolean("niacin", false)) names.add("Niacin");
        if (preferences.getBoolean("riboflavin", false)) names.add("Riboflavin");
        if (preferences.getBoolean("thiamin", false)) names.add("Thiamin");
        if (preferences.getBoolean("vitamin_k", false)) names.add("Vitamin K");
        if (preferences.getBoolean("vitamin_e", false)) names.add("Vitamin E");
        if (preferences.getBoolean("vitamin_d", false)) names.add("Vitamin D");
        if (preferences.getBoolean("vitamin_c", false)) names.add("Vitamin C");
        if (preferences.getBoolean("vitamin_b12", false)) names.add("Vitamin B12");
        if (preferences.getBoolean("vitamin_b6", false)) names.add("Vitamin B6");
        if (preferences.getBoolean("vitamin_a", false)) names.add("Vitamin A");
        if (preferences.getBoolean("protein", false)) names.add("Protein");
        if (preferences.getBoolean("sugar", false)) names.add("Sugar");
        if (preferences.getBoolean("fiber", false)) names.add("Fiber");
        if (preferences.getBoolean("carbohydrates", false)) names.add("Carbohydrates");
        if (preferences.getBoolean("sodium", false)) names.add("Sodium");
        if (preferences.getBoolean("cholesterol", false)) names.add("Cholesterol");
        if (preferences.getBoolean("trans_fat", false)) names.add("Trans Fat");
        if (preferences.getBoolean("saturated_fat", false)) names.add("Saturated Fat");
        if (preferences.getBoolean("total_fat", false)) names.add("Total Fat");
        allNutritionNames = (String[]) names.toArray();

        addInitialEntries(allNutritionNames);
        setUpBarGraphDisplay();
        setUpAxes();
        setUpLegend();
    }

    public HorizontalBarChart getChart() {
        return chart;
    }

    public ArrayList<BarEntry> getEntries() {
        return entries;
    }

    public void changeEntry() {

    }

    private void addInitialEntries(String[] allNutritionNames) {

        if (entries.size() == 0) {
            for (int i = 0; i < allNutritionNames.length; i++) {
                entries.add(new BarEntry((float)i,new float[]{0f,100f,0f}));
                labels.add(allNutritionNames[i]);
            }
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

    private void setUpBarGraphDisplay() {
        chart.setDrawBarShadow(false); // remove bar shadow
        chart.getDescription().setEnabled(false); // remove description
        chart.setScaleEnabled(false);
        chart.setFitBars(true);
        chart.setDrawGridBackground(false); // No Grid Lines
        chart.animateY(2500); // Bars Animate on Start

        // Add Spacing Top & Bottom
        chart.setExtraTopOffset(10f);
        chart.setExtraBottomOffset(10f);
        chart.setExtraLeftOffset(33f);
    }

    private void setUpAxes() {

        // Left X-axis
        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setTextSize(14.5f);

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

    private void setUpLegend() {
        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setDrawInside(false);
        l.setFormSize(15f);
        l.setTextSize(15f);
        l.setXEntrySpace(20f);
        l.setWordWrapEnabled(true);
    }
}
