package edu.eci.cosw.climapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LauraRB on 9/05/2018.
 */

public class User implements Serializable {

    private int id;
    private String name;
    private String email;
    private String password;
    private String image;
    private int points;
    private List<Report> report = new ArrayList<>();
    private List<Zone> zones = new ArrayList<>();

    public User() {
    }

    /**
     *
     * @param email
     * @param password
     * @param name
     * @param image
     * @param points
     */
    public User( String email, String password, String name, String image,int points ) {
        this.email = email;
        this.password = password;
        this.image = image;
        this.name = name;
        this.points=points;
    }
    /**
     *
     * @param email
     * @param password
     * @param name
     * @param image
     * @param points
     */
    public User(int id, String email, String password, String name, String image,int points ) {
        this.email = email;
        this.password = password;
        this.image = image;
        this.name = name;
        this.points=points;
        this.id=id;
    }

    public String getName(){
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<Report> getReport() {
        return report;
    }

    public void setReport(List<Report> report) {
        this.report = report;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }
}
