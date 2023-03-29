package com.example.signalgeneratorapp;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.*;

public class SineSynth {
    private final Synthesizer mSynth;
    private final LinearRamp mAmpJack; // for smoothing and splitting the level
    private final SineOscillator mOscLeft;
    private final SineOscillator mOscRight;

    private final SawtoothOscillator metaOscillator;
    private final SchmidtTrigger schmidtTrigger;
    private final LineOut mLineOut; // stereo output

    public SineSynth() {
        // Create a JSyn synthesizer that uses the Android output.
        mSynth = JSyn.createSynthesizer(new JSynAndroidAudioDevice());

        SignalManager.createInstance(mSynth);

        // Create the unit generators and add them to the synthesizer
        mSynth.add(mAmpJack = new LinearRamp());
        mSynth.add(mOscLeft = new SineOscillator());
        mSynth.add(mOscRight = new SineOscillator());
        mSynth.add(mLineOut = new LineOut());
        mSynth.add(metaOscillator = new SawtoothOscillator());
        mSynth.add(schmidtTrigger = new SchmidtTrigger());

        // Split level setting to both oscillators.
        mAmpJack.output.connect(mOscLeft.amplitude);
        mAmpJack.output.connect(mOscRight.amplitude);
        mAmpJack.time.set(0.1); // duration of ramp

        schmidtTrigger.output.connect(mAmpJack.input);
        schmidtTrigger.setLevel.set(0.8);
        schmidtTrigger.resetLevel.set(0.7);

        metaOscillator.output.connect(schmidtTrigger.input);
        metaOscillator.frequency.setup(0.01, 1, 1);

        // Connect an oscillator to each channel of the LineOut
        mOscLeft.output.connect(0, mLineOut.input, 0);
        mOscRight.output.connect(0, mLineOut.input, 1);

        // Setup ports for nice UI
        getAmplitudePort().setName("Level");
        getAmplitudePort().setup(0.0, 0.5, 1.0);
        getLeftFrequencyPort().setName("FreqLeft");
        getLeftFrequencyPort().setup(100.0, 300.0, 1000.0);
        getRightFrequencyPort().setName("FreqRight");
        getRightFrequencyPort().setup(100.0, 400.0, 1000.0);
    }

    public void start() {
        mSynth.start();
        mLineOut.start();
    }

    public void stop() {
        mLineOut.stop();
        mSynth.stop();
    }

    public UnitInputPort getAmplitudePort() { return mAmpJack.getInput(); }
    public UnitInputPort getLeftFrequencyPort() { return mOscLeft.frequency; }
    public UnitInputPort getRightFrequencyPort() { return mOscRight.frequency; }
}
