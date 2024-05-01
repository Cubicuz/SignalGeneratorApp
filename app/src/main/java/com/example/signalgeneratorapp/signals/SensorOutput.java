package com.example.signalgeneratorapp.signals;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.StorageManager;
import com.example.signalgeneratorapp.util;
import com.jsyn.ports.ConnectableInput;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitSink;
import com.softsynth.shared.time.TimeStamp;
import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.example.signalgeneratorapp.util.SensorDimensions;

/**
 * One SensorOutput exists per sensor to reduce the amount of listeners
 */
public class SensorOutput{
    private final Sensor sensor;
    private final int dimensions;
    public String name;

    private int connectedCounter = 0;
    private final Context context;

    private final LinkedList<SensorOutputDimension> outputDimensions = new LinkedList<>();

    private final LinkedList<Set<UnitInputPort>> connectedPorts = new LinkedList<>();

    public SensorOutput(@NotNull Sensor sensor, Context context) {
        name = sensor.getName();
        this.context = context;
        this.sensor = sensor;
        dimensions = SensorDimensions.getOrDefault(sensor.getType(), 1);

        for (int i = 0; i< dimensions; i++) {
            connectedPorts.add(new HashSet<>());
            outputDimensions.add(new SensorOutputDimension(i));
        }
    }
    public int getSensorType(){ return sensor.getType(); }
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
    public float getMaximumRange() { return sensor.getMaximumRange(); }
    public interface SensorEventHandler{
        void onSensorChanged(final float[] values, long nanoTimeStamp);
    }
    private final List<SensorEventHandler> sehs = Collections.synchronizedList(new ArrayList<>());
    public void connect(SensorEventHandler seh){
        if (sehs.contains(seh)){
            return;
        }
        sehs.add(seh);
        connectedCounter++;
        if (connectedCounter == 1){
            RegisterListener();
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
    public void disconnect(SensorEventHandler seh){
        if (!sehs.contains(seh)){
            return;
        }
        sehs.remove(seh);
        connectedCounter--;
        if (connectedCounter == 0){
            UnregisterListener();
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
                connectedPorts.get(i).forEach(unitInputPort -> convertAndSetSensorToPortValue(event.values[finalI], unitInputPort, getSensorOutputDimension(finalI))
                );
            }
            sehs.forEach(sensorEventHandler -> sensorEventHandler.onSensorChanged(event.values, event.timestamp));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        private double convertAndSetSensorToPortValue(double value, UnitInputPort port, SensorOutputDimension sensorOutputDimension){
            boolean negative = util.SensorsWithNegativeRange.contains(sensor.getType());

            double maxPort = port.getMaximum();
            double minPort = port.getMinimum();

            //double maxSensor = sensor.getMaximumRange();
            //double minSensor = negative ? -maxSensor : 0;
            double maxSensor = sensorOutputDimension.usermax;
            double minSensor = sensorOutputDimension.usermin;

            double rangePort = maxPort - minPort;
            double rangeSensor = negative ? 2*maxSensor : maxSensor;

            double output = (value - minSensor) / rangeSensor * rangePort + minPort;
            output = Double.max(output, minPort);
            output = Double.min(output, maxPort);
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
        // those are for storage
        private static final String userMinName = "usermin";
        private static final String userMaxName = "usermax";
        private double usermin = util.SensorsWithNegativeRange.contains(sensor.getType()) ? -sensor.getMaximumRange() : 0;
        private double usermax = sensor.getMaximumRange();

        public double getUsermin(){return usermin;}
        public double getUsermax(){return usermax;}
        public void setUsermin(double um){
            usermin = um;
            StorageManager.getInstance().store(generatePreferencesStringForVariable(userMinName), Double.toString(um));
        }
        public void setUsermax(double um){
            usermax = um;
            StorageManager.getInstance().store(generatePreferencesStringForVariable(userMaxName), Double.toString(um));
        }

        public void resetUserMin(){
            setUsermin(util.SensorsWithNegativeRange.contains(sensor.getType()) ? -sensor.getMaximumRange() : 0);
        }
        public void resetUserMax(){
            setUsermax(sensor.getMaximumRange());
        }

        private String generatePreferencesStringForVariable(String variableName){
            return context.getString(R.string.storage_key_sensor_output_prefix) + "." + dimension + "." + variableName;
        }

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
            if (StorageManager.getInstance().contains(generatePreferencesStringForVariable(userMinName))){
                usermin = Double.parseDouble(StorageManager.getInstance().load(generatePreferencesStringForVariable(userMinName)));
            }
            if (StorageManager.getInstance().contains(generatePreferencesStringForVariable(userMaxName))){
                usermax = Double.parseDouble(StorageManager.getInstance().load(generatePreferencesStringForVariable(userMaxName)));
            }
        }
    }
}
