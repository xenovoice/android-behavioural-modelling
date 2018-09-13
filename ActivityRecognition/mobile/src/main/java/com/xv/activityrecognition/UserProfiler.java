package com.xv.activityrecognition;

import android.icu.util.Calendar;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserProfiler {
    private final static String TAG = "UserProfile";

    boolean isUser;
    String dayOfWeek;
    List<String> monday = new ArrayList<String>();
    List<String> tuesday = new ArrayList<String>();
    List<String> wednesday = new ArrayList<String>();
    List<String> thursday = new ArrayList<String>();
    List<String> friday = new ArrayList<String>();
    List<String> saturday = new ArrayList<String>();
    List<String> sunday = new ArrayList<String>();


    public void getDay(String logFile) {
        try {
            String fileName = logFile.replaceFirst("[.][^.]+$", "");
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = ymd.parse(fileName);
            ymd.applyPattern("EEEE");
            dayOfWeek = ymd.format(dt);
            Log.d(TAG, "Day of log is: " + dayOfWeek);
        } catch (Exception e) {}

    }


    public double processLog(String logFile) {
        List<String> dailyActivities = new ArrayList<String>();
        List<String> mealTimes = new ArrayList<String>();
        double userMatch = 0.0;
        int matchCount = 0;

        //Based on directory of file
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/"+logFile);

        String fileName = logFile.replaceFirst("[.][^.]+$", "");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))){
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // System.out.println("Time: "+ values[0] + "\tActivity: " + values[1]);
                dailyActivities.add(values[0]+","+values[1]);
                // activities.add(values[1]);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        try (BufferedReader br = Files.newBufferedReader(Paths.get(logFile), StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null;) {
                String[] values = line.split(",");
                // System.out.println("Time: "+ values[0] + "\tActivity: " + values[1]);
                dailyActivities.add(values[0]+","+values[1]);
                // activities.add(values[1]);
            }
        } catch (Exception e) {}
        */

        for (String da: dailyActivities) {
            String[] temp = da.split(",");
            String time = temp[0];
            String activity = temp[1];

            if (activity.equals("EatingDrinking")) {
                String hour = time.substring(0, 2);
                if (!mealTimes.contains(hour)) {
                    mealTimes.add(hour);
                }
            }
        }

        Log.d(TAG,"Meal Times: ");
        for (String mt: mealTimes) {
            Log.d(TAG,mt + "\t");
        }
        Log.d(TAG, "");

        switch(dayOfWeek) {
            case "Monday":
                if (monday.isEmpty())
                {
                    monday = mealTimes;
                }
                else
                {
                    for (int x = 0; x < monday.size(); x++)
                    {
                        for (int y = 0; y < mealTimes.size(); y++ )
                        {
                            if (monday.get(x).equals(mealTimes.get(y)))
                            {
                                matchCount++;
                            }
                        }
                    }
                    if (monday.size() <= mealTimes.size())
                    {
                        userMatch = (double)matchCount / (double)mealTimes.size();
                    }
                    else
                    {
                        userMatch = (double)matchCount / (double)monday.size();
                    }
                }
                break;
            case "Tuesday":
                if (tuesday.isEmpty())
                {
                    tuesday = mealTimes;
                }
                else
                {
                    for (int x = 0; x < tuesday.size(); x++)
                    {
                        for (int y = 0; y < mealTimes.size(); y++ )
                        {
                            if (tuesday.get(x).equals(mealTimes.get(y)))
                            {
                                matchCount++;
                            }
                        }
                    }
                    if (tuesday.size() <= mealTimes.size())
                    {
                        userMatch = (double)matchCount / (double)mealTimes.size();
                    }
                    else
                    {
                        userMatch = (double)matchCount / (double)tuesday.size();
                    }
                }
                break;
            case "Wednesday":
                if (wednesday.isEmpty())
                {
                    wednesday = mealTimes;
                }
                else
                {
                    for (int x = 0; x < wednesday.size(); x++)
                    {
                        for (int y = 0; y < mealTimes.size(); y++ )
                        {
                            if (wednesday.get(x).equals(mealTimes.get(y)))
                            {
                                matchCount++;
                            }
                        }
                    }
                    if (wednesday.size() <= mealTimes.size())
                    {
                        userMatch = (double)matchCount / (double)mealTimes.size();
                    }
                    else
                    {
                        userMatch = (double)matchCount / (double)wednesday.size();
                    }
                }
                break;
            case "Thursday":
                if (thursday.isEmpty())
                {
                    thursday = mealTimes;
                }
                else
                {
                    for (int x = 0; x < thursday.size(); x++)
                    {
                        for (int y = 0; y < mealTimes.size(); y++ )
                        {
                            if (thursday.get(x).equals(mealTimes.get(y)))
                            {
                                matchCount++;
                            }
                        }
                    }
                    if (thursday.size() <= mealTimes.size())
                    {
                        userMatch = (double)matchCount / (double)mealTimes.size();
                    }
                    else
                    {
                        userMatch = (double)matchCount / (double)thursday.size();
                    }
                }
                break;
            case "Friday":
                if (friday.isEmpty())
                {
                    friday = mealTimes;
                }
                else
                {
                    for (int x = 0; x < friday.size(); x++)
                    {
                        for (int y = 0; y < mealTimes.size(); y++ )
                        {
                            if (friday.get(x).equals(mealTimes.get(y)))
                            {
                                matchCount++;
                            }
                        }
                    }
                    if (friday.size() <= mealTimes.size())
                    {
                        userMatch = (double)matchCount / (double)mealTimes.size();
                    }
                    else
                    {
                        userMatch = (double)matchCount / (double)friday.size();
                    }
                }
                break;
            case "Saturday":
                if (saturday.isEmpty())
                {
                    saturday = mealTimes;
                }
                else
                {
                    for (int x = 0; x < saturday.size(); x++)
                    {
                        for (int y = 0; y < mealTimes.size(); y++ )
                        {
                            if (saturday.get(x).equals(mealTimes.get(y)))
                            {
                                matchCount++;
                            }
                        }
                    }
                    if (saturday.size() <= mealTimes.size())
                    {
                        userMatch = (double)matchCount / (double)mealTimes.size();
                    }
                    else
                    {
                        userMatch = (double)matchCount / (double)saturday.size();
                    }
                }
                break;
            case "Sunday":
                if (sunday.isEmpty())
                {
                    sunday = mealTimes;
                }
                else {
                    if (sunday.equals(mealTimes))
                    {
                        userMatch = 1.0;
                    }
                    else
                    {
                        for (int x = 0; x < sunday.size(); x++)
                        {
                            for (int y = 0; y < mealTimes.size(); y++ )
                            {
                                if (sunday.get(x).equals(mealTimes.get(y)))
                                {
                                    matchCount++;
                                }
                            }
                        }
                        if (sunday.size() <= mealTimes.size())
                        {
                            userMatch = (double)matchCount / (double)mealTimes.size();
                        }
                        else
                        {
                            userMatch = (double)matchCount / (double)sunday.size();
                        }
                    }
                }
                break;
            default:
                userMatch = 0.0;
        }

        return userMatch;

    }
}
