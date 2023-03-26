package com.example.signalgeneratorapp;

import com.example.signalgeneratorapp.signals.Signal;
import com.example.signalgeneratorapp.signals.SineSignal;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.SineOscillator;

import java.util.*;




public final class SignalManager {
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
    private final Map<String, Signal> signals = new HashMap<>();

    public Collection<Signal> getSignalList(){
        return signals.values();
    }

    private SignalSensorConnection conn;

    private final Synthesizer synthesizer;

    public Signal addSignal(String name){
        if (signals.containsKey(name)){
            return signals.get(name);
        }
        signals.put(name, new SineSignal(name, synthesizer));
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

    private SignalManager(Synthesizer synthesizer){
        this.synthesizer = synthesizer;
    }
    private static SignalManager instance;
    public static SignalManager getInstance() { return instance; }
    public static SignalManager createInstance(Synthesizer synthesizer) {
        if (instance != null){
            return instance;
        }
        return instance = new SignalManager(synthesizer);
    }
}
