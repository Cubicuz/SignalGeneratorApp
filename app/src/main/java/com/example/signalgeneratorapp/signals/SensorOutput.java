package com.example.signalgeneratorapp.signals;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ProgressBar;
import com.example.signalgeneratorapp.util;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.LinearRamp;
import org.jetbrains.annotations.NotNull;


import java.util.*;

import static com.example.signalgeneratorapp.util.SensorDimensions;

public class SensorOutput{
    private final Sensor sensor;
    private final int dimensions;
    public String name;

    private LinkedList<Set<UnitInputPort>> connectedPorts = new LinkedList<>();

    public SensorOutput(@NotNull Sensor sensor) {
        name = sensor.getName();
        this.sensor = sensor;
        dimensions = SensorDimensions.getOrDefault(sensor.getType(), 1);

        for (int i = 0; i< dimensions; i++) {
            connectedPorts.add(new HashSet<>());
        }
    }

    public void connect(UnitInputPort unitInputPort, int dimension){
        if (dimension >= dimensions){
            throw new RuntimeException("illegal dimension " + dimension + " on sensor " + sensor.getName());
        }
        connectedPorts.get(dimension).add(unitInputPort);
    }

    public void disconnect(UnitInputPort unitInputPort, int dimension){
        if (dimension >= dimensions){
            throw new RuntimeException("illegal dimension " + dimension + " on sensor " + sensor.getName());
        }
        connectedPorts.get(dimension).remove(unitInputPort);
    }


    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != sensor.getType()){
                return;
            }
            for (int i = 0; i<dimensions; i++){
                int finalI = i;
                connectedPorts.get(i).forEach(unitInputPort -> {convertAndSetSensorToPortValue(event.values[finalI], unitInputPort);}
                );
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        private double convertAndSetSensorToPortValue(double value, UnitInputPort port){
            boolean negative = util.SensorsWithNegativeRange.contains(sensor.getType());

            double maxPort = port.getMaximum();
            double minPort = port.getMinimum();

            double maxSensor = sensor.getMaximumRange();
            double minSensor = negative ? -maxSensor : 0;

            double rangePort = maxPort - minPort;
            double rangeSensor = negative ? 2*maxSensor : maxSensor;

            double output = (value - minSensor) / rangeSensor * rangePort + minPort;
            port.set(output);
            return output;
        }


    };
    public void RegisterListener(Context context){
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void UnregisterListener(Context context){
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(sensorEventListener);
    }


}
