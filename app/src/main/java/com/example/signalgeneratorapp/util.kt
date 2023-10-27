package com.example.signalgeneratorapp

import android.hardware.Sensor

object util {
    @JvmField
    val SensorsWithNegativeRange: Set<Int> = setOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_LINEAR_ACCELERATION,
            Sensor.TYPE_ROTATION_VECTOR
    )

    @JvmField
    val SensorDimensions = mapOf( // Motion Sensors
        Sensor.TYPE_ACCELEROMETER to 3,
        Sensor.TYPE_GRAVITY to 3,
        Sensor.TYPE_GYROSCOPE to 3,
        Sensor.TYPE_LINEAR_ACCELERATION to 3,
        Sensor.TYPE_ROTATION_VECTOR to 4,
        Sensor.TYPE_STEP_COUNTER to 1,  // Position Sensors
        Sensor.TYPE_GAME_ROTATION_VECTOR to 3,
        Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR to 3,
        Sensor.TYPE_MAGNETIC_FIELD to 3,
        Sensor.TYPE_PROXIMITY to 1,  // Environment Sensor
        Sensor.TYPE_AMBIENT_TEMPERATURE to 1,
        Sensor.TYPE_LIGHT to 1,
        Sensor.TYPE_PRESSURE to 1,
        Sensor.TYPE_RELATIVE_HUMIDITY to 1
    )
    const val INTENT_SIGNAL_NAME = "intent_signal_name"
}
