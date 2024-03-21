package com.example.signalgeneratorapp.Games.party

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    // field
    Box(modifier = Modifier
        .size(1000.dp, 1000.dp)
        .background(Color.Black))
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var ballColor by remember {
        mutableStateOf(Color.Yellow)
    }
    // ball
    Box(modifier = Modifier
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .size(20.dp, 20.dp)
        .clip(CircleShape)
        .background(ballColor)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    ballColor = Color.Red
                },
                onTap = {
                    ballColor = Color.Blue
                }
            )
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                },
                onDragEnd = { ballColor = Color.Yellow },
                onDragCancel = {ballColor = Color.Green}
            )
        })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val mutableState = remember { mutableStateOf(State(ballPosition = Pair(50f, 50f))) }
    GameField(mutableState)
}