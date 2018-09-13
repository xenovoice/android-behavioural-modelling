package com.xv.activityrecognition;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 5/7/2018.
 */

class DataModelMainActivity {
    private final String TAG = "DataModelMainActivity";
    private int image;
    private String orgResName;
    private String resId;
    private String date;

    public DataModelMainActivity(int image, String name, int id, String date) {
        this.image = image;
        this.orgResName = name;
        this.resId = formatName(id);
        this.date = formatDate(date);
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return orgResName;
    }

    public String getActivity() {
        return resId;
    }

    public void setActivity(String activity) {
        this.resId = activity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String formatName(int name) {
        //String[] strings = name.split("_");
        //char count = strings[0].charAt(strings[0].length()-1);
        String new_name = "Activity History " + Integer.toString(name+1);
        Log.d(TAG, new_name);
        return new_name;
    }

    public String formatDate(String date) {
        /*List<String> strings = new ArrayList<String>();
        int index = 0;
        while (index < date.length()) {
            strings.add(date.substring(index, Math.min(index + 2,date.length())));
            index += 2;
        }*/
        String new_date = "Recorded at " + date;
        return new_date;
    }
}
