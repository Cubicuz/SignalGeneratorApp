package com.example.signalgeneratorapp;

import com.example.signalgeneratorapp.signals.SensorOutput;
import com.example.signalgeneratorapp.signals.Signal;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ConnectionManager {


    // Any output can be connected to many inputs.
    private HashMap<UnitOutputPort, LinkedList<UnitInputPort>> outputToInputs = new HashMap<>();
    // Any input can be connected to one output
    private HashMap<UnitInputPort, UnitOutputPort> inputToOutput = new HashMap<>();

    public void connect(UnitInputPort uip, UnitOutputPort uop){
        connect(uip, uop, true);
    }
    private void connect(UnitInputPort uip, UnitOutputPort uop, boolean store){
        if (inputToOutput.containsKey(uip)){
            // a connection for this input exists already
            if (uop == inputToOutput.get(uip)){
                // this connection is already made
                return;
            }
            // remove the existing connection
            disconnect(uip);
        }

        // add new connection
        uop.connect(uip);
        inputToOutput.put(uip, uop);
        if (outputToInputs.containsKey(uop)){
            outputToInputs.get(uop).add(uip);
        } else {
            outputToInputs.put(uop, new LinkedList<>(List.of(uip)));
        }
        if (store){
            storeConnection(uip, uop);
        }
    }


    public void disconnect(UnitInputPort uip){
        if (inputToOutput.containsKey(uip)){
            // a connection for this input exists
            UnitOutputPort uop = inputToOutput.get(uip);

            // remove the existing connection
            uop.disconnect(uip);
            outputToInputs.get(uop).remove(uip);
            if (outputToInputs.get(uop).isEmpty()){
                outputToInputs.remove(uop);
            }
            inputToOutput.remove(uip);
        }
        deleteConnection(uip);
    }

    public void disconnect(UnitOutputPort uop){
        if (outputToInputs.containsKey(uop)){
            // make copy of inputlist
            LinkedList<UnitInputPort> inputs = new LinkedList<>(outputToInputs.get(uop));
            inputs.forEach(unitInputPort -> disconnect(unitInputPort));
        }
    }

    public void disconnect(Signal signal){
        signal.inputsPorts().forEach(unitInputPort -> disconnect(unitInputPort));
        signal.outputsPorts().forEach(unitOutputPort -> disconnect(unitOutputPort));
        disconnectLineout(signal.firstOutputPort(), 0);
        disconnectLineout(signal.firstOutputPort(), 1);
    }

    public UnitOutputPort getConnected(UnitInputPort uip){
        return inputToOutput.get(uip);
    }

    // lineout interface
    private LinkedList<HashSet<UnitOutputPort>> lineoutConnections = new LinkedList<>();
    public void connectLineout(UnitOutputPort uop, int leftRight){
        connectLineout(uop, leftRight, true);
    }
    private void connectLineout(UnitOutputPort uop, int leftRight, boolean store){
        if (lineoutConnections.get(leftRight).contains(uop)){
            // connection already there
            return;
        }
        uop.connect(0, SignalManager.getInstance().lineout(), leftRight);
        lineoutConnections.get(leftRight).add(uop);
        if (store){
            storeLineoutConnections();
        }
    }

    public boolean isLineoutConnected(UnitOutputPort uop, int leftRight){
        return lineoutConnections.get(leftRight).contains(uop);
    }

    public void disconnectLineout(UnitOutputPort uop, int leftRight){
        if (lineoutConnections.get(leftRight).contains(uop)){
            uop.disconnect(0, SignalManager.getInstance().lineout(), leftRight);
            lineoutConnections.get(leftRight).remove(uop);
            storeLineoutConnections();
        }
    }

    private void loadLineoutConnection(){
        String keyLeft = App.getContext().getString(R.string.storage_key_lineout_connection_prefix) + ".left";
        String keyRight = App.getContext().getString(R.string.storage_key_lineout_connection_prefix) + ".right";
        if (StorageManager.getInstance().contains(keyLeft)){
            String leftValue = StorageManager.getInstance().load(keyLeft);
            if (leftValue != ""){
                String[] leftValues = leftValue.split("\\.");
                for (int i=0;i<leftValues.length;i+=2){
                    Signal signal = SignalManager.getInstance().getSignal(leftValues[i]);
                    UnitOutputPort uop = signal.firstOutputPort();
                    connectLineout(uop, 0, false);
                }
            }
        }
        if (StorageManager.getInstance().contains(keyRight)){
            String rightValue = StorageManager.getInstance().load(keyRight);
            if (rightValue != ""){
                String[] rightValues = rightValue.split("\\.");
                for (int i=0;i<rightValues.length;i+=2){
                    Signal signal = SignalManager.getInstance().getSignal(rightValues[i]);
                    UnitOutputPort uop = signal.firstOutputPort();
                    connectLineout(uop, 1, false);
                }
            }
        }

    }

    private void storeLineoutConnections(){
        String keyLeft = App.getContext().getString(R.string.storage_key_lineout_connection_prefix) + ".left";
        String keyRight = App.getContext().getString(R.string.storage_key_lineout_connection_prefix) + ".right";
        StringBuilder valueLeft = new StringBuilder();
        StringBuilder valueRight = new StringBuilder();
        for (UnitOutputPort unitOutputPort : lineoutConnections.get(0)) {
            Signal signal = SignalManager.getInstance().getSignal(unitOutputPort);
            valueLeft.append(signal.name).append(".").append(unitOutputPort.getName()).append(".");
        }
        for (UnitOutputPort unitOutputPort : lineoutConnections.get(1)) {
            Signal signal = SignalManager.getInstance().getSignal(unitOutputPort);
            valueRight.append(signal.name).append(".").append(unitOutputPort.getName()).append(".");
        }
        StorageManager.getInstance().store(keyLeft, valueLeft.toString());
        StorageManager.getInstance().store(keyRight, valueRight.toString());
    }

    private void loadFromPreferences(String key) {
        if (!StorageManager.getInstance().contains(key)){ return; }

        String[] inValues = key.split("\\.");
        if (inValues.length != 3){
            int i=0;
        }
        String inSignalName = inValues[1];
        String inInputName = inValues[2];
        AtomicReference<UnitInputPort> uip = new AtomicReference<>();
        Signal inSignal = SignalManager.getInstance().getSignal(inSignalName);
        inSignal.inputsPorts().forEach(unitInputPort -> {
            if (unitInputPort.getName().equals(inInputName)){
                uip.set(unitInputPort);
            };
        });
        if (uip.get() == null){
            throw new RuntimeException("UnitInputPort name does not exist! " + inInputName + " key: " + key);
        }

        String[] outValues = StorageManager.getInstance().load(key).split("\\.");
        AtomicReference<UnitOutputPort> uop = new AtomicReference<>();
        if (Objects.equals(outValues[0], "signal")){
            String outSignalName = outValues[1];
            String unitOutputPortName = outValues[2];
            Signal sig = SignalManager.getInstance().getSignal(outSignalName);
            sig.outputsPorts().forEach(unitOutputPort -> {
                if (Objects.equals(unitOutputPort.getName(), unitOutputPortName)){
                    uop.set(unitOutputPort);
                }
            });
            if (uop.get() == null){
                throw new RuntimeException("UnitOutputPort name does not exist! " + outSignalName + "." + unitOutputPortName);
            }

        } else if (outValues[0].equals("sensor")) {
            int sensorType = Integer.parseInt(outValues[1]);
            int outIndex = Integer.parseInt(outValues[2]);
            uop.set(SensorOutputManager.getInstance().getSensorOutput(sensorType).getSensorOutputDimension(outIndex));
        } else {
            throw new RuntimeException("Connection entry is neither from a signal nor from a sensor! " + outValues[0]);
        }
        connect(uip.get(), uop.get(), false);
    }
    private LinkedList<String> generatePossibleStorageKeys(){
        LinkedList<String> keys = new LinkedList<>();
        String prefix = App.getContext().getString(R.string.storage_key_connection_prefix);
        SignalManager.getInstance().getSignalList().forEach(signal -> {
            signal.inputsPorts().forEach(unitInputPort -> {
                String key = prefix + "." + signal.name + "." + unitInputPort.getName();
                keys.add(key);
            });
        });
        return keys;
    }

    public void loadFromPreferences(){
        generatePossibleStorageKeys().forEach(this::loadFromPreferences);
        loadLineoutConnection();
    }

    private void storeConnection(UnitInputPort uip, UnitOutputPort uop){
        Signal sigOut = SignalManager.getInstance().getSignal(uop);
        SensorOutput so = SensorOutputManager.getInstance().getSensorOutput(uop);
        Signal sigIn = SignalManager.getInstance().getSignal(uip);
        String in = App.getContext().getString(R.string.storage_key_connection_prefix) + "." + sigIn.name + "." + uip.getName();
        String out;
        if (sigOut != null){
            out = "signal." + sigOut.name + "." + uop.getName();
        } else if (so != null) {
            out = "sensor." + so.getSensorType() + "." + ((SensorOutput.SensorOutputDimension) uop).dimension;
        } else {
            throw new RuntimeException("UnitOutputPort is neither from a signal nor from a sensor! " + uop.getName());
        }
        StorageManager.getInstance().store(in, out);
    }

    private void deleteConnection(UnitInputPort uip){
        Signal sigIn = SignalManager.getInstance().getSignal(uip);
        String in = App.getContext().getString(R.string.storage_key_connection_prefix) + "." + sigIn.name + "." + uip.getName();
        if (StorageManager.getInstance().contains(in)){
            StorageManager.getInstance().remove(in);
        }
    }

    // this has to be called before the actual change in SignalManager
    public void renameSignal(String from, String to){
        // get all connections that have to be modified
        LinkedList<UnitInputPort> connsOut = new LinkedList<>();
        LinkedList<UnitInputPort> connsIn = new LinkedList<>();
        String prefix = App.getContext().getString(R.string.storage_key_connection_prefix);
        Signal signal = SignalManager.getInstance().getSignal(from);
        signal.inputsPorts().forEach(unitInputPort -> {
            if (inputToOutput.containsKey(unitInputPort)){
                connsOut.add(unitInputPort);
            }
        });
        connsOut.forEach(unitInputPort -> {
            String oldKey = prefix + "." + from + "." + unitInputPort.getName();
            String newKey = prefix + "." + to + "." + unitInputPort.getName();
            String value = StorageManager.getInstance().load(oldKey);
            StorageManager.getInstance().remove(oldKey);
            StorageManager.getInstance().store(newKey, value);
        });
        signal.outputsPorts().forEach(unitOutputPort -> {
            if (outputToInputs.containsKey(unitOutputPort)){
                connsIn.addAll(outputToInputs.get(unitOutputPort));
            }
        });
        connsIn.forEach(unitInputPort -> {
            Signal sigIn = SignalManager.getInstance().getSignal(unitInputPort);
            String key = App.getContext().getString(R.string.storage_key_connection_prefix) + "." + sigIn.name + "." + unitInputPort.getName();
            String oldVal = StorageManager.getInstance().load(key);
            String valueNew = oldVal.replace("."+from+".", "."+to+".");
            StorageManager.getInstance().store(key, valueNew);
        });

    }


    public LinkedList<UnitInputPort> getConnected(UnitOutputPort uop){
        return outputToInputs.get(uop);
    }

    private ConnectionManager(){
        lineoutConnections.add(new HashSet<>());
        lineoutConnections.add(new HashSet<>());
    }
    private static ConnectionManager instance;
    public static ConnectionManager getInstance(){
        if(instance != null){
            return instance;
        }
        return instance = new ConnectionManager();
    }
}
