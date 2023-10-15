package com.example.signalgeneratorapp.signals.presets;

import com.example.signalgeneratorapp.signals.SignalWithAmplitude;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.SineOscillator;

public class AmpWave extends SignalWithAmplitude {
    private final SineOscillator amplitudeWave;
    private final SineOscillator sound;

    public AmpWave(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        synthesizer.add(amplitudeWave = new SineOscillator());
        synthesizer.add(sound = new SineOscillator());

        amplitudeWave.frequency.set(.2);
        amplitudeWave.frequency.setName("waveFrequency");
        amplitudeWave.amplitude.setName("amplitude");
        sound.frequency.setName("soundFrequency");

        amplitudeWave.output.connect(sound.amplitude);
        inputs.add(amplitudeWave.frequency);
        inputs.add(sound.frequency);
        inputs.add(amplitudeWave.amplitude);
        outputs.add(sound.output);
    }

    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(amplitudeWave);
        synthesizer.remove(sound);
        super.finalize();
    }

    public UnitInputPort waveFrequency() { return amplitudeWave.frequency; }
    public UnitInputPort soundFrequency() { return sound.frequency; }
    public UnitInputPort amplitude() { return amplitudeWave.amplitude; }

    @Override
    public String getType() { return type; }
    public static final String type = "ampwave";
}
