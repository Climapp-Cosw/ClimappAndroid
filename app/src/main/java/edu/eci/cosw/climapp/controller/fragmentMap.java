package edu.eci.cosw.climapp.controller;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.Coordinate;
import edu.eci.cosw.climapp.model.Report;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.model.Zone;
import edu.eci.cosw.climapp.network.NetworkException;
import edu.eci.cosw.climapp.network.RequestCallback;
import edu.eci.cosw.climapp.network.RetrofitNetwork;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by LauraRB on 11/05/2018.
 */

public class fragmentMap extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 1;
    private Coordinate coordinate;
    private View view;
    private int rain;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        final SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        myMAPF.getMapAsync(this);
        //onRequestPermissionsResult(1,2,2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectWeather(view);
            }
        });
        return view;
    }

    private void selectWeather(View view){
        LayoutInflater li = LayoutInflater.from(view.getContext());
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
        builder1.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View v=inflater.inflate(R.layout.activity_dialog_report, null);
        builder1.setView(v);
        final AlertDialog alert11 = builder1.create();
        v.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert11.dismiss();
            }
        });
        v.findViewById(R.id.acept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewReport(alert11);
            }
        });
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.groupradio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rain=0;
                if (checkedId == R.id.rain){
                    rain=1;
                }else if (checkedId == R.id.hailstone){
                    rain=2;
                }else if (checkedId == R.id.tunderstorm){
                    rain=3;
                }
            }
        });
        alert11.show();
    }

    private void addNewReport(final AlertDialog alert){
        SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        if(settings.getString("userEmail","")!=""){
            final RetrofitNetwork rfN = new RetrofitNetwork();
            rfN.userByEmail(settings.getString("userEmail",""), new RequestCallback<User>() {
                @Override
                public void onSuccess(User response) {
                    Report report= new Report(4.6373,-74.09999,0,response,new Zone(),0,0,rain);
                    rfN.createReport(report, new RequestCallback<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            alert.dismiss();
                        }
                        @Override
                        public void onFailed(NetworkException e) {
                            Toast.makeText(getContext(), "Invalid User.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onFailed(NetworkException e) {
                    Toast.makeText(getActivity(), "Invalid User.",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng cali = new LatLng(3.4383, -76.5161);
        googleMap.addMarker(new MarkerOptions().position(cali).title("Cali la Sucursal del cielo"));
        CameraPosition cameraPosition = CameraPosition.builder().target(cali).zoom(10).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }
    }
}