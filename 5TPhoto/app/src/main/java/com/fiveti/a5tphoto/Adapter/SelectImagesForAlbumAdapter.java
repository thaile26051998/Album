package com.fiveti.a5tphoto.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fiveti.a5tphoto.Activity.SelectImagesActivity;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.Fragment.SquareImageView;
import com.fiveti.a5tphoto.R;

import java.util.ArrayList;

public class SelectImagesForAlbumAdapter extends ArrayAdapter<Album> {

    Context context;
    ViewHolder viewHolder;
    ArrayList<Album> all_images_path = new ArrayList<>();
    int int_position;


    public SelectImagesForAlbumAdapter(Context context, ArrayList<Album> allPath, int int_position) {
        super(context, R.layout.item_select_images_grid_layout, allPath);
        this.all_images_path = allPath;
        this.context = context;
        this.int_position = int_position;
    }

    @Override
    public int getCount() {
        return all_images_path.get(int_position).getAllImagePath().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (all_images_path.get(int_position).getAllImagePath().size() > 0) {
            return all_images_path.get(int_position).getAllImagePath().size();
        } else {
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_select_images_grid_layout, parent, false);
            // viewHolder.folder = (TextView) convertView.findViewById(R.id.txtFolder);
            viewHolder.selectImage = (CheckBox) convertView.findViewById(R.id.selectCheckBox);
            viewHolder.imageAlbum = (SquareImageView) convertView.findViewById(R.id.imgAlbum);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(SelectImagesActivity.click_mode)
        {
            viewHolder.selectImage.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.selectImage.setVisibility(View.GONE);
        }

        viewHolder.selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 /*   if(viewHolder.selectImage.isChecked())
                    {
                        viewHolder.imageAlbum.layout(5, 5, 5, 5);
                    }
                    else
                    {
                        viewHolder.imageAlbum.layout(0, 0, 0, 0);
                    }*/
                SelectImagesActivity.prepareSelection(v, position);
            }
        });


        Glide.with(context).load("file://" + all_images_path.get(int_position).getAllImagePath().get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(viewHolder.imageAlbum);


        return convertView;

    }

    private static class ViewHolder {
        CheckBox selectImage;
        SquareImageView imageAlbum;
    }
}

