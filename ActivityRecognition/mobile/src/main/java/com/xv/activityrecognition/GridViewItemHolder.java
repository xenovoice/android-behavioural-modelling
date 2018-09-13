package com.xv.activityrecognition;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by User on 8/7/2018.
 */

public class GridViewItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public ImageView image;
    public TextView bookName;
    //private TextView authorName;

    public GridViewItemHolder(View itemView)
    {
        super(itemView);
        itemView.setOnClickListener(this);
        image = (ImageView) itemView.findViewById(R.id.menuImage);
        bookName = (TextView) itemView.findViewById(R.id.bookName);
        //authorName = (TextView) itemView.findViewById(R.id.AuthorName);
    }

    @Override
    public void onClick(View view)
    {
        Context context = view.getContext();
        switch (getAdapterPosition()) {
            case 0:
                Intent dailyStats = new Intent(context, DailyStatsActivity.class);
                context.startActivity(dailyStats);
                break;
            case 1:
                Intent fitnessStats = new Intent(context, FitnessStatsActivity.class);
                context.startActivity(fitnessStats);
                break;
            case 2:
                Intent foodStats = new Intent(context, FoodStatsActivity.class);
                context.startActivity(foodStats);
                break;
            case 3:
                Intent locationStats = new Intent(context, LocationStatsActivity.class);
                context.startActivity(locationStats);
                break;
        }
        /*Toast.makeText(view.getContext(),
                "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT)
                .show();*/
    }
}