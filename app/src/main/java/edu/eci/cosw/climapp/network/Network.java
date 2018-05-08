package edu.eci.cosw.climapp.network;

import java.util.List;


import edu.eci.cosw.climapp.model.LoginWrapper;
import edu.eci.cosw.climapp.model.Token;

/**
 * Created by JuanArevaloMerchan on 18/04/2018.
 */

public interface Network {
    void login(LoginWrapper loginWrapper, RequestCallback<Token> requestCallback);
}
