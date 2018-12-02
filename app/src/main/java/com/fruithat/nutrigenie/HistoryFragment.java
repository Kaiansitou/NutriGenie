package com.fruithat.nutrigenie;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private LineChart historyChart;

    public HistoryFragment() {
        // Required empty public constructor
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyChart = (LineChart) view.findViewById(R.id.chart);
        drawChart();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);

    }
    private void drawChart() {
        historyChart.setBackgroundColor(Color.WHITE);
        historyChart.setDrawBorders(true);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd"); // here set the pattern as you date in string was containing like date/month/year
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

  //      int[] tempdata1 = new int[]{30,23,20,30,23,21,20,12,33,22,11,23,45,61,43,29,11,93,22,11,34,55,22,11};
   //     int[] tempdata2 = new int[]{39,22,11,16,12,20,30,23,20,30,23,21,20,12,33,22,11,23,45,60,43,21,11,93};

        LineDataSet dataCalories = addValues("Calories",calories);
        dataCalories.setColor(Color.RED);
        dataCalories.setLineWidth(1f);

        LineDataSet dataCalcium = addValues("Calcium",calcium);
        dataCalcium.setColor(Color.red(10));
        dataCalcium.setLineWidth(1f);

        LineDataSet dataSodium = addValues("Sodium", sodium);
        dataSodium.setColor(Color.BLUE);
        dataCalcium.setLineWidth(1f);

        LineDataSet dataCarbs = addValues("Carbohydrates",carbs);
        dataCarbs.setColor(Color.GREEN);

        LineDataSet dataCholesterol = addValues("Cholesterol", cholestrol);
        dataCholesterol.setColor(Color.YELLOW);

        LineDataSet dataIron = addValues("Iron",iron);
        dataIron.setColor(Color.BLACK);

        LineDataSet dataProtein = addValues("Protein", protien);
        dataProtein.setColor(Color.CYAN);

        LineDataSet dataSugar = addValues("Sugar",sugar);
        dataSugar.setColor(Color.MAGENTA);

        LineDataSet dataTransFat = addValues("TransFat", transFat);
        dataTransFat.setColor(Color.green(100));

        LineDataSet dataSaturatedFat = addValues("Saturated Fat", saturatedFat);
        dataSaturatedFat.setColor(Color.DKGRAY);

        LineData lineData = new LineData(dataCalories, dataCalcium, dataSodium, dataCarbs, dataCholesterol, dataIron, dataProtein, dataSaturatedFat,
                dataSugar, dataTransFat);
        historyChart.setData(lineData);

       /* ArrayList<String> xAxis = new ArrayList<>();
        for(int i = 0; i < tempdata1.length/7; i++) {
            xAxis.add("Mon");
            xAxis.add("Tues");
            xAxis.add("Wed");
            xAxis.add("Thur");
            xAxis.add("Fri");
            xAxis.add("Sun");
            xAxis.add("Sat");
        }*/


        historyChart.getXAxis().setValueFormatter(new MyXAxisValueFormatter(xAxis));
        historyChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        historyChart.enableScroll();
        Legend l = historyChart.getLegend();
        l.setEnabled(true);
        l.setWordWrapEnabled(true);
     //   l.setXEntrySpace(5f);
        l.setTextSize(15);


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
    private ArrayList<String> setMonthView(ArrayList<String> xAxis) {

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
