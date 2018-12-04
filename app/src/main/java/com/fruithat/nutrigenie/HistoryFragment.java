package com.fruithat.nutrigenie;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private LineChart historyChart;
    private EditText startDate;
    private EditText endDate;
    private TextView error;
    Calendar myCalendar = null;
    Calendar myCalendar2 = null;
    String myFormat = "MM/dd/yy";
    Date startDateFinal = null;
    Date endDateFinal = null;
    float caloriesF = 0;
    float calciumF = 0;
    float sodiumF = 0;
    float carbsF = 0;
    float cholestrolF = 0;
    float ironF = 0;
    float protienF = 0;
    float sugarF = 0;
    float totalFatF = 0;
    SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat, Locale.US);
    public HistoryFragment() {
        // Required empty public constructor
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        historyChart = (LineChart) view.findViewById(R.id.chart);
        error = view.findViewById(R.id.errorMessage);
        startDate = view.findViewById(R.id.start);
        endDate = view.findViewById(R.id.end);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                if(myCalendar != null) {
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    myCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    myCalendar.set(Calendar.MINUTE, 0);
                    myCalendar.set(Calendar.SECOND, 0);
                    myCalendar.set(Calendar.MILLISECOND, 0);
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    startDateFinal = myCalendar.getTime();
                    startDate.setText(sdf2.format(myCalendar.getTime()));
                    myCalendar = null;

                }
                if(myCalendar2 != null){
                    myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    myCalendar2.set(Calendar.HOUR_OF_DAY, 0);
                    myCalendar2.set(Calendar.MINUTE, 0);
                    myCalendar2.set(Calendar.SECOND, 0);
                    myCalendar2.set(Calendar.MILLISECOND, 0);
                    myCalendar2.set(Calendar.YEAR, year);
                    myCalendar2.set(Calendar.MONTH, monthOfYear);
                    endDateFinal = myCalendar2.getTime();
                    endDate.setText(sdf2.format(myCalendar2.getTime()));
                    myCalendar2 = null;
                    try {
                        drawChart();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            //    startDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        /*DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, monthOfYear);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }

        }; */



        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                myCalendar = Calendar.getInstance();
                 DatePickerDialog firstDate = new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                firstDate.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                myCalendar2 = Calendar.getInstance();
                DatePickerDialog secDate = new DatePickerDialog(getContext(), date, myCalendar2
                        .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH));
                secDate.show();
            }
        });

        try {
            drawChart();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);

    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void drawChart() throws ParseException {
        // String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        //Default past 7 Days
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, -5);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startTime = cal.getTime();
        Date dayEnd = new Date();

        if(startDateFinal != null && endDateFinal != null) {
            Log.i("HERE", "ADDED FK");
            startTime = startDateFinal;
            dayEnd = endDateFinal;
        }
        historyChart.setBackgroundColor(Color.WHITE);
        historyChart.setDrawBorders(true);


        ArrayList<String> xAxis = new ArrayList<>();
        ArrayList<Float> calories = new ArrayList<>();
        ArrayList<Float> calcium = new ArrayList<>();
        ArrayList<Float> sodium = new ArrayList<>();
        ArrayList<Float> carbs = new ArrayList<>();
        ArrayList<Float> cholestrol = new ArrayList<>();
        ArrayList<Float> iron = new ArrayList<>();
        ArrayList<Float> protien = new ArrayList<>();
        ArrayList<Float> sugar = new ArrayList<>();
        ArrayList<Float> totalfat = new ArrayList<>();

        NutritionHistory instance = NutritionHistory.getInstance();
       /* NutritionInformation.NutritionInformationBuilder nb = new NutritionInformation.NutritionInformationBuilder()
                .calcium(1.0)
                .calories(100)
                .cholesterol(10)
                .carbohydrates(8.0)
                .iron(1.0)
                .sodium(1.0)
                .totalFat(1)
                .sugar(14)
                .protein(20);

        NutritionInformation n = nb.build();
        instance.addNutritionInformation("Apple",n); */


        NutritionHistory.getInstance().getNutritionInformation(startTime, dayEnd, nutritionInformation -> {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase.child("account").child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    NutritionInformation.NutritionInformationBuilder builder = new NutritionInformation.NutritionInformationBuilder();
                    NutritionInformation current = builder.build();
/*
                    for(Long day: nutritionInformation.keySet()) {
                       caloriesF += (float) nutritionInformation.get(day).getCalories();
                       calciumF += (float) nutritionInformation.get(day).getCalcium();
                       sodiumF += (float)nutritionInformation.get(day).getSodium();
                       carbsF += (float)nutritionInformation.get(day).getCarbohydrates();
                       cholestrolF += (float)nutritionInformation.get(day).getCholesterol();
                       ironF += (float)nutritionInformation.get(day).getIron();
                       protienF += (float)nutritionInformation.get(day).getProtein();
                       sugarF += (float)nutritionInformation.get(day).getSugar();
                       totalFatF += (float)nutritionInformation.get(day).getTotalFat();
                    }
                    for(Long day: nutritionInformation.keySet()) {
                        xAxis.add(sdf2.format(new Date(day)).toString());
                        calories.add(calciumF);
                        calcium.add(calciumF);
                        sodium.add(sodiumF);
                        carbs.add(carbsF);
                        cholestrol.add(cholestrolF);
                        iron.add(ironF);
                        protien.add(protienF);
                        sugar.add(sugarF);
                        totalfat.add(totalFatF);
                    }*/
                    for(Long day: nutritionInformation.keySet()) {
                        xAxis.add(sdf2.format(new Date(day)).toString());
                        calories.add( (float) nutritionInformation.get(day).getCalories());
                        calcium.add((float) nutritionInformation.get(day).getCalcium());
                        sodium.add((float)nutritionInformation.get(day).getSodium());
                        carbs.add((float) nutritionInformation.get(day).getCarbohydrates());
                        cholestrol.add((float)nutritionInformation.get(day).getCholesterol());
                        iron.add((float)nutritionInformation.get(day).getIron());
                        protien.add((float)nutritionInformation.get(day).getProtein());
                        sugar.add((float)nutritionInformation.get(day).getSugar());
                        totalfat.add((float)nutritionInformation.get(day).getTotalFat());
                    }
                    Log.i("HERE", xAxis.toString());
                    LineDataSet dataCalories = addValues("Calories", calories);
                    dataCalories.setColor(Color.RED);
                    dataCalories.setLineWidth(2f);

                    LineDataSet dataCalcium = addValues("Calcium",calcium);
                    dataCalcium.setColor(Color.rgb(181, 126	, 220));
                    dataCalcium.setLineWidth(2f);

                    LineDataSet dataSodium = addValues("Sodium", sodium);
                    dataSodium.setColor(Color.BLUE);
                    dataSodium.setLineWidth(2f);

                    LineDataSet dataCarbs = addValues("Carbohydrates",carbs);
                    dataCarbs.setColor(Color.GREEN);
                    dataCarbs.setLineWidth(2f);

                    LineDataSet dataCholesterol = addValues("Cholesterol", cholestrol);
                    dataCholesterol.setColor(Color.YELLOW);
                    dataCholesterol.setLineWidth(2f);

                    LineDataSet dataIron = addValues("Iron",iron);
                    dataIron.setColor(Color.BLACK);
                    dataIron.setLineWidth(2f);

                    LineDataSet dataProtein = addValues("Protein", protien);
                    dataProtein.setColor(Color.CYAN);
                    dataProtein.setLineWidth(2f);

                    LineDataSet dataSugar = addValues("Sugar",sugar);
                    dataSugar.setColor(Color.MAGENTA);
                    dataSugar.setLineWidth(2f);

                    LineDataSet dataTotalFat = addValues("Total Fat", totalfat);
                    dataTotalFat.setColor(Color.rgb(248	,131, 121));
                    dataTotalFat.setLineWidth(2f);

                    LineData lineData = new LineData(dataCalories, dataCalcium ,dataSodium, dataCarbs, dataIron, dataSugar, dataProtein, dataTotalFat, dataCholesterol);
                    historyChart.setData(lineData);
                    historyChart.getXAxis().setDrawAxisLine(true);
                    historyChart.setVisibleXRange(0, xAxis.size() - 1);
                    historyChart.getXAxis().setValueFormatter(new MyXAxisValueFormatter(xAxis));
                    historyChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    historyChart.enableScroll();
                    historyChart.getDescription().setEnabled(false);
                    historyChart.getXAxis().setLabelCount(5, true);
                    historyChart.setExtraBottomOffset(20f);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        designChart(xAxis);
        historyChart.invalidate();
    }

    public void designChart(ArrayList<String> xAxis) {

        Legend l = historyChart.getLegend();
        l.setEnabled(true);
        l.setDrawInside(false);
        l.setWordWrapEnabled(true);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setFormSize(15f);
        l.setXOffset(5f);
        l.setYOffset(10f);
        l.setXEntrySpace(7f);
        l.setFormLineWidth(2f);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setYEntrySpace(5f);
        l.setTextSize(10f);
        l.setStackSpace(5f);
        historyChart.getAxisLeft().setEnabled(true); //show y-axis at left
        historyChart.getAxisRight().setEnabled(false); //hide y-axis at right
        historyChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (float) value + "g"; // yVal is a string array
            }
        });

        historyChart.animateX(3000);
    }

    private LineDataSet addValues(String label, ArrayList<Float> data) {
        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < data.size(); i++) {
            entries.add(new Entry((float)i, data.get(i)));
        }
        LineDataSet dataSet = new LineDataSet(entries, label); //later enable legend, set color
        return dataSet;

    }

    private class MyXAxisValueFormatter implements IAxisValueFormatter {
        private ArrayList<String> mValues;
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // Simple version. You should use a DateFormatter to specify how you want to textually represent your date.
            return mValues.get((int) value);
        }
        public MyXAxisValueFormatter(ArrayList<String> values) {
            this.mValues = values;
        }
    }


}
