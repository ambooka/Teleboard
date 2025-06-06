package com.msah.teleboard.boards.models;

import android.text.TextUtils;


public class Image {
    public String path;
    public String name;
    public long time;

    public Image() {
    }

    public Image(String path, String name, long time){
        this.path = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return TextUtils.equals(this.path, other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
