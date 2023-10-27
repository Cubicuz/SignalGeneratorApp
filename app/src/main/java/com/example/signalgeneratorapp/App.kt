package com.example.signalgeneratorapp

import android.app.Application
import android.content.Context

// context workaround from https://stackoverflow.com/questions/4391720/how-can-i-get-a-resource-content-from-a-static-context/4391811#4391811
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        @JvmStatic
        var context: Context? = null
            private set
    }
}
