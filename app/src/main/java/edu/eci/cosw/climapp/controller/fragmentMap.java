package edu.eci.cosw.climapp.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class fragmentMap extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 1;
    private Coordinate coordinate;
    private Marker myposition;
    private View view;
    private int rain;
    private int weather;
    private String token;
    private GoogleApiClient googleApiClient;
    private static final int ACCESS_LOCATION_PERMISSION_CODE = 10;
    private final LocationRequest locationRequest = new LocationRequest();
    private boolean click;


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

        googleApiClient = new GoogleApiClient.Builder(getContext()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        googleApiClient.connect();
        click=false;
        return view;
    }

    /**
     * Metodo para seleccionar el clima y crear el reporte
     * @param view
     */
    private void selectWeather(View view){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
        builder1.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        final View v=inflater.inflate(R.layout.activity_dialog_report, null);
        builder1.setView(v);
        final AlertDialog alert11 = builder1.create();
        radioButtons(v);
        v.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert11.dismiss();
            }
        });
        v.findViewById(R.id.acept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weather!=-1 && rain!=-1){
                    ((ProgressBar)v.findViewById(R.id.progressBar4)).setVisibility(view.VISIBLE);
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
        alert11.show();
    }

    /**
     * Metodo para identificar los climas seleccionados para el reporte
     * @param v
     */
    private void radioButtons(View v){
        rain=-1;
        weather=-1;
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
                }/*else if (checkedId == R.id.norain){
                    rain=0;
                }*/
            }
        });
        RadioGroup radioGroup1 = (RadioGroup) v.findViewById(R.id.groupradio1);
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.windy){
                    weather=3;
                }else if (checkedId == R.id.sunny){
                    weather=0;
                }else if (checkedId == R.id.icy){
                    weather=2;
                }else if (checkedId == R.id.cloudy){
                    weather=1;
                }

            }
        });
    }

    /**
     * Metodo que crea un nuevo reporte y lo envia al backend
     * @param alert
     * @param v
     */
    private void addNewReport(final AlertDialog alert,final View v){

        SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        if(settings.getString("userEmail","")!=""){
            final RetrofitNetwork rfN = new RetrofitNetwork();
            rfN.userByEmail(settings.getString("userEmail",""), new RequestCallback<User>() {
                @Override
                public void onSuccess(User response) {
                    updatePointsUser(v);
                    Report report= new Report(coordinate.getLatitude(),coordinate.getLongitude(),weather,response,new Zone(),0,0,rain);
                    rfN.createReport(report, new RequestCallback<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    alert.dismiss();
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
        mMap.clear();
        drawReports();
        drawReportsSensor();
    }

    /**
     * Metodo para pedir permisos
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == -1) {
                return;
            }
        }
        switch (requestCode) {
            case ACCESS_LOCATION_PERMISSION_CODE:
                showMyLocation();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Metodo para mostrar el mapa al iniciar actividad o fragmento
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showMyLocation();
        drawReports();
        drawReportsSensor();
    }

    /**
     * Metodo para dibujar todos los reportes existentes
     */
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

    /**
     * Metodo para dibujar los reportes de los sensores
     */
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

    /**
     * metodo que dibuja los circulos
     * @param lat
     * @param lng
     * @return
     */
    private LatLng drawCircle(double lat, double lng, int weather, int rain){
        int color;
        if(weather==0){
            //Soleado Amarillo
            color = Color.argb(128, 255, 255, 0);
        }
        else if(weather==1){
            color = Color.argb(128, 202, 196, 176);
        }
        else if(weather==2){
            color = Color.argb(128, 0, 0, 179);
        }
        else if(weather==3){
            color = Color.argb(128, 204, 204, 255);
        }
        else{
            //Informacion del sensor
            color = Color.argb(128, 204, 204, 255);
        }

        int border;
        //lluvia
        if(rain==0){
            border = Color.argb(128, 0, 0, 0);
        }
        else if(rain==1){
            border = Color.argb(128, 217, 217, 217);
        }
        else if(rain==2){
            border = Color.argb(128, 153, 153, 153);
        }
        else{
            border = Color.argb(128, 204, 204, 255);
        }

        mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(450)
                .strokeWidth(20)
                .strokeColor(border)
                .fillColor(color)
                .clickable(false));
        return new LatLng(lat,  lng);
    }

    /**
     * Metodo para dibujar los markers de los sensores
     * @param sensor
     */
    private void drawCircleSensor(Sensor sensor){
        int clima;
        int rain;
        if(sensor.isRain()){
            clima = 1;
            rain = 0;
        }
        else{
            clima = 0;
            rain = 4;
        }


        Marker m2= mMap.addMarker(new MarkerOptions().position(drawCircle(sensor.getLatitude(),  sensor.getLongitude(), clima, rain)).
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

    /**
     * Metodo para dibujar los markers de los reportes
     * @param reports
     */
    private void drawCircleReport(final Report reports){
        Marker m =mMap.addMarker(
                new MarkerOptions().position(drawCircle(reports.getLatitude(),reports.getLongitude(), reports.getWeather(), reports.getRain())).
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

    /**
     * Metodo que se ecuta al dar click en un marker
     * @param marker
     */
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
            ((TextView)v.findViewById(R.id.textViewmap)).setText(report.getDateTimeReport().toString());
            ((TextView)v.findViewById(R.id.textmap)).setText("Rain: "+report.getRain());
            ((TextView)v.findViewById(R.id.textmap1)).setText("Weather: "+report.getWeather());
            ((TextView)v.findViewById(R.id.textmap2)).setText("like: "+report.getLike());
            ((TextView)v.findViewById(R.id.textmap3)).setText("Dislike: "+report.getDislike());

            if(((ImageButton)v.findViewById(R.id.upbt)).isEnabled()){
                ((ImageButton)v.findViewById(R.id.upbt)).setVisibility(View.VISIBLE);
                ((Space)v.findViewById(R.id.space1)).setVisibility(View.VISIBLE);
                ((ImageButton)v.findViewById(R.id.downbt)).setVisibility(View.VISIBLE);
            }
            v.findViewById(R.id.upbt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ImageButton) v.findViewById(R.id.upbt)).setVisibility(View.GONE);
                    ((Space) v.findViewById(R.id.space1)).setVisibility(View.GONE);
                    ((ImageButton)v.findViewById(R.id.downbt)).setVisibility(View.GONE);
                    ((ImageButton) v.findViewById(R.id.upbt)).setEnabled(false);
                    ((ImageButton)v.findViewById(R.id.downbt)).setEnabled(false);
                    Report report=(Report) marker.getTag();
                    report.setLike(report.getLike()+1);
                    updateReport(report,v);
                    report.setDateTimeReport(null);
                    ((TextView)v.findViewById(R.id.textmap2)).setText("like: "+report.getLike());
                    updatePointsUser(v);
                }
            });
            v.findViewById(R.id.downbt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ImageButton) v.findViewById(R.id.upbt)).setVisibility(View.GONE);
                    ((Space) v.findViewById(R.id.space1)).setVisibility(View.GONE);
                    ((ImageButton)v.findViewById(R.id.downbt)).setVisibility(View.GONE);
                    ((ImageButton) v.findViewById(R.id.upbt)).setEnabled(false);
                    ((ImageButton)v.findViewById(R.id.downbt)).setEnabled(false);
                    Report report=(Report) marker.getTag();
                    report.setDislike(report.getDislike()+1);
                    report.setDateTimeReport(null);
                    updateReport(report,v);
                    ((TextView)v.findViewById(R.id.textmap3)).setText("Dislike: "+report.getDislike());
                    updatePointsUser(v);
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
    private void updatePointsUser(View v){
        v.findViewById(R.id.progressBar5).setVisibility(View.VISIBLE);
        bdSQLite usdbh = new bdSQLite(getContext(), 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        SQLiteDatabase db2 = usdbh.getReadableDatabase();
        SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        db.execSQL("UPDATE users SET points=points+1 WHERE email='"+settings.getString("userEmail","")+"'");
        Cursor c = db2.rawQuery(" SELECT * FROM users WHERE email='"+settings.getString("userEmail","")+"'", null);
        int id=-1;/*
        NavigationView navigationView = (NavigationView)fin.(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        TextView txtpoint = (TextView) hView.findViewById(R.id.textpoints);*/
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
               id=c.getInt(0);
               //txtpoint.setText(String.valueOf(c.getInt(4)));
            } while(c.moveToNext());
        }
        db.close();
        final RetrofitNetwork rfN = new RetrofitNetwork();
        final int finalId = id;
        rfN.updatePointsUser(id, new RequestCallback<User>() {
            @Override
            public void onSuccess(User response) {
                Log.d("User update points", String.valueOf(finalId));
            }
            @Override
            public void onFailed(NetworkException e) {
                Toast.makeText(getContext(), "Invalid User.",Toast.LENGTH_SHORT).show();
            }
        },settings.getString(LoginActivity.TOKEN_NAME,""));
        v.findViewById(R.id.progressBar5).setVisibility(View.GONE);

    }



    /**
     * Metodo para actualizar el reporte con los votos
     * @param report
     * @param v1
     */
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


    /**
     * Metodo para mostrar mi ubicacion
     */
    @SuppressLint("MissingPermission")
    public void showMyLocation() {
        if (mMap != null) {
            String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
            if (hasPermissions(getContext(), permissions)) {
                mMap.setMyLocationEnabled(true);

                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if (lastLocation != null) {
                    addMarkerAndZoom(lastLocation, "My Location", 15);
                }
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, ACCESS_LOCATION_PERMISSION_CODE);
            }
        }
    }

    /**
     * Permisos
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (context!=null){
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * Agrega el marcador de mi posicion
     * @param location
     * @param title
     * @param zoom
     */
    public void addMarkerAndZoom(Location location, String title, int zoom) {
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        coordinate = new Coordinate(location.getLatitude(),location.getLongitude());
        if (myposition!=null){
            myposition.remove();
        }else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
        }
        myposition = mMap.addMarker(new MarkerOptions().position(myLocation).title("My position")
                .rotation(-45).flat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

    }

    /**
     * Connected
     * @param bundle
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        showMyLocation();
                        stopLocationUpdates();
                    }
                });
    }

    /**
     * OnConnected
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }
        });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
