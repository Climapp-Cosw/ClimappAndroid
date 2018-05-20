package edu.eci.cosw.climapp.model;

import java.util.Date;

/**
 * Created by LauraRB on 9/05/2018.
 */

public class Report {
    private int id;
    private Date dateTimeReport;
    private int weather;
    private User reportedUser;
    private Zone zone;
    private double latitude;
    private double longitude;
    private int dislike;
    private int like;
    private int rain;
    /**
     *
     * @param lat
     * @param lon
     * @param clima
     * @param u
     * @param z
     * @param dislike
     * @param like
     */

    public Report(double lat, double lon, int clima, User u, Zone z,int dislike,int like,int rain) {
        this.dateTimeReport =  null;
        this.weather = clima;
        this.reportedUser = u;
        this.zone = z;
        this.longitude = lon;
        this.latitude = lat;
        this.dislike = dislike;
        this.like = like;
        this.rain=rain;
    }

    public Report(int id,double lat, double lon, int clima, User u, Zone z,int dislike,int like,int rain) {
        this.dateTimeReport =  null;
        this.weather = clima;
        this.reportedUser = u;
        this.zone = z;
        this.longitude = lon;
        this.latitude = lat;
        this.dislike = dislike;
        this.like = like;
        this.rain=rain;
        this.id=id;
    }

    public Report() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateTimeReport() {
        return dateTimeReport;
    }

    public void setDateTimeReport(Date dateTimeReport) {
        this.dateTimeReport = dateTimeReport;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public User getReportedUser() {
        return reportedUser;
    }

    public void setReportedUser(User reportedUser) {
        this.reportedUser = reportedUser;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
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

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getRain() {
        return rain;
    }

    public void setRain(int rain) {
        this.rain = rain;
    }
}
