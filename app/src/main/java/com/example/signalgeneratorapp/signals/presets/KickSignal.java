package com.example.signalgeneratorapp.signals.presets;

import com.example.signalgeneratorapp.signals.Signal;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.*;

public class KickSignal extends Signal {

    private final PulseOscillator pulseosc;
    private final SineOscillator sineosc;
    private final Add addosc;
    private final Multiply multosc;


    public KickSignal(String name, Synthesizer synthesizer) {
        super(name, synthesizer);
        synthesizer.add(pulseosc = new PulseOscillator());
        synthesizer.add(sineosc = new SineOscillator());
        synthesizer.add(addosc = new Add());
        synthesizer.add(multosc = new Multiply());

        pulseosc.amplitude.set(1);
        pulseosc.frequency.set(1.0);

        addosc.inputA.connect(pulseosc.output);
        addosc.inputB.set(1);

        multosc.inputA.connect(addosc.output);
        multosc.inputB.set(1.0); // allow the output to be scaled

        sineosc.amplitude.connect(multosc.output);

        pulseosc.frequency.setName("kickFrequency");
        pulseosc.width.setName("kickWidth");
        sineosc.frequency.setName("soundFrequency");
        multosc.inputB.setName("amplitude");

        inputs.add(pulseosc.frequency);
        inputs.add(sineosc.frequency);
        inputs.add(pulseosc.width);
        inputs.add(multosc.inputB);
        outputs.add(sineosc.output);
    }

    @Override
    protected void finalize() throws Throwable {
        synthesizer.remove(pulseosc);
        synthesizer.remove(sineosc);
        synthesizer.remove(addosc);
        synthesizer.remove(multosc);
        super.finalize();
    }

    public UnitInputPort kickFrequency() { return pulseosc.frequency; }
    public UnitInputPort soundFrequency() { return sineosc.frequency; }
    public UnitInputPort amplitude() { return multosc.inputB; }
    @Override
    public String getType() { return type; }
    public static final String type = "kicksignal";
}
