package com.example.signalgeneratorapp.Games.Move

import android.hardware.Sensor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.signalgeneratorapp.SensorOutputManager
import com.example.signalgeneratorapp.SignalManager
import com.example.signalgeneratorapp.ui.theme.SignalGeneratorAppTheme

class MoveGameActivity : ComponentActivity() {
    private val moveGame = MoveGame()
    private var sensorCallback: ((FloatArray, Long)->Unit) = { values, nanoTimeStamp ->  moveGame.provideSensorEvent(MoveGame.SensorEvent(values.clone(), nanoTimeStamp))}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignalGeneratorAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Content(moveGame)
                }
            }
        }
        moveGame.updateTickListener = {
            intensity.value = moveGame.intensity
            highestWiggle.value = moveGame.highestWiggle
        }
        SensorOutputManager.getInstance().getSensorOutput(Sensor.TYPE_ACCELEROMETER).connect(sensorCallback)

        intensity.value = 0.0f
    }

    override fun onStart() {
        super.onStart()
        moveGame.start()
    }

    override fun onStop() {
        super.onStop()
        moveGame.stop()
        SensorOutputManager.getInstance().getSensorOutput(Sensor.TYPE_ACCELEROMETER).disconnect(sensorCallback)
    }

    override fun onResume() {
        super.onResume()
        SensorOutputManager.getInstance().start()
        SignalManager.getInstance().startAudio()
    }

    override fun onPause() {
        super.onPause()
        SignalManager.getInstance().stopAudio()
        SensorOutputManager.getInstance().stop()
    }
}

val intensity = mutableStateOf(0.5f)
val highestWiggle = mutableStateOf(0.5f)
@Composable
fun Content(mv: MoveGame? = null) {
    val fontSize = 20.sp
    val mIntensity by intensity
    val mHighestWiggle by highestWiggle

    Column (Modifier.fillMaxSize()) {
        Text("Intensity", fontSize = fontSize)
        Slider(value = mIntensity,
            onValueChange = { mv!!.intensity = it
            intensity.value = it})
        Text("HighestWiggle", fontSize = fontSize)
        LinearProgressIndicator(
            progress = mHighestWiggle,
        )
        Text(mHighestWiggle.toString(), fontSize = fontSize)

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SignalGeneratorAppTheme {
        Content()
    }
}