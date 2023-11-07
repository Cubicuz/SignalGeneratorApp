package com.example.signalgeneratorapp.Games.freeze

import android.hardware.Sensor
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signalgeneratorapp.ConnectionManager
import com.example.signalgeneratorapp.Games.Move.MoveGame.SensorEvent
import com.example.signalgeneratorapp.SensorOutputManager
import com.example.signalgeneratorapp.SignalManager
import com.example.signalgeneratorapp.signals.SignalWithAmplitude
import com.example.signalgeneratorapp.signals.presets.AmpWave
import com.example.signalgeneratorapp.signals.presets.FreqWave
import com.example.signalgeneratorapp.signals.presets.KickSignal
import com.example.signalgeneratorapp.signals.presets.WaveModFreq
import com.example.signalgeneratorapp.ui.theme.SignalGeneratorAppTheme
import com.jsyn.Synthesizer

class FreezeGameActivity : ComponentActivity () {
    internal val freezeGame = FreezeGame()
    private var sensorAccCallback: ((FloatArray, Long)->Unit) = { values, nanoTimeStamp ->  freezeGame.provideAccelerationSensorEvent(
        SensorEvent(values.clone(), nanoTimeStamp))}
    private var sensorRotCallback: ((FloatArray, Long)->Unit) = { values, nanoTimeStamp ->  freezeGame.provideRotationSensorEvent(
        SensorEvent(values.clone(), nanoTimeStamp))}
    private var signal : SignalWithAmplitude? = null
    private val signalName = "freezegame-signal"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignalGeneratorAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Content(this)
                }
            }
            // on below line we are keeping screen as ON.
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        freezeGame.updateTickListener = {
            intensity.value = freezeGame.intensity
            accelerationIntensity.value = freezeGame.latestAcceleration
            rotationIntensity.value = freezeGame.latestRotation
        }
        SensorOutputManager.getInstance().getSensorOutput(Sensor.TYPE_ACCELEROMETER).connect(sensorAccCallback)
        SensorOutputManager.getInstance().getSensorOutput(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR).connect(sensorRotCallback)
        signal = SignalManager.getInstance().getSignal(signalName) as SignalWithAmplitude?
        selectedSignalType.value = signal?.type ?: "none"
        intensity.value = 0.0f
    }

    override fun onStart() {
        super.onStart()
        freezeGame.start()
    }

    override fun onStop() {
        super.onStop()
        freezeGame.stop()
        SensorOutputManager.getInstance().getSensorOutput(Sensor.TYPE_ACCELEROMETER).disconnect(sensorAccCallback)
        SensorOutputManager.getInstance().getSensorOutput(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR).disconnect(sensorRotCallback)
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

    internal fun setSignal(type : String){
        var constructor : ((String, Synthesizer) -> SignalWithAmplitude) ? = null
        when (type){
            KickSignal.type -> constructor = ::KickSignal
            AmpWave.type -> constructor = ::AmpWave
            FreqWave.type -> constructor = ::FreqWave
            WaveModFreq.type -> constructor = ::WaveModFreq
            "none" -> SignalManager.getInstance().removeSignal(signalName)
            else -> throw RuntimeException("this was not expected")
        }

        if (SignalManager.getInstance().signalNameExists(signalName)){
            val old = SignalManager.getInstance().getSignal(signalName)
            if (old.type.equals(type)){
                // we already have the correct signal
                signal = old as SignalWithAmplitude?
                ConnectionManager.getInstance().connect(signal?.amplitude(), freezeGame.getOutputPort())
                return
            } else {
                // delete the unfitting
                SignalManager.getInstance().removeSignal(signalName)
            }
        }

        signal = SignalManager.getInstance().addSignal(signalName, constructor)
        ConnectionManager.getInstance().connect(signal?.amplitude(), freezeGame.getOutputPort())
        ConnectionManager.getInstance().connectLineout(signal?.firstOutputPort(), 0)
        ConnectionManager.getInstance().connectLineout(signal?.firstOutputPort(), 1)
    }
}

internal val intensity = mutableStateOf(0.0f)
internal val accelerationIntensity = mutableStateOf(0.0f)
internal val rotationIntensity = mutableStateOf(0.0f)
internal val expanded = mutableStateOf(false)
internal val selectedSignalType = mutableStateOf("none")
internal val signalTypes = listOf("none", KickSignal.type, AmpWave.type, FreqWave.type, WaveModFreq.type)

@Composable
fun Content(fga: FreezeGameActivity? = null) {
    val fontSize = 20.sp
    val mIntensity by intensity
    val mAccIntensity by accelerationIntensity
    val mRotIntensity by rotationIntensity
    val mExpanded by expanded
    val mSelectedSignalType by selectedSignalType

    val mAccFactor = remember { mutableStateOf(fga?.freezeGame?.accelerationFactor.toString()) }
    val mRotFactor = remember { mutableStateOf(fga?.freezeGame?.rotationFactor.toString()) }

    val mSwitchX = remember { mutableStateOf(fga?.freezeGame?.dimensionEnabled(0) ?: true) }
    val mSwitchY = remember { mutableStateOf(fga?.freezeGame?.dimensionEnabled(1) ?: true) }
    val mSwitchZ = remember { mutableStateOf(fga?.freezeGame?.dimensionEnabled(2) ?: true) }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text("Intensity:", fontSize = fontSize)
        LinearProgressIndicator(
            progress = mIntensity
        )
        Text("Acceleration intensity:", fontSize = fontSize)
        Text(mAccIntensity.toString(), fontSize = fontSize)
        Text("Rotation intensity:", fontSize = fontSize)
        Text(mRotIntensity.toString(), fontSize = fontSize)
        Text("Acceleration Factor:", fontSize = fontSize)
        TextField(
            value = mAccFactor.value,
            onValueChange = {
                fga?.freezeGame?.accelerationFactor = it.toFloat()
                mAccFactor.value = fga?.freezeGame?.accelerationFactor.toString()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Text("Rotation Factor:", fontSize = fontSize)
        TextField(
            value = mRotFactor.value,
            onValueChange = {
                fga?.freezeGame?.rotationFactor = it.toFloat()
                mRotFactor.value = fga?.freezeGame?.rotationFactor.toString()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Button(onClick = {
            fga?.freezeGame?.reset()
        }){
            Text("Reset position")
        }


        Row {
            Text("Signal: ", fontSize = fontSize)
            Box(Modifier.fillMaxWidth().wrapContentSize(Alignment.TopStart)){
                Text(
                    mSelectedSignalType,
                    modifier = Modifier.fillMaxWidth()
                        .clickable(onClick = { expanded.value = true })
                        .background(Color.Gray),
                    fontSize = fontSize)
                DropdownMenu(expanded = mExpanded,
                    onDismissRequest = { expanded.value = false }
                ){
                    for (signalItem in signalTypes) {
                        DropdownMenuItem(onClick = {
                            selectedSignalType.value = signalItem
                            fga?.setSignal(signalItem)
                        },
                            text = { Text(signalItem) })
                    }
                }
            }
        }
        Row (verticalAlignment = Alignment.CenterVertically) {
            Text("Dimension X: ",
                fontSize = fontSize
            )
            Switch(
                checked = mSwitchX.value,
                onCheckedChange = {
                    mSwitchX.value = it
                    fga?.freezeGame?.enableDimension(0, it)
                }
            )
        }
        Row (verticalAlignment = Alignment.CenterVertically) {
            Text("Dimension Y: ",
                fontSize = fontSize
            )
            Switch(
                checked = mSwitchY.value,
                onCheckedChange = {
                    mSwitchY.value = it
                    fga?.freezeGame?.enableDimension(1, it)
                }
            )
        }
        Row (verticalAlignment = Alignment.CenterVertically) {
            Text("Dimension Z: ",
                fontSize = fontSize
            )
            Switch(
                checked = mSwitchZ.value,
                onCheckedChange = {
                    mSwitchZ.value = it
                    fga?.freezeGame?.enableDimension(2, it)
                }
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SignalGeneratorAppTheme {
        Content()
    }
}
