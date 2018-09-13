package com.xv.activityrecognition;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

/**
 * This is the activity after DailyStatsActivity onclick,
 * that displays a user's performed activities throughout a day
 */

public class ResultsFragment extends AppCompatActivity {
    private static final String TAG = "ResultsFragment";
    private ListView resultsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_results);

        getIncomingIntent();
    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");

        if(getIntent().hasExtra("resName")){
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            String resourceName = getIntent().getStringExtra("resName");
            Map<String, List<List>> activityResults = getActivityResults(resourceName);
            setResults(activityResults);
        }
    }

    private double activityDuration(String start, String end) {
        DateFormat stf = new SimpleDateFormat("hh:mm:ss");
        DateFormat etf = new SimpleDateFormat("hh:mm:ss");
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

    private Map<String,List<List>> getActivityResults(String resourceName) {
        File f = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "/activity_logs/"+resourceName);
        //int resId = this.getResources().getIdentifier(resourceName,"raw",this.getPackageName());
        //InputStream is = getResources().openRawResource(resId);
        //InputStreamReader isr = new InputStreamReader(is);
        //BufferedReader reader = new BufferedReader(isr);

        List<String[]> activitiesUnFiltered = new ArrayList<>();

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

        List<List> eatingDrinkingHist = new ArrayList<>();
        List<List> walkingHist = new ArrayList<>();
        List<List> runningHist = new ArrayList<>();
        List<List> jjHist = new ArrayList<>();
        Map<String,List<List>> activityHistory = new HashMap<String,List<List>>();

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
                /** when it's the last activity **/
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

    private void setResults(Map<String, List<List>> activityResults) {
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
        resultsList = (ListView) findViewById(android.R.id.list);
        AdapterResultsFragment adapter = new AdapterResultsFragment(this, map);
        resultsList.setAdapter(adapter);
    }
}
