package edu.eci.cosw.climapp.services;

import java.util.List;

import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.LoginWrapper;
import edu.eci.cosw.climapp.model.Report;
import edu.eci.cosw.climapp.model.Token;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.model.Zone;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * Created by JuanArevaloMerchan on 18/04/2018.
 */

public interface NetworkService {

    @POST( "users/login" )
    Call<Token> login(@Body LoginWrapper user);

    @POST( "users/" )
    Call<User> signUp(@Body User user);

    @GET( "users/{email}" )
    Call<User> userByEmail(@Path("email")String email);

    @POST( "reports/newReport" )
    Call<ResponseBody> createReport(@Body Report report);

    @GET( "reports/" )
    Call<List<Report>> getReports();

    @GET( "zones/" )
    Call<List<Zone>> getZones();
}
