package com.xv.activityrecognition;

import android.icu.text.DateFormat;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.util.AndroidUtilsLight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static com.xv.activityrecognition.MainActivity.currentWeek;
import static com.xv.activityrecognition.MainActivity.fDay;
import static com.xv.activityrecognition.MainActivity.getCurrentWeek;
import static com.xv.activityrecognition.MainActivity.lDay;

/**
 * This activity is after MainActivity button onclick
 */

public class WeeklyStatsActivity extends AppCompatActivity {
    private static final String TAG = "WeeklyStatsActivity";

    private ListView resultsList;

    private ArrayList<String> resNames = new ArrayList<>();
    private ArrayList<Integer> resIds = new ArrayList<>();
    private ArrayList<String> resDates = new ArrayList<>();
    private TextView mCurrentWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeklystats);

        setTitle("Weekly Stats");
        mCurrentWeek = (TextView) findViewById(R.id.currentWeek);

        /*File activity_logs_dir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS),"/activity_logs/");
        File[] historyLogs = activity_logs_dir.listFiles();

        // Step 1: Read the names
        // Step 2: Read the dates
        for (int i = 0; i < historyLogs.length; i++) {
            Log.d(TAG, historyLogs[i].getName());
            resNames.add(historyLogs[i].getName());
            resIds.add(i);
            resDates.add(historyLogs[i].getName().substring(0, historyLogs[i].getName().lastIndexOf(".")));
        }*/

        mCurrentWeek.setText(currentWeek);

        Map<String, List<List>> activityResults = getWeeklyStats(fDay, lDay);
        setResults(activityResults);
    }

    public double activityDuration(String start, String end) {
        DateFormat stf = new android.icu.text.SimpleDateFormat("hh:mm:ss");
        DateFormat etf = new android.icu.text.SimpleDateFormat("hh:mm:ss");
        Long duration = null;
        try {
            Date startDateTime = stf.parse(start);
            Date endDateTime = etf.parse(end);
            duration = new Long((endDateTime.getTime() - startDateTime.getTime())/1000);     /* in seconds */
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return duration.doubleValue();
        }
    }

    public Map<String,List<List>> getWeeklyStats(String fDay, String lDay) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS)+"/activity_logs/"+fDay+"_"+lDay+"/");
        File[] files = dir.listFiles();
        //Log.d(TAG, Arrays.toString(files));

        List<String[]> activitiesUnFiltered = new ArrayList<>();
        Map<String,List<List>> activityHistory = new HashMap<String,List<List>>();

        for (File f : files) {
            try {
                FileInputStream is = new FileInputStream(f);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, line);
                    String[] resultArr = line.split(",");
                    activitiesUnFiltered.add(resultArr);
                }
                //isr.close();
                //is.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //int resId = this.getResources().getIdentifier(resourceName,"raw",this.getPackageName());
        //InputStream is = getResources().openRawResource(resId);
        //InputStreamReader isr = new InputStreamReader(is);
        //BufferedReader reader = new BufferedReader(isr);

        List<List> eatingDrinkingHist = new ArrayList<>();
        List<List> walkingHist = new ArrayList<>();
        List<List> runningHist = new ArrayList<>();
        List<List> jjHist = new ArrayList<>();
        activityHistory = new HashMap<String,List<List>>();

        String activity = null;
        String startTime = null;
        String endTime = null;
        double duration;

        for (int i = 0; i < activitiesUnFiltered.size()-1; i++) {
            List<Object> hist = new ArrayList<>();
            if (i == 0) {
                startTime = activitiesUnFiltered.get(i)[0];
                activity = activitiesUnFiltered.get(i)[1];
            }
            if (!(activitiesUnFiltered.get(i)[1].equals(activitiesUnFiltered.get(i+1)[1]))) {
                endTime = activitiesUnFiltered.get(i)[0];
                duration = activityDuration(startTime, endTime);
                hist.add(startTime);
                hist.add(duration);

                if (activity.equals("EatingDrinking")) {
                    eatingDrinkingHist.add(hist);
                }
                if (activity.equals("Walking")) {
                    walkingHist.add(hist);
                }
                if (activity.equals("Running")) {
                    runningHist.add(hist);
                }
                if (activity.equals("JumpingJack")) {
                    jjHist.add(hist);
                }

                //Log.d(TAG, Arrays.toString(activitiesUnFiltered.get(i)));
                //Log.d(TAG, Arrays.toString(activitiesUnFiltered.get(i+1)));

                startTime = activitiesUnFiltered.get(i+1)[0];
                activity = activitiesUnFiltered.get(i+1)[1];

            } else {
                // when it's the last activity
                if (i == activitiesUnFiltered.size() - 2) {
                    //Log.d(TAG, String.valueOf(i));
                    endTime = activitiesUnFiltered.get(i+1)[0];
                    duration = activityDuration(startTime, endTime);
                    hist.add(startTime);
                    hist.add(duration);

                    if (activity.equals("EatingDrinking")) {
                        eatingDrinkingHist.add(hist);
                    }
                    if (activity.equals("Walking")) {
                        walkingHist.add(hist);
                    }
                    if (activity.equals("Running")) {
                        runningHist.add(hist);
                    }
                    if (activity.equals("JumpingJack")) {
                        jjHist.add(hist);
                    }
                }
            }
        }

        activityHistory.put("Eating&Drinking",eatingDrinkingHist);
        activityHistory.put("Walking",walkingHist);
        activityHistory.put("Running",runningHist);
        activityHistory.put("JumpingJacks",jjHist);

        return activityHistory;
    }

    public void setResults(Map<String, List<List>> activityResults) {
        Map<String,Integer> map = new HashMap<>();
        for (Map.Entry<String, List<List>> entry : activityResults.entrySet()) {
            String activity = entry.getKey();
            int activityCount = entry.getValue().size();
            for (Object o : entry.getValue()) {
                Log.d(TAG,String.valueOf(o));
            }
            map.put(activity,activityCount);
        }

        //Log.d(TAG, String.valueOf(map));
        resultsList = (ListView) findViewById(R.id.weeklyStatsListView);
        AdapterWeeklyStats adapter = new AdapterWeeklyStats(this, map);
        resultsList.setAdapter(adapter);
    }
}
