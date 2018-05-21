package edu.eci.cosw.climapp.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.eci.cosw.climapp.DirectionsJSONParser;
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
    ArrayList markerPoints= new ArrayList();
    private boolean ubico = false;

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

    /**
     * Metodo para identificar los climas seleccionados para el reporte
     * @param v
     */
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

    /**
     * Metodo para identificar los climas seleccionados para el reporte
     * @param v
     * @return
     */
    private String checkboxButtons(View v){
        String s="";
        if (((CheckBox)v.findViewById(R.id.windy)).isChecked()) {s+="w";}
        if (((CheckBox)v.findViewById(R.id.sunny)).isChecked()) {s+="s";}
        if (((CheckBox)v.findViewById(R.id.cloudy)).isChecked()) {s+="c";}
        if (((CheckBox)v.findViewById(R.id.icy)).isChecked()) {s+="i";}
        return s;
    }

    /**
     * Metodo que crea un nuevo reporte y lo envia al backend
     * @param alert
     * @param v
     */
    private void addNewReport(final AlertDialog alert,final View v){
        ((ProgressBar)v.findViewById(R.id.progressBar4)).setVisibility(view.VISIBLE);
        SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        if(settings.getString("userEmail","")!=""){
            final RetrofitNetwork rfN = new RetrofitNetwork();
            rfN.userByEmail(settings.getString("userEmail",""), new RequestCallback<User>() {
                @Override
                public void onSuccess(User response) {
                    Report report= new Report(coordinate.getLatitude(),coordinate.getLongitude(),weather,response,new Zone(),0,0,rain);
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
        drawRoute();


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
                    ((ImageButton) v.findViewById(R.id.upbt)).setVisibility(View.GONE);
                    ((Space) v.findViewById(R.id.space1)).setVisibility(View.GONE);
                    ((ImageButton)v.findViewById(R.id.downbt)).setVisibility(View.GONE);
                    marker.setTag(report);
                }
            });
            v.findViewById(R.id.downbt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Report report=(Report) marker.getTag();
                    report.setDislike(report.getDislike()+1);
                    updateReport(report,v);
                    ((TextView)v.findViewById(R.id.textmap3)).setText("Dislike: "+report.getDislike());
                    ((ImageButton) v.findViewById(R.id.upbt)).setVisibility(View.GONE);
                    ((Space) v.findViewById(R.id.space1)).setVisibility(View.GONE);
                    ((ImageButton)v.findViewById(R.id.downbt)).setVisibility(View.GONE);
                    marker.setTag(report);
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
            Log.i("Ecaaaaa","1");
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
        else{
            Log.i("Ecaaaaa","2");
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
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
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

        }
        myposition = mMap.addMarker(new MarkerOptions().position(myLocation).title("My position")
                .rotation(-45).flat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        //CameraPosition cameraPosition = CameraPosition.builder().target(cali).zoom(14).build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
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
                        if(!ubico) {
                            showMyLocation();
                            stopLocationUpdates();
                            ubico = true;
                        }
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














    private void drawRoute(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    mMap.clear();
                    drawReports();
                    drawReportsSensor();
                    Log.i("ENTROOO!!!!!","1");
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = (LatLng) markerPoints.get(0);
                    LatLng dest = (LatLng) markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            }
        });
    }




    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
