package com.xv.activityrecognition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by User on 12/7/2018.
 */

public class AdapterWeeklyStats extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private final ArrayList mData;

    public AdapterWeeklyStats(Context context, Map<String, Integer> map) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, Integer> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map.Entry<String, Integer> item = getItem(position);

        //result = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        convertView = mLayoutInflater.inflate(R.layout.item_weeklystats,null);
        TextView tv1 = ((TextView) convertView.findViewById(R.id.label2));
        RoundCornerProgressBar tv2 = ((RoundCornerProgressBar) convertView.findViewById(R.id.progressBar));
        ImageView activityImage = ((ImageView) convertView.findViewById(R.id.activityImage));

        if (item.getKey().equals("Eating&Drinking")) {
            activityImage.setImageResource(R.mipmap.ic_food);
            tv1.setText(String.valueOf(item.getValue() + " meals"));
        }
        else if (item.getKey().equals("Walking")) {
            activityImage.setImageResource(R.mipmap.ic_walking);
            tv1.setText("Walked " + String.valueOf(item.getValue()) + " times");
        }
        else if (item.getKey().equals("Running")) {
            activityImage.setImageResource(R.mipmap.ic_running);
            tv1.setText("Ran " + String.valueOf(item.getValue()) + " times");
        }
        if (item.getKey().equals("JumpingJacks")) {
            activityImage.setImageResource(R.mipmap.ic_jumpingjack);
            tv1.setText("Did " + String.valueOf(item.getValue()) + " sets of jumping jacks");
        }


        tv2.setProgress(item.getValue());

        return convertView;
    }
}
