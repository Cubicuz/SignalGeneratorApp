package com.example.signalgeneratorapp;

import java.util.*;

import static android.hardware.Sensor.*;

public class util {
    public static final Set<Integer> SensorsWithNegativeRange = new TreeSet<Integer>(Arrays.asList(TYPE_ACCELEROMETER,
            TYPE_GRAVITY,
            TYPE_GEOMAGNETIC_ROTATION_VECTOR,
            TYPE_GYROSCOPE,
            TYPE_MAGNETIC_FIELD,
            TYPE_ACCELEROMETER,
            TYPE_LINEAR_ACCELERATION,
            TYPE_ROTATION_VECTOR
    ));

    public static final Map<Integer, Integer> SensorDimensions = Map.ofEntries(
            // Motion Sensors
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_ACCELEROMETER, 3),
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_GRAVITY, 3),
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_GYROSCOPE, 3),
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_LINEAR_ACCELERATION, 3),
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_ROTATION_VECTOR, 4),
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_STEP_COUNTER, 1),

            // Position Sensors
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_GAME_ROTATION_VECTOR, 3),
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_GEOMAGNETIC_ROTATION_VECTOR, 3),
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_MAGNETIC_FIELD, 3),
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_PROXIMITY, 1),

            // Environment Sensor
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_AMBIENT_TEMPERATURE, 1),
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_LIGHT, 1),
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_PRESSURE, 1),
            new AbstractMap.SimpleEntry<Integer, Integer>(TYPE_RELATIVE_HUMIDITY, 1)
    );

    public static final String INTENT_SIGNAL_NAME = "intent_signal_name";
}
