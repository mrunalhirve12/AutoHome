package com.example.mrunal.autohome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static int mDAC1count;
    TextView mDAC1OutTextView, mTempTextView, mChannelADC3TextView, mChannelADC4TextView, mChannelADC5TextView;
    Button mAdd;
    Button mSub;
    SeekBar mPWM4bar;
    SeekBar mPWM5bar;
    SeekBar mPWM3bar;
    ProgressBar mPWM6ProgressBar;

    DatabaseReference mPWM3Value;
    DatabaseReference mPWM4Value;
    DatabaseReference mPWM5Value;
    DatabaseReference mDAC1OutValue;
    DatabaseReference mTempValue;
    DatabaseReference mADC3Value;
    DatabaseReference mADC4Value;
    DatabaseReference mADC5Value;
    DatabaseReference mPWM6Value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setLogo(R.drawable.autohome_icon_fg);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mDAC1OutTextView = findViewById(R.id.DAC1OUTValueTextView);
        mTempTextView = findViewById(R.id.tempTextView);
        mChannelADC3TextView = findViewById(R.id.ChannelADC3ValueTextView);
        mChannelADC4TextView = findViewById(R.id.ChannelADC4ValueTextView);
        mChannelADC5TextView = findViewById(R.id.ChannelADC5ValueTextView);
        mAdd = findViewById(R.id.addButton);
        mSub = findViewById(R.id.subButton);
        mPWM3bar = findViewById(R.id.PWM3SeekBar);
        mPWM4bar = findViewById(R.id.PWM4SeekBar);
        mPWM5bar = findViewById(R.id.PWM5SeekBar);
        mPWM6ProgressBar = findViewById(R.id.PWM6ProgressBar);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        mPWM3Value = myRef.child("PWM3");
        mPWM4Value = myRef.child("PWM4");
        mPWM5Value = myRef.child("PWM5");
        mDAC1OutValue = myRef.child("DAC1OUT");
        mTempValue = myRef.child("ADA5IN");
        mADC3Value = myRef.child("ADC3IN");
        mADC4Value = myRef.child("ADC4IN");
        mADC5Value = myRef.child("ADC5IN");
        mPWM6Value = myRef.child("PWM6");

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDAC1OutTextView.getText().equals("")){
                    mDAC1count = 0;
                }
                else{
                    mDAC1count = Integer.parseInt(mDAC1OutTextView.getText().toString());
                    if (mDAC1count < 31 ){
                        mDAC1count ++;
                        mDAC1OutTextView.setText(String.valueOf(mDAC1count));
                        mDAC1OutValue.setValue(mDAC1count);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Range 0 to 31", Toast.LENGTH_LONG).show();
                    }
                }
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

        mTempValue.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTempTextView.setText(dataSnapshot.getValue(Double.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mADC3Value.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChannelADC3TextView.setText(dataSnapshot.getValue(Integer.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mADC4Value.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChannelADC4TextView.setText(dataSnapshot.getValue(Integer.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mADC5Value.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChannelADC5TextView.setText(dataSnapshot.getValue(Integer.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPWM6Value.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPWM6ProgressBar.setProgress(dataSnapshot.getValue(Integer.class), true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //parse data to recycler view adapter and call notifyDatasetChange()
                mDAC1OutTextView.setText(dataSnapshot.child("DAC1OUT").getValue(Integer.class).toString());
                mPWM3bar.setProgress(dataSnapshot.child("PWM3").getValue(Integer.class), true);
                mPWM4bar.setProgress(dataSnapshot.child("PWM4").getValue(Integer.class), true);
                mPWM5bar.setProgress(dataSnapshot.child("PWM5").getValue(Integer.class), true);
                mTempTextView.setText(dataSnapshot.child("ADA5IN").getValue(Double.class).toString());
                mChannelADC3TextView.setText(dataSnapshot.child("ADC3IN").getValue(Integer.class).toString());
                mChannelADC4TextView.setText(dataSnapshot.child("ADC4IN").getValue(Integer.class).toString());
                mChannelADC5TextView.setText(dataSnapshot.child("ADC5IN").getValue(Integer.class).toString());
                mPWM6ProgressBar.setProgress(dataSnapshot.child("PWM6").getValue(Integer.class), true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        mDAC1OutTextView.setText(String.format(Locale.getDefault(), "%d", 0));

}
}
