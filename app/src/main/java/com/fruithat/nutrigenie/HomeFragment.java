package com.fruithat.nutrigenie;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import sharefirebasepreferences.crysxd.de.lib.SharedFirebasePreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HomeFragment extends Fragment {
    String TAG = "Home Fragment";
    int green = Color.rgb(5, 205, 110); //Green
    int light_gray = Color.rgb(220, 220, 220); //Light Gray
    int red = Color.rgb(223, 61, 61); //Red

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout defined in fragment_home.xml
        // The last parameter is false because the returned view does not need to be attached to the container ViewGroup
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
                    for (Long k : nutritionInformation.keySet()) {
                        Log.i(TAG, "Value: " + String.valueOf(nutritionInformation.get(k).getCalcium()));
                    }
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SharedFirebasePreferences preferences = SharedFirebasePreferences.getDefaultInstance(getActivity());
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

        String[] nutritionNames = names.stream().toArray(String[]::new);
        Log.i(TAG, String.valueOf(nutritionNames.length));
        HorizontalBarChart stackedBarChart = view.findViewById(R.id.home_horizontal_stacked_barchart);

        BarChartBuilder barChartBuilder = new BarChartBuilder(stackedBarChart);
        barChartBuilder.changePreferences(nutritionNames);

        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        NutritionHistory.getInstance().getNutritionInformation(cal.getTime(), new Date(), nutritionInformation -> {
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

                    NutritionInformation.NutritionInformationBuilder builder = new NutritionInformation.NutritionInformationBuilder();
                    NutritionInformation current = builder.build();

                    for (NutritionInformation info : nutritionInformation.values()) {
                        current = builder
                                .calories(current.getCalories() + info.getCalories())
                                .cholesterol(current.getCholesterol() + info.getCholesterol())
                                .fiber(current.getFiber() + info.getFiber())
                                .potassium(current.getPotassium() + info.getPotassium())
                                .protein(current.getProtein() + info.getProtein())
                                .saturatedFat(current.getSaturatedFat() + info.getSaturatedFat())
                                .sodium(current.getSodium() + info.getSodium())
                                .sugar(current.getSugar() + info.getSugar())
                                .totalFat(current.getTotalFat() + info.getTotalFat())
                                .transFat(current.getTransFat() + info.getTransFat())
                                .build();
                    }

                    barChartBuilder.changeEntry(nutritionNames, "Potassium", (float) (current.getPotassium() / (caloriesNeeded / 2000 * 3500)));
                    barChartBuilder.changeEntry(nutritionNames, "Protein", (float) (current.getProtein() / (caloriesNeeded / 2000 * 50)));
                    barChartBuilder.changeEntry(nutritionNames, "Sugar", 100f + (float) current.getSugar());
                    barChartBuilder.changeEntry(nutritionNames, "Fiber", (float) (current.getFiber() / (caloriesNeeded / 2000 * 25)));
                    barChartBuilder.changeEntry(nutritionNames, "Carbohydrates", (float) (current.getCarbohydrates() / (caloriesNeeded / 2000 * 300)));
                    barChartBuilder.changeEntry(nutritionNames, "Sodium", (float) (current.getSodium() / (caloriesNeeded / 2000 * 2400)));
                    barChartBuilder.changeEntry(nutritionNames, "Cholesterol", (float) (current.getCholesterol() / (caloriesNeeded / 2000 * 300)));
                    barChartBuilder.changeEntry(nutritionNames, "Trans Fat", 100f + (float) current.getTransFat());
                    barChartBuilder.changeEntry(nutritionNames, "Saturated Fat", (float) (current.getSaturatedFat() / (caloriesNeeded / 2000 * 20)));
                    barChartBuilder.changeEntry(nutritionNames, "Total Fat", (float) (current.getTotalFat() / (caloriesNeeded / 2000 * 65)));

                    stackedBarChart.invalidate();

                    PieChart pieChart = makePieChart(
                            view.findViewById(R.id.home_piechart),
                            caloriesNeeded,
                            current.getCalories());

                    pieChart.invalidate();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        LayoutParams params = stackedBarChart.getLayoutParams();
        params.height = 1800;
        stackedBarChart.setLayoutParams(params);
        Log.i(TAG, String.valueOf(params.height));


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


        PieChart pieChart = makePieChart(
                view.findViewById(R.id.home_piechart),
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
        pieChart.setExtraTopOffset(20f);
        pieChart.setExtraBottomOffset(20f);
        pieChart.setExtraLeftOffset(30f);
        pieChart.setExtraRightOffset(30f);

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
        pieDataSet.setValueLinePart1Length(0.3f);
        pieDataSet.setValueLinePart2Length(0.1f);
        pieDataSet.setValueFormatter(new PercentFormatter());
        pieDataSet.setValueTextSize(17.5f);
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
