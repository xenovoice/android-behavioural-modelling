package com.xv.activityrecognition;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener, MessageClient.OnMessageReceivedListener {
    private final static String TAG = "MainActivity";
    private static final int N_SAMPLES = 200;
    private static List<Float> x;
    private static List<Float> y;
    private static List<Float> z;
    private Button mWearHistoryBtn;
    private TextView sittingTextView;
    private TextView standingTextView;
    private TextView runningTextView;
    private TextView walkingTextView;
    private float[] results;
    private TensorFlowClassifier classifier;
    String datapath = "/message_path";
    File activity_logs_folder;
    FileWriter writer;

    public static String fDay;
    public static String lDay;
    public static String currentWeek;

    private String[] labels = {"Walking", "Running", "Sitting", "Standing"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getCurrentWeek();

        mWearHistoryBtn = (Button) findViewById(R.id.wearHistoryBtn);

        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();

        activity_logs_folder = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS)+"/activity_logs/"+fDay+"_"+lDay+"/");
        if (!activity_logs_folder.exists()) {
            activity_logs_folder.mkdirs();
        }

//        try {
//            writer = new FileWriter(new File(activity_logs_folder, todayString + ".txt"), true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        List<Sensor> deviceSensors = getSensorManager().getSensorList(Sensor.TYPE_ALL);

        for(Sensor type : deviceSensors){
            Log.e("sensors",type.getStringType());
        }

        runningTextView = (TextView) findViewById(R.id.running_prob);
        sittingTextView = (TextView) findViewById(R.id.sitting_prob);
        standingTextView = (TextView) findViewById(R.id.standing_prob);
        walkingTextView = (TextView) findViewById(R.id.walking_prob);
        mWearHistoryBtn = (Button) findViewById(R.id.wearHistoryBtn);

        mWearHistoryBtn.setOnClickListener(this);

        classifier = new TensorFlowClassifier(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, Loading.class);
        intent.putExtra("STATE", "LOADING");
        startActivity(intent);
    }

    protected void onPause() {
        Wearable.getMessageClient(this).removeListener(this);
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 50000);
        Wearable.getMessageClient(this).addListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        activityPrediction();
        x.add(event.values[0]);
        y.add(event.values[1]);
        z.add(event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void activityPrediction() {
        if (x.size() == N_SAMPLES && y.size() == N_SAMPLES && z.size() == N_SAMPLES) {
            List<Float> data = new ArrayList<>();
            data.addAll(x);
            data.addAll(y);
            data.addAll(z);

            results = classifier.predictProbabilities(toFloatArray(data));

            runningTextView.setText(Float.toString(round(results[0], 2)));
            sittingTextView.setText(Float.toString(round(results[2], 2)));
            standingTextView.setText(Float.toString(round(results[3], 2)));
            walkingTextView.setText(Float.toString(round(results[1], 2)));

            float highestProbability = results[0];
            int position = 0;
            String activity;

            for (int counter = 1; counter < results.length; counter++) {
                if (results[counter] > highestProbability) {
                    highestProbability = results[counter];
                    position = counter;
                }
            }

            switch (position) {
                case 0:
                    activity = "Sitting";
                    break;
                case 1:
                    activity = "Standing";
                    break;
                case 2:
                    activity = "Running";
                    break;
                case 3:
                    activity = "Walking";
                    break;
                default:
                    activity = "Unknown";
                    break;
            }

            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
            Date now = new Date();
            String timeString = timeFormatter.format(now);
            try {
                int a = 0;
                //writer.write(String.format("%s,%s\n",timeString, activity));
            } catch (Exception e) {
                e.printStackTrace();
            }

            x.clear();
            y.clear();
            z.clear();
        }
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }


    public static void getCurrentWeek() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayString = dateFormatter.format(todayDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        int week = cal.get(Calendar.WEEK_OF_YEAR);

        cal.set(Calendar.WEEK_OF_YEAR, week);   //set the calendar to that week
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); //get start date
        String startDate = dateFormatter.format(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); //get start date
        String endDate = dateFormatter.format(cal.getTime());

        fDay = startDate;
        Log.d(TAG, "START "+fDay);
        lDay = endDate;
        Log.d(TAG, "END "+lDay);

        try {
            Date firstDay = dateFormatter.parse(startDate);
            Date lastDay = dateFormatter.parse(endDate);
            String firstDayString = simpleDateFormat.format(firstDay);
            String lastDayString = simpleDateFormat.format(lastDay);
            currentWeek = String.format("%s to %s",firstDayString,lastDayString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        String line = new String(messageEvent.getData());
        Log.d(TAG, line);

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = dateFormatter.format(todayDate);

        Log.d(TAG, todayDate.toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        int week = cal.get(Calendar.WEEK_OF_YEAR);

        Log.d(TAG, "WEEK " + String.valueOf(week));
        cal.set(Calendar.WEEK_OF_YEAR, week);   //set the calendar to that week
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); //get start date
        String startDate = dateFormatter.format(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); //get start date
        String endDate = dateFormatter.format(cal.getTime());

        Log.d(TAG, "START DATE: "+startDate);
        Log.d(TAG, "END DATE: "+endDate);

        String folderName = fDay+"_"+lDay;

        File activityFile = new File("/storage/emulated/0/Documents/activity_logs/" + folderName + "/" + todayString + ".txt");
        if (activityFile.exists()) {
            try {
                writer = new FileWriter(new File("/storage/emulated/0/Documents/activity_logs/" + folderName + "/" + todayString + ".txt"), true);
                writer.write(String.format("%s\n", line));
                writer.close();
                Log.d(TAG,"written");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                writer = new FileWriter(new File("/storage/emulated/0/Documents/activity_logs/" + folderName + "/" + todayString + ".txt"), true);
                writer.write(String.format("%s\n", line));
                writer.close();
                Log.d(TAG,"written");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}