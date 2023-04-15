package com.example.signalgeneratorapp.signals;

import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.SchmidtTrigger;

public class SchmidtTriggerSignal extends Signal{

    private final SchmidtTrigger schmidtTrigger;
    public SchmidtTriggerSignal(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        synthesizer.add(schmidtTrigger = new SchmidtTrigger());
        inputs.add(schmidtTrigger.input);
        inputs.add(schmidtTrigger.setLevel);
        inputs.add(schmidtTrigger.resetLevel);
        outputs.add(schmidtTrigger.output);
    }
    @Override
    public String getType() {
        return type;
    }
    public static final String type = "schmidtTrigger";
    public UnitInputPort input(){
        return schmidtTrigger.input;
    }
    public UnitInputPort setLevel(){
        return schmidtTrigger.setLevel;
    }
    public UnitInputPort resetLevel(){
        return schmidtTrigger.resetLevel;
    }

    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(schmidtTrigger);
        super.finalize();
    }
}
