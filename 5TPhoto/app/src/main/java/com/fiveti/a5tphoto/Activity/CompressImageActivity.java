package com.fiveti.a5tphoto.Activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fiveti.a5tphoto.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

//tham khảo: https://github.com/zetbaitsu/Compressor
public class CompressImageActivity extends AppCompatActivity {


    ImageView img_original, img_compressed;
    EditText edtRatio;
    Button btnSelectImage, btnSave;
    private File actualImage;
    private File compressedImage;

    int ratioNum;
    String ratio;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress_image);


        img_original = (ImageView) findViewById(R.id.img_original);
        img_compressed = (ImageView) findViewById(R.id.img_compressed);
        edtRatio = (EditText) findViewById(R.id.edt_ratio);
        btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
        btnSave = (Button) findViewById(R.id.btnSaveImage);


        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratio = edtRatio.getText().toString();
                if(ratio.isEmpty()){
                    edtRatio.setError("Please choose compress ratio");
                    edtRatio.requestFocus();
                    return;
                }

                if(Integer.parseInt(ratio)<=1){
                    edtRatio.setError("Ratio must >= 1");
                    edtRatio.requestFocus();
                    return;
                }

                ratioNum = Integer.parseInt(ratio);
                edtRatio.setVisibility(View.INVISIBLE);
                chooseImage(view);
            }
        });
    }


    public void chooseImage(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                actualImage = FileUtil.from(this, data.getData());
                // code to compress image and stores it Pictures folder

                ExifInterface exifInterface = new ExifInterface(actualImage.getAbsolutePath());

                compressedImage = new Compressor.Builder(this)
                        .setMaxWidth(Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)) / ratioNum)
                        .setMaxHeight(Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)) / ratioNum)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .build()
                        .compressToFile(actualImage);



                img_original.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                img_compressed.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap b = null;
                        b = BitmapFactory.decodeFile(compressedImage.getAbsolutePath());
                        if (b != null) {
                            Toast.makeText(CompressImageActivity.this, "Image compressed", Toast.LENGTH_SHORT).show();
                            InsertImageToGallery(getContentResolver(), b);
                            finish();

                            Intent intentMain = new Intent(CompressImageActivity.this, MainActivity.class);
                            startActivity(intentMain);
                        }
                    }
                });

            } catch (IOException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void InsertImageToGallery(ContentResolver contentResolver, Bitmap bitmap) {
        String photoUriStr = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "", "");
        Uri photoUri = Uri.parse(photoUriStr);
        long now = System.currentTimeMillis() / 1000;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_ADDED, now);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, now);
        values.put(MediaStore.Images.Media.DATE_TAKEN, now);

        contentResolver.update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values,
                MediaStore.Images.Media._ID + "=?", new String[]{ContentUris.parseId(photoUri) + ""});

        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photoUri);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(scanFileIntent);
    }
}

