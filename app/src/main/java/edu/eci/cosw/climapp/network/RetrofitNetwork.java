package edu.eci.cosw.climapp.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.eci.cosw.climapp.model.LoginWrapper;
import edu.eci.cosw.climapp.model.Report;
import edu.eci.cosw.climapp.model.Token;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.model.Zone;
import edu.eci.cosw.climapp.services.NetworkService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by JuanArevaloMerchan on 18/04/2018.
 */

public class RetrofitNetwork implements Network {
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    //private static final String BASE_URL = "http://192.168.0.15:8080/";
    private NetworkService networkService;

    private ExecutorService backgroundExecutor = Executors.newFixedThreadPool( 1 );

    public RetrofitNetwork() {
        Retrofit retrofit =
                new Retrofit.Builder().baseUrl( BASE_URL ).addConverterFactory( GsonConverterFactory.create() ).build();
        networkService = retrofit.create( NetworkService.class );
    }

    @Override
    public void login(final LoginWrapper loginWrapper, final RequestCallback<Token> requestCallback )    {
        backgroundExecutor.execute( new Runnable()        {
            @Override
            public void run()            {
                Call<Token> call = networkService.login( loginWrapper );
                try                {
                    Response<Token> execute = call.execute();
                    requestCallback.onSuccess( execute.body() );
                }
                catch ( IOException e )                {
                    requestCallback.onFailed( new NetworkException(null, e ) );
                }
            }
        } );

    }

    @Override
    public void signUp(final User user, final RequestCallback<User> requestCallback) {
        backgroundExecutor.execute( new Runnable()        {
            @Override
            public void run()            {
                Call<User> call = networkService.signUp(user);
                try                {
                    Response<User> execute = call.execute();
                    requestCallback.onSuccess( execute.body() );
                }
                catch ( IOException e )                {
                    requestCallback.onFailed( new NetworkException(null, e ) );
                }
            }
        } );
    }

    @Override
    public void createReport(final  Report report,final RequestCallback<Report> requestCallback) {
        backgroundExecutor.execute( new Runnable()        {
            @Override
            public void run()            {
                Call<Report> call = networkService.createReport(report);
                try                {
                    Response<Report> execute= call.execute();
                    requestCallback.onSuccess( execute.body() );
                }
                catch ( IOException e )                {
                    requestCallback.onFailed( new NetworkException(null, e ) );
                }
            }
        } );
    }
    /*
    @Override
    public List<Report> getReports(RequestCallback<Report> requestCallback) {
        List<Report> res=new ArrayList<>();
        backgroundExecutor.execute( new Runnable()        {
            @Override
            public void run()            {
                Call<Report> call = networkService.getReports();
                try                {
                    Response<Report> execute= call.execute();
                    requestCallback.onSuccess( execute.body() );
                }
                catch ( IOException e )                {
                    requestCallback.onFailed( new NetworkException(null, e ) );
                }
            }
        } );
        return res;
    }*/
    @Override
    public List<Zone> getZones(final RequestCallback<Zone> requestCallback) {
        List<Zone> res=new ArrayList<>();
        backgroundExecutor.execute( new Runnable()        {
            @Override
            public void run()            {
                Call<Zone> call = networkService.getZones();
                try                {
                    Response<Zone> execute= call.execute();
                    requestCallback.onSuccess(execute.body());
                }
                catch ( IOException e )                {
                    requestCallback.onFailed( new NetworkException(null, e ) );
                }
            }
        } );
        return res;
    }


    public void addSecureTokenInterceptor( final String token )    {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor( new Interceptor(){
            @Override
            public okhttp3.Response intercept( Chain chain )throws IOException{
                Request original = chain.request();
                Request request = original.newBuilder().header( "Accept", "application/json" ).header( "Authorization","Bearer "
                                + token ).method(
                        original.method(), original.body() ).build();
                return chain.proceed( request );
            }
        } );
        Retrofit retrofit =
                new Retrofit.Builder().baseUrl( BASE_URL ).addConverterFactory( GsonConverterFactory.create() ).client(
                        httpClient.build() ).build();
        networkService = retrofit.create( NetworkService.class );
    }

}