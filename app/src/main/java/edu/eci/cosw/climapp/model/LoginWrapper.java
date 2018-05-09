package edu.eci.cosw.climapp.model;

/**
 * Created by JuanArevaloMerchan on 18/04/2018.
 */

public class LoginWrapper {
    private final String username;
    private final String email;
    private final String password;

    public LoginWrapper( String username, String password, String email )    {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getEmail(){
        return email;
    }
}
