package com.xv.activityrecognition;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
//import android.speech.tts.TextToSpeech;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * This is the MainActivity
 */

//public class MainActivity extends WearableActivity implements SensorEventListener, TextToSpeech.OnInitListener{
public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final int N_SAMPLES = 200;
    private static List<Float> x;
    private static List<Float> y;
    private static List<Float> z;
    private TextView eatingdrinkingTextView;
    private TextView jumpingjackTextView;
    private TextView runningTextView;
    private TextView walkingTextView;
    Button sendButton;

    private float[] results;
    private TensorFlowClassifier classifier;
    final String TAG = "ActivityRecognition";
    FileWriter writer;
    String datapath = "/message_path";
    File activity_logs_folder;
    String todayString;

    private String[] labels = {"Eating&Drinking", "Walking", "Running", "Jumping Jack"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();

        activity_logs_folder = new File(Environment.getExternalStorageDirectory(),"activity_logs");
        if (!activity_logs_folder.exists()) {
            activity_logs_folder.mkdirs();
        }

        Log.d(TAG, "Writing to " + activity_logs_folder);
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        todayString = dateFormatter.format(todayDate);
//        try {
//            writer = new FileWriter(new File(activity_logs_folder, todayString + ".txt"), true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        eatingdrinkingTextView = (TextView) findViewById(R.id.eatingdrinking_prob);
        walkingTextView = (TextView) findViewById(R.id.walking_prob);
        jumpingjackTextView = (TextView) findViewById(R.id.jumpingjack_prob);
        runningTextView = (TextView) findViewById(R.id.running_prob);

        classifier = new TensorFlowClassifier(getApplicationContext());

        sendButton = findViewById(R.id.sendbutton);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FileInputStream inputStream;
                BufferedReader reader;
                String activityFileLocation = activity_logs_folder + "/" +todayString + ".txt";
                File activityFile = new File(activityFileLocation);
                Log.d(TAG, activityFileLocation);
                try {
                    inputStream = new FileInputStream(activityFile);
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while((line = reader.readLine()) != null) {
                        new SendThread(datapath, line).start();
                    }
                    reader.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

//        textToSpeech = new TextToSpeech(this, this);
//        textToSpeech.setLanguage(Locale.US);
    }

//    @Override
//    public void onInit(int status) {
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (results == null || results.length == 0) {
//                    return;
//                }
//                float max = -1;
//                int idx = -1;
//                for (int i = 0; i < results.length; i++) {
//                    if (results[i] > max) {
//                        idx = i;
//                        max = results[i];
//                    }
//                }
//
//                textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
//            }
//        }, 2000, 5000);
//    }

    protected void onPause() {
        getSensorManager().unregisterListener(this);
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
//        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 50000);
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

            eatingdrinkingTextView.setText(Float.toString(round(results[0], 2)));
            walkingTextView.setText(Float.toString(round(results[3], 2)));
            runningTextView.setText(Float.toString(round(results[2], 2)));
            jumpingjackTextView.setText(Float.toString(round(results[1], 2)));

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
                    activity = "EatingDrinking";
                    break;
                case 1:
                    activity = "JumpingJack";
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
                writer = new FileWriter(new File(activity_logs_folder, todayString + ".txt"), true);
                writer.write(String.format("%s,%s\n",timeString, activity));
                writer.close();
                Log.d(TAG,activity);
            } catch (IOException e) {
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

    //This sends the message to the wearable device.
    class SendThread extends Thread {
        String path;
        String message;

        //constructor
        SendThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            Log.d(TAG, "run");
            try {
                List<Node> nodes = Tasks.await(nodeListTask);
                Log.d(TAG, "nodes0");
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());
                    Log.d(TAG, "nodes1");
                    try {
                        Integer result = Tasks.await(sendMessageTask);
                        Log.v(TAG, "SendThread: message send to " + node.getDisplayName());
                        Log.d(TAG, String.valueOf(result));

                    } catch (ExecutionException exception) {
                        Log.e(TAG, "Task failed: " + exception);

                    } catch (InterruptedException exception) {
                        Log.e(TAG, "Interrupt occurred: " + exception);
                    }

                }

            } catch (ExecutionException exception) {
                Log.e(TAG, "Task failed: " + exception);

            } catch (InterruptedException exception) {
                Log.e(TAG, "Interrupt occurred: " + exception);
            }
        }
    }
}
