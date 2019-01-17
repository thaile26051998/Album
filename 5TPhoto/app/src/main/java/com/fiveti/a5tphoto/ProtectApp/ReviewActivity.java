package com.fiveti.a5tphoto.ProtectApp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.fiveti.a5tphoto.R;

public class ReviewActivity extends AppCompatActivity {
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Handler handler;
    SharedPreferences sharedPreferences;
    String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //load password
        sharedPreferences = getSharedPreferences("PREFERENCES", 0);
        pass = sharedPreferences.getString("password","");


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(pass.equals("")){
                    //Nếu không có password
                    Intent intent = new Intent(getApplicationContext(), CreatePassActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //Nếu có password
                    Intent intent = new Intent(getApplicationContext(), ConfirmPassActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 800);
    }
}
