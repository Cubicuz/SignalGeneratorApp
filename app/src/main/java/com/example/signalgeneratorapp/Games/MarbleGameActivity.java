package com.example.signalgeneratorapp.Games;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import com.example.signalgeneratorapp.ConnectionManager;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.SensorOutputManager;
import com.example.signalgeneratorapp.SignalManager;
import com.example.signalgeneratorapp.signals.SensorOutput;
import com.example.signalgeneratorapp.signals.Signal;
import com.example.signalgeneratorapp.signals.SignalWithAmplitude;
import com.example.signalgeneratorapp.signals.presets.FreqWave;
import com.example.signalgeneratorapp.signals.presets.KickSignal;
import com.example.signalgeneratorapp.signals.presets.WaveModFreq;
import com.example.signalgeneratorapp.signals.presets.AmpWave;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitOutputPort;

import java.util.function.BiFunction;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;

public class MarbleGameActivity extends Activity {

    private MarbleGame marbleGame;
    public final String MarbleGamePrefix = "Marble";
    public String[] directions = {"top", "left", "right", "bottom"};
    private Spinner[] mSpinners = new Spinner[4];
    private SignalWithAmplitude[] mSignals = new SignalWithAmplitude[4];
    private ArrayAdapter<String> AmplitudeSignalAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marble_game_layout);

        MarbleGameView marbleGameView = findViewById(R.id.marbleGameView);
        marbleGame = new MarbleGame();
        marbleGame.setMarbleGameView(marbleGameView);

        SensorOutput sensorOutput = SensorOutputManager.getInstance().getSensorOutput(TYPE_ACCELEROMETER);

        sensorOutput.connect(marbleGame.getXTiltPort(), 0);
        sensorOutput.connect(marbleGame.getYTiltPort(), 1);

        Button reset = findViewById(R.id.marbeGameResetButton);
        reset.setOnClickListener(v -> marbleGame.reset());

        Button calibrate = findViewById(R.id.marbeGameCalibrateButton);
        calibrate.setOnClickListener(v -> marbleGame.calibrate());

        mSpinners[0] = findViewById(R.id.marbleTopSpinner);
        mSpinners[1] = findViewById(R.id.marbleLeftSpinner);
        mSpinners[2] = findViewById(R.id.marbleRightSpinner);
        mSpinners[3] = findViewById(R.id.marbleBottomSpinner);

        AmplitudeSignalAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        AmplitudeSignalAdapter.add("none");
        AmplitudeSignalAdapter.add(KickSignal.type);
        AmplitudeSignalAdapter.add(AmpWave.type);
        AmplitudeSignalAdapter.add(FreqWave.type);
        AmplitudeSignalAdapter.add(WaveModFreq.type);

        for (int i=0;i<4;i++){
            mSpinners[i].setAdapter(AmplitudeSignalAdapter);
        }

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (int i=0;i<4;i++){
                    if (parent == mSpinners[i]){setSignal(AmplitudeSignalAdapter.getItem(position), i);}
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        for (int i=0;i<4;i++){
            mSpinners[i].setOnItemSelectedListener(itemSelectedListener);
        }

        loadSignals();
        marbleGame.reset();
    }

    private String getSignalName(int direction){
        return MarbleGamePrefix + directions[direction];
    }
    private SignalWithAmplitude setSignal(String type, int direction){
        BiFunction<String, Synthesizer, SignalWithAmplitude> fn;
        String signalName = getSignalName(direction);
        switch (type) {
            case KickSignal.type:
                fn = KickSignal::new;
                break;
            case AmpWave.type:
                fn = AmpWave::new;
                break;
            case FreqWave.type:
                fn = FreqWave::new;
                break;
            case WaveModFreq.type:
                fn = WaveModFreq::new;
                break;
            case "none":
                SignalManager.getInstance().removeSignal(signalName);
                mSignals[direction] = null;
                return null;
            default:
                throw new RuntimeException("this was not expected");
        }
        if (SignalManager.getInstance().signalNameExists(signalName)){
            Signal old = SignalManager.getInstance().getSignal(signalName);
            if (old.getType().equals(type)){
                // we already have the correct signal
                SignalWithAmplitude oldS = (SignalWithAmplitude) old;
                ConnectionManager.getInstance().connect(oldS.amplitude(), getOutputPortForDirection(direction));
                mSignals[direction] = oldS;
                return oldS;
            } else {
                // delete the unfitting
                SignalManager.getInstance().removeSignal(signalName);
            }
        }

        SignalWithAmplitude s = SignalManager.getInstance().addSignal(signalName, fn);
        ConnectionManager.getInstance().connect(s.amplitude(), getOutputPortForDirection(direction));
        ConnectionManager.getInstance().connectLineout(s.firstOutputPort(), 0);
        ConnectionManager.getInstance().connectLineout(s.firstOutputPort(), 1);
        mSignals[direction] = s;
        return s;
    }
    private void loadSignals(){
        for (int i=0;i<4;i++){
            String signalName = getSignalName(i);
            if (SignalManager.getInstance().signalNameExists(signalName)){
                mSignals[i] = (SignalWithAmplitude) SignalManager.getInstance().getSignal(signalName);
                mSpinners[i].setSelection(AmplitudeSignalAdapter.getPosition(mSignals[i].getType()));
            }
        }
    }

    private UnitOutputPort getOutputPortForDirection(int direction){
        switch (direction) {
            case 0:
                return marbleGame.getYLow();
            case 1:
                return marbleGame.getXLow();
            case 2:
                return marbleGame.getXHigh();
            case 3:
                return marbleGame.getYHigh();
        }
        throw new RuntimeException("direction cannot be larger than 3 but was " + direction);
    }

    @Override
    protected void onStart() {
        super.onStart();
        marbleGame.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        marbleGame.stop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        SensorOutputManager.getInstance().start();
        SignalManager.getInstance().startAudio();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SignalManager.getInstance().stopAudio();
        SensorOutputManager.getInstance().stop();
    }
}