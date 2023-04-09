package com.example.signalgeneratorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import com.jsyn.ports.UnitInputPort;
import org.w3c.dom.Text;

public class SensorInput implements SensorEventListener {
    private int sensorType;
    private TextView x, y, z;

    public SensorInput(int sensorType, TextView x, TextView y, TextView z) {
        this.sensorType = sensorType;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() != sensorType){
            return;
        }
        if (x != null){
            x.setText("" + sensorEvent.values[0]);
        }
        if (y != null){
            y.setText("" + sensorEvent.values[1]);
        }
        if (z != null){
            z.setText("" + sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
