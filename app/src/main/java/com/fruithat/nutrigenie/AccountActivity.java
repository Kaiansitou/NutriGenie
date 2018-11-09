package com.fruithat.nutrigenie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AccountActivity extends AppCompatActivity {

    private TextView mTextMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        mTextMessage = (TextView) findViewById(R.id.account);

    }

}
