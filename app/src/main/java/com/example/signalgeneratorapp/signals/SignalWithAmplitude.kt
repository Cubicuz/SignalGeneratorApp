package com.example.signalgeneratorapp.signals;

import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;

public abstract class SignalWithAmplitude extends Signal{
    public SignalWithAmplitude(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
    }

    public abstract UnitInputPort amplitude();
}
