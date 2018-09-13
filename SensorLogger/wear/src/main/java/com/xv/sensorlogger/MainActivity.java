package com.xv.sensorlogger;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends WearableActivity implements SensorEventListener2, AdapterView.OnItemSelectedListener {

    private TextView mTextView;
    SensorManager manager;
    ImageButton buttonStart;
    ImageButton buttonStop;
    boolean isRunning;
    final String TAG = "SensorLogger";
    final int startDelay = 4;
    int duration = 20;
    FileWriter writer;
    FileWriter writer2;
    Spinner users_spinner;
    Spinner activities_spinner;
    String userId;
    String activity;
    Toast mToast = null;
    Vibrator vibrator;
    long[] vibrationStartPattern = {0, 500, 50, 300};
    long[] vibrationStopPattern = {0, 1000, 50, 2000};
    final int indexInPatternToRepeat = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();

        isRunning = false;

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        users_spinner = (Spinner)findViewById(R.id.users_spinner);
        ArrayAdapter<CharSequence> users_adapter = ArrayAdapter.createFromResource(this,
                R.array.users_array, android.R.layout.simple_spinner_item);
        users_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        users_spinner.setAdapter(users_adapter);
        users_spinner.setOnItemSelectedListener(this);

        activities_spinner = (Spinner)findViewById(R.id.activities_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activities_spinner.setAdapter(adapter);
        activities_spinner.setOnItemSelectedListener(this);

        SharedPreferences sharedPref = getSharedPreferences("prefs",MODE_PRIVATE);
        int previous_user = sharedPref.getInt("user",-1);
        int previous_activity = sharedPref.getInt("activity",-1);
        if(previous_user != -1) {
            // set the selected value of the spinner
            users_spinner.setSelection(previous_user, true);
        }
        if(previous_activity != -1) {
            // set the selected value of the spinner
            activities_spinner.setSelection(previous_activity, true);
        }

        buttonStart = (ImageButton)findViewById(R.id.buttonStart);
        buttonStop = (ImageButton)findViewById(R.id.buttonStop);

        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);

                String activity = activities_spinner.getSelectedItem().toString();

                File accel_logs_folder = new File(Environment.getExternalStorageDirectory(),"accel_logs");
                if (!accel_logs_folder.exists()) {
                    accel_logs_folder.mkdirs();
                }

                Log.d(TAG, "Writing to " + accel_logs_folder);
                try {
                    writer = new FileWriter(new File(accel_logs_folder, activity +  "_" + System.currentTimeMillis() + ".txt"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File heart_rate_logs_folder = new File(Environment.getExternalStorageDirectory(),"heart_rate_logs");
                if (!heart_rate_logs_folder.exists()) {
                    heart_rate_logs_folder.mkdirs();
                }

                Log.d(TAG, "Writing to " + heart_rate_logs_folder);
                try {
                  writer2 = new FileWriter(new File(heart_rate_logs_folder, activity +  "_" + System.currentTimeMillis() + ".txt"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (activity.equals("Drinking")) {
                    duration = startDelay + 10;
                }
                else if (activity.equals("Eating with Knife")) {
                    duration = startDelay + 10;
                }
                else if (activity.equals("Eating with Spoon")) {
                    duration = startDelay + 10;
                }
                else if (activity.equals("Jumping Jack")) {
                    duration = startDelay + 10;
                }
                else if (activity.equals("Running")) {
                    duration = startDelay + 10;
                }
                else if (activity.equals("Typing")) {
                    duration = startDelay + 5;
                }
                else {
                    duration = startDelay + 10;
                }

                startCountDownTimer(startDelay);
                vibrator.vibrate(vibrationStartPattern, indexInPatternToRepeat);

                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 50000);
                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_HEART_RATE), 50000);
//                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 50000);
//                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED), 0);
//                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 0);
//                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED), 0);
//                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), 0);
//                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), 0);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonStart.setEnabled(true);
                        buttonStop.setEnabled(false);
                        isRunning = false;
                        manager.flush(MainActivity.this);
                        manager.unregisterListener(MainActivity.this);
                        vibrator.vibrate(vibrationStopPattern, indexInPatternToRepeat);
                        try {
                            writer.close();
                            writer2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, duration * 1000);

                isRunning = true;
                return true;
            }
        });

        buttonStop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
                isRunning = false;
                manager.flush(MainActivity.this);
                manager.unregisterListener(MainActivity.this);
                try {
                    writer.close();
                    writer2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int selected_user = users_spinner.getSelectedItemPosition();
        int selected_activity = activities_spinner.getSelectedItemPosition();
        SharedPreferences sharedPref = getSharedPreferences("prefs",0);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putInt("user",selected_user);
        prefEditor.putInt("activity",selected_activity);
        prefEditor.commit();

        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        if (item.contains("0")) {
            if (item.equals("Corliss (01)")) {
                userId = "01";
            }
            else if (item.equals("Kian Boon (02)")) {
                userId = "02";
            }
            else if (item.equals("Nicholas (03)")) {
                userId = "03";
            }
            else {
                userId = "04";
            }
        }
        else {
            activity = item;
        }

        // Showing selected spinner item
//        Toast.makeText(parent.getContext(), "userId: " + userId + " activity: " + activity, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    private void startCountDownTimer(int noOfSeconds) {
        CountDownTimer countDownTimer = new CountDownTimer(noOfSeconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(MainActivity.this, "Starting in: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT);
                mToast.show();

            }
            public void onFinish() {
                mToast = Toast.makeText(MainActivity.this, "Starting!", Toast.LENGTH_SHORT);
                mToast.show();
            }
        }.start();
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        if(isRunning) {
            try {
                switch(evt.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
//                        writer.write(String.format("%d; ACC; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                        writer.write(String.format("%s,%s,%d,%f,%f,%f\n",userId, activity, evt.timestamp, evt.values[0], evt.values[1], evt.values[2]));
//                        break;
                    case Sensor.TYPE_HEART_RATE:
                        //Log.d(TAG,"Heart Rate:"+(int)evt.values[0]);
                        String heart_rate = Integer.toString((int)evt.values[0]);
                        writer2.write(String.format("%s,%s,%d,%s\n",userId, activity, evt.timestamp, heart_rate));
                        break;
//                    case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
//                        writer.write(String.format("%d; GYRO_UN; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], evt.values[4], evt.values[5]));
//                        break;
//                    case Sensor.TYPE_GYROSCOPE:
//                        writer.write(String.format("%d; GYRO; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
//                        break;
//                    case Sensor.TYPE_MAGNETIC_FIELD:
//                        writer.write(String.format("%d; MAG; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
//                        break;
//                    case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
//                        writer.write(String.format("%d; MAG_UN; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
//                        break;
//                    case Sensor.TYPE_ROTATION_VECTOR:
//                        writer.write(String.format("%d; ROT; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], 0.f, 0.f));
//                        break;
//                    case Sensor.TYPE_GAME_ROTATION_VECTOR:
//                        writer.write(String.format("%d; GAME_ROT; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], 0.f, 0.f));
//                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
