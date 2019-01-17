package com.fiveti.a5tphoto.Database;

import java.io.Serializable;
import java.util.ArrayList;

public class Favorite implements Serializable {


    ArrayList<String> allImagePath;


    public ArrayList<String> getAllImagePath() {
        return allImagePath;
    }

    public void setAllImagePath(ArrayList<String> allImagePath) {
        this.allImagePath = allImagePath;
    }

}
