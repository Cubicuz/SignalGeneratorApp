package com.example.signalgeneratorapp.Storage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.signalgeneratorapp.R
import com.example.signalgeneratorapp.StorageManager

class StorageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.storage_layout)

        var recyclerView : RecyclerView = findViewById(R.id.presetRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = PresetListAdapter()
        recyclerView.adapter = adapter
    }
}