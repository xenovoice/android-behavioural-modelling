package com.xv.activityrecognition;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by User on 8/7/2018.
 */

public class TileMenuAdapter extends RecyclerView.Adapter<GridViewItemHolder>
{
        private List<GridViewItem> itemList;
        private GridViewItem headerItem;
        private Context context;

        public TileMenuAdapter(Context context, List<GridViewItem> itemList)
        {
            this.itemList = itemList;
            this.context = context;
        }

        @Override
        public GridViewItemHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_item, null);
            GridViewItemHolder rcv = new GridViewItemHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(GridViewItemHolder holder, int position)
        {
            holder.image.setImageResource(itemList.get(position).getImage());
            holder.bookName.setText(itemList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return this.itemList.size();
        }
}
