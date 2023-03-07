package com.example.signalgeneratorapp;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;

public class SensorSignalAdapter extends BaseAdapter {

    private ArrayAdapter<String> sensorNamesAdapter;
    public SensorSignalAdapter(ArrayAdapter<String> sensorNamesAdapter)
    {
        this.sensorNamesAdapter = sensorNamesAdapter;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.sensor_signal_layout, viewGroup, false);
        }

        Spinner sensor = view.findViewById(R.id.spinnerSensor);
        Spinner signal = view.findViewById(R.id.spinnerSignal);

        sensor.setAdapter(sensorNamesAdapter);


        return view;
    }
}
