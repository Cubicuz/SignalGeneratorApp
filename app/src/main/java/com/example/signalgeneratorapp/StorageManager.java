package com.example.signalgeneratorapp;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageManager {

    private SharedPreferences sharedPref;
    public void store(String key, String value){

    }

    public String load(String key){
        return "";
    }

    private StorageManager(Context context){
        sharedPref = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }
}
