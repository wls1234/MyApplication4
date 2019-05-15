package com.example.asus.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RateCalcActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RateCalcActivity";
    float rate=0f;
    EditText inp2;
    TextView show2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_calc);
        String title = getIntent().getStringExtra("title");
        rate = getIntent().getFloatExtra("rate", 0f);
        Log.d(TAG, "onCreate: rate" + rate);
        ((TextView) findViewById(R.id.title2)).setText(title);
        inp2 = findViewById(R.id.inp2);
        show2=findViewById(R.id.show2);


    }

    @Override
    public void onClick(View v) {
        String str=inp2.getText().toString();
        float r=Float.parseFloat(str);
        float val=r/rate*100;
        Log.i(TAG, "onClick: "+val);
        show2.setText(""+val);
    }
}
