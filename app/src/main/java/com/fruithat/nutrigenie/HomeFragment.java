package com.fruithat.nutrigenie;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.*;
import android.widget.RelativeLayout;
import com.github.mikephil.charting.*;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.components.Legend;



import com.github.mikephil.charting.formatter.PercentFormatter;

import android.graphics.Color;

import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    public HomeFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout defined in fragment_home.xml
        // The last parameter is false because the returned view does not need to be attached to the container ViewGroup
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        HorizontalBarChart stackedBarChart = (HorizontalBarChart) view.findViewById(R.id.home_bar_chart);

        stackedBarChart.setDrawBarShadow(false);

        //stackedBarChart.setDrawValueAboveBar(true);

        stackedBarChart.getDescription().setEnabled(false);

        Legend l = stackedBarChart.getLegend();
        l.setEnabled(true);


        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setFormSize(15f);
        l.setTextSize(15f);
        l.setXEntrySpace(20f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setWordWrapEnabled(true);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, new float[] { 10f, 90f, 0f }));
        entries.add(new BarEntry(1f, new float[] { 0f, 0f, 150f}));
        entries.add(new BarEntry(2f, new float[] { 90f, 10f, 0f }));
        entries.add(new BarEntry(3f, new float[] { 7f, 93f, 0f }));
        entries.add(new BarEntry(4f, new float[] { 33f, 67f, 0f }));
        entries.add(new BarEntry(5f, new float[] { 55f, 45f, 0f }));
        entries.add(new BarEntry(6f, new float[] { 10f, 90f, 0f }));
        entries.add(new BarEntry(7f, new float[] { 77f, 23f, 0f }));
        entries.add(new BarEntry(8f, new float[] { 1f, 99f, 0f }));
        //Sodium, Dietary Fiber, Saturated Fat, Total Sugars, Total Fat, Cholesterol and Protein


        final int[] MY_COLORS = {
                Color.rgb(66,81,181), //Blue
                Color.rgb(220,220,220), //Light Gray
                Color.rgb(195,23,23) //Red
        };

        XAxis xl = stackedBarChart.getXAxis();
        xl.setPosition(XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Sodium");
        labels.add("Total Sugars");
        labels.add("Saturated Fat");
        labels.add("Calcium");
        labels.add("Carbohydrates");
        labels.add("Cholesterol");
        labels.add("Dietary Fiber");
        labels.add("Protein");
        labels.add("Iron");

        xl.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return labels.get((int) value);
            }

        });

        YAxis yl = stackedBarChart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f);
        stackedBarChart.setFitBars(true);


        YAxis yr = stackedBarChart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);
        stackedBarChart.setFitBars(true);
        stackedBarChart.animateY(2500);

        stackedBarChart.setScaleEnabled(false);
        stackedBarChart.setVisibleXRangeMaximum(labels.size()); // allow 20 values to be displayed at once on the x-axis, not more
        stackedBarChart.moveViewToX(5);
        BarDataSet set = new BarDataSet(entries, "");
        set.setStackLabels(new String[]{
                "% of Daily Value Consumed", "% of Daily Value Available", "Exceeded % of Daily Value"
        });
        set.setColors(MY_COLORS[0],MY_COLORS[1], MY_COLORS[2]);
        set.setValueTextSize(12f);
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        xl.setTextSize(13f);
        BarData data = new BarData(set);
        data.setBarWidth(0.75f);
        stackedBarChart.setExtraTopOffset(75f);
        stackedBarChart.setExtraBottomOffset(10f);
        stackedBarChart.setMaxVisibleValueCount(labels.size());

        set.setHighlightEnabled(false);
        stackedBarChart.setDrawGridBackground(false);
        stackedBarChart.setData(data);


        stackedBarChart.invalidate();
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't destroy Fragment on reconfiguration
        setRetainInstance(true);


    }


}
