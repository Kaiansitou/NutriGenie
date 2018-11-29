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
                EditText b = view.findViewById(R.id.editText2);
                EditText c = view.findViewById(R.id.editText3);
                EditText d = view.findViewById(R.id.editText4);
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

//        Button calculate = (Button) view.findViewById(R.id.calculate);
//
//        calculate.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Integer weight = view.findViewById(R.id.weight);
//                Integer women_BMR = 655+(4.3 * weight) + (12.7 * (cm * 0.393700787)) - (4.7 * year);
//                Integer men_BMR;
//
//            }
//        });

        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't destroy Fragment on reconfiguration
        setRetainInstance(true);
    }

}
