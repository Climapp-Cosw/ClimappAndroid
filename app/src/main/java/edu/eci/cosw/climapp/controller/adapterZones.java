package edu.eci.cosw.climapp.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.Zone;

public class adapterZones extends ArrayAdapter {
    Context context;
    String[ ]modelItems;
    ArrayList<Zone> modelItems2;
    @SuppressWarnings("unchecked")

    public adapterZones(Context context, String[ ] resource)
    {
        super(context,R.layout.row,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
    }
    public adapterZones(Context context, ArrayList<Zone> resource)
    {
        super(context,R.layout.row,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems2 = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.namezone1);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
        if(modelItems!=null){
            name.setText(String.valueOf(position));
            cb.setText(modelItems[position]);
        }else {
            name.setText(String.valueOf(modelItems2.get(position).getNumber()));
            cb.setText(modelItems2.get(position).getName());

        }


        return convertView;
    }
}

