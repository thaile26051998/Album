package com.fiveti.a5tphoto.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fiveti.a5tphoto.Adapter.SelectImagesForAlbumAdapter;
import com.fiveti.a5tphoto.Adapter.SelectImg3dAdapter;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.PhotoThreeD.MyGLActivity;
import com.fiveti.a5tphoto.PhotoThreeD.PhotoCube;
import com.fiveti.a5tphoto.R;

import java.io.IOException;
import java.util.ArrayList;

public class SelectImg3d extends AppCompatActivity {
    public static ArrayList<String> newAlbum;


    public static int numImages = 0;
    public static Boolean click_mode;
    public static TextView countItemsSelected;
    public static ArrayList<Album> all_path;

    private String ARRAY_PATH = "array_path";
    GridView gvAlbum;
    SelectImg3dAdapter adapter;
    int posAlbum = 0;
    String albumName;
    Toolbar toolbar;
    Button finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_images);

        numImages = 0;
        click_mode = false;
        all_path = new ArrayList<Album>();


        newAlbum = new ArrayList<String>();
        newAlbum.clear();
        all_path.clear();

        // dua het tat ca duong dan hinh anh vao mot thu muc de load vao tab layout gallery
        ArrayList<String> allImagePath = new ArrayList<>();
        for (int i = 0; i < MainActivity.all_images_path.size(); i++) {

            for (int j = 0; j < MainActivity.all_images_path.get(i).getAllImagePath().size(); j++) {

                allImagePath.add(MainActivity.all_images_path.get(i).getAllImagePath().get(j));
            }
        }

        Album obj = new Album();
        obj.setAlbumName(MainActivity.all_images_path.get(0).getAlbumName());
        obj.setAllImagePath(allImagePath);
        all_path.add(obj);


        toolbar = findViewById(R.id.actionbar_select_image);
        setSupportActionBar(toolbar);


        finish = (Button) findViewById(R.id.selectImagesFinish);
        finish.setVisibility(View.GONE);
        countItemsSelected = (TextView) findViewById(R.id.count_items_selected);
        countItemsSelected.setVisibility(View.GONE);

        gvAlbum = (GridView) findViewById(R.id.gridViewSelectImage);
        adapter = new SelectImg3dAdapter(this, all_path, posAlbum);
        gvAlbum.setAdapter(adapter);

        gvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                click_mode = true;
                countItemsSelected.setVisibility(View.VISIBLE);
                finish.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newAlbum.size() > 6) {
                    Toast.makeText(v.getContext(), "Số ảnh vượt quá 6", Toast.LENGTH_LONG).show();
                } else if (newAlbum.size() < 6) {
                    Toast.makeText(v.getContext(), "Ảnh không đủ 6", Toast.LENGTH_LONG).show();
                } else showMain();
            }

        });

    }

    public static void prepareSelection(View v, int position) {
        if (((CheckBox) v).isChecked()) {
            newAlbum.add(all_path.get(0).getAllImagePath().get(position));
            numImages++;
            countItemsSelected.setText(numImages + "");
        } else {
            newAlbum.remove(all_path.get(0).getAllImagePath().get(position));
            numImages--;
            countItemsSelected.setText(numImages + "");
        }
    }

    public void showMain() {
        Intent intent = new Intent(this, MyGLActivity.class);
        intent.putStringArrayListExtra("key", newAlbum);
        startActivity(intent);
        this.finish();
    }

}
