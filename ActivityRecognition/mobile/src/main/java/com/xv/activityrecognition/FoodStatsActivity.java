package com.xv.activityrecognition;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.xv.activityrecognition.MainActivity.fDay;
import static com.xv.activityrecognition.MainActivity.lDay;

public class FoodStatsActivity extends AppCompatActivity {

    private static final String TAG = "MealTimeStatsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodstats);

        File parentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/activity_logs/");


        //Based on directory of file
        File currentWeekDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/activity_logs/"+fDay+"_"+lDay);
        File[] currentWeekFiles = currentWeekDir.listFiles();

        for (File f : currentWeekFiles) {
            String fname = f.toString();
            Log.d(TAG,fname);
        }

        UserProfiler up = new UserProfiler();

        up.getDay("2018-06-28.txt");
        double userMatch = up.processLog("2018-06-25.txt");
        userMatch = Math.round(userMatch*100.0)/100.0;
        Log.d(TAG, "User match: " + userMatch + "\n");

        up.getDay("2018-07-02.txt");
        userMatch = up.processLog("2018-07-02.txt");
        userMatch = Math.round(userMatch*100.0)/100.0;
        Log.d(TAG, "User match: " + userMatch + "\n");

        up.getDay("2018-07-09.txt");
        userMatch = up.processLog("2018-07-09.txt");
        userMatch = Math.round(userMatch*100.0)/100.0;
        Log.d(TAG, "User match: " + userMatch + "\n");
    }

    public String getSubDirOfWeek(String date) throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = dateFormatter.parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        int week = cal.get(Calendar.WEEK_OF_YEAR);

        //Log.d(TAG, "WEEK " + String.valueOf(week));
        cal.set(Calendar.WEEK_OF_YEAR, week);                       //set the calendar to that week
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);             //get start date
        String startDate = dateFormatter.format(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);   //get start date
        String endDate = dateFormatter.format(cal.getTime());

        String folderName = startDate+"_"+endDate;

        return folderName;
    }
}
