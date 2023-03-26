package com.example.signalgeneratorapp.signals;

import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.SineOscillator;

import java.util.LinkedList;

public class SineSignal extends Signal {
    private final SineOscillator oscillator;

    public SineSignal (String name, Synthesizer synthesizer){
        super(name, synthesizer);
        this.synthesizer.add(oscillator = new SineOscillator());
        inputs.add(oscillator.amplitude);
        inputs.add(oscillator.frequency);
        outputs.add(oscillator.output);
    }

    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(oscillator);
        super.finalize();
    }
}
