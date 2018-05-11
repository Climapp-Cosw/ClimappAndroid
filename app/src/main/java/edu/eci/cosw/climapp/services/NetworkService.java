package edu.eci.cosw.climapp.services;

import java.util.List;

import edu.eci.cosw.climapp.model.LoginWrapper;
import edu.eci.cosw.climapp.model.Token;
import edu.eci.cosw.climapp.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


/**
 * Created by JuanArevaloMerchan on 18/04/2018.
 */

public interface NetworkService {

    @POST( "users/login" )
    Call<Token> login(@Body LoginWrapper user);

    @POST( "users/" )
    Call<User> signUp(@Body User user);
}
