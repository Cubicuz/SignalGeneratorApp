package com.example.signalgeneratorapp.SensorEdit;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.SensorOutputManager;
import com.example.signalgeneratorapp.signals.SensorOutput;
import com.example.signalgeneratorapp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SensorEditActivity extends Activity {
    private SensorManager sensorManager;
    private ArrayAdapter<String> arrayAdapter;
    private Spinner sensorSpinner;
    private final Map<String, Sensor> nameToSensor = new HashMap<>();
    private Sensor selectedSensor;


    private Button buttonXMin, buttonXMax, buttonYMin, buttonYMax, buttonZMin, buttonZMax;
    private Button buttonXMinReset, buttonXMaxReset, buttonYMinReset, buttonYMaxReset, buttonZMinReset, buttonZMaxReset;
    private EditText editTextXMin, editTextXMax, editTextYMin, editTextYMax, editTextZMin, editTextZMax;

    private TextView textSensorValueX, textSensorValueY, textSensorValueZ;
    private ProgressBar progressSensorValueX, progressSensorValueY, progressSensorValueZ;
    private float[] lastSensorValues = new float[3];
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (selectedSensor.getType() != event.sensor.getType()){
                return;
            }
            float max = event.sensor.getMaximumRange();
            textSensorValueX.setText(String.format("%.02f", event.values[0]));
            setSensorValueOnProgressBar(event, progressSensorValueX, 0);
            lastSensorValues[0] = event.values[0];
            if (event.values.length > 1) {
                textSensorValueY.setText(String.format("%.02f", event.values[1]));
                setSensorValueOnProgressBar(event, progressSensorValueY, 1);
                lastSensorValues[1] = event.values[1];
            } else {
                textSensorValueY.setText("-.--");
                progressSensorValueY.setProgress(0);
            }
            if (event.values.length > 2) {
                textSensorValueZ.setText(String.format("%.02f", event.values[2]));
                setSensorValueOnProgressBar(event, progressSensorValueZ, 2);
                lastSensorValues[2] = event.values[2];
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
        setContentView(R.layout.sensor_edit_layout);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorSpinner = findViewById(R.id.sensorEditSensorSpinner);
        textSensorValueX = findViewById(R.id.textViewSensorValueX);
        textSensorValueY = findViewById(R.id.textViewSensorValueY);
        textSensorValueZ = findViewById(R.id.textViewSensorValueZ);
        progressSensorValueX = findViewById(R.id.progressBarSensorValueX);
        progressSensorValueY = findViewById(R.id.progressBarSensorValueY);
        progressSensorValueZ = findViewById(R.id.progressBarSensorValueZ);

        buttonXMin = findViewById(R.id.buttonXMin);
        buttonXMax = findViewById(R.id.buttonXMax);
        buttonYMin= findViewById(R.id.buttonYMin);
        buttonYMax= findViewById(R.id.buttonYMax);
        buttonZMin= findViewById(R.id.buttonZMin);
        buttonZMax= findViewById(R.id.buttonZMax);
        buttonXMinReset= findViewById(R.id.buttonSensorEditResetXMin);
        buttonXMaxReset= findViewById(R.id.buttonSensorEditResetXMax);
        buttonYMinReset= findViewById(R.id.buttonSensorEditResetYMin);
        buttonYMaxReset= findViewById(R.id.buttonSensorEditResetYMax);
        buttonZMinReset= findViewById(R.id.buttonSensorEditResetZMin);
        buttonZMaxReset= findViewById(R.id.buttonSensorEditResetZMax);
        editTextXMin = findViewById(R.id.editTextNumberSignedXMin);
        editTextXMax = findViewById(R.id.editTextNumberSignedXMax);
        editTextYMin = findViewById(R.id.editTextNumberSignedYMin);
        editTextYMax = findViewById(R.id.editTextNumberSignedYMax);
        editTextZMin = findViewById(R.id.editTextNumberSignedZMin);
        editTextZMax = findViewById(R.id.editTextNumberSignedZMax);
        addHandler(buttonXMin, buttonXMax, buttonXMinReset, buttonXMaxReset, editTextXMin, editTextXMax, 0);
        addHandler(buttonYMin, buttonYMax, buttonYMinReset, buttonYMaxReset, editTextYMin, editTextYMax, 1);
        addHandler(buttonZMin, buttonZMax, buttonZMinReset, buttonZMaxReset, editTextZMin, editTextZMax, 2);

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
                updateDimensionalAvailability();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void addHandler(Button min, Button max, Button resetMin, Button resetMax, EditText textmin, EditText textmax, int dimension){
        min.setOnClickListener(v -> {
            SensorOutput so = SensorOutputManager.getInstance().getSensorOutput(selectedSensor);
            double usermin = lastSensorValues[dimension];
            so.getSensorOutputDimension(dimension).usermin = usermin;
            textmin.setText(String.format(Locale.ENGLISH, "%.02f", usermin));
        });
        resetMin.setOnClickListener(v -> {
            SensorOutput so = SensorOutputManager.getInstance().getSensorOutput(selectedSensor);
            so.getSensorOutputDimension(dimension).resetUserMin();
            textmin.setText(String.format(Locale.ENGLISH, "%.02f", so.getSensorOutputDimension(dimension).usermin));
        });
        textmin.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                SensorOutput so = SensorOutputManager.getInstance().getSensorOutput(selectedSensor);
                try {
                    Double val = Double.parseDouble(textmin.getText().toString());
                    so.getSensorOutputDimension(dimension).usermin = val;
                } catch (NumberFormatException e){
                    textmin.setText(String.format(Locale.ENGLISH, "%.02f", so.getSensorOutputDimension(dimension).usermin));
                }
            }
        });
        max.setOnClickListener(v -> {
            SensorOutput so = SensorOutputManager.getInstance().getSensorOutput(selectedSensor);
            double usermax = lastSensorValues[dimension];
            so.getSensorOutputDimension(dimension).usermax = usermax;
            textmax.setText(String.format(Locale.ENGLISH, "%.02f", usermax));
        });
        resetMax.setOnClickListener(v -> {
            SensorOutput so = SensorOutputManager.getInstance().getSensorOutput(selectedSensor);
            so.getSensorOutputDimension(dimension).resetUserMax();
            textmax.setText(String.format(Locale.ENGLISH, "%.02f", so.getSensorOutputDimension(dimension).usermax));
        });
        textmax.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                SensorOutput so = SensorOutputManager.getInstance().getSensorOutput(selectedSensor);
                try {
                    Double val = Double.parseDouble(textmax.getText().toString());
                    so.getSensorOutputDimension(dimension).usermax = val;
                } catch (NumberFormatException e){
                    textmax.setText(String.format(Locale.ENGLISH, "%.02f", so.getSensorOutputDimension(dimension).usermax));
                }
            }
        });

    }

    private void updateDimensionalAvailability(){
        // make y and z unclickable for onedimensional sensors
        int dimensions = util.SensorDimensions.get(selectedSensor.getType());
        SensorOutput so = SensorOutputManager.getInstance().getSensorOutput(selectedSensor);

        editTextXMin.setText(String.format(Locale.ENGLISH, "%.02f", so.getSensorOutputDimension(0).usermin));
        editTextXMax.setText(String.format(Locale.ENGLISH, "%.02f", so.getSensorOutputDimension(0).usermax));

        if (dimensions > 1){
            editTextYMin.setText(String.format(Locale.ENGLISH, "%.02f", so.getSensorOutputDimension(1).usermin));
            editTextYMax.setText(String.format(Locale.ENGLISH, "%.02f", so.getSensorOutputDimension(1).usermax));
        } else {
            editTextYMin.setText("--.-");
            editTextYMax.setText("--.-");
        }
        buttonYMin.setEnabled(dimensions > 1);
        buttonYMax.setEnabled(dimensions > 1);
        buttonYMinReset.setEnabled(dimensions > 1);
        buttonYMaxReset.setEnabled(dimensions > 1);

        if (dimensions > 2){
            editTextZMin.setText(String.format(Locale.ENGLISH, "%.02f", so.getSensorOutputDimension(2).usermin));
            editTextZMax.setText(String.format(Locale.ENGLISH, "%.02f", so.getSensorOutputDimension(2).usermax));
        } else {
            editTextZMin.setText("--.-");
            editTextZMax.setText("--.-");
        }
        buttonZMin.setEnabled(dimensions > 2);
        buttonZMax.setEnabled(dimensions > 2);
        buttonZMinReset.setEnabled(dimensions > 2);
        buttonZMaxReset.setEnabled(dimensions > 2);

    }
}
