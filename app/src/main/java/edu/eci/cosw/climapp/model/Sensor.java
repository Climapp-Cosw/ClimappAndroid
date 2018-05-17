package edu.eci.cosw.climapp.model;


public class Sensor {
    private int id;
    private float temperature;
    private float pollution;
    private boolean rain;
    private float humidity;
    private Zone zone_id;
    private double latitude;
    private double longitude;

    public Sensor(int id, float temp, float hum, float poll, Zone z,double lat, double lon,boolean rain){
        this.id=id;
        this.temperature=temp;
        this.humidity=hum;
        this.pollution=poll;
        this.rain=rain;
        this.latitude=lat;
        this.longitude=lon;
        this.zone_id=z;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getPollution() {
        return pollution;
    }

    public void setPollution(float pollution) {
        this.pollution = pollution;
    }

    public boolean isRain() {
        return rain;
    }

    public void setRain(boolean rain) {
        this.rain = rain;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public Zone getZone_id() {
        return zone_id;
    }

    public void setZone_id(Zone zone_id) {
        this.zone_id = zone_id;
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
}
