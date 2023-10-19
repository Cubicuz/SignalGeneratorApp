package com.example.signalgeneratorapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import static com.example.signalgeneratorapp.StorageHelperKt.copyTo;

public class StorageManager {

    private final SharedPreferences sharedGlobalsPref;
    /**
     * preferenceId 0 is always the current state.
     * It can be stored to another preference with id 1 to ...
     * or load from another preference with id 1 to ...
     */
    private final SharedPreferences sharedCurrentPref;
    private final HashSet<String> storedPreferenceNames;
    private final Context context;

    public void store(String key, String value){
        SharedPreferences.Editor editor = sharedCurrentPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void storeSet(String key, Set<String> set){
        SharedPreferences.Editor editor = sharedCurrentPref.edit();
        editor.putStringSet(key, set);
        editor.apply();
    }

    public void storeToPreset(String name) {
        SharedPreferences to = context.getSharedPreferences(context.getString(R.string.preference_file_key) + "_" + name, Context.MODE_PRIVATE);
        if (storedPreferenceNames.contains(name)){
            to.edit().clear().apply();
        } else {
            storedPreferenceNames.add(name);
            sharedGlobalsPref.edit().putStringSet(
                    context.getString(R.string.storage_key_name_of_preference),
                    storedPreferenceNames).apply();
        }
        copyTo(sharedCurrentPref, to);
    }

    public void loadFromPreset(String name){
        if (!storedPreferenceNames.contains(name)){
            return;
        }
        SharedPreferences loadFrom = context.getSharedPreferences(context.getString(R.string.preference_file_key) + "_" + name, Context.MODE_PRIVATE);
        //clear everything
        SignalManager.getInstance().removeAll();
        sharedCurrentPref.edit().clear().apply();

        copyTo(loadFrom, sharedCurrentPref);

        SignalManager.getInstance().loadFromPreferences();
        ConnectionManager.getInstance().loadFromPreferences();
    }
    public void deletePreset(String name){
        if (!storedPreferenceNames.contains(name)){
            return;
        }
        storedPreferenceNames.remove(name);
        sharedGlobalsPref.edit().putStringSet(
                context.getString(R.string.storage_key_name_of_preference),
                storedPreferenceNames).apply();
        SharedPreferences delete = context.getSharedPreferences(context.getString(R.string.preference_file_key) + "_" + name, Context.MODE_PRIVATE);
        delete.edit().clear().apply();

    }

    public HashSet<String> getStoredPreferenceNames(){ return storedPreferenceNames; }

    public String load(String key){
        return sharedCurrentPref.getString(key, "");
    }

    public Set<String> loadSet(String key){
        return sharedCurrentPref.getStringSet(key, null);
    }

    public boolean contains(String key){
        return sharedCurrentPref.contains(key);
    }

    public void remove(String key){
        sharedCurrentPref.edit().remove(key).apply();
    }

    private StorageManager(Context context){
        this.context = context;
        sharedGlobalsPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        storedPreferenceNames = new HashSet<>(sharedGlobalsPref.getStringSet(context.getString(R.string.storage_key_name_of_preference), new HashSet<>()));

        sharedCurrentPref = context.getSharedPreferences(context.getString(R.string.preference_file_key) + "_" + 0, Context.MODE_PRIVATE);

    }
    private static StorageManager instance;
    public static void createInstance(Context context){
        if (instance == null){
            instance = new StorageManager(context);
        }
    }
    public static StorageManager getInstance(){ return instance; }
}
