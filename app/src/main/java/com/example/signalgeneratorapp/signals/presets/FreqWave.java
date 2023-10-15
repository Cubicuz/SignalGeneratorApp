package com.example.signalgeneratorapp.signals.presets;

import com.example.signalgeneratorapp.signals.SignalWithAmplitude;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.Multiply;
import com.jsyn.unitgen.SineOscillator;

public class FreqWave extends SignalWithAmplitude {
    private final SineOscillator frequencyWave;
    private final Multiply frequencyWaveSquared;
    private final SineOscillator sound;

    public FreqWave(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        synthesizer.add(frequencyWave = new SineOscillator());
        synthesizer.add(frequencyWaveSquared = new Multiply());
        synthesizer.add(sound = new SineOscillator());

        frequencyWave.frequency.set(.2);
        frequencyWave.frequency.setName("waveFrequency");
        frequencyWave.amplitude.setName("waveAmplitude");
        frequencyWave.amplitude.set(30);
        sound.amplitude.setName("amplitude");

        frequencyWave.output.connect(frequencyWaveSquared.inputA);
        frequencyWave.output.connect(frequencyWaveSquared.inputB);
        frequencyWaveSquared.output.connect(sound.frequency);

        inputs.add(frequencyWave.frequency);
        inputs.add(sound.amplitude);
        inputs.add(frequencyWave.amplitude);
        outputs.add(sound.output);
    }

    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(frequencyWave);
        synthesizer.remove(sound);
        super.finalize();
    }

    public UnitInputPort waveFrequency() { return frequencyWave.frequency; }
    public UnitInputPort waveAmplitude() { return frequencyWave.amplitude; }
    public UnitInputPort amplitude() { return sound.amplitude; }

    @Override
    public String getType() { return type; }
    public static final String type = "freqwave";}
