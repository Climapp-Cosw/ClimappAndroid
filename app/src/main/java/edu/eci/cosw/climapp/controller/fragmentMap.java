package edu.eci.cosw.climapp.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.Coordinate;

/**
 * Created by LauraRB on 11/05/2018.
 */

public class fragmentMap extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 1;
    private Coordinate LatLng;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        final SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        myMAPF.getMapAsync(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectWeather(view);
            }
        });
        return view;
    }

    private void selectWeather(View view){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
        builder1.setMessage("Creating a report...");
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
                //servicio web que no se me toca aprender
                //toca hacer algoritmo para mirar si pertenece a una zona pero primero extraer la zona
            }
        });
        alert11.show();
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
