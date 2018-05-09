package edu.eci.cosw.climapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LauraRB on 9/05/2018.
 */

public class Zone {

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
    public Zone(int number, String name, List<Coordinate> coordinates){
        this.number = number;
        this.name = name;
        this.coordinates = coordinates;
    }
}
