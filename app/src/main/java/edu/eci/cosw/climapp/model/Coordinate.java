package edu.eci.cosw.climapp.model;

/**
 * Created by LauraRB on 9/05/2018.
 */

public class Coordinate {
    private double latitude;
    private double longitude;
    private Zone zone;

    /**
     * @param latitude
     * @param longitude
     */
    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.zone=null;
    }

    public Coordinate(){

    }
}
