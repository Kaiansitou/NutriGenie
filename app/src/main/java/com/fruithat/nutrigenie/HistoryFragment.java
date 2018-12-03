package com.fruithat.nutrigenie;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
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
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
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
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    startDate.setText(sdf.format(myCalendar.getTime()));
                    myCalendar = null;

                }
                if(myCalendar2 != null){
                    myCalendar2.set(Calendar.YEAR, year);
                    myCalendar2.set(Calendar.MONTH, monthOfYear);
                    myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    endDate.setText(sdf.format(myCalendar2.getTime()));
                    myCalendar2 = null;

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
        getData();
        //Default past 7 Days
        Date dayStart = new Date();
        sdf.format(dayStart);
        Calendar c = Calendar.getInstance();
        c.setTime(dayStart);
        c.add(Calendar.DATE, -7);
        Date dayEnd = c.getTime();
        sdf.format(dayEnd);
        historyChart.setBackgroundColor(Color.WHITE);
        historyChart.setDrawBorders(true);
        try {
             dayStart = sdf.parse(startDate.toString());
             dayEnd = sdf.parse(endDate.toString());
        } catch (ParseException e) {
            error.setText("Please Select Valid Start and End Date!");
        }


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

            NutritionHistory.getInstance().getNutritionInformation(dayStart, dayEnd, new NutritionHistoryCallback() {
                @Override
                public void onDataReceived(HashMap<Long, NutritionInformation> nutritionInformation) {
                    Log.i("Here", "RECEVIED DATA");
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
                        totalfat.add((float)nutritionInformation.get(day).getTotalFat());
                    }

                }
            });

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
      //  historyChart.setVisibleXRange(0, xAxis.size());
        historyChart.getXAxis().setValueFormatter(new MyXAxisValueFormatter(xAxis));
        historyChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        historyChart.enableScroll();
        historyChart.getDescription().setEnabled(false);
        historyChart.getXAxis().setLabelCount(xAxis.size(), true);
        historyChart.setExtraBottomOffset(20f);
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

        historyChart.animateX(3000);
        historyChart.invalidate();
    }
    private void getData() throws ParseException {
        String date = new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        Date startTime = sdf.parse(date + " 00:00:00");
        Date endTime = sdf.parse(date + " 23:59:59");

        NutritionHistory instance = NutritionHistory.getInstance();
        NutritionInformation.NutritionInformationBuilder nb = new NutritionInformation.NutritionInformationBuilder()
         .calories(1.0)
                .calcium(5.2)
          .sodium(2.9)
                .carbohydrates(2.9)
        .cholesterol(20)
        .iron(3.9)
        .protein(4.9)
         .sugar(9.5)
      .totalFat(9.4);
        NutritionInformation n = nb.build();

        NutritionInformation.NutritionInformationBuilder nb2 = new NutritionInformation.NutritionInformationBuilder()
                .calories(5.0)
                .calcium(5.2)
                .sodium(9.9)
                .carbohydrates(6.9)
                .cholesterol(5)
                .iron(7.9)
                .protein(9.9)
                .sugar(2.5)
                .totalFat(6.4);
        NutritionInformation n2 = nb.build();
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
