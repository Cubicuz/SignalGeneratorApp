package com.example.signalgeneratorapp;

import com.example.signalgeneratorapp.signals.*;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.LineOut;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.function.BiFunction;


public final class SignalManager {

    private final Map<String, Signal> signals = new HashMap<>();
    private long signalCount = 0;
    private final Map<UnitOutputPort, Signal> outputToSignal = new HashMap<>();
    private final Map<UnitInputPort, Signal> inputToSignal = new HashMap<>();

    public Collection<Signal> getSignalList() { return signals.values(); }
    public Signal getSignal(String name) { return signals.get(name); }
    public boolean signalNameExists(String name) { return signals.containsKey(name); }
    public Signal getSignal(UnitOutputPort uop) { return outputToSignal.get(uop); }
    public Signal getSignal(UnitInputPort uip) { return inputToSignal.get(uip); }

    private final Synthesizer synthesizer;
    // The lineout is not part of the signals because it has only one UnitInputPort with 2 dimensions.
    // This can not be supported with current design
    private final LineOut lineOut; // stereo output

    public <E extends Signal> E addSignal(String name, BiFunction<String, Synthesizer, E> fn){
        if (signals.containsKey(name)){
            throw new RuntimeException("name " + name + " already exists");
        }
        E signal = fn.apply(name, synthesizer);
        signals.put(name, signal);
        signal.outputsPorts().forEach(unitOutputPort -> outputToSignal.put(unitOutputPort, signal));
        signal.inputsPorts().forEach(unitInputPort -> inputToSignal.put(unitInputPort, signal));
        signalCount++;
        storeSignalToPreferences(signal);
        signalsChanged.firePropertyChange("signal count", signalCount -1, signalCount);
        return signal;
    }

    public <E extends Signal> E addOrGetSignal(String name, BiFunction<String, Synthesizer, E> fn) {
        if (signalNameExists(name)){
            return (E) getSignal(name);
        } else {
            return addSignal(name, fn);
        }
    }

    public void removeSignal(String name){
        Signal signal = signals.get(name);
        if (signal == null){return;}
        ConnectionManager.getInstance().disconnect(signal);
        signal.delete();
        signals.remove(name);
        signal.outputsPorts().forEach(outputToSignal::remove);
        signal.inputsPorts().forEach(inputToSignal::remove);
        removeSignalFromPreferences(signal);
        signalCount--;
        signalsChanged.firePropertyChange("signal count", signalCount +1, signalCount);
    }

    private final PropertyChangeSupport signalsChanged;

    public void addSignalsChangedListener(PropertyChangeListener pcl){
        signalsChanged.addPropertyChangeListener(pcl);
    }
    public void removeSignalsChangedListener(PropertyChangeListener pcl){
        signalsChanged.removePropertyChangeListener(pcl);
    }

    public void changeSignalName(String from, String to) {
        if (signals.containsKey(to)){
            // to already exists
        } else if (!signals.containsKey(from)) {
            // from does not exist
        } else {
            ConnectionManager.getInstance().renameSignal(from, to);
            Signal sig = signals.remove(from);
            sig.name = to;
            signals.put(to, sig);
            // call event or so
            updateSignalNameInPreferences(sig, from, to);
            signalsChanged.firePropertyChange("signal name", from, to);
            // TODO: fix stored connections
        }
    }

    public void startAudio() {
        synthesizer.start();
        lineOut.start();
    }

    public void stopAudio() {
        lineOut.stop();
        synthesizer.stop();
    }

    public UnitInputPort lineout(){
        return lineOut.input;
    }

    public void loadFromPreferences(){
        if (!StorageManager.getInstance().contains(App.getContext().getString(R.string.storage_key_signal_names_prefix))){
            return;
        }
        Set<String> names = StorageManager.getInstance().loadSet(App.getContext().getString(R.string.storage_key_signal_names_prefix));
        names.forEach(name -> {
            if (!StorageManager.getInstance().contains(App.getContext().getString(R.string.storage_key_signal_names_prefix)+name)){
                throw new RuntimeException("Signal name is in set but signal type is not saved!");
            }
            String type = StorageManager.getInstance().load(App.getContext().getString(R.string.storage_key_signal_names_prefix)+name);
            Signal signal = createSignalFromPreferenceData(name, type);
            signal.load();
        });
    }

    private Signal createSignalFromPreferenceData(String name, String type){
         switch (type) {
             case AddSignal.type:
                 return addSignal(name, AddSignal::new);
             case Compare.type:
                 return addSignal(name, Compare::new);
             case DivideSignal.type:
                 return addSignal(name, DivideSignal::new);
             case LinearRampSignal.type:
                 return addSignal(name, LinearRampSignal::new);
             case MaximumSignal.type:
                 return addSignal(name, MaximumSignal::new);
             case MinimumSignal.type:
                 return addSignal(name, MinimumSignal::new);
             case SawtoothSignal.type:
                 return addSignal(name, SawtoothSignal::new);
             case SchmidtTriggerSignal.type:
                 return addSignal(name, SchmidtTriggerSignal::new);
             case SineSignal.type:
                 return addSignal(name, SineSignal::new);
             default:
                 throw new RuntimeException("unavailable signal type: " + type + " for signal name: " + name);
        }
    }

    private void storeToPreferences(){
        StorageManager.getInstance().storeSet(App.getContext().getString(R.string.storage_key_signal_names_prefix), signals.keySet());
        signals.forEach((s, signal) -> {
            StorageManager.getInstance().store(App.getContext().getString(R.string.storage_key_signal_names_prefix)+s, signal.getType());
        });
    }

    private void storeSignalToPreferences(Signal signal){
        StorageManager.getInstance().storeSet(App.getContext().getString(R.string.storage_key_signal_names_prefix), signals.keySet());
        StorageManager.getInstance().store(App.getContext().getString(R.string.storage_key_signal_names_prefix)+signal.name, signal.getType());
    }

    private void removeSignalFromPreferences(Signal signal){
        StorageManager.getInstance().storeSet(App.getContext().getString(R.string.storage_key_signal_names_prefix), signals.keySet());
        StorageManager.getInstance().remove(App.getContext().getString(R.string.storage_key_signal_names_prefix)+signal.name);
    }

    private void updateSignalNameInPreferences(Signal signal, String oldName, String newName){
        StorageManager.getInstance().store(App.getContext().getString(R.string.storage_key_signal_names_prefix)+newName, signal.getType());
        StorageManager.getInstance().remove(App.getContext().getString(R.string.storage_key_signal_names_prefix)+oldName);
        StorageManager.getInstance().storeSet(App.getContext().getString(R.string.storage_key_signal_names_prefix), signals.keySet());
    }

    private SignalManager(){
        synthesizer = JSyn.createSynthesizer(new JSynAndroidAudioDevice());
        synthesizer.add(lineOut = new LineOut());
        signalsChanged = new PropertyChangeSupport(this);
    }
    private static SignalManager instance;
    public static SignalManager getInstance() {
        if (instance != null){
            return instance;
        }
        return instance = new SignalManager();
    }
}
