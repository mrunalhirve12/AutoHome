package com.vasaikar.autohomehardware;

import android.app.Activity;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Date;

/**
 * The Main activity of the app that runs interfaces with the
 * PIC and Firebase.
 * @author Shubham Vasaikar
 * @version 1.0
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
    DatabaseReference mDac1OutDbRef;
    DatabaseReference mAdc3In;
    DatabaseReference mAdc4In;
    DatabaseReference mAdc5In;
    DatabaseReference mTimeStamp;
    Gpio mLedGpio;
    Thread mBlinkLed;
    volatile boolean mStopBlinking;

    /**
     * Called when the app is run on the Raspberry Pi.
     * Purposely not set to run at boot as it does not
     * allow easy configuration changes like WiFi using
     * the display out.
     * @param savedInstanceState unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            mLedGpio = manager.openGpio("BCM17");
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
        mDac1OutDbRef = mDbRef.child("DAC1OUT");
        mAdc3In = mDbRef.child("ADC3IN");
        mAdc4In = mDbRef.child("ADC4IN");
        mAdc5In = mDbRef.child("ADC5IN");
        mTimeStamp = mDbRef.child("TIMESTAMP");
        mStopBlinking = false;

        mPwm3DbRef.addValueEventListener(new ValueEventListener() {
            /**
             * Listen for changes on PWM3 value in Firebase DB and write to
             * PIC register.
             * @param dataSnapshot The instantaneous snapshot of the changed value.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int pwm3 = dataSnapshot.getValue(Integer.class);
                try {
                    mDevice.writeRegByte(0x00, (byte) pwm3);
                } catch (IOException e) {
                    Log.d(TAG, "Failed to write to PWM3");
                    e.printStackTrace();
                    setLed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Cancelled");
            }
        });

        mPwm4DbRef.addValueEventListener(new ValueEventListener() {
            /**
             * Listen for changes on PWM4 value in Firebase DB and write to
             * PIC register.
             * @param dataSnapshot The instantaneous snapshot of the changed value.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int pwm4 = dataSnapshot.getValue(Integer.class);
                try {
                    mDevice.writeRegByte(0x01, (byte) pwm4);
                } catch (IOException e) {
                    Log.d(TAG, "Failed to write to PWM4");
                    e.printStackTrace();
                    setLed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Cancelled");
            }
        });

        mPwm5DbRef.addValueEventListener(new ValueEventListener() {
            /**
             * Listen for changes on PWM4 value in Firebase DB and write to
             * PIC register.
             * @param dataSnapshot The instantaneous snapshot of the changed value.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int pwm5 = dataSnapshot.getValue(Integer.class);
                try {
                    mDevice.writeRegByte(0x02, (byte) pwm5);
                } catch (IOException e) {
                    Log.d(TAG, "Failed to write to PWM5");
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Cancelled");
            }
        });

        mDac1OutDbRef.addValueEventListener(new ValueEventListener() {
            /**
             * Listen for changes on DAC1OUT value in Firebase DB and write to
             * PIC register.
             * @param dataSnapshot The instantaneous snapshot of the changed value.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int dac1Out = dataSnapshot.getValue(Integer.class);
                try {
                    //Log.d(TAG, "DAC1OUT: " + (byte) dac1Out);
                    mDevice.writeRegByte(0x04, (byte) dac1Out);
                } catch (IOException e) {
                    Log.d(TAG, "Failed to write to PWM5");
                    e.printStackTrace();
                    setLed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Thread to read the temperature value and set the
        // PWM motor duty cycle accordingly.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        byte[] b = new byte[2];
                        b[0] = mDevice.readRegByte(0x05);
                        b[1] = mDevice.readRegByte(0x06);
                        // Log.d(TAG, "LSB: " + lsb + " MSB: " + msb);
                        double temp = toInt(b) / 10.0;
                        mTempDbRef.setValue(temp);
                        updateTimestamp();
                        setMotorSpeed(temp);
                        // Log.d(TAG, "Temp: " + temp);
                        Thread.sleep(3000);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    setLed();
                }
            }

            /**
             * Function to set the motor duty cycle based on the
             * temperature.
             * @param temp
             */
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
                    setLed();
                }
                updateTimestamp();
            }
        }).start();

        // Thread to read ADC values and update
        // the values on the Firebase database.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        mAdc3In.setValue(readADC("ADC3IN"));
                        mAdc4In.setValue(readADC("ADC4IN"));
                        mAdc5In.setValue(readADC("ADC5IN"));
                        updateTimestamp();
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Thread to blink LED on program start.
        mBlinkLed = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                    // mStopBlinking is a global boolean variable that will
                    // ensure that the thread is stopped when the value is
                    // set to false.
                    while (!mStopBlinking) {
                        mLedGpio.setValue(true);
                        //Log.d("LedDemo", "Before delay");
                        Thread.sleep(400);
                        //Log.d("LedDemo", "After delay");
                        mLedGpio.setValue(false);
                        Thread.sleep(400);
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    setLed();
                }
            }
        });

        mBlinkLed.start(); // Start blinking LED
    }

    /**
     * Destroy resources and close connections
     * to the peripherals.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close I2C device", e);
                setLed();
            }
        }
        if (mLedGpio != null) {
            try {
                mLedGpio.close();
                mLedGpio = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close GPIO device", e);
                setLed();
            }
        }
    }

    /**
     * Get the value of the ADC
     * @param ADC The string of the ADC pin name
     *            to read value from
     * @return int value of the register
     */
    private int readADC(String ADC) {
        byte[] b;
        int val = 0;

        try {
            switch (ADC) {
                case "ADC3IN":
                    b = new byte[2];
                    b[0] = mDevice.readRegByte(0x07);
                    b[1] = mDevice.readRegByte(0x08);
                    val = toInt(b);
                    break;

                case "ADC4IN":
                    b = new byte[2];
                    b[0] = mDevice.readRegByte(0x09);
                    b[1] = mDevice.readRegByte(0x0a);
                    val = toInt(b);
                    break;

                case "ADC5IN":
                    b = new byte[2];
                    b[0] = mDevice.readRegByte(0x0b);
                    b[1] = mDevice.readRegByte(0x0c);
                    val = toInt(b);
                    break;

                default:
                    b = new byte[2];
                    b[0] = mDevice.readRegByte(0x07);
                    b[1] = mDevice.readRegByte(0x08);
                    val = toInt(b);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            setLed();
        }
        return val;
    }

    /**
     * Set the LED in case of an exceptiom.
     */
    private void setLed() {
        mStopBlinking = true;
        try {
            mLedGpio.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert the byte array to an integer.
     * https://stackoverflow.com/questions/44040416/convert-two-unsigned-bytes-to-an-int-in-java
     * @param b
     * @return int representation of the byte array.
     */
    private int toInt(byte[] b) {
        int x = (0 << 24) | (0 << 16)
                | ((b[1] & 0xFF) << 8) | ((b[0] & 0xFF) << 0);
        return x;
    }

    /**
     * Update the timestamp in the Firebase DB whenever a change is made.
     */
    private void updateTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date now = new Date();
        mTimeStamp.setValue(sdf.format(now));
    }
}
