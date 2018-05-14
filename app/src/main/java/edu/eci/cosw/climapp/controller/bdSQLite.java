package edu.eci.cosw.climapp.controller;

/**
 * Created by LauraRB on 13/05/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class bdSQLite extends SQLiteOpenHelper {
        //Sentencia SQL para crear la tabla de Usuarios
        String sqlCreate = "CREATE TABLE users (id INTEGER primary key, name varchar,email varchar,password varchar,points int)";
        public  bdSQLite(Context contexto, int version) {
            super(contexto, "users", null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Se ejecuta la sentencia SQL de creación de la tabla
            db.execSQL(sqlCreate);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
            db.execSQL("DROP TABLE IF EXISTS users");
            //Se crea la nueva versión de la tabla
            onCreate(db);
        }

}
