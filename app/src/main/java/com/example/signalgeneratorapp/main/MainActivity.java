package com.example.signalgeneratorapp.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.signalgeneratorapp.*;
import com.example.signalgeneratorapp.SignalEdit.SignalEditActivity;

import java.util.List;

public class MainActivity extends Activity {
    private SineSynth mSineSynth;
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private Spinner sensorSpinner;
    private SensorInput sensorInput;
    private ArrayAdapter<String> arrayAdapter;
    private RecyclerView listViewMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        mSineSynth = new SineSynth();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorSpinner = findViewById(R.id.spinnerSensorSelect);
        listViewMain = findViewById(R.id.linearLayoutMain);

        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        sensorInput = new SensorInput(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, mSineSynth.getLeftFrequencyPort(), mSineSynth.getRightFrequencyPort(), findViewById(R.id.textViewX), findViewById(R.id.textViewY), findViewById(R.id.textViewZ));

        // list all the sensors present in the device
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        for (Sensor sensor: deviceSensors) {
            if (sensor.getName() != null){
                arrayAdapter.add(sensor.getName());
            }
        }
        sensorSpinner.setAdapter(arrayAdapter);

        SensorSignalAdapter ssa = new SensorSignalAdapter();
        ssa.setOnClickListener((position, signal) -> {
            Intent intent = new Intent(MainActivity.this, SignalEditActivity.class);
            // Passing the data to the SignalEditActivity
            intent.putExtra(util.INTENT_SIGNAL_NAME, signal.name);
            startActivity(intent);
        });

        listViewMain.setLayoutManager(new LinearLayoutManager(this));
        listViewMain.setAdapter(ssa);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSineSynth.start();
        sensorManager.registerListener(sensorInput, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSineSynth.stop();
        sensorManager.unregisterListener(sensorInput);
    }
}