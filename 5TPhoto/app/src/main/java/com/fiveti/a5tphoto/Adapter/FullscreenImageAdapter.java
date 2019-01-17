package com.fiveti.a5tphoto.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fiveti.a5tphoto.Activity.FullscreenImageActivity;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class FullscreenImageAdapter extends PagerAdapter {

    private ArrayList<Album> images;
    private int positionAlbum;
    private FullscreenImageActivity context;
    public PhotoView fullImage;
    private int hideToolbar = 0;

    public FullscreenImageAdapter(FullscreenImageActivity context, ArrayList<Album> images, int positionAlbum) {
        this.images = images;
        this.context = context;
        this.positionAlbum = positionAlbum;
    }

    @Override
    public int getCount() {
        return images.get(positionAlbum).getAllImagePath().size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_fullscreen_image, container, false);

        fullImage = view.findViewById(R.id.fullImageView);
        Glide.with(context)
                .load("file://" + images.get(positionAlbum).getAllImagePath().get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(fullImage);


        fullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideToolbar = (hideToolbar + 1) % 2;
                if (hideToolbar == 1) {
                    context.EnterFullScreen();
                } else {
                    context.ExitFullScreen();
               }
            }
        });

        container.addView(view);

        return view;
    }

}