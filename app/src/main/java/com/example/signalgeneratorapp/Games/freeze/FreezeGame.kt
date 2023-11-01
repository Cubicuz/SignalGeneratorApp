package com.example.signalgeneratorapp.Games.freeze

import android.os.SystemClock
import com.example.signalgeneratorapp.Games.Move.MoveGame
import com.example.signalgeneratorapp.SignalManager
import com.example.signalgeneratorapp.StorageManager
import com.example.signalgeneratorapp.signals.LinearRampSignal
import com.jsyn.ports.UnitOutputPort
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class FreezeGame {
    private val accelerationFactorStorageKey = "moveGameAccelerationFactorStorageKey"
    var accelerationFactor : Float = StorageManager.getInstance().loadGlobalFloat(accelerationFactorStorageKey, 1.0f)
        set(value) {
            field = value
            StorageManager.getInstance().storeGlobal(accelerationFactorStorageKey, value)
        }

    private val rotationFactorStorageKey = "moveGameRotationFactorStorageKey"
    var rotationFactor : Float = StorageManager.getInstance().loadGlobalFloat(rotationFactorStorageKey, 1.0f)
        set(value) {
            field = value
            StorageManager.getInstance().storeGlobal(rotationFactorStorageKey, value)
        }


    private val intensityMaximum = 1.0f
    @Volatile
    var intensity = 0.0f

    @Volatile
    var latestAcceleration = 0f
    @Volatile
    var latestRotation = 0f

    private val setRefAcc = AtomicBoolean(false)
    private val setRefRot = AtomicBoolean(false)
    fun reset(){ setRefAcc.set(true); setRefRot.set(true)}

    private val accelerationSensorEvents = ArrayBlockingQueue<MoveGame.SensorEvent>(100)
    private val rotationSensorEvents = ArrayBlockingQueue<MoveGame.SensorEvent>(100)
    private var referenceAccelerationSensorEvent = MoveGame.SensorEvent(FloatArray(3), 0L)
    private var referenceRotationSensorEvent = MoveGame.SensorEvent(FloatArray(3), 0L)
    private var tickTimeStamp: Long = SystemClock.elapsedRealtimeNanos()
    private val gameTickTimeInNanos: Long = 100_000_000

    private val output: LinearRampSignal = SignalManager.getInstance().addOrGetSignal("freezeOutput", ::LinearRampSignal)
    init {
        output.time().set(0.01)
    }
    fun getOutputPort(): UnitOutputPort {
        return output.firstOutputPort()
    }
    fun provideAccelerationSensorEvent(se : MoveGame.SensorEvent) {
        if (referenceAccelerationSensorEvent.nanoTimeStamp != 0L){
            accelerationSensorEvents.offer(se)
        } else {
            referenceAccelerationSensorEvent = se
        }
    }
     fun provideRotationSensorEvent(se : MoveGame.SensorEvent) {
        if (referenceRotationSensorEvent.nanoTimeStamp != 0L){
            rotationSensorEvents.offer(se)
        } else {
            referenceRotationSensorEvent = se
        }
    }
    private fun updateAcceleration() {
        val se: MoveGame.SensorEvent = accelerationSensorEvents.take()
        if (setRefAcc.get()){
            referenceAccelerationSensorEvent = se
            setRefAcc.set(false)
        }
        val acc = calcMovementDiff(referenceAccelerationSensorEvent, se) * accelerationFactor
        latestAcceleration = acc
        updateTick()
    }
    private fun updateRotation() {
        val se: MoveGame.SensorEvent = rotationSensorEvents.take()
        if (setRefRot.get()){
            referenceRotationSensorEvent = se
            setRefRot.set(false)
        }
        val rot = calcMovementDiff(referenceRotationSensorEvent, se) * rotationFactor
        latestRotation = rot
        updateTick()
    }
    var updateTickListener: (()->Unit)? = null
    private fun updateTick() {
        intensity = min(intensityMaximum, latestRotation + latestAcceleration)
        output.input().set(intensity.toDouble())
        updateTickListener?.invoke()
    }
    private fun calcMovementDiff(s1: MoveGame.SensorEvent, s2: MoveGame.SensorEvent): Float {
        val vecX = s2.values[0] - s1.values[0]
        val vecY = s2.values[1] - s1.values[1]
        val vecZ = s2.values[2] - s1.values[2]
        return vecX * vecX + vecY * vecY + vecZ * vecZ
    }

    private val running = AtomicBoolean(true)
    inner class AccelerationGameThread: Thread (){
        override fun run() {
            while (running.get()){
                updateAcceleration()
            }
        }
    }
    inner class RotationGameThread: Thread (){
        override fun run() {
            while (running.get()){
                updateRotation()
            }
        }
    }
    private var agt : AccelerationGameThread? = null
    private var rgt : RotationGameThread? = null

    fun start() {
        agt = AccelerationGameThread()
        agt!!.start()
        rgt = RotationGameThread()
        rgt!!.start()
    }

    fun stop() {
        running.set(false)
        accelerationSensorEvents.offer(referenceAccelerationSensorEvent)
        rotationSensorEvents.offer(referenceRotationSensorEvent)
        output.input().set(0.0)
    }
}