package com.fiveti.a5tphoto.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.Fragment.GalleryFragment;
import com.fiveti.a5tphoto.R;
import com.gjiazhe.panoramaimageview.GyroscopeObserver;
import com.gjiazhe.panoramaimageview.PanoramaImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

//tham khảo: youtube
public class PanoramaActivity extends AppCompatActivity {
    private GyroscopeObserver gyroscopeObserver;
    private int posAlbum, posImage;
    public static ArrayList<Album> allPath = new ArrayList<>();
    private String ARRAY_PATH = "array_path";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_panorama);
        gyroscopeObserver = new GyroscopeObserver();
        gyroscopeObserver.setMaxRotateRadian(Math.PI / 4);
        allPath = GalleryFragment.allPathGalery;

        //lấy id hình ảnh từ fullscreen activity
        Intent intent = getIntent();
        posAlbum = Objects.requireNonNull(intent.getExtras()).getInt("idAlbum");
        posImage = Objects.requireNonNull(intent.getExtras()).getInt("idImage");

        PanoramaImageView panoramaImageView = findViewById(R.id.panorama_image_view);
        panoramaImageView.setGyroscopeObserver(gyroscopeObserver);
        Picasso.get().load("file://" + allPath.get(posAlbum).getAllImagePath().get(posImage)).into(panoramaImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register GyroscopeObserver.
        gyroscopeObserver.register(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister GyroscopeObserver.
        gyroscopeObserver.unregister();
    }
}
