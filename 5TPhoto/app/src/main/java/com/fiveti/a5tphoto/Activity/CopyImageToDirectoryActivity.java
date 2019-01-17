package com.fiveti.a5tphoto.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fiveti.a5tphoto.Adapter.AlbumAdapter;
import com.fiveti.a5tphoto.Adapter.CopyImageAdapter;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.OpenCamera.openCamera;
import com.fiveti.a5tphoto.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CopyImageToDirectoryActivity extends AppCompatActivity {
    CopyImageAdapter adapter;
    GridView gvAlbum;
    ArrayList<Album> allPathsAlbum;
    int posImage, posAlbum;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_album);

        gvAlbum = (GridView) findViewById(R.id.gridViewAlbum);
        allPathsAlbum = new ArrayList<Album>();
        allPathsAlbum = MainActivity.all_images_path;
        context = getApplication();

        for(int i = 0; i < allPathsAlbum.size(); i++)
        {
            if(allPathsAlbum.get(i).getType() == 1)
            {
                allPathsAlbum.remove(i);
            }
        }

        Bundle b = this.getIntent().getExtras();
        posImage = b.getInt("idImage");
        posAlbum = b.getInt("idAlbum");

        adapter = new CopyImageAdapter(this, allPathsAlbum);
        gvAlbum.setAdapter(adapter);

        gvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sourcePath = allPathsAlbum.get(posAlbum).getAllImagePath().get(posImage);
                File source = new File(sourcePath);
                File destination = getOutputMediaFile(allPathsAlbum.get(i).getAlbumName());

                try
                {
                    FileUtils.copyFile(source, destination);
                    refreshGallery(context, destination.getAbsolutePath());
                    loadMain();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void refreshGallery(Context context, String filePath) {
        // ScanFile so it will be appeared on Gallery
        MediaScannerConnection.scanFile(context,
                new String[]{filePath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }

    public File getOutputMediaFile(String directory) {

        // Chọn vị trí lưu file
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                directory);

        // Tạo thư mục để lưu file nếu thư mục đó chưa tồn tại
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(directory, "Oops! Failed create "
                        + directory + " directory");
                return null;
            }
        }

        // Chuẩn bị tên file để lưu (lấy ngày tháng năm)
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + "." + "jpg");
        return mediaFile;
    }

    void loadMain()
    {
        Intent iCopyImage = new Intent(this, MainActivity.class);
        startActivity(iCopyImage);
    }
}
