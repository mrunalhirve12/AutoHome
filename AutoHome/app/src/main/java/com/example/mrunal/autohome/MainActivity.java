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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //instance variables
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
    DatabaseReference mTimeStamp;


    /**
     * Method Responsible to create activity
     * @param savedInstanceState saves the state of the object*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getSupportActionBar().setLogo(R.drawable.autohome_icon_fg);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);

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

        //creating reference to Firebase database
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
        mTimeStamp = myRef.child("TIMESTAMP");

        /**
         * Method sets onClickListener on Add button to increase the DAC1OUT temperature
         * @param View.OnClickListener  registers a callback to be invoked
         * when the view is clicked */
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
                        updateTimestamp();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Range 0 to 31", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        /**
         * Method sets onClickListener on Add button to decrease the DAC1OUT temperature
         * @param View.OnClickListener  registers a callback to be invoked
         * when the view is clicked */
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
                        updateTimestamp();
                    }
                }
            }
        });

        /**
         * Method sets onnSeekBarChangeListener on seekbar to change the RGB LED red value
         * @param OnSeekBarChangeListener() callback that notifies clients when the progress
         * level has been changed.
         * Reference : https://firebase.google.com/docs/reference/android/com/google/firebase/database/ValueEventListener
         */
        mPWM3bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser){
                mPWM3Value.setValue(progresValue);
                updateTimestamp();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * Method sets onnSeekBarChangeListener on seekbar to change the RGB LED green value
         * @param OnSeekBarChangeListener() callback that notifies clients when the progress
         * level has been changed.
         */
        mPWM4bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                mPWM4Value.setValue(progresValue);
                updateTimestamp();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /**
         * Method sets onnSeekBarChangeListener on seekbar to change the RGB LED blue value
         * @param OnSeekBarChangeListener() callback that notifies clients when the progress
         * level has been changed.
         */
        mPWM5bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                mPWM5Value.setValue(progresValue);
                updateTimestamp();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * Method sets addValueEventListener on TempValue
         * @param ValueEventListener() receive events about changing Ambient temperature
         */
        mTempValue.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTempTextView.setText(dataSnapshot.getValue(Double.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * Method sets addValueEventListener on ADC3 value
         * @param ValueEventListener() receive events about changing ADC3 value
         */
        mADC3Value.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChannelADC3TextView.setText(dataSnapshot.getValue(Integer.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * Method sets addValueEventListener on ADC4 value
         * @param ValueEventListener() receive events about changing ADC4 value
         */
        mADC4Value.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChannelADC4TextView.setText(dataSnapshot.getValue(Integer.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * Method sets addValueEventListener on ADC5 value
         * @param ValueEventListener() receive events about changing ADC5 value
         */
        mADC5Value.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChannelADC5TextView.setText(dataSnapshot.getValue(Integer.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * Method sets addValueEventListener on PWM6 value
         * @param ValueEventListener() receive events about changing PWM6 value via progress bar
         */
        mPWM6Value.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPWM6ProgressBar.setProgress(dataSnapshot.getValue(Integer.class), true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * Method sets addListenerForSingleValueEvent on all values at start through the firebase
         * @param ValueEventListener() to notify the single event listener at start of the event.
         */
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //sets value onetime from firebase on start of application and on device rotation
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
    /**
     * Method updates the timestamp value in firebase*/
    private void updateTimestamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        mTimeStamp.setValue(dtf.format(now));
    }
}
