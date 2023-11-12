package com.example.signalgeneratorapp.signals

import com.example.signalgeneratorapp.signals.presets.AmpWave
import com.example.signalgeneratorapp.signals.presets.FreqWave
import com.example.signalgeneratorapp.signals.presets.KickSignal
import com.example.signalgeneratorapp.signals.presets.WaveModFreq
import com.jsyn.Synthesizer
import com.jsyn.ports.UnitInputPort

abstract class SignalWithAmplitude(name: String?, synthesizer: Synthesizer?) : Signal(name, synthesizer) {
    abstract fun amplitude(): UnitInputPort?

    companion object{
        @JvmStatic
        val SignalWithAmplitudeTypes = mapOf(
            SineSignal.type to ::SineSignal,
            AmpWave.type to ::AmpWave,
            FreqWave.type to ::FreqWave,
            KickSignal.type to ::KickSignal,
            WaveModFreq.type to ::WaveModFreq,
            SawtoothSignal.type to ::SawtoothSignal

        )
    }
}
