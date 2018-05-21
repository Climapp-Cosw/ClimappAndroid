package edu.eci.cosw.climapp.controller;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import  edu.eci.cosw.climapp.R;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import edu.eci.cosw.climapp.model.Coordinate;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.model.Zone;
import edu.eci.cosw.climapp.network.NetworkException;
import edu.eci.cosw.climapp.network.RequestCallback;
import edu.eci.cosw.climapp.network.RetrofitNetwork;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class MainMapReport extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 1;
    private Coordinate LatLng;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    public TextView txtname, txtemail, txtpoint;
    public ImageView imguser;
    public List<Zone> data ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //menu
        //
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        ConfigInitialUser();
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        //----------------------------------------------------------------------------------
        //inicio fragmento mapa1

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragments, new fragmentMap());
        ft.commit();
        toolbar.setTitle("Home");
    }
    public void ConfigInitialUser(){
        //
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //con esto generamos el usuario en el header del menu-------------------------------
        View hView = navigationView.getHeaderView(0);
        txtname = (TextView) hView.findViewById(R.id.textuser);
        txtemail = (TextView) hView.findViewById(R.id.textemail);
        txtpoint = (TextView) hView.findViewById(R.id.textpoints);
        imguser =(ImageView) hView.findViewById(R.id.imageViewuser);
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        bdSQLite usdbh = new bdSQLite(this, 1);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM users WHERE email='"+settings.getString("userEmail","")+"'", null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                txtname.setText(c.getString(1));
                txtemail.setText(c.getString(2));
                txtpoint.setText(String.valueOf(c.getInt(4)));
                if(c.getString(5)!=null){
                    Picasso.with(this).load(c.getString(5)).into(imguser);
                }
            } while(c.moveToNext());
        }
        db.close();
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            ConfigInitialUser();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        ConfigInitialUser();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {

            /*Intent intent = new Intent(this, MainMapReport.class);
            startActivity(intent);
            finish();*/
            toolbar.setTitle("Map");
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragments, new fragmentMap());
            ft.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        if (id == R.id.nav_about) {
            // Handle the camera action

        } else if (id == R.id.nav_close) {
            bdSQLite usdbh = new bdSQLite(this, 1);
            SQLiteDatabase db = usdbh.getWritableDatabase();
            db.execSQL("DROP TABLE users");
            usdbh.onCreate(db);
            db.close();
            SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(LoginActivity.TOKEN_NAME, "");
            editor.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();



        }else if (id == R.id.nav_map) {
            toolbar.setTitle("Map");
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragments, new fragmentMap());
            ft.commit();
        }
        else if (id == R.id.nav_zones) {
            toolbar.setTitle("Favorite zones");
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragments, new fragmentFavoriteZones());
            ft.commit();
        } else if (id == R.id.nav_edit_profile) {
            toolbar.setTitle("Edit Profile");
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragments, new fragmentEditProfile());
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
