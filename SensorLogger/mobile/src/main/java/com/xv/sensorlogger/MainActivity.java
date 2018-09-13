package com.xv.sensorlogger;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class MainActivity extends AppCompatActivity implements SensorEventListener2, AdapterView.OnItemSelectedListener {

    SensorManager manager;
    Button buttonStart;
    Button buttonStop;
    boolean isRunning;
    final String TAG = "SensorLogger";
    final int startDelay = 4;
    int duration = 20;
    FileWriter writer;
    Spinner users_spinner;
    Spinner activities_spinner;
    Toast mToast = null;
    Vibrator vibrator;
    long[] vibrationStartPattern = {0, 500, 50, 300};
    long[] vibrationStopPattern = {0, 1000, 50, 2000};
    String userId;
    String activity;
    final int indexInPatternToRepeat = -1;

    private long lastUpdate = 0;

    private static List<Long> lastUpdates;

    private float last_x;
    private float last_y;
    private float last_z;
    private float vLength;
    private static List<Float> x;
    private static List<Float> y;
    private static List<Float> z;
    private static List<Float> vectorLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        long minTime = 1000;
        long minDistance = 0;

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
        } catch (SecurityException e) {

        }

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

        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);

        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);

                File accel_logs_folder = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS),"/mobile_accel_logs/");
                if (!accel_logs_folder.exists()) {
                    accel_logs_folder.mkdirs();
                }

                try {
                    writer = new FileWriter(new File(accel_logs_folder, "mobile_accel_" + System.currentTimeMillis() + ".txt"));
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
//                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_HEART_RATE), 50000);
//                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 0);
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
                vibrator.vibrate(vibrationStopPattern, indexInPatternToRepeat);
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            /** Do work with new location. Implementation of this method will be covered later. **/
            //Log.d(TAG, "Current Location :" + location.getLatitude() + "," + location.getLongitude());
        }
    };

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
//        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        Toast.makeText(parent.getContext(), "userId: " + userId + " activity: " + activity, Toast.LENGTH_LONG).show();

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
                        last_x = evt.values[0];
                        last_y = evt.values[1];
                        last_z = evt.values[2];
                        vLength = (float) Math.sqrt(last_x * last_x + last_y * last_y + last_z * last_z);
                        writer.write(String.format("%s,%s,%d,%f,%f,%f,%f\n",userId, activity, evt.timestamp, last_x, last_y, last_z, vLength));
                        break;
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
