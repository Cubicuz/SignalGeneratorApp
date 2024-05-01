package com.example.signalgeneratorapp.Games.party

import android.util.Log
import com.example.signalgeneratorapp.ConnectionManager
import com.example.signalgeneratorapp.SignalManager
import com.example.signalgeneratorapp.StorageManager
import com.example.signalgeneratorapp.signals.LinearRampSignal
import com.example.signalgeneratorapp.signals.SignalWithAmplitude
import java.util.concurrent.atomic.AtomicBoolean

class PartyGame {
    val directionNames = arrayOf("up", "down", "left", "right")
    val UP = 0; val DOWN = 1; val LEFT = 2; val RIGHT = 3
    private var referenceX = 0.0
    private var referenceY = 0.0
    var maximumSensorRange : Double = 1.0
    private val selfmadeSensorBoundariesBasename = "selfmadeSensorBoundaries"
    private val sefmadeSensorBoundariesDefaults = arrayOf(-1.0f, 1.0f, -1.0f, 1.0f)
    private var selfmadeSensorBoundaries = Array<Double>(4) { i -> StorageManager.getInstance().loadGlobalFloat(selfmadeSensorBoundariesBasename + directionNames[i], sefmadeSensorBoundariesDefaults[i]).toDouble() } // up, down, left, right

    private val resetReferenceOnNextSensorData = AtomicBoolean(false)
    private var resetSensorBoundaries = false
    private var resetSensorBoundariesDirection = 0

    fun provideRotationSensorEvent(fa : FloatArray) : Pair<Double, Double> {
        var xy1 = alignSensorValuesToReference(fa[0].toDouble(), fa[1].toDouble())
        var xy2 = normAlignedSensorValuesToSelfmadeBoundaries(xy1)
        var xy3 = update(xy2.first, xy2.second)
        Log.i("xvalue: ", " " + fa[0].toString() + " : " + xy1.first.toString() + " : " + xy2.first.toString() + " : " + xy3.first.toString())
        return xy3
    }

    fun setSensorBoundaries(direction: Int) {
        resetSensorBoundariesDirection = direction
        resetSensorBoundaries = true
    }
    private fun alignSensorValuesToReference(x: Double, y: Double) : Pair<Double, Double>{
        if (resetReferenceOnNextSensorData.get()){
            resetReferenceOnNextSensorData.set(false)
            referenceX = x
            referenceY = y
        }
        var xm = x - referenceX
        var ym = y - referenceY

        if (xm > maximumSensorRange) {
            xm -= 2*maximumSensorRange
        } else if (xm < -maximumSensorRange) {
            xm += 2*maximumSensorRange
        }

        if (ym > maximumSensorRange) {
            ym -= 2*maximumSensorRange
        } else if (ym < -maximumSensorRange) {
            ym += 2* maximumSensorRange
        }

        return Pair(xm, ym)
    }
    private fun resetSensorBoundaries(value: Pair<Double, Double>){
            resetSensorBoundaries = false
            when (resetSensorBoundariesDirection){
                UP -> if (value.second < 0) {
                    selfmadeSensorBoundaries[UP] = value.second
                    StorageManager.getInstance().storeGlobal(selfmadeSensorBoundariesBasename + directionNames[UP], value.second.toFloat())
                }
                DOWN -> if (value.second > 0) {
                    selfmadeSensorBoundaries[DOWN] = value.second
                    StorageManager.getInstance().storeGlobal(selfmadeSensorBoundariesBasename + directionNames[DOWN], value.second.toFloat())
                }
                LEFT -> if (value.first < 0) {
                    selfmadeSensorBoundaries[LEFT] = value.first
                    StorageManager.getInstance().storeGlobal(selfmadeSensorBoundariesBasename + directionNames[LEFT], value.first.toFloat())
                }
                RIGHT -> if (value.first > 0) {
                    selfmadeSensorBoundaries[RIGHT] = value.first
                    StorageManager.getInstance().storeGlobal(selfmadeSensorBoundariesBasename + directionNames[RIGHT], value.first.toFloat())
                }
            }
    }
    private fun normAlignedSensorValuesToSelfmadeBoundaries(value: Pair<Double, Double>) : Pair<Double, Double>{
        if (resetSensorBoundaries){
            resetSensorBoundaries(value)
        }
        var x = value.first
        x = if (x>0) {
                if (x > selfmadeSensorBoundaries[RIGHT]){
                    Log.i("boundary right", selfmadeSensorBoundaries[RIGHT].toString())
                    1.0
                } else {
                    x/selfmadeSensorBoundaries[RIGHT]
                }
            } else {
                if (x < selfmadeSensorBoundaries[LEFT]){
                    Log.i("boundary left", selfmadeSensorBoundaries[LEFT].toString())
                    -1.0
                } else {
                    -x/selfmadeSensorBoundaries[LEFT]
                }
            }
        var y = value.second
            y = if (y>0) {
                if (y > selfmadeSensorBoundaries[DOWN]){
                    1.0
                } else {
                    y/selfmadeSensorBoundaries[DOWN]
                }
            } else {
                if (y < selfmadeSensorBoundaries[UP]){
                    -1.0
                } else {
                    -y/selfmadeSensorBoundaries[UP]
                }
            }
        return Pair(x, y)
    }


    // input is normed X and Y coordinate from -1 to 1.
    // negative is top left, positive is bottom right
    fun update(x: Double, y: Double) : Pair<Double, Double>{
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
        return Pair(x, y)
    }
    fun resetReferencePoint(){
        resetReferenceOnNextSensorData.set(true)
    }

    fun setOutputSignal(signal: SignalWithAmplitude, index: Int){
        if (index > 4) {
            throw Exception("index too high $index")
        }
        ConnectionManager.getInstance().connect(signal.amplitude(), outputSignals[index].firstOutputPort())
    }

    private val basename = "partyGameOutput"
    private val signalNames = Array(4) { i -> basename + directionNames[i]}
    private val outputSignals : Array<LinearRampSignal> = Array(4) { i -> SignalManager.getInstance().addOrGetSignal(signalNames[i], ::LinearRampSignal) }
}

