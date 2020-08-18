package com.example.scanner.bc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.scanner.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> listItems = new ArrayList<>();

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        CustomViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_items, null, false);

            holder = new CustomViewHolder();
            holder.prod_name = (TextView)convertView.findViewById(R.id.prod_name);
            holder.price = (TextView)convertView.findViewById(R.id.price);
            convertView.setTag(holder);
        }
        else {
            holder = (CustomViewHolder) convertView.getTag();
        }

        final ListViewItem item = listItems.get(position);

        holder.price.setText(new DecimalFormat("###,###").format(item.getPrice())+" 원");
        holder.prod_name.setText(item.getProd_name());

        return convertView;

    }

    class CustomViewHolder {
        TextView prod_name;
        TextView price;
    }

    public void addItem(ListViewItem item) {
        listItems.add(item);
    }

    public void clear() {
        listItems.clear();
    }

    public void remove(int position) {
        listItems.remove(position);
    }
}
