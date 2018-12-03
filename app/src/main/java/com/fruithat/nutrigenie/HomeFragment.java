package com.fruithat.nutrigenie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;

import com.github.mikephil.charting.formatter.*;

import android.util.Log;

import com.github.mikephil.charting.formatter.PercentFormatter;

import android.graphics.Color;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.ParseException;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeFragment extends Fragment {
    String TAG = "Home Fragment";
    int green = Color.rgb(5,205,110); //Green
    int light_gray = Color.rgb(220,220,220); //Light Gray
    int red = Color.rgb(223,61,61); //Red
    public HomeFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout defined in fragment_home.xml
        // The last parameter is false because the returned view does not need to be attached to the container ViewGroup
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:m:ss"); // here set the pattern as you date in string was containing like date/month/year

        try {
            Date startTime = sdf.parse(date + " 00:00:00");
            Date endTime = sdf.parse(date + " 23:59:59");
            NutritionHistory instance = NutritionHistory.getInstance();
            NutritionInformation.NutritionInformationBuilder nb = new NutritionInformation.NutritionInformationBuilder()
                    .calcium(1.0)
                    .carbohydrates(8.0)
                    .fiber(17.0)
                    .vitaminA(1.0)
                    .vitaminC(14.0)
                    .iron(1.0)
                    .calcium(1.0)
                    .servingSize(1.0)
                    .servingsPerContainer(1.0)
                    .servingType("Cup");
            NutritionInformation n = nb.build();

            Log.i(TAG, "Data added");
            //instance.addNutritionInformation("Apple",n);
            instance.getNutritionInformation(startTime, endTime, new NutritionHistoryCallback() {
                @Override
                public void onDataReceived(HashMap<Long, NutritionInformation> nutritionInformation) {
                    for (Long k: nutritionInformation.keySet()) {
                        Log.i(TAG, "Value: " + String.valueOf(nutritionInformation.get(k).getCalcium()));
                    }
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }

        BarChart stackedBarChart = new BarChart(view.findViewById(R.id.home_bar_chart), getActivity());
        //stackedBarChart.changePreferences(new String[]{"Trans Fat",
        //        "Saturated Fat",
        //        "Total Fat"});
        //stackedBarChart.increaseNutritionEntryValue();
        HorizontalBarChart chart = stackedBarChart.getChart();
        ArrayList<BarEntry> entries = stackedBarChart.getEntries();
        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setStackLabels(new String[]{
                "% of Daily Value Consumed", "% of Daily Value Available", "Total % Consumed (Exceeded)"
        });

        barDataSet.setColors(green, light_gray, red); // Set Stacked Bar Colors
        barDataSet.setValueTextSize(15f);
        barDataSet.setHighlightEnabled(false); // Turn off Bar Highlight when Selected
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.75f); // Width of Bars\
        chart.setData(data);

        chart.getXAxis().setLabelCount(entries.size());

        // Enable Vertical Scrolling
        chart.setVisibleXRangeMaximum(8f);
        chart.moveViewTo(0,chart.getAxisLeft().getAxisMaximum(), YAxis.AxisDependency.LEFT);
        chart.invalidate();


        PieChart pieChart = makePieChart(
                view.findViewById(R.id.total_calorie_pie_chart),
                2000.0,
                567.0);
        pieChart.invalidate();

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't destroy Fragment on reconfiguration
        setRetainInstance(true);


    }

    public PieChart makePieChart(View view, double totalCaloriesAvailable, double totalCaloriesConsumed) {
        PieChart pieChart = (PieChart) view;

        // Center Circle Display
        pieChart.setHoleRadius(80f);
        pieChart.setCenterText("Calories\n" + String.valueOf((int) totalCaloriesConsumed) + "/" + String.valueOf((int) totalCaloriesAvailable));
        pieChart.setCenterTextSize(17f);
        pieChart.setCenterTextColor(Color.GRAY);
        pieChart.setTransparentCircleRadius(85f);

        // Display Margins
        pieChart.setExtraTopOffset(10f);
        pieChart.setExtraBottomOffset(20f);
        pieChart.setExtraLeftOffset(25f);
        pieChart.setExtraRightOffset(25f);

        pieChart.getLegend().setEnabled(false); // Disable Legend
        pieChart.setDescription(null); // Disable Description
        pieChart.setDrawSliceText(false); // Disable Text Label
        pieChart.setRotationEnabled(false); // Disable Rotation of Chart

        ArrayList<PieEntry> pieData = new ArrayList<>();
        if (totalCaloriesConsumed > totalCaloriesAvailable) {
            pieData.add(new PieEntry((float) (totalCaloriesConsumed / totalCaloriesAvailable) * 100f, "Calories Consumed (Exceeded)"));
        } else {
            pieChart.setUsePercentValues(true); // Convert to Percentage Format
            pieData.add(new PieEntry((float) (totalCaloriesAvailable - totalCaloriesConsumed), "Calories Available"));
            pieData.add(new PieEntry((float) totalCaloriesConsumed, "Calories Consumed"));
        }

        PieDataSet pieDataSet = new PieDataSet(pieData, "");

        // Make Value Labels Outside Pie Chart
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1Length(0.1f);
        pieDataSet.setValueLinePart2Length(0.5f);
        pieDataSet.setValueFormatter(new PercentFormatter());
        pieDataSet.setValueTextSize(15f);
        pieDataSet.setValueTextColor(Color.BLACK);

        if (totalCaloriesConsumed > totalCaloriesAvailable) {
            pieDataSet.setColors(red);
        } else {
            pieDataSet.setColors(light_gray, green);
        }

        PieData data = new PieData(pieDataSet);
        pieChart.setData(data);

        return pieChart;
    }
}
