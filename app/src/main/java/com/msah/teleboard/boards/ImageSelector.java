package com.msah.teleboard.boards;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;

/**
 * 图片选择器
 */
public class ImageSelector {

    public static final String EXTRA_RESULT = ImageSelectorActivity.EXTRA_RESULT;
    private boolean mIsShowCarema = true;
    private int mMaxCount = 9;
    private int mSelectMode = ImageSelectorActivity.MODE_MULTI;
    private ArrayList<String> mOriginData;
    private static ImageSelector sSelector;
    private Context mContext;
    private int mRequstType;

    private ImageSelector(Context context){
        mContext = context;
    }

    public static ImageSelector create(Context context){
        if(sSelector == null){
            sSelector = new ImageSelector(context);
        }
        return sSelector;
    }

    public ImageSelector showCamera(boolean show){
        mIsShowCarema = show;
        return sSelector;
    }

    public ImageSelector count(int count){
        mMaxCount = count;
        return sSelector;
    }

    public ImageSelector single(){
        mSelectMode = ImageSelectorActivity.MODE_SINGLE;
        return sSelector;
    }

    public ImageSelector multi(){
        mSelectMode = ImageSelectorActivity.MODE_MULTI;
        return sSelector;
    }

    public ImageSelector origin(ArrayList<String> images){
        mOriginData = images;
        return sSelector;
    }

    public void start(Activity activity, int requestCode){
        if(hasPermission()) {
            activity.startActivityForResult(createIntent(), requestCode);
        }else{
            Toast.makeText(mContext, "No permission", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean hasPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            // Permission was added in API Level 16
            return ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private Intent createIntent(){

        return createIntent(null);
    }

    private Intent createIntent(int[] boundsInts) {
        Intent intent = new Intent(mContext, ImageSelectorActivity.class);
        if (boundsInts != null) {
            intent.putExtra(ImageSelectorActivity.EXTRA_BOUNDS, boundsInts);
        }
        intent.putExtra(ImageSelectorActivity.EXTRA_SHOW_CAMERA, mIsShowCarema);
        intent.putExtra(ImageSelectorActivity.EXTRA_SELECT_COUNT, mMaxCount);
        if(mOriginData != null){
            intent.putStringArrayListExtra(ImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mOriginData);
        }
        intent.putExtra(ImageSelectorActivity.EXTRA_SELECT_MODE, mSelectMode);
        intent.putExtra(ImageSelectorActivity.EXTRA_REQUEST_TYPE, mRequstType);
        return intent;
    }
}

