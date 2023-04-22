package com.example.signalgeneratorapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class StorageManager {

    private SharedPreferences sharedPref;
    public void store(String key, String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void storeSet(String key, Set<String> set){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(key, set);
        editor.apply();
    }

    public String load(String key){
        return sharedPref.getString(key, "");
    }

    public Set<String> loadSet(String key){
        return sharedPref.getStringSet(key, null);
    }

    public boolean contains(String key){
        return sharedPref.contains(key);
    }

    public void remove(String key){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.commit();
    }

    private StorageManager(Context context){
        sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }
    private static StorageManager instance;
    public static void createInstance(Context context){
        if (instance == null){
            instance = new StorageManager(context);
        }
    }
    public static StorageManager getInstance(){ return instance; }
}
