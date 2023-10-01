package com.example.signalgeneratorapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class StorageManager {

    private final SharedPreferences sharedPref;
    private SharedPreferences sharedSelectedPref;
    private final Context context;
    private int selectedPreferenceID = 0;
    public void store(String key, String value){
        SharedPreferences.Editor editor = sharedSelectedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void storeSet(String key, Set<String> set){
        SharedPreferences.Editor editor = sharedSelectedPref.edit();
        editor.putStringSet(key, set);
        editor.apply();
    }

    public void selectPreference(int i){
        selectedPreferenceID = i;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(context.getString(R.string.storage_key_selected_preference), i);
        editor.apply();
        sharedSelectedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key) + "_" + i, Context.MODE_PRIVATE);
    }

    public String load(String key){
        return sharedSelectedPref.getString(key, "");
    }

    public Set<String> loadSet(String key){
        return sharedSelectedPref.getStringSet(key, null);
    }

    public boolean contains(String key){
        return sharedSelectedPref.contains(key);
    }

    public void remove(String key){
        SharedPreferences.Editor editor = sharedSelectedPref.edit();
        editor.remove(key);
        editor.commit();
    }

    private StorageManager(Context context){
        this.context = context;
        sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        selectedPreferenceID = sharedPref.getInt(context.getString(R.string.storage_key_selected_preference), 0);
        selectPreference(selectedPreferenceID);
    }
    private static StorageManager instance;
    public static void createInstance(Context context){
        if (instance == null){
            instance = new StorageManager(context);
        }
    }
    public static StorageManager getInstance(){ return instance; }
}
