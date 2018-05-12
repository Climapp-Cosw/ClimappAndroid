package edu.eci.cosw.climapp.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.LoginWrapper;
import edu.eci.cosw.climapp.model.Token;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.network.NetworkException;
import edu.eci.cosw.climapp.network.RequestCallback;
import edu.eci.cosw.climapp.network.RetrofitNetwork;

public class SignUpActivity extends AppCompatActivity {

    EditText txt_name, txt_email, txt_password, txt_confirmPassword, txt_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        txt_name = findViewById(R.id.txt_name);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        txt_confirmPassword = findViewById(R.id.txt_confirmPass);
        txt_picture = findViewById(R.id.txt_profile);

        txt_email.setText(getIntent().getExtras().get(LoginActivity.NAME_NAME).toString());
        txt_password.setText(getIntent().getExtras().get(LoginActivity.PASSWORD_NAME).toString());
    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void addNewUser(View view){
        if(validForm()){
            User user = new User(txt_email.getText().toString(), txt_password.getText().toString(), txt_name.getText().toString(), txt_picture.getText().toString(), 0);
            RetrofitNetwork rfN = new RetrofitNetwork();
            rfN.signUp(user, new RequestCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    RetrofitNetwork rf = new RetrofitNetwork();
                    LoginWrapper lw = new LoginWrapper(txt_email.getText().toString(), txt_password.getText().toString(), txt_email.getText().toString());
                    rf.login(lw, new RequestCallback<Token>() {
                        @Override
                        public void onSuccess(Token response) {
                            if(response!=null){
                                String token = response.getAccessToken();
                                saveToken(token);
                                goToMain(token);
                            }
                            else{
                                Toast.makeText(SignUpActivity.this, "Invalid User.",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailed(NetworkException e) {
                            e.printStackTrace();
                            Toast.makeText(SignUpActivity.this, "An error occured.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailed(NetworkException e) {
                    Toast.makeText(SignUpActivity.this, "An error occured.",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void saveToken(String token){
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LoginActivity.TOKEN_NAME, token);
        editor.commit();
    }

    public boolean validForm(){
        if(txt_name.length()==0){
            txt_name.setError("Please enter your name");
            return false;
        }
        else if(txt_email.length()==0){
            txt_email.setError("Please enter your e-mail");
            return false;
        }
        else if(txt_password.length()==0){
            txt_password.setError("Please enter your password");
            return false;
        }
        else if(txt_confirmPassword.length()==0){
            txt_confirmPassword.setError("Please enter your password");
            return false;
        }
        else if(txt_picture.length()==0){
            txt_picture.setError("Please enter your picture link");
            return false;
        }
        else if(!txt_password.getText().toString().trim().equalsIgnoreCase(txt_confirmPassword.getText().toString())){
            txt_password.setError("Your password doesn't match");
            return false;
        }
        else{
            return true;
        }
    }

    public void goToMain(String token) {
        Intent intent = new Intent(this, MainMapReport.class);
        intent.putExtra(LoginActivity.TOKEN_NAME,token);
        startActivity(intent);
    }
}
