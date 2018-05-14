package edu.eci.cosw.climapp.controller;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.network.NetworkException;
import edu.eci.cosw.climapp.network.RequestCallback;
import edu.eci.cosw.climapp.network.RetrofitNetwork;

/**
 * Created by LauraRB on 11/05/2018.
 */

public class fragmentEditProfile extends Fragment {
    private View view;
    private EditText txtname, txtemail, txtpassword, txtconfirmPassword, txt_picture;
    private ImageView imguser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        txtname = (EditText) view.findViewById(R.id.newnameuser);
        txtemail = (EditText)view.findViewById(R.id.newemail);
        txtpassword = (EditText)view.findViewById(R.id.newpassword);
        txtconfirmPassword = (EditText)view.findViewById(R.id.newconfirmpassword);
        imguser =(ImageView) view.findViewById(R.id.imguser);
        ConfigInitialUser();
        return view;
    }
    public void ConfigInitialUser(){
        SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        bdSQLite usdbh = new bdSQLite(getContext(), 1);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM users WHERE email='"+settings.getString("userEmail","")+"' ", null);
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                txtname.setText(c.getString(1));
                txtemail.setText(c.getString(2));
                txtpassword.setText(c.getString(3));
                txtpassword.setText(c.getString(3));
            } while(c.moveToNext());
        }
        db.close();
    }






}
