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

import com.fiveti.a5tphoto.Activity.GalleryActivity;
import com.fiveti.a5tphoto.Activity.MainActivity;
import com.fiveti.a5tphoto.Adapter.AlbumAdapter;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.Database.SQLiteDatabase;
import com.fiveti.a5tphoto.R;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {

    AlbumAdapter adapter;
    GridView gvAlbum;
    SQLiteDatabase db;
    ArrayList<Album> allPathsAlbum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_album, container, false);

        db = new SQLiteDatabase(getActivity(), "FiveTPhoto.sqlite", null, 1);

        gvAlbum = (GridView)v.findViewById(R.id.gridViewAlbum);
        allPathsAlbum = new ArrayList<Album>();

        getImagesPath(getActivity());
        readSQliteDatabaseAlbum(db);

        adapter = new AlbumAdapter(v.getContext(), allPathsAlbum);
        gvAlbum.setAdapter(adapter);

        gvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent iAlbum = new Intent(getActivity(), GalleryActivity.class);
                //Gửi vị trí ảnh hiện tại
                Bundle bGallery = new Bundle();
                bGallery.putInt("posAlbum", i);
                iAlbum.putExtras(bGallery);
                startActivity(iAlbum);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null){
            getImagesPath(getActivity());
            readSQliteDatabaseAlbum(db);
            adapter.notifyDataSetChanged();
        }
    }

    void getImagesPath(Activity activity) {
        allPathsAlbum.clear();
        boolean boolean_folder = false;
        int int_position = 0;
        Uri uri;
        Cursor cursor;
        //int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = activity.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(0);

            for (int i = 0; i < allPathsAlbum.size(); i++) {
                if (allPathsAlbum.get(i).getAlbumName().equals(cursor.getString(1))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }

            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(allPathsAlbum.get(int_position).getAllImagePath());
                al_path.add(absolutePathOfImage);
                allPathsAlbum.get(int_position).setAllImagePath(al_path);
                allPathsAlbum.get(int_position).setType(0);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                Album obj_model = new Album();
                obj_model.setAlbumName(cursor.getString(1));
                obj_model.setAllImagePath(al_path);
                obj_model.setType(0);
                allPathsAlbum.add(obj_model);
            }
        }
    }

    void readSQliteDatabaseAlbum(SQLiteDatabase db)
    {
        boolean boolean_folder = false;
        int int_position = 0;
        Cursor albumData = db.GetData("SELECT * FROM Album");

        while (albumData.moveToNext())
        {
            for (int i = 0; i < allPathsAlbum.size(); i++) {
                if (allPathsAlbum.get(i).getAlbumName().equals(albumData.getString(1))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }

            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(allPathsAlbum.get(int_position).getAllImagePath());
                al_path.add(albumData.getString(0));
                allPathsAlbum.get(int_position).setAllImagePath(al_path);
                allPathsAlbum.get(int_position).setType(1);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(albumData.getString(0));
                Album obj_model = new Album();
                obj_model.setAlbumName(albumData.getString(1));
                obj_model.setAllImagePath(al_path);
                obj_model.setType(1);
                allPathsAlbum.add(obj_model);
            }
        }
    }
}
