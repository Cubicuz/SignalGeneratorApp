package com.example.signalgeneratorapp.Games.shock

import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.signalgeneratorapp.signals.SignalWithAmplitude
import com.example.signalgeneratorapp.signals.SineSignal
import com.example.signalgeneratorapp.ui.theme.SignalGeneratorAppTheme

class ShockGameActivity : ComponentActivity() {
    private var signal : SineSignal? = null
    private var internalOnOffSignal : LinearRampSignal? = null
    val signalName = "shockgame-signal"
    val internalOnOffSignalName = "shockgame-OnOff-signal"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signal = SignalManager.getInstance().getSignal(signalName) as SineSignal?
        internalOnOffSignal = SignalManager.getInstance().addOrGetSignal(internalOnOffSignalName, ::LinearRampSignal)
        selectedSignalType.value = signal?.type ?: "none"
        internalOnOffSignal?.time()?.set(0.01)

        if (signal == null) {
            signal = SignalManager.getInstance().addSignal(signalName, ::SineSignal)
            ConnectionManager.getInstance().connect(signal?.amplitude(), internalOnOffSignal?.firstOutputPort())
            ConnectionManager.getInstance().connectLineout(signal?.firstOutputPort(), 0)
            ConnectionManager.getInstance().connectLineout(signal?.firstOutputPort(), 1)
        }
        frequency.value = (signal?.frequency()?.value?.toFloat()!!)

        setContent {
            SignalGeneratorAppTheme {
                content(internalOnOffSignal, signal)
            }
            // on below line we are keeping screen as ON.
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

    }

    override fun onResume() {
        super.onResume()
        SignalManager.getInstance().startAudio()
        frequency.value = (signal?.frequency()?.value?.toFloat()!!)

    }

    override fun onPause() {
        super.onPause()
        SensorOutputManager.getInstance().stop()
    }

}

internal val selectedSignalType = mutableStateOf("none")
internal val signalTypes : List<String> = listOf("none").plus(SignalWithAmplitude.SignalWithAmplitudeTypes.keys)
internal val frequency = mutableStateOf(0.0f)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun content(signal: LinearRampSignal?, sineSignal: SineSignal?) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        var mFrequency by frequency
        Text(text = "Frequency: " + (mFrequency*mFrequency).toString())
        Slider(mFrequency,
            onValueChange = {
                mFrequency = it
                sineSignal?.frequency()?.set((it * it).toDouble())},
            valueRange = 1f..100f
            )
        var buttonColor by remember { mutableStateOf(Color.Green) }
        Button(onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            modifier = Modifier.pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {buttonColor = Color.Red
                    signal?.input()?.set(1.0)}
                MotionEvent.ACTION_UP -> {buttonColor = Color.Green
                    signal?.input()?.set(0.0)}
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
        content(null, null)
    }
}
