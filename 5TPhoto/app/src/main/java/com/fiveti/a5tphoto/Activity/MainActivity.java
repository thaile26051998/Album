package com.fiveti.a5tphoto.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fiveti.a5tphoto.Database.SQLiteDatabase;
import com.fiveti.a5tphoto.Fragment.AlbumFragment;
import com.fiveti.a5tphoto.Fragment.GalleryFragment;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.OpenCamera.openCamera;
import com.fiveti.a5tphoto.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Album> all_images_path = new ArrayList<>();
    private static final int REQUEST_PERMISSIONS = 100;

    public static SQLiteDatabase myAlbumdb;
    public static SQLiteDatabase myFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        setSupportActionBar(mToolbar);



        myAlbumdb = new SQLiteDatabase(this, "FiveTPhoto.sqlite", null, 1);
        //myAlbumdb.QueryData("drop table if exists Album");
        // tạo bảng
        myAlbumdb.QueryData("CREATE TABLE IF NOT EXISTS Album (Image_Path TEXT , Album_Name TEXT)");

        myFavorite = new SQLiteDatabase(this, "FiveTPhoto.sqlite", null, 1);
        myFavorite.QueryData("CREATE TABLE IF NOT EXISTS Favorite (Image_Path TEXT )");

        getImagesPath(this);
        readSQliteDatabaseAlbum(myAlbumdb);

        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.add(getResources().getString(R.string.first), 1);
        adapter.add(getResources().getString(R.string.second), 2);
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void add(String title, int id) {
            Fragment fragment = null;
            if (id == 1) {
                fragment = openGallery();
            } else if (id == 2) {
                fragment = openAlbum();
            }
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            case R.id.action_camera:
                Intent iCamera = new Intent(this, openCamera.class);
                startActivity(iCamera);
                break;
            case R.id.action_create_album:
                Intent iCreateAl = new Intent(this, CreateAlbumActivity.class);
                startActivity(iCreateAl);
                break;
            case R.id.action_send_feedback:
                //tạo intent email
                Intent Email = new Intent(Intent.ACTION_SEND);

                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "lequocthai1998@gmail.com" });
                Email.putExtra(Intent.EXTRA_SUBJECT, "5TPhoto Feedback");
                Email.putExtra(Intent.EXTRA_TEXT, "Dear ...," + "");

                //gửi intent email
                startActivity(Intent.createChooser(Email, "Send Feedback:"));
                break;
            case R.id.action_view:
                Intent iCreate = new Intent(this, SelectImg3d.class);
                startActivity(iCreate);

                Toast.makeText(this,"Vui lòng chọn 6 hình",Toast.LENGTH_LONG).show();
                break;
            case R.id.action_compress_image:
                Intent intentCompress = new Intent(this, CompressImageActivity.class);
                startActivity(intentCompress);
                break;

            case R.id.action_favorite_list:

                break;
            case R.id.action_download_image:
                String url = "https://drive.google.com/drive/folders/1L9gFitE3QE_Gv7104APEEoSh7ZfyXVzb?usp=sharing";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public static void getImagesPath(Activity activity) {
        all_images_path.clear();
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

            for (int i = 0; i < all_images_path.size(); i++) {
                if (all_images_path.get(i).getAlbumName().equals(cursor.getString(1))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }

            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(all_images_path.get(int_position).getAllImagePath());
                al_path.add(absolutePathOfImage);
                all_images_path.get(int_position).setAllImagePath(al_path);
                all_images_path.get(int_position).setType(0);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                Album obj_model = new Album();
                obj_model.setAlbumName(cursor.getString(1));
                obj_model.setAllImagePath(al_path);
                obj_model.setType(0);
                all_images_path.add(obj_model);
            }
        }
    }

    public static void readSQliteDatabaseAlbum(SQLiteDatabase db)
    {
        boolean boolean_folder = false;
        int int_position = 0;
        Cursor albumData = db.GetData("SELECT * FROM Album");

        while (albumData.moveToNext())
        {
            for (int i = 0; i < all_images_path.size(); i++) {
                if (all_images_path.get(i).getAlbumName().equals(albumData.getString(1))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }

            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(all_images_path.get(int_position).getAllImagePath());
                al_path.add(albumData.getString(0));
                all_images_path.get(int_position).setAllImagePath(al_path);
                all_images_path.get(int_position).setType(1);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(albumData.getString(0));
                Album obj_model = new Album();
                obj_model.setAlbumName(albumData.getString(1));
                obj_model.setAllImagePath(al_path);
                obj_model.setType(1);
                all_images_path.add(obj_model);
            }
        }
    }

    public Fragment openGallery() {
        Fragment fragment = new GalleryFragment();
        return fragment;
    }

    public Fragment openAlbum() {
        Fragment fragment = new AlbumFragment();
        return fragment;
    }
}
