package com.fiveti.a5tphoto.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.fiveti.a5tphoto.Adapter.CopyImageAdapter;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MoveImageToDirectoryActivity extends AppCompatActivity {
    CopyImageAdapter adapter;
    GridView gvAlbum;
    ArrayList<Album> allPathsAlbum;
    int posImage, posAlbum;
    Context context;
    String curPath;

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

        curPath = allPathsAlbum.get(posAlbum).getAllImagePath().get(posImage);

        adapter = new CopyImageAdapter(this, allPathsAlbum);
        gvAlbum.setAdapter(adapter);

        gvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File source = new File(curPath);
                File destination = getOutputMediaFile(allPathsAlbum.get(i).getAlbumName());

                try
                {
                    FileUtils.moveFile(source, destination);
                    //FileUtils.copyFile(source, destination);
                    DeleteImage();
                    refreshGallery(context, destination.getAbsolutePath());
                    Toast.makeText(context, "di chuyển đến " + allPathsAlbum.get(i).getAlbumName() +" thành công", Toast.LENGTH_SHORT).show();
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

    void DeleteImage() {
        final File deleteFile = new File(curPath);

        //int pos = getPosImageReal();
        int pos = posImage;
        if (allPathsAlbum.get(posAlbum).getType() == 0) {
            // Nguồn tham khảo: http://stackoverflow.com/a/20780472#1#L0
            String[] projection = {MediaStore.Images.Media._ID};

            String selection = MediaStore.Images.Media.DATA + " = ?";
            String[] selectionArgs = new String[]{deleteFile.getAbsolutePath()};

            Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = getContentResolver();
            Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
            if (c.moveToFirst()) {
                long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                contentResolver.delete(deleteUri, null, null);
            } else {
                return;
            }
            c.close();

        }
    }
}
