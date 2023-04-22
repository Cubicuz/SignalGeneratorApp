package com.example.signalgeneratorapp;

import android.app.Application;
import android.content.Context;

// context workaround from https://stackoverflow.com/questions/4391720/how-can-i-get-a-resource-content-from-a-static-context/4391811#4391811
public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext(){return context;}
}
