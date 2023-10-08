package com.example.signalgeneratorapp.signals.presets;

import com.example.signalgeneratorapp.signals.SignalWithAmplitude;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.Multiply;
import com.jsyn.unitgen.SineOscillator;

public class WaveModFreq extends SignalWithAmplitude {
    private final SineOscillator amplitudeWave;
    private final SineOscillator waveFrequencyMod;
    private final Multiply waveFrequencyModOffset;
    private final SineOscillator sound;

    public WaveModFreq(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        synthesizer.add(amplitudeWave = new SineOscillator());
        synthesizer.add(waveFrequencyMod = new SineOscillator());
        synthesizer.add(waveFrequencyModOffset = new Multiply());
        synthesizer.add(sound = new SineOscillator());

        waveFrequencyMod.frequency.setName("waveModFreq");
        waveFrequencyMod.frequency.set(0.02);
        waveFrequencyModOffset.inputA.connect(waveFrequencyMod.output);
        waveFrequencyModOffset.inputB.connect(waveFrequencyMod.output);

        amplitudeWave.frequency.connect(waveFrequencyModOffset.output);
        amplitudeWave.frequency.setName("waveFrequency");
        amplitudeWave.amplitude.setName("amplitude");
        sound.frequency.setName("soundFrequency");

        amplitudeWave.output.connect(sound.amplitude);
        inputs.add(sound.frequency);
        inputs.add(waveFrequencyMod.frequency);
        inputs.add(amplitudeWave.amplitude);
        outputs.add(sound.output);
    }

    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(amplitudeWave);
        synthesizer.remove(waveFrequencyMod);
        synthesizer.remove(waveFrequencyModOffset);
        synthesizer.remove(sound);
        super.finalize();
    }

    public UnitInputPort waveFrequency() { return amplitudeWave.frequency; }
    public UnitInputPort soundFrequency() { return sound.frequency; }
    public UnitInputPort amplitude() { return amplitudeWave.amplitude; }

    @Override
    public String getType() { return type; }
    public static final String type = "wavesignalmodfreq";}
