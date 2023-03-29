package com.example.signalgeneratorapp;

import com.example.signalgeneratorapp.signals.Signal;
import com.example.signalgeneratorapp.signals.SineSignal;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;


public final class SignalManager {

    private final Map<String, Signal> signals = new HashMap<>();

    public Collection<Signal> getSignalList(){
        return signals.values();
    }


    private final Synthesizer synthesizer;
    // The lineout is not part of the signals because it has only one UnitInputPort with 2 dimensions.
    // This can not be supported with current design
    private final LineOut lineOut; // stereo output

    public <E extends Signal> E addSignal(String name, BiFunction<String, Synthesizer, E> fn){
        if (signals.containsKey(name)){
            throw new RuntimeException("name " + name + " already exists");
        }
        E signal = fn.apply(name, synthesizer);
        signals.put(name, signal);
        return signal;
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

    public void startAudio() {
        synthesizer.start();
        lineOut.start();
    }

    public void stopAudio() {
        lineOut.stop();
        synthesizer.stop();
    }

    private SignalManager(Synthesizer synthesizer){
        this.synthesizer = synthesizer;
        synthesizer.add(lineOut = new LineOut());
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
