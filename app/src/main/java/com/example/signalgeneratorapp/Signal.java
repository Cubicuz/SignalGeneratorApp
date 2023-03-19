package com.example.signalgeneratorapp;

import com.jsyn.unitgen.SineOscillator;

public class Signal {
    public String name;
    public SineOscillator oscillator;

    public Signal(String name, SineOscillator sineOscillator) {
        this.name = name;
        this.oscillator = sineOscillator;
    }
}
