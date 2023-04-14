package com.example.signalgeneratorapp.signals;

import com.jsyn.Synthesizer;
import com.jsyn.unitgen.Maximum;

public class MaximumSignal extends Signal{
    private final com.jsyn.unitgen.Maximum maximum;
    public MaximumSignal(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        this.synthesizer.add(maximum = new Maximum());
        inputs.add(maximum.inputA);
        inputs.add(maximum.inputB);
        outputs.add(maximum.output);
    }

    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(maximum);
        super.finalize();
    }
}
