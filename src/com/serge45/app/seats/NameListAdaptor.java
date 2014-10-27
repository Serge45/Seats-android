package com.serge45.app.seats;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class NameListAdaptor extends BaseAdapter {
    class NameListItemViewHolder {
        private TextView numTextView;
        private TextView nameTextView;
    };

    private List<Pair<Integer, String> > numToNameList;

    private LayoutInflater inflater;
    
    public NameListAdaptor(Context context) {
        numToNameList = new ArrayList<Pair<Integer,String>>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public void setNumToNameList(List<Pair<Integer, String> > l) {
        numToNameList = l;
    }

    public List<Pair<Integer, String>> getNumToNameList() {
        return numToNameList;
    }

    @Override
    public int getCount() {
        return numToNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return numToNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return numToNameList.get(position).first.longValue();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NameListItemViewHolder viewHolder = null;
        
        if (convertView == null) {
            viewHolder = new NameListItemViewHolder();
            convertView = inflater.inflate(R.layout.name_list_item, parent, false);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NameListItemViewHolder) convertView.getTag();
        }
        viewHolder.numTextView = (TextView) convertView.findViewById(R.id.name_list_item_num);
        viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_list_item_name);
        viewHolder.numTextView.setText(numToNameList.get(position).first.toString());
        viewHolder.nameTextView.setText(numToNameList.get(position).second);
        return convertView;
    }

}
