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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }
}
