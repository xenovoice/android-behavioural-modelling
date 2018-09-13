package com.xv.activityrecognition;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 5/7/2018.
 */

class AdapterMainActivity extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DataModelMainActivity> items;
    private Context mContext;

    public AdapterMainActivity(ArrayList<DataModelMainActivity> data, Context context) {
        this.items = data;
        this.mContext = context;
    }

    public void addItem(DataModelMainActivity result) {
        items.add(result);
    }

    public void replaceItems(ArrayList<DataModelMainActivity> newItems) {
        this.items.clear();
        for(DataModelMainActivity item: newItems)
            this.items.add(item);
    }

    public void insertItem(DataModelMainActivity item) {
        items.add(0, item);
    }

    public void clearItems(){
        items.clear();
    }

    public void AddResults(ArrayList<DataModelMainActivity> result) {
        items.addAll(result);
    }

    public DataModelMainActivity getItemAt(int position){
        return  items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new MessageViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final DataModelMainActivity model = items.get(position);
        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        messageViewHolder.imageViewIcon.setBackgroundResource(model.getImage());
        messageViewHolder.textViewName.setText(model.getActivity());
        messageViewHolder.textViewDate.setText(model.getDate());

        ((MessageViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String resName = model.getName();
                //Log.d("resName", resName);
                Intent intent = new Intent(v.getContext(), ResultsFragment.class);
                intent.putExtra("resName", resName);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return  super.getItemViewType(position);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewIcon;
        TextView textViewName;
        TextView textViewDate;
        CardView cardView;

        private MessageViewHolder(View itemView, AdapterMainActivity adapter) {
            super(itemView);

            imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
            textViewName = (TextView) itemView.findViewById(R.id.textViewActivity);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}
