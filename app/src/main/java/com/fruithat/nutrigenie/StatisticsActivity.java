package com.fruithat.nutrigenie;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import sharefirebasepreferences.crysxd.de.lib.SharedFirebasePreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    int green = Color.rgb(5,205,110); //Green
    int light_gray = Color.rgb(220,220,220); //Light Gray
    int red = Color.rgb(223,61,61); //Red

    private Button keepButton;
    private Button discardButton;
    private NutritionHistory nh;

    private static final String TAG = "Statistics Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Log.i(TAG, "In statistics activity");

        NutrientConverter nc = new NutrientConverter(2000f);
        keepButton = findViewById(R.id.keep_button);
        discardButton = findViewById(R.id.discard_button);
        nh = NutritionHistory.getInstance();

        keepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //nh.addNutritionInformation();
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

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

        ArrayList<String> names = new ArrayList<>();
//        names.add("Calcium");
        names.add("Potassium");
//        names.add("Iron");
//        names.add("Calcium");
//        names.add("Folate");
//        names.add("Biotin");
//        names.add("Pantothenic Acid");
//        names.add("Niacin");
//        names.add("Riboflavin");
//        names.add("Thiamin");
//        names.add("Vitamin K");
//        names.add("Vitamin E");
//        names.add("Vitamin D");
//        names.add("Vitamin C");
//        names.add("Vitamin B12");
//        names.add("Vitamin B6");
//        names.add("Vitamin A");
        names.add("Protein");
        names.add("Sugar");
        names.add("Fiber");
        names.add("Carbohydrates");
        names.add("Sodium");
        names.add("Cholesterol");
//        names.add("Trans Fat");
//        names.add("Saturated Fat");
        names.add("Total Fat");

        String[] nutritionNames = names.stream().toArray(String[]::new);
        HorizontalBarChart stackedBarChart = findViewById(R.id.home_horizontal_stacked_barchart);

        Intent intent = getIntent();
        Bundle bundleData = intent.getExtras();
        NutritionInformation ni = bundleData.getParcelable("info");
        // check for null


        BarChartBuilder barChartBuilder = new BarChartBuilder(stackedBarChart);
        barChartBuilder.changePreferences(nutritionNames);
        float percent;
        try {
            percent = nc.convert("Sugar", (float)ni.getSugar());
            barChartBuilder.changeEntry(nutritionNames, "Sugar", percent);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            percent = nc.convert("Sodium", (float)ni.getSodium());
            barChartBuilder.changeEntry(nutritionNames, "Sodium", percent);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            percent = nc.convert("Protein", (float)ni.getProtein());
            barChartBuilder.changeEntry(nutritionNames, "Protein", percent);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            percent = nc.convert("Total Fat", (float)ni.getTotalFat());
            barChartBuilder.changeEntry(nutritionNames, "Total Fat", percent);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            percent = nc.convert("Trans Fat", (float)ni.getTransFat());
            barChartBuilder.changeEntry(nutritionNames, "Trans Fat", percent);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            percent = nc.convert("Saturated Fat", (float)ni.getSaturatedFat());
            barChartBuilder.changeEntry(nutritionNames, "Saturated Fat", percent);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            percent = nc.convert("Carbohydrates", (float)ni.getCarbohydrates());
            barChartBuilder.changeEntry(nutritionNames, "Carbohydrates", percent);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            percent = nc.convert("Fiber", (float)ni.getFiber());
            barChartBuilder.changeEntry(nutritionNames, "Fiber", percent);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            percent = nc.convert("Cholesterol", (float)ni.getCholesterol());
            barChartBuilder.changeEntry(nutritionNames, "Cholesterol", percent);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            percent = nc.convert("Potassium", (float)ni.getPotassium());
            barChartBuilder.changeEntry(nutritionNames, "Potassium", percent);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        ViewGroup.LayoutParams params = stackedBarChart.getLayoutParams();
        params.height = 1800;
        stackedBarChart.setLayoutParams(params);
        Log.i(TAG,String.valueOf(params.height));


        ArrayList<BarEntry> entries = barChartBuilder.getEntries();

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setStackLabels(new String[]{
                "% of Daily Value Consumed", "% of Daily Value Available", "Total % Consumed (Exceeded)"
        });

        barDataSet.setColors(green, light_gray, red); // Set Stacked Bar Colors
        barDataSet.setValueTextSize(15f);
        barDataSet.setHighlightEnabled(false); // Turn off Bar Highlight when Selected
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.75f); // Width of Bars
        stackedBarChart.setData(data);

        stackedBarChart.getXAxis().setLabelCount(25);//.setLabelCount(entries.size());

        // Enable Vertical Scrolling
        //stackedBarChart.setVisibleXRangeMaximum(8f);
        //stackedBarChart.moveViewTo(0,stackedBarChart.getAxisLeft().getAxisMaximum(), YAxis.AxisDependency.LEFT);

        //barDataSet.notifyDataSetChanged();
        //stackedBarChart.notifyDataSetChanged();
        stackedBarChart.invalidate();
    }
}
