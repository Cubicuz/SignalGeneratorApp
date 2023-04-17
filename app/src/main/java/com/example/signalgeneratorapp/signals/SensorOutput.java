package com.example.signalgeneratorapp.signals;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ProgressBar;
import com.example.signalgeneratorapp.util;
import com.jsyn.Synthesizer;
import com.jsyn.ports.ConnectableInput;
import com.jsyn.ports.ConnectableOutput;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.UnitSink;
import com.softsynth.shared.time.TimeStamp;
import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;


import java.util.*;

import static com.example.signalgeneratorapp.util.SensorDimensions;

public class SensorOutput{
    private final Sensor sensor;
    private final int dimensions;
    public String name;
    private int connectedCounter = 0;
    private Context context;

    private final LinkedList<SensorOutputDimension> outputDimensions = new LinkedList<>();

    private LinkedList<Set<UnitInputPort>> connectedPorts = new LinkedList<>();

    public SensorOutput(@NotNull Sensor sensor, Context context) {
        name = sensor.getName();
        this.sensor = sensor;
        dimensions = SensorDimensions.getOrDefault(sensor.getType(), 1);

        for (int i = 0; i< dimensions; i++) {
            connectedPorts.add(new HashSet<>());
            outputDimensions.add(new SensorOutputDimension(i));
        }
        this.context = context;
    }

    public void connect(UnitInputPort unitInputPort, int dimension){
        if (dimension >= dimensions){
            throw new RuntimeException("illegal dimension " + dimension + " on sensor " + sensor.getName());
        }
        if (connectedPorts.get(dimension).add(unitInputPort)){
            connectedCounter++;
            if (connectedCounter == 1){
                RegisterListener();
            }
        }
    }

    public void disconnect(UnitInputPort unitInputPort, int dimension){
        if (dimension >= dimensions){
            throw new RuntimeException("illegal dimension " + dimension + " on sensor " + sensor.getName());
        }
        if(connectedPorts.get(dimension).remove(unitInputPort)){
            connectedCounter--;
            if (connectedCounter == 0){
                UnregisterListener();
            }
        }
    }

    public boolean isSensorInUse(){
        return connectedCounter > 0;
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
    public void RegisterListener(){
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void UnregisterListener(){
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(sensorEventListener);
    }

    public LinkedList<SensorOutputDimension> getSensorOutputDimensions(){
        return outputDimensions;
    }

    public SensorOutputDimension getSensorOutputDimension(int dimension){
        if (dimension >= dimensions){
            return null;
        }
        return outputDimensions.get(dimension);
    }

    public class SensorOutputDimension extends UnitOutputPort {
        public final int dimension;

        public double usermin=-Double.MAX_VALUE;
        public double usermax=Double.MAX_VALUE;

        @Override
        public void connect(UnitInputPort unitInputPort){
            SensorOutput.this.connect(unitInputPort, dimension);
        }
        @Override
        public void disconnect(UnitInputPort unitInputPort){
            SensorOutput.this.disconnect(unitInputPort, dimension);
        }
        public void connect(int var1, UnitInputPort var2, int var3) {throw new NotImplementedError("this wasnt planned");}
        public void connect(int var1, UnitInputPort var2, int var3, TimeStamp var4) {throw new NotImplementedError("this wasnt planned");}
        public void connect(ConnectableInput var1) {
            throw new NotImplementedError("this wasnt planned");
        }
        public void connect(UnitSink var1) {
            throw new NotImplementedError("this wasnt planned");
        }
        public void disconnect(int var1, UnitInputPort var2, int var3) {throw new NotImplementedError("this wasnt planned");}
        public void disconnect(int var1, UnitInputPort var2, int var3, TimeStamp var4) {throw new NotImplementedError("this wasnt planned");}
        public void disconnect(ConnectableInput var1) {
            throw new NotImplementedError("this wasnt planned");
        }

        protected SensorOutputDimension(int dimension){
            super(SensorOutput.this.name + " " + dimension);
            this.dimension = dimension;
        }
    }
}
