package edu.eci.cosw.climapp.network;

import java.util.List;


import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.LoginWrapper;
import edu.eci.cosw.climapp.model.Report;
import edu.eci.cosw.climapp.model.Sensor;
import edu.eci.cosw.climapp.model.Token;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.model.Zone;
import okhttp3.ResponseBody;

/**
 * Created by JuanArevaloMerchan on 18/04/2018.
 */

public interface Network {
    void login(LoginWrapper loginWrapper, RequestCallback<Token> requestCallback);

    void signUp(User user, RequestCallback<User> requestCallback);

    void userByEmail(String s, RequestCallback<User> requestCallback);

    void updateUser(int id, RequestCallback<User> requestCallback, String token, User user);

    void createReport(Report report,RequestCallback<ResponseBody> requestCallback);

    void getReports(RequestCallback<List<Report>> requestCallback);

    void getZones(RequestCallback<List<Zone>> requestCallback);

    void getFavoriteZones(String email,RequestCallback<List<Zone>> requestCallback,String token);

    void addFavoriteZones(String email,Zone zone,RequestCallback<List<Zone>> requestCallback,String token);

    void deleteFavoriteZones(String email, Zone zone, RequestCallback<List<Zone>> requestCallback, String token);

    void updateReport(Report report, RequestCallback<Report> requestCallback, String token);

    void updatePointsUser(int id, RequestCallback<User> requestCallback, String token);
    
    void getReportsSensors(RequestCallback<List<Sensor>> requestCallback, String token);
}
