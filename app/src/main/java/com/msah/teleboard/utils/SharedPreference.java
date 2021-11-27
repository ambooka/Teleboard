package com.msah.teleboard.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.msah.teleboard.R;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreference
{
    private SharedPreferences sharedPreferences;

    public SharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.login_preferences), MODE_PRIVATE);
    }



    public String getIdUser() {
        return sharedPreferences.getString("idUser", " ");
    }

    public void setIdUser(String isUser) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idUser", isUser);
        editor.apply();
    }

    public void setUser(String user)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", user);
        editor.apply();
    }
    public String getUser() {
        return sharedPreferences.getString("user", "");
    }


}
