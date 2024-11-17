package com.example.signalgeneratorapp.Games.shock

import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.signalgeneratorapp.ConnectionManager
import com.example.signalgeneratorapp.SensorOutputManager
import com.example.signalgeneratorapp.SignalManager
import com.example.signalgeneratorapp.signals.LinearRampSignal
import com.example.signalgeneratorapp.signals.SineSignal
import com.example.signalgeneratorapp.ui.theme.SignalGeneratorAppTheme
import kotlin.math.sqrt

class ShockGameActivity : ComponentActivity() {
    private var signalLeft : SineSignal? = null
    private var internalOnOffSignalLeft : LinearRampSignal? = null
    private var signalRight : SineSignal? = null
    private var internalOnOffSignalRight : LinearRampSignal? = null
    val signalNameLeft = "shockgame-signal-left"
    val internalOnOffSignalNameLeft = "shockgame-OnOff-signal-left"
    val signalNameRight = "shockgame-signal-right"
    val internalOnOffSignalNameRight = "shockgame-OnOff-signal-right"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signalLeft = SignalManager.getInstance().addOrGetSignal(signalNameLeft, ::SineSignal)
        internalOnOffSignalLeft = SignalManager.getInstance().addOrGetSignal(internalOnOffSignalNameLeft, ::LinearRampSignal)
        internalOnOffSignalLeft?.time()?.set(0.01)

        signalRight = SignalManager.getInstance().addOrGetSignal(signalNameRight, ::SineSignal)
        internalOnOffSignalRight = SignalManager.getInstance().addOrGetSignal(internalOnOffSignalNameRight, ::LinearRampSignal)
        internalOnOffSignalRight?.time()?.set(0.01)

        ConnectionManager.getInstance().connect(signalLeft?.amplitude(), internalOnOffSignalLeft?.firstOutputPort())
        ConnectionManager.getInstance().connect(signalRight?.amplitude(), internalOnOffSignalRight?.firstOutputPort())
        ConnectionManager.getInstance().connectLineout(signalLeft?.firstOutputPort(), 0)
        ConnectionManager.getInstance().connectLineout(signalRight?.firstOutputPort(), 1)

        frequency.value = sqrt((signalLeft?.frequency()?.value?.toFloat()!!))

        setContent {
            SignalGeneratorAppTheme {
                content(internalOnOffSignalLeft, signalLeft, internalOnOffSignalRight, signalRight)
            }
            // on below line we are keeping screen as ON.
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

    }

    override fun onResume() {
        super.onResume()
        SignalManager.getInstance().startAudio()

    }

    override fun onPause() {
        super.onPause()
        internalOnOffSignalLeft?.input()?.set(0.0)
        internalOnOffSignalRight?.input()?.set(0.0)
        SensorOutputManager.getInstance().stop()
    }

}

internal val frequency = mutableStateOf(0.0f)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun content(signalLeft: LinearRampSignal?, sineSignalLeft: SineSignal?, signalRight: LinearRampSignal?, sineSignalRight: SineSignal?) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        var mFrequency by frequency
        var buttonColor by remember { mutableStateOf(Color.Green) }
        Text(text = "Frequency: " + (mFrequency*mFrequency).toString())
        Slider(value = mFrequency,
            onValueChange = {
                mFrequency = it
                sineSignalLeft?.frequency()?.set((it * it).toDouble())
                sineSignalRight?.frequency()?.set((it * it).toDouble())
                            },
            valueRange = 1f..100f,
            modifier = Modifier.pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {buttonColor = Color.Red
                        signalLeft?.input()?.set(1.0)
                        signalRight?.input()?.set(1.0)}
                }
                false
            },
            onValueChangeFinished = {
                buttonColor = Color.Green
                signalLeft?.input()?.set(0.0)
                signalRight?.input()?.set(0.0)
            }
            )



        var buttonColorLeft by remember { mutableStateOf(Color.Green) }
        var buttonColorRight by remember { mutableStateOf(Color.Green) }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Button(onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = buttonColorLeft),
                modifier = Modifier.pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {buttonColorLeft = Color.Red
                            signalLeft?.input()?.set(1.0)}
                        MotionEvent.ACTION_UP -> {buttonColorLeft = Color.Green
                            signalLeft?.input()?.set(0.0)}
                        else -> false
                    }
                    true
                }.weight(1f))
            { Text("~ Shock Left ~") }
            Button(onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = buttonColorRight),
                modifier = Modifier.pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {buttonColorRight = Color.Red
                            signalRight?.input()?.set(1.0)}
                        MotionEvent.ACTION_UP -> {buttonColorRight = Color.Green
                            signalRight?.input()?.set(0.0)}
                        else -> false
                    }
                    true
                }.weight(1f))
            { Text("~ Shock Right ~") }
       }

        Button(onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            modifier = Modifier.pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {buttonColor = Color.Red
                    signalLeft?.input()?.set(1.0)
                    signalRight?.input()?.set(1.0)}
                MotionEvent.ACTION_UP -> {buttonColor = Color.Green
                    signalLeft?.input()?.set(0.0)
                    signalRight?.input()?.set(0.0)}
                else -> false
            }
            true
            }.fillMaxWidth())
        { Text("~~~ Shock ~~~") }

    }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SignalGeneratorAppTheme {
        content(null, null, null, null)
    }
}
