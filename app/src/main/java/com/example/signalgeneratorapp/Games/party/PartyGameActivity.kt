package com.example.signalgeneratorapp.Games.party

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

class PartyGameActivity : ComponentActivity () {
    val mutableState = mutableStateOf(State(ballPosition = Pair(50f, 50f)))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameField(state = mutableState)
        }
    }
}
data class State(
    var ballPosition: Pair<Float, Float>
)

@Composable
fun GameField(state: MutableState<State>) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var ballColor by remember {
        mutableStateOf(Color.Yellow)
    }
    val ballsize = 20
    val ballsizehalf = ballsize / 2
    // field
    Box(modifier = Modifier
        .size(1000.dp, 1000.dp)
        .background(Color.Black)
        .pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown().also {
                    it.consume()
                    offsetX = it.position.x - ballsizehalf.dp.toPx()
                    offsetY = it.position.y - ballsizehalf.dp.toPx()
                    ballColor = Color.Yellow
                }

                var change = awaitTouchSlopOrCancellation(down.id) { change, over ->
                    val original = Offset(offsetX, offsetY)
                    val summed = original + over
                    val newValue = Offset(
                        x = summed.x.coerceIn(0f, size.width - ballsize.dp.toPx()),
                        y = summed.y.coerceIn(0f, size.height - ballsize.dp.toPx())
                    )
                    offsetX = newValue.x
                    offsetY = newValue.y
                    change.consume()
                    ballColor = Color.Red
                }
                while (change != null && change.pressed) {
                    change = awaitDragOrCancellation(change.id)
                    if (change != null && change.pressed) {
                        val original = Offset(offsetX, offsetY)
                        val summed = original + change.positionChange()
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - ballsize.dp.toPx()),
                            y = summed.y.coerceIn(0f, size.height - ballsize.dp.toPx())
                        )
                        offsetX = newValue.x
                        offsetY = newValue.y
                        change.consume()
                        ballColor = Color.Green
                    }
                }
            }
        }
    )
    // ball
    Box(modifier = Modifier
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .size(ballsize.dp, ballsize.dp)
        .clip(CircleShape)
        .background(ballColor)
        )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val mutableState = remember { mutableStateOf(State(ballPosition = Pair(50f, 50f))) }
    GameField(mutableState)
}