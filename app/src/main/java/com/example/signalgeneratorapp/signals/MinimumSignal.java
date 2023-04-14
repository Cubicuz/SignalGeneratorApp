package com.example.signalgeneratorapp.signals;

import com.jsyn.Synthesizer;
import com.jsyn.unitgen.Minimum;

public class MinimumSignal extends Signal{
    private final com.jsyn.unitgen.Minimum minimum;

    public MinimumSignal(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        this.synthesizer.add(minimum = new Minimum());
        inputs.add(minimum.inputA);
        inputs.add(minimum.inputB);
        outputs.add(minimum.output);
    }

    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(minimum);
        super.finalize();
    }
}
