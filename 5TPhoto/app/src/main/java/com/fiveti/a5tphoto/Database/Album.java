package com.fiveti.a5tphoto.Database;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {

    String albumName;
    int type; //loai Album, 0 nếu là Album của đt, 1 là Album tự tạo
    ArrayList<String> allImagePath;

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public ArrayList<String> getAllImagePath() {
        return allImagePath;
    }

    public void setAllImagePath(ArrayList<String> allImagePath) {
        this.allImagePath = allImagePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
