package com.fiveti.a5tphoto.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fiveti.a5tphoto.R;

public class CreateAlbumActivity extends AppCompatActivity {
    EditText albumName;
    Button selectImage;
    String name;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
        albumName = (EditText) findViewById(R.id.album_name_editText);
        selectImage = (Button) findViewById(R.id.select_image_button);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowGalleySelectImage();
            }
        });

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("5TPhoto");
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    void ShowGalleySelectImage() {
        String name = albumName.getText().toString();
        if (name.isEmpty()) {
            albumName.setError("Please type album name");
            albumName.requestFocus();
            return;
        }

        Intent iCreateAl = new Intent(this, SelectImagesActivity.class);
        Bundle bCreateAl = new Bundle();
        bCreateAl.putString("albumName", name);
        iCreateAl.putExtras(bCreateAl);
        startActivity(iCreateAl);
    }
}
