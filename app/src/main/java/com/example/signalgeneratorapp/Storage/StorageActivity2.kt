package com.example.signalgeneratorapp.Storage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.signalgeneratorapp.Storage.ui.theme.SignalGeneratorAppTheme
import com.example.signalgeneratorapp.StorageManager

class StorageActivity2 : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val presetList = remember {
                mutableStateListOf<String>()
            }
            presetList.addAll(StorageManager.getInstance().storedPreferenceNames)
            SignalGeneratorAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PresetList(presetList)
                }
            }
        }
    }

}



@Composable
fun PresetList(presets: SnapshotStateList<String>) {
    Box {
        LazyColumn (modifier = Modifier.fillMaxSize()) {
            itemsIndexed(presets){ _, item ->
                Row (verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { StorageManager.getInstance().loadFromPreset(item)}){
                        Icon(Icons.Filled.Upload, contentDescription = "upload")
                    }
                    Text(text = item, modifier = Modifier
                        .weight(1f))
                    IconButton(onClick = { StorageManager.getInstance().storeToPreset(item)}){
                        Icon(Icons.Filled.SaveAs, contentDescription = "save")
                    }
                    IconButton(onClick = {
                        StorageManager.getInstance().deletePreset(item)
                        presets.remove(item)
                    }){
                        Icon(Icons.Filled.Delete, contentDescription = "delete")
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { presets.add("test") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(all = 16.dp)
        ) {
            Icon(Icons.Filled.Add, "add preset")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SignalGeneratorAppTheme {
        val presetList = remember {
            mutableStateListOf<String>("first", "second")
        }
        PresetList(presetList)
    }
}