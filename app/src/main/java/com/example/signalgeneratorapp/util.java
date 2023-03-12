package com.example.signalgeneratorapp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static android.hardware.Sensor.*;

public class util {
    public static Set<Integer> SensorsWithNegativeRange = new TreeSet<Integer>(Arrays.asList(TYPE_ACCELEROMETER,
            TYPE_GRAVITY,
            TYPE_GEOMAGNETIC_ROTATION_VECTOR,
            TYPE_GYROSCOPE,
            TYPE_MAGNETIC_FIELD
    ));
}
