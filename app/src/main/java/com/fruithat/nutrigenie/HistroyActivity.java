package com.fruithat.nutrigenie;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.github.mikephil.charting.*;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;

public class HistroyActivity extends AppCompatActivity {
    private LineChart historyChart;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        historyChart = (LineChart) findViewById(R.id.chart);
        drawChart();
    }

    private void drawChart() {
        historyChart.setBackgroundColor(Color.WHITE);
        historyChart.setDrawBorders(true);
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 30));
        entries.add(new Entry(1, 10));
        LineDataSet dataSet = new LineDataSet(entries, "Calories"); //later enable legend
        LineData lineData = new LineData(dataSet);
        historyChart.setData(lineData);
        historyChart.invalidate();
        //addValues(entries, "Calories");

    }

    private void addValues(List<Entry>  entries, String label) {
        /* for(? data: ?){
            entries.add(new Entry(data.getValueX(), data.getValueY()));
        }*/

    }

}
