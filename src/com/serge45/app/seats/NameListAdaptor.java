package com.serge45.app.seats;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NameListAdaptor extends BaseAdapter {
    class NameListItemViewHolder {
        private TextView numTextView;
        private TextView nameTextView;
        private TextView statusTextView;
        private ImageView photoImageView;
    };

    private List<StudentInfo> infoList;
    private LayoutInflater inflater;
    
    public NameListAdaptor(Context context) {
        infoList = new ArrayList<StudentInfo>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public void setInfoList(List<StudentInfo> l) {
        infoList = l;
    }

    public List<StudentInfo> getInfoList() {
        return infoList;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public Object getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return infoList.get(position).num;
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
        viewHolder.statusTextView = (TextView) convertView.findViewById(R.id.name_list_item_status);
        viewHolder.photoImageView = (ImageView) convertView.findViewById(R.id.name_list_item_photo); 
        viewHolder.numTextView.setText(String.valueOf(infoList.get(position).num));
        viewHolder.nameTextView.setText(infoList.get(position).name);
        return convertView;
    }

}
