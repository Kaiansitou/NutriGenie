package com.fruithat.nutrigenie;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import sharefirebasepreferences.crysxd.de.lib.SharedFirebasePreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    int green = Color.rgb(5,205,110); // Green
    int red = Color.rgb(223,61,61); // Red

    private NutritionHistory nh;
    private NutritionInformation ni;

    private static final String TAG = "Statistics Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Button keepButton = findViewById(R.id.keep_button);
        Button discardButton = findViewById(R.id.discard_button);
        nh = NutritionHistory.getInstance();

        Intent intent = getIntent();
        Bundle bundleData = intent.getExtras();
        ni = bundleData.getParcelable("info");

        // Send scan information to firebase and return to main activity
        keepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nh.addNutritionInformation("real_scan", ni);
                Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Discard scan information and return to main activity
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        createChart();
    }

    private void createChart() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("account").child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               double caloriesNeeded = 2000L;
               for (DataSnapshot data : dataSnapshot.getChildren()) {
                   switch (data.getKey()) {
                       case "calories":
                           caloriesNeeded = Double.parseDouble(String.valueOf(data.getValue()));
                   }
               }

               NutrientConverter nc = new NutrientConverter(caloriesNeeded);

               // Row names
               ArrayList<String> names = new ArrayList<>();
               names.add("Potassium");
               names.add("Protein");
               names.add("Sugar");
               names.add("Fiber");
               names.add("Carbohydrates");
               names.add("Sodium");
               names.add("Cholesterol");
               names.add("Total Fat");
               names.add("Calories");

               String[] nutritionNames = names.stream().toArray(String[]::new);
               HorizontalBarChart stackedBarChart = findViewById(R.id.statistics_horizontal_stacked_barchart);


               BarChartBuilder barChartBuilder = new BarChartBuilder(stackedBarChart);
               barChartBuilder.changePreferences(nutritionNames);
               float percent;
               try {
                   percent = nc.convert("Calories", (float)ni.getCalories());
                   barChartBuilder.changeEntry(nutritionNames, "Calories", percent);
               } catch (NullPointerException e) {
                   Log.e(TAG, e.getMessage());
               }

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

               ArrayList<BarEntry> entries = barChartBuilder.getEntries();

               BarDataSet barDataSet = new BarDataSet(entries, "");
               barDataSet.setStackLabels(new String[]{
                       "% of Daily Value Consumed", "% of Daily Value Available", "Total % Consumed (Exceeded)"
               });

               barDataSet.setColors(green, red); // Set Stacked Bar Colors
               barDataSet.setValueTextSize(15f);
               barDataSet.setHighlightEnabled(false); // Turn off Bar Highlight when Selected
               barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

               BarData data = new BarData(barDataSet);
               data.setBarWidth(0.75f); // Width of Bars
               stackedBarChart.setData(data);

               stackedBarChart.getXAxis().setLabelCount(25);
               stackedBarChart.invalidate();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
        });
    }
}
