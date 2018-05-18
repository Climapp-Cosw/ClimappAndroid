package edu.eci.cosw.climapp.controller;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.Zone;
import edu.eci.cosw.climapp.network.NetworkException;
import edu.eci.cosw.climapp.network.RequestCallback;
import edu.eci.cosw.climapp.network.RetrofitNetwork;

/**
 * Created by LauraRB on 11/05/2018.
 */

public class fragmentFavoriteZones extends Fragment {
    private View view;
    private View view2;
    private ListView list;
    private String[] data ;
    private adapterZones adapterZones;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_zones, container, false);
        view2 = inflater.inflate(R.layout.row, container, false);
        list = (ListView) view.findViewById(R.id.listZones);
        getZones();


        return view;
    }
    private void click(){


    }
    private void getZones(){
        ((ProgressBar)view.findViewById(R.id.progressBar2)).setVisibility(view.VISIBLE);
        final RetrofitNetwork rfN = new RetrofitNetwork();
        rfN.getZones( new RequestCallback<List<Zone>>() {
            @Override
            public void onSuccess(final List<Zone> response) {
                SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                rfN.getFavoriteZones(settings.getString("userEmail",""),new RequestCallback<List<Zone>>(){
                    @Override
                    public void onSuccess(final List<Zone> response2) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                adapterZones= new adapterZones(getContext(), (ArrayList<Zone>) response,(ArrayList<Zone>) response2);
                                list.setAdapter(adapterZones);
                                ((ProgressBar)view.findViewById(R.id.progressBar2)).setVisibility(view.GONE);
                            }
                        });
                    }
                    @Override
                    public void onFailed(NetworkException e) {

                    }
                },settings.getString(LoginActivity.TOKEN_NAME, ""));


            }
            @Override
            public void onFailed(NetworkException e) {
                Toast.makeText(getContext(), "Invalid User.",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
