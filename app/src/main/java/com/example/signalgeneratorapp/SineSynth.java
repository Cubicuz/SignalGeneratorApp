package com.example.signalgeneratorapp;

import com.example.signalgeneratorapp.signals.LinearRampSignal;
import com.example.signalgeneratorapp.signals.SawtoothSignal;
import com.example.signalgeneratorapp.signals.SchmidtTriggerSignal;
import com.example.signalgeneratorapp.signals.SineSignal;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.*;

public class SineSynth {
    private final LinearRampSignal mAmpJack; // for smoothing and splitting the level
    private final SineSignal mOscLeft;
    private final SineSignal mOscRight;
    private final SawtoothSignal metaOscillator;
    private final SchmidtTriggerSignal schmidtTrigger;

    public SineSynth() {
        // Create a JSyn synthesizer that uses the Android output.

        SignalManager sm = SignalManager.getInstance();

        mOscLeft = sm.addSignal("sineLeft", SineSignal::new);
        mOscRight = sm.addSignal("sineRight", SineSignal::new);

        mAmpJack = sm.addSignal("AmpJack", LinearRampSignal::new);
        metaOscillator = sm.addSignal("SawTooth", SawtoothSignal::new);
        schmidtTrigger = sm.addSignal("schmidtTrigger", SchmidtTriggerSignal::new);

        // Create the unit generators and add them to the synthesizer

        // Split level setting to both oscillators.
        ConnectionManager.getInstance().connect(mOscLeft.amplitude(), mAmpJack.firstOutputPort());
        ConnectionManager.getInstance().connect(mOscRight.amplitude(), mAmpJack.firstOutputPort());
        mAmpJack.time().set(0.1); // duration of ramp

        ConnectionManager.getInstance().connect(mAmpJack.input(), schmidtTrigger.firstOutputPort());
        schmidtTrigger.setLevel().set(0.3);
        schmidtTrigger.resetLevel().set(0.2);

        ConnectionManager.getInstance().connect(schmidtTrigger.input(), metaOscillator.firstOutputPort());
        metaOscillator.frequency().setup(0.01, 1, 1);

        // Connect an oscillator to each channel of the LineOut
        ConnectionManager.getInstance().connectLineout(mOscLeft.firstOutputPort(), 0);
        ConnectionManager.getInstance().connectLineout(mOscRight.firstOutputPort(), 1);

        // Setup ports for nice UI
        getAmplitudePort().setName("Level");
        getAmplitudePort().setup(0.0, 0.5, 1.0);
        getLeftFrequencyPort().setName("FreqLeft");
        getLeftFrequencyPort().setup(100.0, 300.0, 1000.0);
        getRightFrequencyPort().setName("FreqRight");
        getRightFrequencyPort().setup(100.0, 400.0, 1000.0);
    }


    public UnitInputPort getAmplitudePort() { return mAmpJack.input(); }
    public UnitInputPort getLeftFrequencyPort() { return mOscLeft.frequency(); }
    public UnitInputPort getRightFrequencyPort() { return mOscRight.frequency(); }
}
