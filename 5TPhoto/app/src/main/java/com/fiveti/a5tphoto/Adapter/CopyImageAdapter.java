package com.fiveti.a5tphoto.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fiveti.a5tphoto.Fragment.SquareImageView;
import com.fiveti.a5tphoto.Database.Album;
import com.fiveti.a5tphoto.R;

import java.util.ArrayList;

public class CopyImageAdapter extends ArrayAdapter<Album> {

    Context context;
    ViewHolder viewHolder;
    ArrayList<Album> all_images_path = new ArrayList<>();


    public CopyImageAdapter(Context context, ArrayList<Album> al_menu) {
        super(context, R.layout.item_select_album, al_menu);
        this.all_images_path = al_menu;
        this.context = context;


    }

    @Override
    public int getCount() {

        Log.e("ADAPTER LIST SIZE", all_images_path.size() + "");
        return all_images_path.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (all_images_path.size() > 0) {
            return all_images_path.size();
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_select_album, parent, false);
            viewHolder.folder = (TextView) convertView.findViewById(R.id.txtNameAlbum);
            viewHolder.imageAlbum = (SquareImageView) convertView.findViewById(R.id.imgSelectAlbum);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.folder.setText(all_images_path.get(position).getAlbumName());



        Glide.with(context).load("file://" + all_images_path.get(position).getAllImagePath().get(0))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(viewHolder.imageAlbum);


        return convertView;

    }

    private static class ViewHolder {
        TextView folder;
        SquareImageView imageAlbum;
    }
}
