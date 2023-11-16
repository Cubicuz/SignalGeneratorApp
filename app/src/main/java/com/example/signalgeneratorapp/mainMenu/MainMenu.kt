package com.example.signalgeneratorapp.mainMenu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signalgeneratorapp.Games.Marble.MarbleGameActivity
import com.example.signalgeneratorapp.Games.Move.MoveGameActivity
import com.example.signalgeneratorapp.Games.freeze.FreezeGameActivity
import com.example.signalgeneratorapp.IconCopy.SaveAs
import com.example.signalgeneratorapp.IconCopy.Upload
import com.example.signalgeneratorapp.NewSignal.NewSignalActivity
import com.example.signalgeneratorapp.SensorEdit.SensorEditActivity
import com.example.signalgeneratorapp.Storage.StorageActivity2
import com.example.signalgeneratorapp.ui.theme.SignalGeneratorAppTheme
import kotlin.reflect.KClass

class MainMenu : ComponentActivity () {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignalGeneratorAppTheme {
                Surface (modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    content(this)
                }
            }
        }
    }
}
data class activityItem (
    val name: String,
    val activity: KClass<*>,
    val icon: ImageVector = Upload
)

val activities : List<activityItem> = listOf(
    activityItem("marble", MarbleGameActivity::class, Icons.Filled.Place),
    activityItem("freeze", FreezeGameActivity::class, Icons.Filled.Lock),
    activityItem("move", MoveGameActivity::class, Icons.Filled.ArrowForward),
    activityItem("New signal", NewSignalActivity::class, Icons.Default.Add),
    activityItem("sensor edit", SensorEditActivity::class, Icons.Filled.Settings),
    activityItem("save/load", StorageActivity2::class, SaveAs),
)
val fontSize = 20.sp
@Composable
fun menuItem(i: activityItem, mm: MainMenu?) {

    Button(onClick = {
        val i = Intent(mm, i.activity.java)
        mm?.startActivity(i)
    }, shape = RoundedCornerShape(20)){
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(i.icon, contentDescription = i.name)
            Text(i.name, fontSize = fontSize)
        }
    }
}

@Composable
fun content(mm : MainMenu?) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ){
        items(activities) { name ->
            menuItem(name, mm)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SignalGeneratorAppTheme {
        content(null)
    }
}
