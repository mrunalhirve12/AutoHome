package com.example.mrunal.autohome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static int mDAC1count;
    TextView mDAC1OutTextView;
    Button mAdd;
    Button mSub;
    SeekBar mPWM4bar;
    SeekBar mPWM5bar;
    SeekBar mPWM3bar;

    DatabaseReference mPWM3Value;
    DatabaseReference mPWM4Value;
    DatabaseReference mPWM5Value;
    DatabaseReference mDAC1OutValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDAC1OutTextView = findViewById(R.id.DAC1OUTValueTextView);
        mAdd = findViewById(R.id.addButton);
        mSub = findViewById(R.id.subButton);
        mPWM3bar = findViewById(R.id.PWM3SeekBar);
        mPWM4bar = findViewById(R.id.PWM4SeekBar);
        mPWM5bar = findViewById(R.id.PWM5SeekBar);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        mPWM3Value = myRef.child("PWM3");
        mPWM4Value = myRef.child("PWM4");
        mPWM5Value = myRef.child("PWM5");
        mDAC1OutValue = myRef.child("DAC1OUT");

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDAC1OutTextView.getText().equals("")){
                    mDAC1count = 0;
                }
                else{
                    mDAC1count = Integer.parseInt(mDAC1OutTextView.getText().toString());
                }
                mDAC1count ++;
                mDAC1OutTextView.setText(String.valueOf(mDAC1count));
                mDAC1OutValue.setValue(mDAC1count);

            }
        });

        mSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDAC1OutTextView.getText().equals("")){
                    mDAC1count = 0;
                }
                else{
                    mDAC1count = Integer.parseInt(mDAC1OutTextView.getText().toString());
                    if (mDAC1count <= 0) {
                        Toast.makeText(getApplicationContext(), "Range 0 to 31", Toast.LENGTH_LONG).show();
                    }
                    else {
                        mDAC1count --;
                        mDAC1OutTextView.setText(String.valueOf(mDAC1count));
                        mDAC1OutValue.setValue(mDAC1count);

                    }
                }
            }
        });

        mPWM3bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser){
                mPWM3Value.setValue(progresValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mPWM4bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                mPWM4Value.setValue(progresValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mPWM5bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                mPWM5Value.setValue(progresValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
}
}
