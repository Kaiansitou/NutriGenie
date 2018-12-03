package com.fruithat.nutrigenie;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private LineChart historyChart;
    private EditText startDate;
    private EditText endDate;
    Calendar myCalendar;
    Calendar myCalendar2;
    public HistoryFragment() {
        // Required empty public constructor
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        historyChart = (LineChart) view.findViewById(R.id.chart);
        startDate = view.findViewById(R.id.start);
        endDate = view.findViewById(R.id.end);
        myCalendar = Calendar.getInstance();
        myCalendar2 = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                if(!myCalendar.equals(Calendar.getInstance())) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    startDate.setText(sdf.format(myCalendar.getTime()));

                } else {
                    myCalendar2.set(Calendar.YEAR, year);
                    myCalendar2.set(Calendar.MONTH, monthOfYear);
                    myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    endDate.setText(sdf.format(myCalendar2.getTime()));

                }

            }

        };

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar2
                        .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        drawChart();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);

    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void drawChart() {
        historyChart.setBackgroundColor(Color.WHITE);
        historyChart.setDrawBorders(true);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy"); // here set the pattern as you date in string was containing like date/month/year
       /* try {
            String start = startDate.getText().toString();
            String end = startDate.getText().toString();
            Date dayStart = sdf.parse(start);
            Date dayEnd = sdf.parse(end);

        } catch (ParseException e) {
        } */
      //

        /*
        ArrayList<String> xAxis = new ArrayList<>();
        ArrayList<Float> calories = new ArrayList<>();
        ArrayList<Float> calcium = new ArrayList<>();
        ArrayList<Float> sodium = new ArrayList<>();
        ArrayList<Float> carbs = new ArrayList<>();
        ArrayList<Float> cholestrol = new ArrayList<>();
        ArrayList<Float> iron = new ArrayList<>();
        ArrayList<Float> protien = new ArrayList<>();
        ArrayList<Float> sugar = new ArrayList<>();
        ArrayList<Float> transFat = new ArrayList<>();
        ArrayList<Float> saturatedFat = new ArrayList<>();

        try {
            Date dayStart = sdf.parse("11/20/2019");
            Date dayEnd = sdf.parse("11/27/2019");

            NutritionHistory.getInstance().getNutritionInformation(new Date(0), new Date(), new NutritionHistoryCallback() {
                @Override
                public void onDataReceived(HashMap<Long, NutritionInformation> nutritionInformation) {
                    for(Long day: nutritionInformation.keySet()) {
                        xAxis.add(sdf.format(new Date(day)).toString());
                        calories.add((float) nutritionInformation.get(day).getCalories());
                        calcium.add((float)nutritionInformation.get(day).getCalcium());
                        sodium.add((float)nutritionInformation.get(day).getSodium());
                        carbs.add((float)nutritionInformation.get(day).getCarbohydrates());
                        cholestrol.add((float)nutritionInformation.get(day).getCholesterol());
                        iron.add((float)nutritionInformation.get(day).getIron());
                        protien.add((float)nutritionInformation.get(day).getProtein());
                        sugar.add((float)nutritionInformation.get(day).getSugar());
                        transFat.add((float)nutritionInformation.get(day).getTransFat());
                        saturatedFat.add((float)nutritionInformation.get(day).getSaturatedFat());
                    }

                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }
*/
        ArrayList<Float> tempdata1 = new ArrayList<Float>();
        tempdata1.add((float)38.1);
        tempdata1.add((float)23.4);
        tempdata1.add((float)39.1);
        tempdata1.add((float)23.4);
        tempdata1.add((float)39.5);
        tempdata1.add((float)20.5);

        ArrayList<Float> tempdata2 = new ArrayList<Float>();
        tempdata2.add((float)39.5);
        tempdata2.add((float)20.5);
        tempdata2.add((float)19.5);
        tempdata2.add((float)40.5);
        tempdata2.add((float)12.1);
        tempdata2.add((float)24.4);

        LineDataSet dataCalories = addValues("Calories", tempdata1);
        dataCalories.setColor(Color.RED);
        dataCalories.setDrawValues(false);
       // dataCalories.setFormSize();
        dataCalories.setLineWidth(2f);
        dataCalories.setCircleColor(Color.BLACK);


        LineDataSet dataCalcium = addValues("Calcium",tempdata2);
        dataCalcium.setColor(Color.rgb(181, 126	, 220));
        dataCalcium.setDrawValues(false);
        dataCalcium.setLineWidth(1.5f);
        dataCalcium.setCircleColor(Color.BLACK);

        LineDataSet dataSodium = addValues("Sodium", tempdata1);
        dataSodium.setColor(Color.BLUE);
        dataSodium.setLineWidth(1.5f);

        LineDataSet dataCarbs = addValues("Carbohydrates",tempdata2);
        dataCarbs.setColor(Color.GREEN);
        dataCarbs.setLineWidth(1.5f);

        LineDataSet dataCholesterol = addValues("Cholesterol", tempdata1);
        dataCholesterol.setColor(Color.YELLOW);

        LineDataSet dataIron = addValues("Iron",tempdata2);
        dataIron.setColor(Color.BLACK);

        LineDataSet dataProtein = addValues("Protein", tempdata1);
        dataProtein.setColor(Color.CYAN);

        LineDataSet dataSugar = addValues("Sugar",tempdata2);
        dataSugar.setColor(Color.MAGENTA);
/*
        LineDataSet dataTransFat = addValues("TransFat", tempdata1);
        dataTransFat.setColor(Color.rgb(64, 130, 109));
*/
        LineDataSet dataTotalFat = addValues("Total Fat", tempdata2);
        dataTotalFat.setColor(Color.rgb(248	,131, 121));

        LineData lineData = new LineData(dataCalories, dataCalcium ,dataSodium, dataCarbs, dataIron, dataSugar, dataProtein, dataTotalFat, dataCholesterol);
        historyChart.setData(lineData);

       ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("10/11");
        xAxis.add("10/12");
        xAxis.add("10/13");
        xAxis.add("10/14");
        xAxis.add("10/15");
        xAxis.add("10/16");
        /*
        for(int i = 0; i < tempdata1.length/7; i++) {
            xAxis.add("Mon");
            xAxis.add("Tues");
            xAxis.add("Wed");
            xAxis.add("Thur");
            xAxis.add("Fri");
            xAxis.add("Sun");
            xAxis.add("Sat");
        }*/

        historyChart.getXAxis().setDrawAxisLine(true);
      //  historyChart.setVisibleXRange(0, xAxis.size());

        historyChart.getXAxis().setValueFormatter(new MyXAxisValueFormatter(xAxis));
        historyChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        historyChart.enableScroll();
        historyChart.getDescription().setEnabled(false);
        historyChart.getXAxis().setLabelCount(xAxis.size(), true);
        historyChart.setExtraBottomOffset(20f);
      //  historyChart.setPadding(5, 5, 5, 200);
        Legend l = historyChart.getLegend();
        l.setEnabled(true);
        l.setDrawInside(false);
        l.setWordWrapEnabled(true);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
    //   l.setOrientation(Legend.LegendOrientation.VERTICAL);
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
        historyChart.animateX(3000);

        historyChart.invalidate();
    }

    private LineDataSet addValues(String label, ArrayList<Float> data) {
        List<Entry> entries = new ArrayList<>();
        for(int i = 0; i < data.size(); i++) {
            entries.add(new Entry((float)i, data.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, label); //later enable legend, set color
        return dataSet;

    }
    private ArrayList<String> setAxis(ArrayList<String> xAxis) {

        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APIRL");
        xAxis.add("MAY");
        xAxis.add("JUNE");
        xAxis.add("JULY");
        xAxis.add("AUG");
        xAxis.add("SEPT");
        xAxis.add("OCT");
        xAxis.add("NOV");
        xAxis.add("DEC");
        return xAxis;

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
