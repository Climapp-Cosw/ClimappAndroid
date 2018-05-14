package edu.eci.cosw.climapp.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
    private ListView list;
    private ArrayList<Zone> data ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_zones, container, false);
        list = (ListView) view.findViewById(R.id.zones);
        data = new ArrayList<Zone>();


        return view;
    }


    private void getZones(){
        final RetrofitNetwork rfN = new RetrofitNetwork();
        rfN.getZones( new RequestCallback<List<Zone>>() {
            @Override
            public void onSuccess(List<Zone> response) {
                for(int i = 0; i < response.size(); i++){
                    data.add(response.get(i));
                }
            }
            @Override
            public void onFailed(NetworkException e) {
                Toast.makeText(getActivity(), "Invalid User.",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getZonesFavorites(){
        final RetrofitNetwork rfN = new RetrofitNetwork();
        rfN.getZones( new RequestCallback<List<Zone>>() {
            @Override
            public void onSuccess(List<Zone> response) {
            }
            @Override
            public void onFailed(NetworkException e) {
                Toast.makeText(getActivity(), "Invalid User.",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
