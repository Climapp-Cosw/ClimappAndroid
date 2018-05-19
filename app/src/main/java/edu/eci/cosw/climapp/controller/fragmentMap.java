package edu.eci.cosw.climapp.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.Coordinate;
import edu.eci.cosw.climapp.model.Report;
import edu.eci.cosw.climapp.model.Sensor;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.model.Zone;
import edu.eci.cosw.climapp.network.NetworkException;
import edu.eci.cosw.climapp.network.RequestCallback;
import edu.eci.cosw.climapp.network.RetrofitNetwork;
import okhttp3.ResponseBody;

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
    private int weather;
    private String token;


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


        SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        token = settings.getString(LoginActivity.TOKEN_NAME, "");
        return view;
    }

    private void selectWeather(View view){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
        builder1.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View v=inflater.inflate(R.layout.activity_dialog_report, null);
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
                String s=checkboxButtons(v);
                if(s!=""){
                    addNewReport(alert11,v);

                }else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Please select a weather");
                    final AlertDialog alert =builder.create();
                    alert.show();
                    // Create the AlertDialog object and return it
                }

            }
        });
        radioButtons(v);
        alert11.show();
    }
    private void radioButtons(View v){
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

    }
    private String checkboxButtons(View v){
        String s="";
        if (((CheckBox)v.findViewById(R.id.windy)).isChecked()) {s+="w";}
        if (((CheckBox)v.findViewById(R.id.sunny)).isChecked()) {s+="s";}
        if (((CheckBox)v.findViewById(R.id.cloudy)).isChecked()) {s+="c";}
        if (((CheckBox)v.findViewById(R.id.icy)).isChecked()) {s+="i";}
        return s;
    }

    private void addNewReport(final AlertDialog alert,final View v){
        ((ProgressBar)v.findViewById(R.id.progressBar4)).setVisibility(view.VISIBLE);
        SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        if(settings.getString("userEmail","")!=""){
            final RetrofitNetwork rfN = new RetrofitNetwork();
            rfN.userByEmail(settings.getString("userEmail",""), new RequestCallback<User>() {
                @Override
                public void onSuccess(User response) {
                    Report report= new Report(4.6373,-74.09999,weather,response,new Zone(),0,0,rain);
                    rfN.createReport(report, new RequestCallback<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            alert.dismiss();
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    ((ProgressBar)v.findViewById(R.id.progressBar4)).setVisibility(view.GONE);
                                    final AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                                    builder2.setMessage("Report created");
                                    final AlertDialog alert2 =builder2.create();
                                    alert2.show();

                                }
                            });

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
        //Poner la ubicacion en la zona y marcador de mi ubicacion y el zoom
        LatLng cali = new LatLng(4.7829933, -74.04244039999999);
        googleMap.addMarker(new MarkerOptions().position(cali).title("My position")
                .rotation(-45).flat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        CameraPosition cameraPosition = CameraPosition.builder().target(cali).zoom(14).build();
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
        drawReports();
        drawReportsSensor();
    }


    private void drawReports(){
        RetrofitNetwork rfN = new RetrofitNetwork();
        rfN.getReports(new RequestCallback<List<Report>>() {
            @Override
            public void onSuccess(final List<Report> response) {
                getActivity().runOnUiThread(new Runnable(){
                    public void run(){
                    for(int i=0; i<response.size(); i++){
                        drawCircleReport(response.get(i));
                    }
                    }
                });
            }
            @Override
            public void onFailed(NetworkException e) {
                //Toast.makeText(getActivity(), "Error on reports",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawReportsSensor(){
        RetrofitNetwork rfN = new RetrofitNetwork();
        rfN.getReportsSensors(new RequestCallback<List<Sensor>>() {
            @Override
            public void onSuccess(final List<Sensor> response) {
                getActivity().runOnUiThread(new Runnable(){
                    public void run(){
                        for(int i=0; i<response.size(); i++){
                            drawCircleSensor(response.get(i));
                        }
                    }
                });
            }

            @Override
            public void onFailed(NetworkException e) {
                //Toast.makeText(getActivity(), "Error on reports",Toast.LENGTH_SHORT).show();
            }
        }, token);
    }

    private LatLng drawCircle(double lat, double lng){
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(450)
                .strokeWidth(2)
                .fillColor(Color.argb(128, 25, 72, 189))
                .clickable(false));
        return new LatLng(lat,  lng);
    }

    private void drawCircleSensor(Sensor sensor){
        Marker m2= mMap.addMarker(new MarkerOptions().position(drawCircle(sensor.getLatitude(),  sensor.getLongitude())).
                title("Sensor").
                snippet("Rain:"+sensor.isRain()+" Pollution:"+sensor.getPollution()+" Humidity:"+sensor.getHumidity()+"% Temp:"+sensor.getTemperature()+"°C").
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        m2.setTag(sensor);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                clickMarker(marker);
                return false;
            }
        });
    }

    private void drawCircleReport(final Report reports){
        Marker m =mMap.addMarker(
                new MarkerOptions().position(drawCircle(reports.getLatitude(),reports.getLongitude())).
                        title("Report").
                        snippet(" Rain: "+reports.getRain()+" Weather: "+reports.getWeather()).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        m.setTag(reports);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                clickMarker(marker);
                return false;
            }
        });

    }

    private void clickMarker(final Marker marker){
        LayoutInflater inflater = getLayoutInflater();
        final View v=inflater.inflate(R.layout.activity_dialog_vote, null);
        if(marker.getTitle().toString().trim().equals("Report")){
            final AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
            builder1.setCancelable(true);
            builder1.setView(v);
            final AlertDialog alert11 = builder1.create();
            Report report=(Report) marker.getTag();
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CharSequence ch=((CharSequence)"Report user");
                ((Toolbar)v.findViewById(R.id.toolbar3)).setTitle(ch);
            }*/
            ((TextView)v.findViewById(R.id.textViewmap)).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.textViewmap)).setText(report.getDateTimeReport().toLocaleString().toString());
            ((TextView)v.findViewById(R.id.textmap)).setText("Rain: "+report.getRain());
            ((TextView)v.findViewById(R.id.textmap1)).setText("Weather: "+report.getWeather());
            ((TextView)v.findViewById(R.id.textmap2)).setText("like: "+report.getLike());
            ((TextView)v.findViewById(R.id.textmap3)).setText("Dislike: "+report.getDislike());
            ((ImageButton)v.findViewById(R.id.upbt)).setVisibility(View.VISIBLE);
            ((Space)v.findViewById(R.id.space1)).setVisibility(View.VISIBLE);
            ((ImageButton)v.findViewById(R.id.downbt)).setVisibility(View.VISIBLE);
            v.findViewById(R.id.upbt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Report report=(Report) marker.getTag();
                    report.setLike(report.getLike()+1);
                    updateReport(report,v);
                    ((TextView)v.findViewById(R.id.textmap2)).setText("like: "+report.getLike());

                }
            });
            v.findViewById(R.id.downbt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Report report=(Report) marker.getTag();
                    report.setLike(report.getDislike()+1);
                    updateReport(report,v);
                    ((TextView)v.findViewById(R.id.textmap3)).setText("Dislike: "+report.getDislike());
                }
            });
            alert11.show();

        }else if(marker.getTitle().toString().trim().equals("Sensor")) {
            final AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
            builder1.setCancelable(true);
            builder1.setView(v);
            final AlertDialog alert11 = builder1.create();
            Sensor sensor = (Sensor) marker.getTag();
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CharSequence ch=((CharSequence)"Sensor report");
                ((Toolbar)v.findViewById(R.id.toolbar3)).setTitle(ch);
            }*/
            ((TextView) v.findViewById(R.id.textViewmap)).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.textmap)).setText("Rain: " + sensor.isRain());
            ((TextView) v.findViewById(R.id.textmap1)).setText("Temperature: " + sensor.getTemperature() + "°C");
            ((TextView) v.findViewById(R.id.textmap2)).setText("Humidity: " + sensor.getHumidity() + "%");
            ((TextView) v.findViewById(R.id.textmap3)).setText("Pollution: " + sensor.getPollution());
            ((ImageButton) v.findViewById(R.id.upbt)).setVisibility(View.GONE);
            ((Space) v.findViewById(R.id.space1)).setVisibility(View.GONE);
            ((ImageButton)v.findViewById(R.id.downbt)).setVisibility(View.GONE);
            alert11.show();
        }
    }
    private void updateReport(Report report,View v1){
        final RetrofitNetwork rfN = new RetrofitNetwork();
        SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        rfN.updateReport(report, new RequestCallback<Report>() {
            @Override
            public void onSuccess(Report response) {
                //alert.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //((ProgressBar)v1.findViewById(R.id.progressBar4)).setVisibility(view.GONE);
                        final AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                        builder2.setMessage("Report update");
                        final AlertDialog alert2 =builder2.create();
                        alert2.show();
                    }
                });
            }
            @Override
            public void onFailed(NetworkException e) {
                Toast.makeText(getContext(), "Invalid User.",Toast.LENGTH_SHORT).show();
            }
        },settings.getString(LoginActivity.TOKEN_NAME,""));
    }
}
