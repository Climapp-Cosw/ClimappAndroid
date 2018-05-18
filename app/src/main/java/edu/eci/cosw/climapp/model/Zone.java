package edu.eci.cosw.climapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LauraRB on 9/05/2018.
 */

public class Zone {
    private int id;
    private int number;
    private String name;
    private List<Coordinate> coordinates=new ArrayList<>();

    /**
     * Constructor
     */
    public Zone(){

    }

    /**
     * Constructor
     * @param number
     * @param name
     * @param coordinates
     */
    public Zone(int id, int number, String name, List<Coordinate> coordinates){
        this.number = number;
        this.name = name;
        this.id = id;
        this.coordinates = coordinates;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
