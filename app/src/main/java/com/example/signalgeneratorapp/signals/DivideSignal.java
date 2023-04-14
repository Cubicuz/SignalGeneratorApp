package com.example.signalgeneratorapp.signals;

import com.jsyn.Synthesizer;
import com.jsyn.unitgen.Divide;

public class DivideSignal extends Signal{
    private final com.jsyn.unitgen.Divide divide;

    public DivideSignal(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        this.synthesizer.add(divide = new Divide());
        inputs.add(divide.inputA);
        inputs.add(divide.inputB);
        outputs.add(divide.output);
    }

    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(divide);
        super.finalize();
    }
}
