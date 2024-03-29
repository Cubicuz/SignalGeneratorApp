package com.example.signalgeneratorapp.signals;

import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.SineOscillator;

import java.util.LinkedList;

public class SineSignal extends SignalWithAmplitude{
    private final SineOscillator oscillator;

    public SineSignal (String name, Synthesizer synthesizer){
        super(name, synthesizer);
        synthesizer.add(oscillator = new SineOscillator());
        inputs.add(oscillator.amplitude);
        inputs.add(oscillator.frequency);
        outputs.add(oscillator.output);
        oscillator.frequency.setMinimum(0.1);
        oscillator.frequency.setMaximum(24000);
    }
    @Override
    public String getType() {
        return type;
    }
    public static final String type = "sine";
    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(oscillator);
        super.finalize();
    }

    public UnitInputPort frequency() {
        return oscillator.frequency;
    }

    public UnitInputPort amplitude() {
        return oscillator.amplitude;
    }
}
