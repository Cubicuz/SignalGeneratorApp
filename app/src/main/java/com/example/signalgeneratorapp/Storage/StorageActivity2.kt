package com.example.signalgeneratorapp.Storage

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.example.signalgeneratorapp.IconCopy.SaveAs
import com.example.signalgeneratorapp.IconCopy.Upload
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
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .height(Dp(60f))
        ){
            // this variable use to handle edit text input value
            val inputvalue = remember { mutableStateOf(TextFieldValue()) }

            TextField(
                value = inputvalue.value,
                onValueChange = {
                    inputvalue.value = it
                },
                modifier = Modifier.weight(0.7f),
                placeholder = { Text(text = "Enter preset name") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = true, keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                ),
                textStyle = TextStyle(
                    color = Color.Black, fontSize = TextUnit.Unspecified,
                    fontFamily = FontFamily.SansSerif
                ),
                maxLines = 1,
                singleLine = true
            )
            val context = LocalContext.current
            Button(
                onClick = {
                    if (presets.contains(inputvalue.value.text)){
                        Toast.makeText(context, "The name is already in use!", Toast.LENGTH_LONG).show()
                    } else {
                        StorageManager.getInstance().storeToPreset(inputvalue.value.text)
                        presets.add(inputvalue.value.text)
                    }
                },
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(5)
            ){
                Text(text = "ADD")
            }
        }

        val activity = (LocalContext.current as? Activity)
        LazyColumn (modifier = Modifier.fillMaxSize()) {
            itemsIndexed(presets){ _, item ->
                Row (verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        StorageManager.getInstance().loadFromPreset(item)
                        activity?.finish()
                    }){
                        Icon(Upload, contentDescription = "upload")
                    }
                    Text(text = item, modifier = Modifier
                        .weight(1f))
                    IconButton(onClick = { StorageManager.getInstance().storeToPreset(item)}){
                        Icon(SaveAs, contentDescription = "save")
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