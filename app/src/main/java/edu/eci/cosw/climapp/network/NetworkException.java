package edu.eci.cosw.climapp.network;

/**
 * Created by JuanArevaloMerchan on 11/04/2018.
 */

public class NetworkException extends Exception {

    public NetworkException(Object o, Exception e){
        e.printStackTrace();
    }
}
