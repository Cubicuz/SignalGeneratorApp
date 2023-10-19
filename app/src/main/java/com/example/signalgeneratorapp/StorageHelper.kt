package com.example.signalgeneratorapp

import android.content.SharedPreferences

fun SharedPreferences.copyTo(dest: SharedPreferences) = with(dest.edit()) {
    for (entry in all.entries) {
        val value = entry.value ?: continue
        val key = entry.key
        when (value) {
            is String -> putString(key, value)
            is Set<*> -> putStringSet(key, value as Set<String>)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Boolean -> putBoolean(key, value)
            else -> error("Unknown value type: $value")
        }
    }
    apply()
}