package com.msah.teleboard.boards.models;

import android.text.TextUtils;

import java.util.List;

public class Folder {
    public String name;
    public String path;
    public Image cover;
    public List<com.msah.teleboard.boards.models.Image> images;

    @Override
    public boolean equals(Object o) {
        try {
            Folder other = (Folder) o;
            return TextUtils.equals(other.path, path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
