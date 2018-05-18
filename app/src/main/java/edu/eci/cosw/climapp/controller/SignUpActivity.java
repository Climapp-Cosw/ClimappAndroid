package edu.eci.cosw.climapp.controller;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.sql.Timestamp;

import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.LoginWrapper;
import edu.eci.cosw.climapp.model.Token;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.network.NetworkException;
import edu.eci.cosw.climapp.network.RequestCallback;
import edu.eci.cosw.climapp.network.RetrofitNetwork;

public class SignUpActivity extends AppCompatActivity {

    EditText txt_name, txt_email, txt_password, txt_confirmPassword, txt_picture;
    private User user;
    FirebaseStorage storage;
    private static final String TAKE_PHOTO = "Take Photo";
    private static final int TAKE_PHOTO_OPTION = 1;

    private static final String CHOOSE_GALLERY = "Choose from Gallery";
    private static final int CHOOSE_GALLERY_OPTION = 2;

    private static final String CANCEL = "Cancel";
    private boolean isImagenValid = false;
    private ImageView image;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private String urlFinal;
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
        image =  (ImageView) findViewById(R.id.profile_picture);
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();


    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void addNewUser(){
        if(validForm()){
            // Create a storage reference from our app
            storageRef = storage.getReference();

            // Create a reference to "mountains.jpg"
            String name = txt_email.getText().toString()+Math.random()+".jpeg";
            final StorageReference mountainsRef = storageRef.child(name);
            // Create a reference to 'images/mountains.jpg'
            StorageReference mountainImagesRef = storageRef.child("images/"+name);
            // While the file names are the same, the references point to different files
            image.setDrawingCacheEnabled(true);
            image.buildDrawingCache();
            Bitmap bitmap = image.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = mountainsRef.putBytes(data);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return mountainsRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        urlFinal = downloadUri.toString();
                        user = new User(txt_email.getText().toString(), txt_password.getText().toString(), txt_name.getText().toString(), downloadUri.toString(), 0);
                        RetrofitNetwork rfN = new RetrofitNetwork();
                        rfN.signUp(user, new RequestCallback<User>() {
                            @Override
                            public void onSuccess(User user2) {
                                user.setId(user2.getId());
                                RetrofitNetwork rf = new RetrofitNetwork();
                                LoginWrapper lw = new LoginWrapper(txt_email.getText().toString(), txt_password.getText().toString(), txt_email.getText().toString());
                                rf.login(lw, new RequestCallback<Token>() {
                                    @Override
                                    public void onSuccess(Token response) {
                                        if(response!=null){
                                            String token = response.getAccessToken();
                                            saveToken(token, urlFinal);
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
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
    }

    public void saveToken(String token, String pathImg){
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LoginActivity.TOKEN_NAME, token);
        editor.putString("userEmail", txt_email.getText().toString().trim());
        editor.putString(LoginActivity.PATH_NAME, pathImg);
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
        else if(!isImagenValid){
            txt_picture.setError("Please select your profile picture");
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
    private void insertUserBD(){
        bdSQLite usdbh = new bdSQLite(this, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();

        usdbh.onUpgrade(db,1, 1);

        db.execSQL("INSERT INTO users (id, name,email,password,points,img) " + "VALUES ("
                + user.getId()+", '"
                + user.getName() +"', '"
                + user.getEmail()+"', '"
                + user.getPassword()+"',"
                + user.getPoints()+",'"
                + urlFinal
                + "')");
        db.close();
    }
    public void goToMain(String token) {
        insertUserBD();
        Intent intent = new Intent(this, MainMapReport.class);
        intent.putExtra(LoginActivity.TOKEN_NAME,token);
        startActivity(intent);
        finish();
    }

    public void addPhoto(View view) {
        final CharSequence[] options = {TAKE_PHOTO, CHOOSE_GALLERY, CANCEL};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(TAKE_PHOTO)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, TAKE_PHOTO_OPTION);
                } else if (options[item].equals(CHOOSE_GALLERY)) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_GALLERY_OPTION);
                } else if (options[item].equals(CANCEL)) {
                    dialog.dismiss();
                }
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == SignUpActivity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_OPTION:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    image.setImageBitmap(photo);
                    isImagenValid = true;
                    break;
                case CHOOSE_GALLERY_OPTION:
                    Uri selectedImageUri = data.getData();
                    if (null != selectedImageUri) {
                        String path = getPathFromURI(selectedImageUri);
                        image.setImageURI(selectedImageUri);
                        isImagenValid = true;
                    }
                    break;
            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void signInAnonymously(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    addNewUser();
                }
                else{
                    Log.w("Error", "signInAnonymously:failure", task.getException());
                }
            }
        });
        // [END signin_anonymously]
    }
}
