package com.fruithat.nutrigenie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Context;
import static android.content.Context.MODE_PRIVATE;

public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout defined in quote_fragment.xml
        // The last parameter is false because the returned view does not need to be attached to the container ViewGroup
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        Button save = (Button)view.findViewById(R.id.save);


        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Profile Saved", Toast.LENGTH_LONG).show();
            }
        });

        Button reset = (Button)view.findViewById(R.id.reset);

        reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText a = view.findViewById(R.id.editText1);
                EditText b = view.findViewById(R.id.editText3);
                EditText c = view.findViewById(R.id.editText2);
                TextView d = view.findViewById(R.id.textView12);
                RadioGroup radioGroup;
                radioGroup = (RadioGroup) view.findViewById(R.id.RGroup);
                radioGroup.clearCheck();

                if(a!=null || b!= null || c!=null || d!=null){
                    a.setText(null);
                    b.setText(null);
                    c.setText(null);
                    d.setText(null);
                }
            }
        });

        Button calculate = (Button) view.findViewById(R.id.calculate);

        calculate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                RadioButton female = view.findViewById(R.id.Female);
                RadioButton male = view.findViewById(R.id.Male);

                EditText weightNum = view.findViewById(R.id.editText3);
                int weight = Integer.parseInt(weightNum.getText().toString());
                EditText cmNum = view.findViewById(R.id.editText1);
                double height = Double.parseDouble(cmNum.getText().toString());
                EditText yearNum = view.findViewById(R.id.editText2);
                int year = Integer.parseInt(yearNum.getText().toString());

                if (female.isChecked()) {

                    double women_BMR = 10 * (weight * 0.45359237) + 6.25 * height - 5 * year - 161;
                    int women = (int) women_BMR;

                    TextView display = (TextView) view.findViewById(R.id.textView12);
                    String value = String.valueOf(women);
                    display.setText(value);

                } else if (male.isChecked()){

                    double men_BMR = 10 * (weight * 0.45359237) + 6.25 * height - 5 * year + 5;
                    int men = (int) men_BMR;

                    TextView display = (TextView) view.findViewById(R.id.textView12);
                    String value = String.valueOf(men);
                    display.setText(value);
                } else {
                    Toast.makeText(getContext(),"Please Select Gender", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't destroy Fragment on reconfiguration
        setRetainInstance(true);
    }

}
