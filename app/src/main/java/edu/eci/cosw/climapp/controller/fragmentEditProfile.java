package edu.eci.cosw.climapp.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import edu.eci.cosw.climapp.R;
import edu.eci.cosw.climapp.model.LoginWrapper;
import edu.eci.cosw.climapp.model.Token;
import edu.eci.cosw.climapp.model.User;
import edu.eci.cosw.climapp.network.NetworkException;
import edu.eci.cosw.climapp.network.RequestCallback;
import edu.eci.cosw.climapp.network.RetrofitNetwork;


/**
 * Created by LauraRB on 11/05/2018.
 */

public class fragmentEditProfile extends Fragment {
    private View view;
    private View view2;
    private EditText txtname, txtemail, txt_points, txt_level;
    private ImageView imguser;
    private ProgressBar progress;
    private Button btn_save, btn_back;
    private ImageButton btn_selectPhoto;
    private String urlImage;
    private int points;
    public static final int POINTS_LEVEL = 50;
    FirebaseStorage storage;
    private static final String TAKE_PHOTO = "Take Photo";
    private static final int TAKE_PHOTO_OPTION = 1;

    private static final String CHOOSE_GALLERY = "Choose from Gallery";
    private static final int CHOOSE_GALLERY_OPTION = 2;

    private static final String CANCEL = "Cancel";
    private boolean isImagenValid = false;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private String urlFinal;
    private User user;
    private String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        view2 = inflater.inflate(R.layout.activity_main, container, false);

        txtname = (EditText) view.findViewById(R.id.txt_name);
        txtemail = (EditText)view.findViewById(R.id.txt_email);
        txt_points = (EditText)view.findViewById(R.id.txt_points);
        txt_level = (EditText)view.findViewById(R.id.txt_level);
        progress = (ProgressBar)view.findViewById(R.id.progressBarLevel);
        imguser =(ImageView) view.findViewById(R.id.profilePicture);

        btn_save = (Button) view.findViewById(R.id.btn_save);
        btn_back = (Button) view.findViewById(R.id.btn_back);
        btn_selectPhoto = (ImageButton) view.findViewById(R.id.btn_image);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInAnonymously();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });
        btn_selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto();
            }
        });
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences settingsT = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        token = settingsT.getString(LoginActivity.TOKEN_NAME, "");
        ConfigInitialUser();


        return view;
    }
    public void ConfigInitialUser(){
        String email = "";
        String ima = "";
        SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        bdSQLite usdbh = new bdSQLite(getContext(), 1);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM users WHERE email='"+settings.getString("userEmail","")+"' ", null);

        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                email = c.getString(2);

            } while(c.moveToNext());
        }
        db.close();

        RetrofitNetwork rfN = new RetrofitNetwork();
        rfN.userByEmail(email, new RequestCallback<User>() {
            @Override
            public void onSuccess(final User response) {
                user = response;
                if(response!=null){
                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            txtemail.setText(user.getEmail());
                            txtname.setText(user.getName());
                            urlImage = user.getImage();
                            points = user.getPoints();
                            txt_points.setText(points+"");
                            txt_level.setText(getLevel(points)+"");
                            progress.setProgress(getProgress(points));
                            if(response.getImage()!=null){
                                Picasso.with(getContext()).load(urlImage).into(imguser);}
                        }
                    });

                }

            }

            @Override
            public void onFailed(NetworkException e) {
                Toast.makeText(getContext(), "An error occured.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getLevel(int points){
        if(points==0){
            return 1;
        }
        else{
            return points/POINTS_LEVEL;
        }
    }

    private int getProgress(int points){
        if(points==0){
            return 0;
        }
        else{
            int level = points/POINTS_LEVEL;
            int restantPoint = points - (POINTS_LEVEL*level);
            return restantPoint*100/POINTS_LEVEL;
        }
    }

    public void addPhoto() {
        final CharSequence[] options = {TAKE_PHOTO, CHOOSE_GALLERY, CANCEL};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                    imguser.setImageBitmap(photo);
                    isImagenValid = true;
                    break;
                case CHOOSE_GALLERY_OPTION:
                    Uri selectedImageUri = data.getData();
                    if (null != selectedImageUri) {
                        String path = getPathFromURI(selectedImageUri);
                        imguser.setImageURI(selectedImageUri);
                        isImagenValid = true;
                    }
                    break;
            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void signInAnonymously() {
        if(isImagenValid) {
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        addNewUser();
                    } else {
                        Log.w("Error", "signInAnonymously:failure", task.getException());
                    }
                }
            });
            // [END signin_anonymously]
        }
        else{
            addNewUser();
        }
    }

    public boolean validForm(){
        if(txtname.length()==0){
            txtname.setError("Please enter your name");
            return false;
        }
        else if(txtemail.length()==0){
            txtemail.setError("Please enter your e-mail");
            return false;
        }
        else{
            return true;
        }
    }

    public void addNewUser(){
        if(validForm()){
            if(isImagenValid) {
                // Create a storage reference from our app
                storageRef = storage.getReference();

                // Create a reference to "mountains.jpg"
                String name = txtemail.getText().toString() + Math.random() + ".jpeg";
                final StorageReference mountainsRef = storageRef.child(name);
                // Create a reference to 'images/mountains.jpg'
                StorageReference mountainImagesRef = storageRef.child("images/" + name);
                // While the file names are the same, the references point to different files
                imguser.setDrawingCacheEnabled(true);
                imguser.buildDrawingCache();
                Bitmap bitmap = imguser.getDrawingCache();
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
                        return mountainsRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            urlFinal = downloadUri.toString();
                            user.setImage(urlFinal);
                            saveUser();
                        } else {
                            Toast.makeText(getContext(), "An error occured.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else{
                saveUser();
            }

        }
    }


    public void saveUser(){
        user.setEmail(txtemail.getText().toString());
        user.setName(txtname.getText().toString());
        //user.setPassword(txt.getText().toString());
        view.findViewById(R.id.progressBarperfil).setVisibility(View.VISIBLE);
        RetrofitNetwork rfN = new RetrofitNetwork();
        rfN.updateUser(user.getId(), new RequestCallback<User>() {
            @Override
            public void onSuccess(User user2) {
                SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(LoginActivity.TOKEN_NAME, token);
                editor.putString("userEmail", user.getEmail());
                editor.commit();
                bdSQLite usdbh = new bdSQLite(getContext(), 1);
                SQLiteDatabase db = usdbh.getWritableDatabase();
                db.execSQL(" UPDATE users SET img='"+user.getImage()+"', email='"+user.getEmail()+"', name='"+user.getName()+"' WHERE id="+String.valueOf(user.getId())+";");
                db.close();
                Handler uiHandler = new Handler(Looper.getMainLooper());
                ConfigbarUser();
                uiHandler.post(new Runnable(){
                    @Override
                    public void run() {
                        view.findViewById(R.id.progressBarperfil).setVisibility(View.GONE);
                        final android.support.v7.app.AlertDialog.Builder builder2 = new android.support.v7.app.AlertDialog.Builder(getActivity());
                        builder2.setMessage("Updated user");
                        final android.support.v7.app.AlertDialog alert2 =builder2.create();
                        alert2.show();
                    }
                });
                goToMain();

            }

            @Override
            public void onFailed(NetworkException e) {
                Toast.makeText(getContext(), "An error occured.", Toast.LENGTH_SHORT).show();
            }
        }, token, user);

    }

    public void goToMain() {
        Intent intent = new Intent(getContext(), MainMapReport.class);
        startActivity(intent);

    }

    public void ConfigbarUser() {
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable(){
            @Override
            public void run() {
                NavigationView navigationView = (NavigationView) view2.findViewById(R.id.nav_view);
                //con esto generamos el usuario en el header del menu-------------------------------
                View hView = navigationView.getHeaderView(0);
                TextView txtnamebar = (TextView) hView.findViewById(R.id.textuser);
                TextView txtemailbar = (TextView) hView.findViewById(R.id.textemail);
                ImageView imguserbar = (ImageView) hView.findViewById(R.id.imageViewuser);
                txtnamebar.setText(txtname.getText().toString());
                txtemailbar.setText(txtemail.getText().toString());
                if(urlFinal!=null){
                    Picasso.with(getContext()).load(user.getImage()).into(imguserbar);
                }
            }
        });

    }


}
