package com.msah.teleboard.boards.models;

import android.graphics.Bitmap;

import com.msah.teleboard.boards.views.CanvasView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SketchData {
    public List<PhotoRecord> photoRecordList;
    public int editMode;

    public SketchData() {
        photoRecordList = new ArrayList<>();
        editMode = CanvasView.EDIT_STROKE;
    }

}
