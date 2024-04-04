package com.example.signalgeneratorapp.Games.party

import android.util.Log
import com.example.signalgeneratorapp.ConnectionManager
import com.example.signalgeneratorapp.SignalManager
import com.example.signalgeneratorapp.signals.LinearRampSignal
import com.example.signalgeneratorapp.signals.SignalWithAmplitude

class PartyGame {


    // input is normed X and Y coordinate from -1 to 1.
    // negative is top left, positive is bottom right
    fun update(x: Double, y: Double){
        x.coerceIn(-1.0, 1.0)
        y.coerceIn(-1.0, 1.0)
        Log.d("update", "X: $x   Y: $y")
        var left=0.0; var right=0.0; var up=0.0; var down=0.0
        if (x > 0) {
            right = x
        } else if (x < 0) {
            left = -x
        }

        if (y > 0) {
            down = y
        } else if (y < 0) {
            up = -y
        }
        outputSignals[0].input().set(up)
        outputSignals[1].input().set(down)
        outputSignals[2].input().set(left)
        outputSignals[3].input().set(right)
    }

    fun setOutputSignal(signal: SignalWithAmplitude, index: Int){
        if (index > 4) {
            throw Exception("index too high $index")
        }
        ConnectionManager.getInstance().connect(signal.amplitude(), outputSignals[index].firstOutputPort())
    }

    private val basename = "partyGameOutput"
    private val signalNames = arrayOf(basename+"up", basename+"down", basename+"left", basename+"right")
    private val outputSignals : Array<LinearRampSignal> = Array(4) { i -> SignalManager.getInstance().addOrGetSignal(signalNames[i], ::LinearRampSignal) }
}

