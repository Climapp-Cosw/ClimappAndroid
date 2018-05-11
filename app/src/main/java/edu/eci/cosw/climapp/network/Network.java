package edu.eci.cosw.climapp.network;

import java.util.List;


import edu.eci.cosw.climapp.model.LoginWrapper;
import edu.eci.cosw.climapp.model.Report;
import edu.eci.cosw.climapp.model.Token;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.model.Zone;

/**
 * Created by JuanArevaloMerchan on 18/04/2018.
 */

public interface Network {
    void login(LoginWrapper loginWrapper, RequestCallback<Token> requestCallback);

    void signUp(User user, RequestCallback<User> requestCallback);

    void createReport(Report report,RequestCallback<Report> requestCallback);

    //List<Report> getReports(RequestCallback<Report> requestCallback);

    List<Zone> getZones(RequestCallback<Zone> requestCallback);
}
