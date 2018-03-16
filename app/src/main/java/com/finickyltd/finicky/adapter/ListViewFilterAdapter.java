package com.finickyltd.finicky.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.finickyltd.finicky.models.ListViewFilterItem;
import com.finickyltd.finicky.R;

public class ListViewFilterAdapter extends BaseAdapter {
    private ArrayList<ListViewFilterItem> listViewFilterItems = new ArrayList<ListViewFilterItem>();
    private Boolean status = true;  // true is collapsed, false is expanded
    public ListViewFilterAdapter(){

    }

    @Override
    public int getCount() {
        return listViewFilterItems.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.filter_item, parent, false);
        }
        final ImageView iconImageView = (ImageView) convertView.findViewById(R.id.ivExpandCollapse);
        //LinearLayout titleContainer = (LinearLayout) convertView.findViewById(R.id.lnTitleContainer);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.tvFilterName);
        final TextView descTextView = (TextView) convertView.findViewById(R.id.tvFilterIngredient);
        final LinearLayout lnTxtContainer = (LinearLayout)convertView.findViewById(R.id.lnTxtContainer);
        LinearLayout lnTitlleContainer = (LinearLayout) convertView.findViewById(R.id.lnTitleContainer);
        final ListViewFilterItem listViewItem = listViewFilterItems.get(position);

        iconImageView.setImageDrawable(listViewItem.getIconExpandCollapse());
        titleTextView.setText(listViewItem.getFilterName());
        descTextView.setText(listViewItem.getFilterIngredient());
        lnTitlleContainer.setBackgroundColor(context.getResources().getColor(listViewItem.getBgColor()));

        if(listViewItem.getVisible() == true)
            iconImageView.setVisibility(View.VISIBLE);
        else
            iconImageView.setVisibility(View.INVISIBLE);



        lnTitlleContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if it is suitable, user cant click the container.
                if(listViewItem.getBgColor() == R.color.suitable){
                    Log.d("suitable", "user can not click this item");
                }
                else {
                    if(status){
                        lnTxtContainer.setVisibility(View.VISIBLE);
                        iconImageView.setImageResource(R.drawable.collapse);
                        status = false;
                    }
                    else {
                        lnTxtContainer.setVisibility(View.GONE);
                        iconImageView.setImageResource(R.drawable.expand);
                        status = true;
                    }
                }
            }
        });
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewFilterItems.get(position);
    }

    public void addItem(int color_id, boolean visible,Drawable icon, String title, String desc){
        ListViewFilterItem item = new ListViewFilterItem();

        item.setIconExpandCollapse(icon);
        item.setFilterName(title);
        item.setBgColor(color_id);
        item.setVisible(visible);
        if(desc == null)
            desc = "";
        item.setFilterIngredient(desc);
        listViewFilterItems.add(item);
    }
}
