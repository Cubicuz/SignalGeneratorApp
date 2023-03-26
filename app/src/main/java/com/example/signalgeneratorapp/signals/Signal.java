package com.example.signalgeneratorapp.signals;

import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.SineOscillator;

import java.util.LinkedList;

public abstract class Signal {
    public String name;
    protected final LinkedList<UnitInputPort> inputs;
    protected final LinkedList<UnitOutputPort> outputs;
    protected final Synthesizer synthesizer;
    public Signal(String name, Synthesizer synthesizer) {
        this.name = name;
        inputs = new LinkedList<>();
        outputs = new LinkedList<>();
        this.synthesizer = synthesizer;
    }

    public LinkedList<UnitInputPort> inputsPorts(){
        return inputs;
    }
    public LinkedList<UnitOutputPort> outputsPorts(){
        return outputs;
    }


}
