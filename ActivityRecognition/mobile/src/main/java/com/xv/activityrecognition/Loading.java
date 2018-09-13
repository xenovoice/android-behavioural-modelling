package com.xv.activityrecognition;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 7/7/2018.
 */

public class Loading extends AppCompatActivity {
    private static int TIME_OUT = 2000; //Time to launch the another activity
    Toolbar toolbar;
    ProgressLayout progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        toolbar = findViewById(R.id.toolbar);
        progressLayout = findViewById(R.id.progress);

        Drawable emptyDrawable = new IconDrawable(this, Iconify.IconValue.zmdi_shopping_basket)
                .colorRes(android.R.color.white);

        //Add which views you don't want to hide. In this case don't hide the toolbar
        List<Integer> skipIds = new ArrayList<>();
        skipIds.add(R.id.toolbar);

        String state = getIntent().getStringExtra("STATE");
        switch (state) {
            case "LOADING":
                progressLayout.showLoading();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getApplicationContext(), TileMenuActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, TIME_OUT);
                break;
            case "EMPTY":
                progressLayout.showEmpty(emptyDrawable,
                        "Empty Shopping Cart",
                        "Please add things in the cart to continue.", skipIds);
                break;
            case "CONTENT":
                progressLayout.showContent();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
