package com.xv.activityrecognition;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static com.xv.activityrecognition.MainActivity.fDay;
import static com.xv.activityrecognition.MainActivity.lDay;


/**
 * Displays daily activity stats
 */

public class DailyStatsActivity extends AppCompatActivity {
    private static final String TAG = "DailyStatsActivity";

    GridLayoutManager gridLayoutManager;
    RecyclerView recyclerView;
    AdapterMainActivity mAdapter;
    ArrayList<DataModelMainActivity> history_activities;

    private ArrayList<String> resNames = new ArrayList<>();
    private ArrayList<Integer> resIds = new ArrayList<>();
    private ArrayList<String> resDates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailystats);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        int activity_logo = R.mipmap.ic_activity;

        setTitle("Daily Stats");

        File activity_logs_dir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS)+"/activity_logs/"+fDay+"_"+lDay+"/");
        File[] historyLogs = activity_logs_dir.listFiles();

        Arrays.sort(historyLogs, Collections.reverseOrder());

        // Step 1: Read the names
        // Step 2: Read the dates
        for (int i = 0; i < historyLogs.length; i++) {
            Log.d(TAG, historyLogs[i].getName());
            resNames.add(historyLogs[i].getName());
            resIds.add(i);
            resDates.add(historyLogs[i].getName().substring(0, historyLogs[i].getName().lastIndexOf(".")));
        }

        history_activities = new ArrayList<DataModelMainActivity>();
        int j = resIds.size();
        for (int i = 0; i < resIds.size(); i++) {
            history_activities.add(new DataModelMainActivity(activity_logo, resNames.get(i), resIds.get(j-1), resDates.get(i)));
            j--;
        }

        mAdapter = new AdapterMainActivity(history_activities, DailyStatsActivity.this);
        recyclerView.setAdapter(mAdapter);
    }
}
