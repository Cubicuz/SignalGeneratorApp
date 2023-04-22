package com.example.signalgeneratorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import com.example.signalgeneratorapp.signals.SensorOutput;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SensorOutputManager {
    private final LinkedList<SensorOutput> sensorOutputs = new LinkedList<>();
    private final HashMap<Integer, SensorOutput> sensorToSensorOutput = new HashMap<>();
    private Context context;
    public LinkedList<SensorOutput> getSensorOutputList(){return sensorOutputs;}
    public SensorOutput getSensorOutput(Sensor sensor){ return sensorToSensorOutput.get(sensor.getType()); }
    public void start(){
        sensorOutputs.forEach(sensorOutput -> {
            if (sensorOutput.isSensorInUse()) {
                sensorOutput.RegisterListener();
            }
        });
    }
    public void stop(){
        sensorOutputs.forEach(sensorOutput -> {
            if (sensorOutput.isSensorInUse()){
                sensorOutput.UnregisterListener();
            }
        });
    }

    private SensorOutputManager(Context context){
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        deviceSensors.forEach(sensor -> {
            SensorOutput so = new SensorOutput(sensor, context);
            sensorOutputs.add(so);
            sensorToSensorOutput.put(sensor.getType(), so);
        });

    }
    private static SensorOutputManager instance;
    public static SensorOutputManager getInstance(){
        return instance;
    }
    public static SensorOutputManager createInstance(Context context){
        if (instance != null){
            return instance;
        }
        return instance = new SensorOutputManager(context);
    }
}
