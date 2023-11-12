package com.example.signalgeneratorapp.Games.Move

import android.content.Intent
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
import com.example.signalgeneratorapp.SensorOutputManager
import com.example.signalgeneratorapp.SignalEdit.SignalEditActivity
import com.example.signalgeneratorapp.SignalManager
import com.example.signalgeneratorapp.signals.SignalWithAmplitude
import com.example.signalgeneratorapp.signals.presets.AmpWave
import com.example.signalgeneratorapp.signals.presets.FreqWave
import com.example.signalgeneratorapp.signals.presets.KickSignal
import com.example.signalgeneratorapp.signals.presets.WaveModFreq
import com.example.signalgeneratorapp.ui.theme.SignalGeneratorAppTheme
import com.example.signalgeneratorapp.util
import com.jsyn.Synthesizer

class MoveGameActivity : ComponentActivity() {
    internal val moveGame = MoveGame()
    private var sensorCallback: ((FloatArray, Long)->Unit) = { values, nanoTimeStamp ->  moveGame.provideSensorEvent(MoveGame.SensorEvent(values.clone(), nanoTimeStamp))}
    private var signal : SignalWithAmplitude? = null
    val signalName = "movegame-signal"
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
        moveGame.updateTickListener = {
            intensity.value = moveGame.intensity
            highestWiggle.value = moveGame.highestWiggle
        }
        SensorOutputManager.getInstance().getSensorOutput(Sensor.TYPE_ACCELEROMETER).connect(sensorCallback)
        signal = SignalManager.getInstance().getSignal(signalName) as SignalWithAmplitude?
        selectedSignalType.value = signal?.type ?: "none"
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
                ConnectionManager.getInstance().connect(signal?.amplitude(), moveGame.getOutputPort())
                return
            } else {
                // delete the unfitting
                SignalManager.getInstance().removeSignal(signalName)
            }
        }

        signal = SignalManager.getInstance().addSignal(signalName, constructor)
        ConnectionManager.getInstance().connect(signal?.amplitude(), moveGame.getOutputPort())
        ConnectionManager.getInstance().connectLineout(signal?.firstOutputPort(), 0)
        ConnectionManager.getInstance().connectLineout(signal?.firstOutputPort(), 1)
    }
}

internal val intensity = mutableStateOf(0.5f)
internal val highestWiggle = mutableStateOf(0.5f)
internal val expanded = mutableStateOf(false)
internal val selectedSignalType = mutableStateOf("none")
internal val signalTypes = listOf("none", KickSignal.type, AmpWave.type, FreqWave.type, WaveModFreq.type)

@Composable
internal fun Content(mva: MoveGameActivity? = null) {
    val fontSize = 20.sp
    val mIntensity by intensity
    val mHighestWiggle by highestWiggle
    val mExpanded by expanded
    val mSelectedSignalType by selectedSignalType

    val mWiggleThreshold = remember { mutableStateOf(mva?.moveGame?.wiggleThreshold.toString())}
    val mStrongWiggleThreshold = remember { mutableStateOf(mva?.moveGame?.strongWiggleThreshold.toString())}
    val mIncrease = remember { mutableStateOf(mva?.moveGame?.intensityIncrement.toString())}
    val mStrongWiggleDecrement = remember { mutableStateOf(mva?.moveGame?.strongWiggleDecrement.toString())}


    Column (Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text("Intensity:", fontSize = fontSize)
        Slider(value = mIntensity,
            onValueChange = { mva!!.moveGame.intensity = it
            intensity.value = it})
        Text("HighestWiggle:", fontSize = fontSize)
        LinearProgressIndicator(
            progress = mHighestWiggle
        )
        Text(mHighestWiggle.toString(), fontSize = fontSize)
        Text("WiggleThreshold:", fontSize = fontSize)
        TextField(
            value = mWiggleThreshold.value,
            onValueChange = {
                mva?.moveGame?.wiggleThreshold = it.toFloat()
                mWiggleThreshold.value = mva?.moveGame?.wiggleThreshold.toString()
                            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Text("StrongWiggleThreshold:", fontSize = fontSize)
        TextField(
            value = mStrongWiggleThreshold.value,
            onValueChange = {
                mva?.moveGame?.strongWiggleThreshold = it.toFloat()
                mStrongWiggleThreshold.value = mva?.moveGame?.strongWiggleThreshold.toString()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Text("StrongWiggleDecrement:", fontSize = fontSize)
        TextField(
            value = mStrongWiggleDecrement.value,
            onValueChange = {
                mva?.moveGame?.strongWiggleDecrement = it.toFloat()
                mStrongWiggleDecrement.value = mva?.moveGame?.strongWiggleDecrement.toString()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Text("Increment without wiggle:", fontSize = fontSize)
        TextField(
            value = mIncrease.value,
            onValueChange = {
                mva?.moveGame?.intensityIncrement = it.toFloat()
                mIncrease.value = mva?.moveGame?.intensityIncrement.toString()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Row {
            Text("Signal: ", fontSize = fontSize)
            Box(Modifier.wrapContentSize(Alignment.TopStart)){
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
                                mva?.setSignal(signalItem)
                                                   },
                            text = { Text(signalItem) })
                    }
                }
            }
        }
        Button(onClick = {
            val i = Intent(mva, SignalEditActivity::class.java)
            i.putExtra(util.INTENT_SIGNAL_NAME, mva?.signalName)
            mva?.startActivity(i)
        }, enabled = selectedSignalType.value != "none"
        ){
            Text("Edit selected signal")
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
