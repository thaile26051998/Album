package com.fiveti.a5tphoto.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fiveti.a5tphoto.Activity.FullscreenImageActivity;
import com.fiveti.a5tphoto.Activity.MainActivity;
import com.fiveti.a5tphoto.Adapter.GridViewAdapter;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.Database.SQLiteDatabase;
import com.fiveti.a5tphoto.R;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    GridView gvAlbum;
    GridViewAdapter adapter;

    private String ARRAY_PATH = "array_path";
    public static ArrayList<Album> allPathGalery = new ArrayList<>();

    public static int NUM_GRID_COLUMNS=4;

    SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);

        gvAlbum = (GridView) v.findViewById(R.id.gridViewGallery);
        db = new SQLiteDatabase(getActivity(), "FiveTPhoto.sqlite", null, 1);

        getImagesPath(getActivity());
        readSQliteDatabaseAlbum(db);
        //getPathGallery();
        //
        adapter = new GridViewAdapter(v.getContext(), allPathGalery, 0);

        int gridWidth=gvAlbum.getResources().getDisplayMetrics().widthPixels;

        int imageWidth=gridWidth/NUM_GRID_COLUMNS;

        gvAlbum.setColumnWidth(imageWidth);

        gvAlbum.setAdapter(adapter);

        gvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                ShowFullscreenImage(i);
            }
        });

        return v;
    }

    void ShowFullscreenImage(int posImage)
    {
        Intent iFullImage = new Intent(getActivity(), FullscreenImageActivity.class);
        //Gửi vị trí ảnh hiện tại và cả mảng file
        Bundle bFullImage = new Bundle();
        bFullImage.putSerializable(ARRAY_PATH, allPathGalery);
        bFullImage.putInt("posAlbum", 0);
        bFullImage.putInt("posImage", posImage);
        iFullImage.putExtras(bFullImage);
        startActivity(iFullImage);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null){
            allPathGalery.clear();
            getImagesPath(getActivity());
            readSQliteDatabaseAlbum(db);
            adapter.notifyDataSetChanged();
          // }
        }
    }

    void getImagesPath(Activity activity) {
        allPathGalery.clear();
        Uri uri;
        Cursor cursor;
        //int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = activity.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        ArrayList<String> al_path = new ArrayList<String>();
        Album obj_model = new Album();

        obj_model.setAlbumName("Gallery");

        while (cursor.moveToNext()) {
                al_path.add(cursor.getString(0));
        }
        obj_model.setAllImagePath(al_path);
        obj_model.setType(2);
        allPathGalery.add(obj_model);
    }

    void readSQliteDatabaseAlbum(SQLiteDatabase db)
    {
        Cursor albumData = db.GetData("SELECT * FROM Album");

            ArrayList<String> al_path = new ArrayList<>();
            while (albumData.moveToNext()) {
                al_path.add(albumData.getString(0));
            }
            allPathGalery.get(0).getAllImagePath().addAll(al_path);
    }
}

