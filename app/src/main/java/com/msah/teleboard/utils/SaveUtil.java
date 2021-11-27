package com.msah.teleboard.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.msah.teleboard.notes.NotesActivity;
import com.msah.teleboard.notes.styles.toolbar.CustomToolbar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class SaveUtil {

    @SuppressLint("SimpleDateFormat")
    public static void saveHtml(Activity activity, String html, String noteName) {


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请授权
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CustomToolbar.REQ_VIDEO);

            }
            File directory = new File(activity.getExternalFilesDir(null) + File.separator + "Notes");
            File dir = new File(activity.getExternalFilesDir(null) + File.separator + "Notes");
            if (!dir.exists()) {
                boolean makedir =  dir.mkdir();
            }
           /**
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh_mm_ss");
            String time = dateFormat.format(new Date());
            String fileName = time.concat(".html");
**/
            File file = new File(activity.getExternalFilesDir(null) + File.separator + "Notes" +File.separator+ noteName);
            if (!file.exists()) {
                boolean isCreated = file.createNewFile();
                if (!isCreated) {
                    Toast.makeText(activity, "Cannot create file at ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(html);
            fileWriter.close();

            Toast.makeText(activity, noteName + " has been saved at ",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Run into error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}

