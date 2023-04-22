package com.example.signalgeneratorapp.signals;

import com.example.signalgeneratorapp.App;
import com.example.signalgeneratorapp.ConnectionManager;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.StorageManager;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.SineOscillator;

import java.util.LinkedList;

public abstract class Signal {
    public String name;
    protected final LinkedList<UnitInputPort> inputs;
    protected final LinkedList<UnitOutputPort> outputs;
    protected final Synthesizer synthesizer;
    public Signal(String name, Synthesizer synthesizer) {
        this.name = name;
        inputs = new LinkedList<>();
        outputs = new LinkedList<>();
        this.synthesizer = synthesizer;
    }

    public LinkedList<UnitInputPort> inputsPorts(){
        return inputs;
    }
    public LinkedList<UnitOutputPort> outputsPorts(){
        return outputs;
    }
    public UnitOutputPort firstOutputPort() {return outputs.getFirst(); }
    public abstract String getType();
    private String generatePreferencesStringForVariable(String variable){
        return App.getContext().getString(R.string.storage_key_signal_prefix) + "." + name + "." + variable;
    }
    public void store(){
        for (int i=0;i<inputs.size();i++){
            StorageManager.getInstance().store(generatePreferencesStringForVariable("inputs"+i), Double.toString(inputs.get(i).get()));
        }
    }
    public void load(){
        for (int i=0;i<inputs.size();i++){
            if (StorageManager.getInstance().contains(generatePreferencesStringForVariable("inputs"+i))){
                inputs.get(i).set(Double.parseDouble(StorageManager.getInstance().load(generatePreferencesStringForVariable("inputs"+i))));
            }
        }
    }

    public void delete(){
        for (int i=0;i<inputs.size();i++){
            if (StorageManager.getInstance().contains(generatePreferencesStringForVariable("inputs"+i))){
                StorageManager.getInstance().remove(generatePreferencesStringForVariable("inputs"+i));
            }
        }
        ConnectionManager.getInstance().disconnect(this);
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
