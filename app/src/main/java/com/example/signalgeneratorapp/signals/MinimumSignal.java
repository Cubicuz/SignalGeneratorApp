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
    public String getType() {
        return type;
    }
    public static final String type = "minimum";
    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(minimum);
        super.finalize();
    }
}
