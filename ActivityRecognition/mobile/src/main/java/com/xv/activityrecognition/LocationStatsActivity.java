package com.xv.activityrecognition;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by User on 10/7/2018.
 */

public class LocationStatsActivity extends AppCompatActivity implements SensorEventListener {

    private Sensor linear_sensor, magnetic_sensor, accel_sensor;
    private SensorManager SM;

    Vibrator vibrator;
    long[] vibrationStartPattern = {0, 50, 5, 30};
    final int indexInPatternToRepeat = -1;

    private TextView tv_result;
    private Button btn_Record, btn_Stop;

    float angle_value = 0;
    float distance_value = 0;
    float Arm_length = 0;

    private Handler handler = new Handler();
    int prepare_seconds = 30; //3 seconds before start
    int milliseconds = 11; //1 second
    float accelerometer_values_list[] = new float[11];
    float accelerometer_list[] = new float[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationstats);

        tv_result = (TextView) findViewById(R.id.tvResults);
        btn_Record = (Button) findViewById(R.id.btnRecord);
        btn_Stop = (Button) findViewById(R.id.btnStop);

        //Create Sensor Manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Accelerometer Sensor
        accel_sensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linear_sensor = SM.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //Register Sensor Listener
        SM.registerListener(this, linear_sensor, 20000);
        SM.registerListener(this,accel_sensor,20000);

        btn_Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });

        btn_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Start of recording timer after 3 seconds delay
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(prepare_seconds>0){
                            prepare_seconds--;
                            //Log.d("TIMME","prepare_seconds: " + prepare_seconds);
                            handler.postDelayed(this,100);
                        }
                        else
                        {
                            //Timer Complete
                            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                            vibrator.vibrate(vibrationStartPattern, indexInPatternToRepeat);
                            //Starts recording
                            record();

                            Arm_length = calArmLength(angle_value,distance_value);

                            Log.d("Arm_length",""+Arm_length);
                            if((Arm_length > 0.25) && (Arm_length < 0.70)){
                                tv_result.setText("-Results-\n Angle:" +angle_value + "\nArc Length(m):" + distance_value + "\nArm Length(m):" +calArmLength(angle_value,distance_value));
                            } else {
                                tv_result.setText("Press Stop & Please re-swing.");
                            }

                        }
                    }
                });

            }
        });

    }

    public void record(){
        //Start of recording timer
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(milliseconds>0){
                    milliseconds--;
                    //Log.d("TIMME","milliseconds: " + milliseconds);
                    handler.postDelayed(this,100);
                }
                else
                {
                    distance_value = calculateDistance();
                    angle_value = calculateAngle(accelerometer_list[0],accelerometer_list[1]);

                }
            }
        });


    }

    public float calculateAngle(float first_y, float last_y) {
        float first_angle, second_angle, angle;
        //θ = sin^-1 (Measured Acceleration / Gravity Acceleration)
        first_angle = (float) Math.asin((first_y)/9.81);
        if (last_y<0){
            //angle is negative
            last_y = last_y * -1.0F;
        }
        second_angle = (float) Math.asin((last_y)/9.81);
        angle = first_angle + second_angle;

        //Convert angle radian to degree
        return (float) Math.toDegrees(angle);
    }



    public float calArmLength(float angle, float arcLength){
        float ArmL = (float) ((arcLength*360F) / (angle * 2F * Math.PI));
        return ArmL;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if (milliseconds == 1) {
                accelerometer_list[0] = sensorEvent.values[1];
            } else if (milliseconds == 10) {
                accelerometer_list[1] = sensorEvent.values[1];
            }

            if ((accelerometer_list[0]!= 0.0)||(accelerometer_list[1]!= 0.0)) {
                Log.d("ACCELEE", "values 1:" + accelerometer_list[0]);
                Log.d("ACCELEE", "values 2:" + accelerometer_list[1]);
            }
        } else if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            //Every 100ms, Retrieve Accelerometer Value
            if (milliseconds == 0) {
                accelerometer_values_list[0] = sensorEvent.values[1];
            } else if (milliseconds == 1) {
                accelerometer_values_list[1] = sensorEvent.values[1];
            } else if (milliseconds == 2) {
                accelerometer_values_list[2] = sensorEvent.values[1];
            } else if (milliseconds == 3) {
                accelerometer_values_list[3] = sensorEvent.values[1];
            } else if (milliseconds == 4) {
                accelerometer_values_list[4] = sensorEvent.values[1];
            } else if (milliseconds == 5) {
                accelerometer_values_list[5] = sensorEvent.values[1];
            } else if (milliseconds == 6) {
                accelerometer_values_list[6] = sensorEvent.values[1];
            } else if (milliseconds == 7) {
                accelerometer_values_list[7] = sensorEvent.values[1];
            } else if (milliseconds == 8) {
                accelerometer_values_list[8] = sensorEvent.values[1];
            } else if (milliseconds == 9) {
                accelerometer_values_list[9] = sensorEvent.values[1];
            } else if (milliseconds == 10) {
                accelerometer_values_list[10] = sensorEvent.values[1];
            } else {
                //Log.d("TIMME","TIMMER NOT 0 or 9");
            }


        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public float calculateDistance(){

        float distance = 0;

        if (accelerometer_values_list!=null){
            for(int counter=accelerometer_values_list.length - 1; counter >= 0;counter--) {
                distance += accelerometer_values_list[counter] * 0.1 * 0.1;
                //Log.d("DISTANCEEE","value: " +accelerometer_values_list[counter] * 0.1 * 0.1);
            }
        }
        if (distance < 0.0) {
            distance = distance * -1.0F;
        }
        return distance;
    }
}
