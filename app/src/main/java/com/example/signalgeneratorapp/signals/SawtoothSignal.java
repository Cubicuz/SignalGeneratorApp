package com.example.signalgeneratorapp.signals;

import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.SawtoothOscillator;

public class SawtoothSignal extends Signal{
    private final SawtoothOscillator sawtoothOscillator;
    public SawtoothSignal(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        synthesizer.add(sawtoothOscillator = new SawtoothOscillator());
        inputs.add(sawtoothOscillator.amplitude);
        inputs.add(sawtoothOscillator.frequency);
        outputs.add(sawtoothOscillator.output);
    }

    public UnitInputPort amplitude(){
        return sawtoothOscillator.amplitude;
    }
    public UnitInputPort frequency(){
        return sawtoothOscillator.frequency;
    }
}
