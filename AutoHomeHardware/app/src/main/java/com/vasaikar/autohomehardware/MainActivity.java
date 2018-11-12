package com.vasaikar.autohomehardware;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Locale;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    public final String TAG = "com.vasaikar.AutoHomeHardware";

    I2cDevice mDevice;

    DatabaseReference mDbRef;
    DatabaseReference mPwm3DbRef;
    DatabaseReference mPwm4DbRef;
    DatabaseReference mPwm5DbRef;
    DatabaseReference mPwm6DbRef;
    DatabaseReference mTempDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            mDevice = manager.openI2cDevice("I2C1", 0x08);
        } catch (IOException e) {
            Log.d(TAG, "Unable to open I2C Device.");
            e.printStackTrace();
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDbRef = database.getReference();
        mPwm3DbRef = mDbRef.child("PWM3");
        mPwm4DbRef = mDbRef.child("PWM4");
        mPwm5DbRef = mDbRef.child("PWM5");
        mPwm6DbRef = mDbRef.child("PWM6");
        mTempDbRef = mDbRef.child("ADA5IN");

        mPwm3DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int pwm3 = dataSnapshot.getValue(Integer.class);
                try {
                    mDevice.writeRegByte(0x00, (byte) pwm3);
                } catch (IOException e) {
                    Log.d(TAG, "Failed to write to PWM3");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Cancelled");
            }
        });

        mPwm4DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int pwm4 = dataSnapshot.getValue(Integer.class);
                try {
                    mDevice.writeRegByte(0x01, (byte) pwm4);
                } catch (IOException e) {
                    Log.d(TAG, "Failed to write to PWM4");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Cancelled");
            }
        });

        mPwm5DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int pwm5 = dataSnapshot.getValue(Integer.class);
                try {
                    mDevice.writeRegByte(0x02, (byte) pwm5);
                } catch (IOException e) {
                    Log.d(TAG, "Failed to write to PWM5");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Cancelled");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        int lsb = (int) mDevice.readRegByte(0x05);
                        int msb = (int) mDevice.readRegByte(0x06);
                        // Log.d(TAG, "LSB: " + lsb + " MSB: " + msb);
                        double temp = ((msb & 0xFF << 8) | lsb & 0xFF) / 10.0;
                        mTempDbRef.setValue(temp);
                        setMotorSpeed(temp);
                        // Log.d(TAG, "Temp: " + temp);
                        Thread.sleep(1000);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            private void setMotorSpeed(double temp) {
                try {
                    if (temp <= 15) {
                        mDevice.writeRegByte(0x03, (byte) 80);
                        mPwm6DbRef.setValue(80);
                    }
                    else if (temp > 15 && temp <= 18) {
                        mDevice.writeRegByte(0x03, (byte) 30);
                        mPwm6DbRef.setValue(30);
                    }
                    else if (temp > 18 && temp <= 22) {
                        mDevice.writeRegByte(0x03, (byte) 50);
                        mPwm6DbRef.setValue(50);
                    }
                    else if (temp > 22 && temp <= 25) {
                        mDevice.writeRegByte(0x03, (byte) 70);
                        mPwm6DbRef.setValue(70);
                    }
                    else {
                        mDevice.writeRegByte(0x03, (byte) 40);
                        mPwm6DbRef.setValue(40);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close I2C device", e);
            }
        }
    }

    public void readTemperature() {

    }
}
