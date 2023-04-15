package com.example.signalgeneratorapp.signals;

import com.jsyn.Synthesizer;
import com.jsyn.unitgen.Add;

public class AddSignal extends Signal{
    private final com.jsyn.unitgen.Add add;
    public AddSignal(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        this.synthesizer.add(add = new Add());
        inputs.add(add.inputA);
        inputs.add(add.inputB);
        outputs.add(add.output);
    }

    @Override
    public String getType() {
        return type;
    }
    public static final String type = "add";

    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(add);
        super.finalize();
    }
}
