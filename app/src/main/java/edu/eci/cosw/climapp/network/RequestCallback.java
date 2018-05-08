package edu.eci.cosw.climapp.network;

/**
 * Created by JuanArevaloMerchan on 18/04/2018.
 */

public interface RequestCallback<T> {
    void onSuccess(T response);

    void onFailed(NetworkException e);
}
