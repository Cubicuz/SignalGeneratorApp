package com.example.signalgeneratorapp.signals;


import com.jsyn.Synthesizer;

// This class has one input, one output and a range. If the input is inside the range, output is 1, otherwise 0
public class Compare extends Signal{
    private final com.jsyn.unitgen.Compare compare;
    public Compare(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        this.synthesizer.add(compare = new com.jsyn.unitgen.Compare());
        inputs.add(compare.inputA);
        inputs.add(compare.inputB);
        outputs.add(compare.output);
    }

    @Override
    public String getType() {
        return type;
    }
    public static final String type = "add";

    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(compare);
        super.finalize();
    }
}
