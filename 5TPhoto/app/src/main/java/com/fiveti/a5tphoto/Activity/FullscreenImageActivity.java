package com.fiveti.a5tphoto.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.fiveti.a5tphoto.Adapter.FullscreenImageAdapter;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.BuildConfig;
import com.fiveti.a5tphoto.Database.SQLiteDatabase;
import com.fiveti.a5tphoto.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class FullscreenImageActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener  {
    public static ArrayList<Album> allPath = new ArrayList<>();
    private String ARRAY_PATH = "array_path";
    Context context;
    int posImage;
    int posAlbum;
    int posAlbumReal;
    Toolbar toolbar;

    BottomNavigationView fullImageNav;
    View hideView;
    //Đường dẫn của ảnh hiện tại
    String curPath = "";
    private ViewPager viewPager;
    private FullscreenImageAdapter fullScreenImageAdapter;

    SQLiteDatabase db;
    public static SQLiteDatabase myFavorite;


    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm");

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen_image_viewpager);

        db = new SQLiteDatabase(this, "FiveTPhoto.sqlite", null, 1);

        myFavorite = new SQLiteDatabase(this, "FiveTPhoto.sqlite", null, 1);
        myFavorite.QueryData("CREATE TABLE IF NOT EXISTS Favorite (Image_Path TEXT )");

        hideView = getWindow().getDecorView();

        viewPager = findViewById(R.id.viewpager);
        fullImageNav = findViewById(R.id.nav_bottom);
        fullImageNav.setOnNavigationItemSelectedListener(this);

        //Khởi tạo context
        context = FullscreenImageActivity.this;

        //Khởi tạo toolbar
        toolbar = findViewById(R.id.nav_actionBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();// Implemented by activity
            }
        });

        //Ẩn toàn bộ thanh thông báo, thanh điều hướng chỉ hiện lên khi được vuốt lên)
        hideView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //Khởi tạo bottom navigation bar
        fullImageNav = findViewById(R.id.nav_bottom);

        Bundle bFullImage = this.getIntent().getExtras();
        //allPath = new ArrayList<>();
        allPath = (ArrayList<Album>) bFullImage.getSerializable(ARRAY_PATH);
        posAlbum = bFullImage.getInt("posAlbum");
        posImage = bFullImage.getInt("posImage");
        curPath = allPath.get(posAlbum).getAllImagePath().get(posImage);
        posAlbumReal = posAlbum;

        setupViewPager();
    }

    private void setupViewPager() {
        fullScreenImageAdapter = new FullscreenImageAdapter(this, allPath, posAlbum);

        viewPager.setAdapter(fullScreenImageAdapter);
        viewPager.addOnPageChangeListener(viewPagerOnPageChangeListener);
        viewPager.setCurrentItem(posImage);
        fullScreenImageAdapter.notifyDataSetChanged();
    }


    // region Listeners
    private final ViewPager.OnPageChangeListener viewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (viewPager != null) {
                viewPager.setCurrentItem(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    // endregion

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        hideView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        switch (id) {
            case R.id.action_info:
                try {
                    ExifInterface exifInterface = new ExifInterface(curPath);
                    String info = "";
                    String attribute = "";
                    attribute = getImageInfo(exifInterface);
                    File file = new File(curPath);

                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    double size = file.length();

                    String temp = "";

                    if (size > 1024 * 1024) {
                        temp = decimalFormat.format(size / (1024 * 1024)) + " MB";
                    } else {
                        if (size > 1024) {
                            temp = decimalFormat.format(size / 1024) + " KB";
                        } else {
                            temp = decimalFormat.format(size) + " B";
                        }
                    }

                    info = simpleDateFormat.format(file.lastModified())
                            + "\n\n" + curPath + "\n"
                            + temp + "    "
                            + attribute;

                    //Show dialog
                    TextView title = new TextView(getApplicationContext());
                    title.setPadding(30, 30, 30, 0);
                    title.setTextSize(25);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText("Image infomation");

                    AlertDialog dialog;
                    dialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert).create();
                    dialog.setCustomTitle(title);
                    dialog.setMessage(info);
                    dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.action_slideShow:
                Toast.makeText(this, "Set as", Toast.LENGTH_SHORT).show();
                Intent intentSlideshow = new Intent(context, SlideshowActivity.class);
                intentSlideshow.putExtra("idImage", posImage); // Lấy position id và truyền cho SlideShowActivity
                intentSlideshow.putExtra("idAlbum", posAlbum);
                startActivity(intentSlideshow);
                return true;

            case R.id.action_setAs:
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_ATTACH_DATA);
                    File file = new File(curPath);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    intent.setDataAndType(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file), getMimeType(curPath));
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "Exception generated", Toast.LENGTH_SHORT).show();
                }

                //Nguồn tham khảo: https://stackoverflow.com/questions/11091980/how-to-use-intent-attach-data
                return true;

            case R.id.action_edit_in:

                return true;
            case R.id.action_panorama:
                Intent intentPano = new Intent(context, PanoramaActivity.class);
                intentPano.putExtra("idImage", posImage);
                intentPano.putExtra("idAlbum", posAlbum);
                startActivity(intentPano);
                return true;
            case R.id.action_Copy:
                Intent iCopyImage = new Intent(context, CopyImageToDirectoryActivity.class);
                iCopyImage.putExtra("idImage", posImage);
                iCopyImage.putExtra("idAlbum", posAlbum);
                startActivity(iCopyImage);
                return true;
            case R.id.action_Move:
                Intent iMoveImage = new Intent(context, MoveImageToDirectoryActivity.class);
                iMoveImage.putExtra("idImage", posImage);
                iMoveImage.putExtra("idAlbum", posAlbum);
                startActivity(iMoveImage);
                return true;

        }

        return true;
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private String getImageInfo(ExifInterface exifInterface) {
        String imgAttribute = "";

        //Lấy ra độ phân giải của ảnh
        if (exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0) == 0) {
            return imgAttribute;
        } else {
            imgAttribute += exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) +
                    "x" + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            if (exifInterface.getAttribute(ExifInterface.TAG_MODEL) == null) {
                return imgAttribute;
            }
        }

        //Lấy ra model của điện thoại
        imgAttribute += "\n\n" + exifInterface.getAttribute(ExifInterface.TAG_MODEL) + "\n";

        // Lấy ra khẩu độ của ảnh
        final DecimalFormat apertureFormat = new DecimalFormat("#.#");
        String aperture = exifInterface.getAttribute(ExifInterface.TAG_F_NUMBER);
        if (aperture != null) {
            Float parseFloat = Float.parseFloat(aperture);
            apertureFormat.format(parseFloat);
            imgAttribute += "f/" + parseFloat + "    ";
        } else {
            imgAttribute += "    ";
        }

        //Lấy ra tiêu cự ống kính
        imgAttribute += exifInterface.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0) + "mm" + "    ";

        //Lấy ra Iso của ảnh
        imgAttribute += "ISO: " + exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS) + "    ";

        //Kiểm tra xem có bật flash hay không
        if (exifInterface.getAttributeInt(ExifInterface.TAG_FLASH, 0) == 0) {
            imgAttribute += "flash: off";
        } else {
            imgAttribute += "flash: on";
        }

        return imgAttribute;
    }


    //Vào chế độ ẩn toàn màn hình
    public void EnterFullScreen() {
        getSupportActionBar().hide();
        fullImageNav.setVisibility(View.GONE);
        hideView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    //Thoát chế độ ẩn toàn màn hình
    public void ExitFullScreen() {
        fullImageNav.setVisibility(View.VISIBLE);
        getSupportActionBar().show();
    }

    public Intent shareIntent() {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        final File photoFile = new File(curPath);
        Uri photoURI = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider",
                photoFile);
        shareIntent.setType("image/jpg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        return shareIntent;

        //Nguồn tham khảo https://stackoverflow.com/questions/50513299/why-this-share-images-not-work-with-all-mobile-devices-only-some
    }


    private void Crop() {
        String filePath = allPath.get(posAlbum).getAllImagePath().get(posImage);
        Uri photoURI = Uri.fromFile(new File(filePath));
        if (photoURI != null) {
            CropImage.activity(photoURI)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap b = null;
                try {
                    b = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InsertImageToGallery(getContentResolver(),b);
                Toast.makeText(this, "Image cropped completely", Toast.LENGTH_SHORT).show();
                showMain();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void InsertImageToGallery(ContentResolver contentResolver, Bitmap bitmap) {
        String photoUriStr = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "" , "");
        Uri photoUri = Uri.parse(photoUriStr);
        long now = System.currentTimeMillis() / 1000;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_ADDED, now);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, now);
        values.put(MediaStore.Images.Media.DATE_TAKEN, now);

        contentResolver.update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values,
                MediaStore.Images.Media._ID + "=?", new String [] { ContentUris.parseId(photoUri) + "" });

        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photoUri);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(scanFileIntent);
        //fullScreenImageAdapter.notifyDataSetChanged();
    }

    void DeleteImage()
    {
        final File deleteFile = new File(curPath);

        // Tạo biến builder thông báo xác nhận việc xóa ảnh
        AlertDialog builder;
        builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();

        builder.setMessage("Dô you want to delete this image");
        //Nếu nhấn Xóa
        builder.setButton(Dialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //int pos = getPosImageReal();
                int pos = posImage;
                if(allPath.get(posAlbum).getType() == 2)
                {
                    pos = getPosImageReal();
                }

                if(MainActivity.all_images_path.get(posAlbumReal).getType() == 0) {
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
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                    }
                    c.close();
                }
                else
                {
                    db.QueryData("DELETE FROM Album WHERE Image_Path = '" + curPath + "' and Album_Name = '" + MainActivity.all_images_path.get(posAlbumReal).getAlbumName() +"'");
                    //
                }

               // AlbumFragment.modeAlbumFragment = 1;

                allPath.get(posAlbum).getAllImagePath().remove(posImage);   // cập nhật lại danh sách đường dẫn trong album

                if (posImage == 0 && allPath.get(posAlbum).getAllImagePath().size() == 0) {
                    posImage--;  // trường hợp xóa ảnh duy nhất của album

                } else if (posImage == allPath.get(posAlbum).getAllImagePath().size()) {
                    // trường hợp xóa ảnh ở vị trí cuối cùng của album
                    posImage--;
                    curPath = allPath.get(posAlbum).getAllImagePath().get(posImage); // cập nhật lại đường dẫn fullscreen mới
                } else {
                    // trường hợp xóa ở các vị trí còn lại
                    curPath = allPath.get(posAlbum).getAllImagePath().get(posImage);

                }

                MainActivity.all_images_path.get(posAlbumReal).getAllImagePath().remove(pos);
                setupViewPager();
                //Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        });

        //Nếu Nhấn hủy
        builder.setButton(Dialog.BUTTON_NEGATIVE, "Thoát", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nva_share:
                /*Toast.makeText(FullscreenImageActivity.this, "Chức năng tạm thời chưa hỗ trợ!", Toast.LENGTH_SHORT).show();*/
                startActivity(Intent.createChooser(shareIntent(), "Chia sẻ ảnh đang sử dụng"));
                break;
            case R.id.nav_edit:
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                final File photoFile = new File(curPath);
                Uri photoURI = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                editIntent.setDataAndType(photoURI,"image/jpg");
                editIntent.putExtra(Intent.EXTRA_STREAM, photoURI);

                startActivity(Intent.createChooser(editIntent, null));
                break;
            case R.id.nav_crop:
                Crop();
                break;
            case R.id.nva_delete:
                DeleteImage();
                break;
        }
        return false;
    }
  /*  void updateData()
    {
        MainActivity.getImagesPath(this);
        MainActivity.readSQliteDatabaseAlbum(db);
    }*/

    int getPosImageReal()
    {
        posAlbumReal = 0;
        int pos = posImage;
        for(int i = 0; i < MainActivity.all_images_path.size(); i++)
        {
            if(pos >= MainActivity.all_images_path.get(i).getAllImagePath().size())
            {
                pos -= MainActivity.all_images_path.get(i).getAllImagePath().size();
                posAlbumReal++;
            }
            else
            {
                break;
            }
        }
        return pos;
    }

    public void showMain()
    {
        Intent iCreateAl= new Intent(this, MainActivity.class);
        startActivity(iCreateAl);
    }
}
