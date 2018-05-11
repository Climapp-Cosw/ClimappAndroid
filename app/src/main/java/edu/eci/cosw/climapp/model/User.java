package edu.eci.cosw.climapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LauraRB on 9/05/2018.
 */

public class User {
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
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

    public String getName(){
        return name;
    }
}
