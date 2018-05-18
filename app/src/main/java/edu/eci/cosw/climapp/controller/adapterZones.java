package edu.eci.cosw.climapp.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.Zone;
import edu.eci.cosw.climapp.network.NetworkException;
import edu.eci.cosw.climapp.network.RequestCallback;
import edu.eci.cosw.climapp.network.RetrofitNetwork;

public class adapterZones extends ArrayAdapter {
    Context context;
    ArrayList<Zone> modelItems;
    ArrayList<String> modelItemsFavorites;
    @SuppressWarnings("unchecked")


    public adapterZones(Context context, ArrayList<Zone> resource, ArrayList<Zone> resource2) {
        super(context,R.layout.row,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
        this.modelItemsFavorites = new ArrayList<>();
        for (int i=0;i<resource2.size();i++){
            modelItemsFavorites.add(resource2.get(i).getName());
        }


    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.namezone1);
        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
        name.setText(String.valueOf(modelItems.get(position).getNumber()));
        cb.setText(modelItems.get(position).getName());

        if(modelItemsFavorites.contains(modelItems.get(position).getName())){
            cb.setChecked(true);

        }
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb.isChecked()){
                    addZoneFavorite(modelItems.get(position));
                }else{
                    deleteZoneFavorite(modelItems.get(position));
                }
            }
        });


        return convertView;
    }
    public void addZoneFavorite(Zone z){
        SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        final RetrofitNetwork rfN = new RetrofitNetwork();
        rfN.addFavoriteZones(settings.getString("userEmail",""),z,new RequestCallback<List<Zone>>(){
            @Override
            public void onSuccess(final List<Zone> response) {
                modelItemsFavorites = new ArrayList<>();
                for (int i=0;i<response.size();i++){
                    modelItemsFavorites.add(response.get(i).getName());
                }
            }
            @Override
            public void onFailed(NetworkException e) {

            }
        },settings.getString(LoginActivity.TOKEN_NAME, ""));
    }

    public void deleteZoneFavorite(Zone z){
        SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        final RetrofitNetwork rfN = new RetrofitNetwork();
        rfN.deleteFavoriteZones(settings.getString("userEmail",""),z,new RequestCallback<List<Zone>>(){
            @Override
            public void onSuccess(final List<Zone> response) {
                modelItemsFavorites = new ArrayList<>();
                for (int i=0;i<response.size();i++){
                    modelItemsFavorites.add(response.get(i).getName());
                }
            }
            @Override
            public void onFailed(NetworkException e) {

            }
        },settings.getString(LoginActivity.TOKEN_NAME, ""));
    }

}

