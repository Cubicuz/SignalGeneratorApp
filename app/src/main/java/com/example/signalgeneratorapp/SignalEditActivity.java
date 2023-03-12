package com.example.signalgeneratorapp;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignalEditActivity extends Activity {
    private SensorManager sensorManager;
    private ArrayAdapter<String> arrayAdapter;
    private Spinner sensorSpinner;
    private Map<String, Sensor> nameToSensor = new HashMap<>();
    private Sensor selectedSensor;

    private TextView textSensorValueX, textSensorValueY, textSensorValueZ;
    private ProgressBar progressSensorValueX, progressSensorValueY, progressSensorValueZ;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (selectedSensor.getType() != event.sensor.getType()){
                return;
            }
            float max = event.sensor.getMaximumRange();
            textSensorValueX.setText(String.format("%.02f", event.values[0]));
            setSensorValueOnProgressBar(event, progressSensorValueX, 0);
            if (event.values.length > 1) {
                textSensorValueY.setText(String.format("%.02f", event.values[1]));
                setSensorValueOnProgressBar(event, progressSensorValueY, 1);
            } else {
                textSensorValueY.setText("-.--");
                progressSensorValueY.setProgress(0);
            }
            if (event.values.length > 2) {
                textSensorValueZ.setText(String.format("%.02f", event.values[2]));
                setSensorValueOnProgressBar(event, progressSensorValueZ, 2);
            } else {
                textSensorValueZ.setText("-.--");
                progressSensorValueZ.setProgress(0);
            }

        }

        private void setSensorValueOnProgressBar(SensorEvent event, ProgressBar progressBar, int valueIndex){

            int maxProgress = progressBar.getMax();
            float maxSensor = event.sensor.getMaximumRange();
            float value = event.values[valueIndex];
            boolean negative = util.SensorsWithNegativeRange.contains(event.sensor.getType());
            int progressValue;
            if (!negative){
                progressValue = (int)(value * maxProgress / maxSensor);
            } else {
                progressValue = (int)((value + maxSensor) * maxProgress / (2*maxSensor) );
            }
            progressBar.setProgress(progressValue);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signal_edit_layout);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorSpinner = findViewById(R.id.signalEditSensorSpinner);
        textSensorValueX = findViewById(R.id.textViewSensorValueX);
        textSensorValueY = findViewById(R.id.textViewSensorValueY);
        textSensorValueZ = findViewById(R.id.textViewSensorValueZ);
        progressSensorValueX = findViewById(R.id.progressBarSensorValueX);
        progressSensorValueY = findViewById(R.id.progressBarSensorValueY);
        progressSensorValueZ = findViewById(R.id.progressBarSensorValueZ);

        // list all the sensors present in the device
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        for (Sensor sensor: deviceSensors) {
            if (sensor.getName() != null){
                arrayAdapter.add(sensor.getName());
                nameToSensor.put(sensor.getName(), sensor);
            }
        }
        sensorSpinner.setAdapter(arrayAdapter);
        sensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (sensorEventListener != null) {
                    sensorManager.unregisterListener(sensorEventListener);
                }
                selectedSensor = nameToSensor.get(parent.getItemAtPosition(position));
                sensorManager.registerListener(sensorEventListener, selectedSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorEventListener != null){
            sensorManager.registerListener(sensorEventListener, selectedSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorEventListener != null){
            sensorManager.unregisterListener(sensorEventListener);
        }
    }
}
