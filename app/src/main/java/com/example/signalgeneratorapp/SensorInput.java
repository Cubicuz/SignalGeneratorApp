package com.example.signalgeneratorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import com.jsyn.ports.UnitInputPort;
import org.w3c.dom.Text;

public class SensorInput implements SensorEventListener {
    private int sensorType;
    private UnitInputPort left;
    private UnitInputPort right;
    private TextView x, y, z;

    public SensorInput(int sensorType, UnitInputPort left, UnitInputPort right, TextView x, TextView y, TextView z) {
        this.sensorType = sensorType;
        this.left = left;
        this.right = right;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() != sensorType){
            return;
        }
        left.set(convertSensorToPortValue(sensorEvent.values[0], left));
        right.set(convertSensorToPortValue(sensorEvent.values[1], right));
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

    private double convertSensorToPortValue(float value, UnitInputPort port){
        double max = port.getMaximum();
        double min = port.getMinimum();
        double range = max - min;
        return min + ((value + 1) * range / 2);
    }
}
