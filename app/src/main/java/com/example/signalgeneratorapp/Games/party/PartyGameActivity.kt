package com.example.signalgeneratorapp.Games.party

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signalgeneratorapp.signals.SignalWithAmplitude
import com.example.signalgeneratorapp.ui.theme.SignalGeneratorAppTheme
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt

class PartyGameActivity : ComponentActivity () {
    internal val partyGame = PartyGame()
    internal val isTouchActive = AtomicBoolean(false)
    private var sensorRotCallback: ((FloatArray, Long)->Unit) = { values, nanoTimeStamp ->
        if (isTouchActive.get()){
            partyGame.update(values[0].toDouble(), values[1].toDouble())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignalGeneratorAppTheme {
                GameField(this)
            }
        }
    }
}
internal val fontSize = 20.sp
internal val colorRight = Color.Yellow
internal val colorDown = Color.Blue
internal val colorLeft = Color.Red
internal val colorUp = Color.Green
internal val colors = arrayOf(colorUp, colorDown, colorLeft, colorRight)

internal val expanded = Array(4) {mutableStateOf(false)}
internal val selectedSignalType = Array(4) {mutableStateOf("none")}
internal val signalTypes : List<String> = listOf("none").plus(SignalWithAmplitude.SignalWithAmplitudeTypes.keys)

internal var movableSpaceX = 0
internal var movableSpaceY = 0
internal fun calcNormedPosition(max: Int, value: Int): Double {
    return ((value*2 - max).toDouble()/max).coerceIn(-1.0, 1.0)
}

@Composable
fun SignalSelection(expanded : MutableState<Boolean>, selectedSignalType : MutableState<String>, index : Int) {
    val mSelectedSignalType by selectedSignalType
    val mExpandedHor by expanded

    Column (
        Modifier
            .fillMaxWidth()
            .background(colors[index])) {
        Row {
            Text("Horizontal signal: ", fontSize = fontSize)
            Box(Modifier.wrapContentSize(Alignment.TopStart)){
                Text(
                    mSelectedSignalType,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { expanded.value = true })
                        .background(Color.Gray),
                    fontSize = fontSize)
                DropdownMenu(expanded = mExpandedHor,
                    onDismissRequest = { expanded.value = false }
                ){
                    for (signalItem in signalTypes) {
                        DropdownMenuItem(onClick = {
                            selectedSignalType.value = signalItem
    //                            fga?.setSignal(signalItem)
                        },
                            text = { Text(signalItem) })
                    }
                }
            }
        }
        Button(onClick = {
    //                val i = Intent(fga, SignalEditActivity::class.java)
    //                i.putExtra(util.INTENT_SIGNAL_NAME, fga?.signalName)
    //                fga?.startActivity(i)
        }, enabled = selectedSignalType.value != "none"
        ){
            Text("Edit horizontal signal")
        }
    }
}
private var initialize = true
@Composable
fun GameField(pga: PartyGameActivity? = null) {

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var ballColor by remember {
        mutableStateOf(Color.Yellow)
    }
    val ballsize = 20
    val ballsizehalf = ballsize / 2

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        // field
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        colorRight,
                        colorDown,
                        colorLeft,
                        colorUp,
                        colorRight
                    )
                )
            )
            .onGloballyPositioned {
                movableSpaceX = it.size.width - ballsize + 1
                movableSpaceY = it.size.height - ballsize + 1
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown().also {

                        // start touch
                        pga?.isTouchActive?.set(true)
                        it.consume()
                        offsetX = it.position.x - ballsizehalf.dp.toPx()
                        offsetY = it.position.y - ballsizehalf.dp.toPx()
                        pga?.partyGame?.update(
                            calcNormedPosition(movableSpaceX, offsetX.roundToInt()),
                            calcNormedPosition(movableSpaceY, offsetY.roundToInt()))
                        ballColor = Color.Yellow
                    }

                    var change = awaitTouchSlopOrCancellation(down.id) { change, over ->
                        // draged further than initial threshold
                        val original = Offset(offsetX, offsetY)
                        val summed = original + over
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - ballsize.dp.toPx()),
                            y = summed.y.coerceIn(0f, size.height - ballsize.dp.toPx())
                        )
                        offsetX = newValue.x
                        offsetY = newValue.y
                        pga?.partyGame?.update(
                            calcNormedPosition(movableSpaceX, offsetX.roundToInt()),
                            calcNormedPosition(movableSpaceY, offsetY.roundToInt()))
                        change.consume()
                        ballColor = Color.Red
                    }
                    if (change == null) {
                        // abort touch
                        ballColor = Color.Magenta
                        pga?.isTouchActive?.set(false)
                    }
                    while (change != null && change.pressed) {
                        change = awaitDragOrCancellation(change.id)
                        if (change != null && change.pressed) {
                            // continue drag
                            val original = Offset(offsetX, offsetY)
                            val summed = original + change.positionChange()
                            val newValue = Offset(
                                x = summed.x.coerceIn(0f, size.width - ballsize.dp.toPx()),
                                y = summed.y.coerceIn(0f, size.height - ballsize.dp.toPx())
                            )
                            offsetX = newValue.x
                            offsetY = newValue.y
                            pga?.partyGame?.update(
                                calcNormedPosition(movableSpaceX, offsetX.roundToInt()),
                                calcNormedPosition(movableSpaceY, offsetY.roundToInt()))
                            change.consume()
                            ballColor = Color.Green
                        } else if (change != null && change.changedToUp()) {
                            // abort drag
                            change.consume()
                            ballColor = Color.Blue
                            pga?.isTouchActive?.set(false)
                        }
                    }
                }
            }
        ){
            // ball
            Box(modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .size(ballsize.dp, ballsize.dp)
                .clip(CircleShape)
                .background(ballColor)
                )

        }
        Row (horizontalArrangement = Arrangement.spacedBy(5.dp))
        {
            Button(onClick = {

            }){
                Text("Zero position")
            }

        }
        for (i in expanded.indices){
            SignalSelection(expanded[i], selectedSignalType[i], i)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SignalGeneratorAppTheme {
        GameField()
    }
}