package com.example.signalgeneratorapp.signals;

import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.LinearRamp;

public class LinearRampSignal extends Signal{
    private final com.jsyn.unitgen.LinearRamp ramp;
    public LinearRampSignal(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        this.synthesizer.add(ramp = new LinearRamp());
        inputs.add(ramp.input);
        inputs.add(ramp.time);
        outputs.add(ramp.output);
    }
    @Override
    public String getType() {
        return type;
    }
    public static final String type = "linearRamp";
    public UnitInputPort input() {
        return ramp.input;
    }

    public UnitInputPort time() {
        return ramp.time;
    }
}
