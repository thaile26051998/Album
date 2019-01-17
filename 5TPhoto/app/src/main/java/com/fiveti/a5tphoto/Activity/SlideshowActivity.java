package com.fiveti.a5tphoto.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.Fragment.GalleryFragment;
import com.fiveti.a5tphoto.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class SlideshowActivity extends AppCompatActivity {
    private String ARRAY_PATH = "array_path";
    int posAlbum, posImage;
    ArrayList<Album> allPath;
    String curPath = "";
    ViewFlipper viewFlipper;
    View hideView;
    Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        //Ẩn toolbar và navigation bar
        hideView = getWindow().getDecorView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_slideshow);

        viewFlipper = findViewById(R.id.flipperView);
        intent = getIntent();

        allPath = GalleryFragment.allPathGalery;
        //Lấy id từ FullScreenImageActivity
        posAlbum = Objects.requireNonNull(intent.getExtras()).getInt("idAlbum");
        posImage = Objects.requireNonNull(intent.getExtras()).getInt("idImage");

        Toast.makeText(this, "Slideshow start!", Toast.LENGTH_LONG).show();

        for (int i = posAlbum; i < allPath.size(); i++) {
            for (int j = posImage; j < allPath.get(i).getAllImagePath().size(); j++) {
                curPath = allPath.get(i).getAllImagePath().get(j);
                addImageToViewFlipper(curPath);
            }
        }

        viewFlipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void addImageToViewFlipper(String curPath) {
        ImageView imageView = new ImageView(SlideshowActivity.this);
        Uri uri = Uri.fromFile(new File(curPath));
        Glide.with(getApplicationContext()).load(uri).into(imageView);
        viewFlipper.addView(imageView);
    }
}
