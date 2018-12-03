package com.fruithat.nutrigenie;


import com.github.mikephil.charting.charts.HorizontalBarChart;
import android.view.View;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

public class BarChart {
    private HorizontalBarChart chart;
    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;
    private String[] allNutritionNames;

    public BarChart(View view) {
        chart = (HorizontalBarChart) view;
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
