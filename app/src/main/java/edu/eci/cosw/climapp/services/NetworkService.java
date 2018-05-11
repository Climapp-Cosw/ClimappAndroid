package edu.eci.cosw.climapp.services;

import edu.eci.cosw.climapp.model.LoginWrapper;
import edu.eci.cosw.climapp.model.Report;
import edu.eci.cosw.climapp.model.Token;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.model.Zone;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


/**
 * Created by JuanArevaloMerchan on 18/04/2018.
 */

public interface NetworkService {

    @POST( "users/login" )
    Call<Token> login(@Body LoginWrapper user);

    @POST( "users/" )
    Call<User> signUp(@Body User user);

    @POST( "reports/newReport" )
    Call<Report> createReport(@Body Report report);
    /*
    @GET( "reports/" )
    Call<Report> getReports();*/

    @GET( "zones/" )
    Call<Zone> getZones();
}
