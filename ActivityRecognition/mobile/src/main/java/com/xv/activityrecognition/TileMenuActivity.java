package com.xv.activityrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This activity displays a Tile Menu for users to choose what they want to view
 */

public class TileMenuActivity extends AppCompatActivity {

    private CardView weeklyHistory;
    private RecyclerView recyclerGridView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private TileMenuAdapter adapter;
    private TextView headerLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilemenu);

        recyclerGridView = (RecyclerView) findViewById(R.id.gridView);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerGridView.setLayoutManager(staggeredGridLayoutManager);
        List<GridViewItem> sList = getListItemData();
        adapter = new TileMenuAdapter(TileMenuActivity.this, sList);
        recyclerGridView.setAdapter(adapter);


        weeklyHistory = (CardView) findViewById(R.id.headerView);
        headerLabel = (TextView) findViewById(R.id.headerTitle);
        headerLabel.setText(R.string.weeklyHistory);
        weeklyHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weeklyStats = new Intent(getApplicationContext(), WeeklyStatsActivity.class);
                startActivity(weeklyStats);
            }
        });
    }

    private List<GridViewItem> getListItemData()
    {
        List<GridViewItem> listViewItems = new ArrayList<GridViewItem>();
        listViewItems.add(new GridViewItem(R.mipmap.ic_daily_image, R.string.dailyHistory, "George Orwell"));
        listViewItems.add(new GridViewItem(R.mipmap.ic_fitness_image, R.string.fitnessHistory, "Jane Austen"));
        listViewItems.add(new GridViewItem(R.mipmap.ic_food_image, R.string.foodHistory, "Gabriel Garcia Marquez"));
        listViewItems.add(new GridViewItem(R.mipmap.ic_location_image, R.string.locationHistory, "Markus Zusak"));

        return listViewItems;
    }

}
