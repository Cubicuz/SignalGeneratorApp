package com.example.signalgeneratorapp;

import com.jsyn.unitgen.SineOscillator;

import java.util.*;




public class SignalManager {
    public class SignalSensorConnection {
        // TODO fill with code and think about a ConnectionManager
        public double convertSensorValueToSignalValue(double sensorValue){
            // if lower cutoff
            // if higher cutoff
            // factor
            // shifting?
            return 0.0;
        }
    }
    private Map<String, Signal> signals = new HashMap<>();

    public Collection<Signal> getSignalList(){
        return signals.values();
    }

    private SignalSensorConnection conn;

    public Signal addSignal(String name){
        if (signals.containsKey(name)){
            return signals.get(name);
        }
        signals.put(name, new Signal(name, new SineOscillator()));
        return signals.get(name);
    }

    public void changeSignalName(String from, String to) {
        if (signals.containsKey(to)){
            // to already exists
        } else if (!signals.containsKey(from)) {
            // from does not exist
        } else {
            Signal sig = signals.remove(from);
            sig.name = to;
            signals.put(to, sig);
            // call event or so
        }
    }
}
