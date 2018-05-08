package edu.eci.cosw.climapp.model;

/**
 * Created by JuanArevaloMerchan on 18/04/2018.
 */

public class LoginWrapper {
    private final String username;

    private final String password;

    public LoginWrapper( String username, String password )    {
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
}
