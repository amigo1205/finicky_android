package com.finickyltd.finicky;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinnerTest extends Activity {
    LinearLayout lnSpinner;
    TextView txtText;
    Spinner spinnerTest;
    ArrayList<String> mArrStrings = new ArrayList<String>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinnertest);
        lnSpinner = (LinearLayout)findViewById(R.id.lnSpinner);
        lnSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerTest.performClick();
            }
        });
        txtText = (TextView)findViewById(R.id.txtText);
        spinnerTest = (Spinner)findViewById(R.id.spinnerTest);
        for(int i = 0; i < 6; i++){
            mArrStrings.add("Hello " + String.valueOf(i));
        }
        ArrayAdapter<String> adapterEst = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mArrStrings);

        spinnerTest.setAdapter(adapterEst);
        spinnerTest.setOnItemSelectedListener(mSpListener);


    }
    private AdapterView.OnItemSelectedListener mSpListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            txtText.setText(mArrStrings.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
}
