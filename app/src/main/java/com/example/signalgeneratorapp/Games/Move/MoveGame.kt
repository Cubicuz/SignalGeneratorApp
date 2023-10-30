package com.example.signalgeneratorapp.Games.Move

import android.os.SystemClock
import com.example.signalgeneratorapp.SignalManager
import com.example.signalgeneratorapp.signals.LinearRampSignal
import com.jsyn.ports.UnitOutputPort
import java.lang.Float.max
import java.lang.Float.min
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

class MoveGame {
    var wiggleThreshold = 10.0F
    var strongWiggleThreshold = 50.0F
    var strongWiggleDecrement = 0.01F

    var intensity = 0.0F
    var intensityIncrement = 0.001F
    var intensityMaximum = 1.0F

    var highestWiggle = 0.0F

    private val sensorEvents = ArrayBlockingQueue<SensorEvent>(100)
    private var lastSensorEvent = SensorEvent(FloatArray(3), 0L)
    private var tickTimeStamp: Long = SystemClock.elapsedRealtimeNanos()
    private val gameTickTimeInNanos: Long = 100_000_000

    private val output: LinearRampSignal = SignalManager.getInstance().addOrGetSignal("moveOutput", ::LinearRampSignal)
    init {
        output.time().set(0.01)
    }
    fun getOutputPort(): UnitOutputPort {
        return output.firstOutputPort()
    }
    fun provideSensorEvent(se : SensorEvent) {
        if (lastSensorEvent.nanoTimeStamp != 0L){
            sensorEvents.offer(se)
        } else {
            lastSensorEvent = se
        }
    }
    private fun update() {
        val se: SensorEvent = sensorEvents.take()
        val wiggle = calcWiggleFactor(lastSensorEvent, se)
        lastSensorEvent = se
        highestWiggle = max(wiggle, highestWiggle)

        if (tickTimeStamp < se.nanoTimeStamp){
            tickTimeStamp = se.nanoTimeStamp + gameTickTimeInNanos
            updateTick()
            highestWiggle = 0f
        }
    }
    private fun updateTick() {
        if (highestWiggle > strongWiggleThreshold){
            intensity = max(intensity - strongWiggleDecrement, 0F)
        } else if (highestWiggle > wiggleThreshold){
            // do not increment
        } else {
            intensity = min(intensity + intensityIncrement, intensityMaximum)
        }
        output.input().set(intensity.toDouble())
        updateTickListener?.invoke()
    }
    var updateTickListener: (()->Unit)? = null
    private fun calcWiggleFactor(s1: SensorEvent, s2: SensorEvent): Float {
        val vecX = s2.values[0] - s1.values[0]
        val vecY = s2.values[1] - s1.values[1]
        val vecZ = s2.values[2] - s1.values[2]
        return vecX * vecX + vecY * vecY + vecZ * vecZ
    }
    private val running = AtomicBoolean(true)
    inner class GameThread : Thread (){
        override fun run() {
            while (running.get()){
                update()
            }
        }
    }
    private var gt : GameThread? = null
    fun start() {
        gt = GameThread()
        gt!!.start()
    }
    fun stop() {
        running.set(false)
        sensorEvents.offer(lastSensorEvent)
    }

    data class SensorEvent (var values: FloatArray, var nanoTimeStamp: Long)

}