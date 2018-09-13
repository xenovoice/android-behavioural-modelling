package com.xv.activityrecognition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by User on 5/7/2018.
 */

public class AdapterResultsFragment extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private final ArrayList mData;

    public AdapterResultsFragment(Context context, Map<String, Integer> map) {
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
        convertView = mLayoutInflater.inflate(R.layout.item_frag_result,null);
        TextView tv1 = ((TextView) convertView.findViewById(R.id.textView1));
        tv1.setText(item.getKey());
        TextView tv2 = ((TextView) convertView.findViewById(R.id.textView2));
        tv2.setText(String.valueOf(item.getValue()));

        return convertView;
    }
}
