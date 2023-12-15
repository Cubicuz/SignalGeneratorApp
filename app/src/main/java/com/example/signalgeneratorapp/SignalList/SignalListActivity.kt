package com.example.signalgeneratorapp.SignalList

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.signalgeneratorapp.SignalEdit.SignalEditActivity
import com.example.signalgeneratorapp.SignalManager
import com.example.signalgeneratorapp.ui.theme.SignalGeneratorAppTheme
import com.example.signalgeneratorapp.util

class SignalListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val presetList = remember {
                mutableStateListOf<String>()
            }
            SignalManager.getInstance().signalList.forEach { presetList.add(it.name) }
            SignalGeneratorAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PresetList(presetList, this)
                }
            }
        }

    }
}

@Composable
fun PresetList(presets: SnapshotStateList<String>, sla: SignalListActivity?) {
    Column {
        LazyColumn (modifier = Modifier.fillMaxSize()) {
            itemsIndexed(presets){ _, item ->
                Row (verticalAlignment = Alignment.CenterVertically) {

                    ClickableText(text = AnnotatedString(item), modifier = Modifier
                        .weight(1f).padding(5.dp),
                        onClick = {
                            val i = Intent(sla, SignalEditActivity::class.java)
                            i.putExtra(util.INTENT_SIGNAL_NAME, item)
                            sla?.startActivity(i)
                        })


                    IconButton(onClick = {
                        SignalManager.getInstance().removeSignal(item)
                        presets.remove(item)
                    }){
                        Icon(Icons.Filled.Delete, contentDescription = "delete")
                    }
                }
                Divider(color = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SignalGeneratorAppTheme {
        val presetList = remember {
            mutableStateListOf("sine A", "sine B", "kick signal1")
        }
        PresetList(presetList, null)
    }
}
