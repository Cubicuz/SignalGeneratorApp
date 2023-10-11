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
import com.example.signalgeneratorapp.signals.presets.KickSignal;
import com.example.signalgeneratorapp.signals.presets.WaveModFreq;
import com.example.signalgeneratorapp.signals.presets.WaveSignal;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitOutputPort;

import java.util.function.BiFunction;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;

public class MarbleGameActivity extends Activity {

    private MarbleGame marbleGame;
    public final String MarbleGamePrefix = "Marble";
    public String[] directions = {"top", "left", "right", "bottom"};
    private Spinner mTopSpinner, mLeftSpinner, mRightSpinner, mBottomSpinner;
    private SignalWithAmplitude[] mSignals = new SignalWithAmplitude[4];

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

        mTopSpinner = findViewById(R.id.marbleTopSpinner);
        mLeftSpinner = findViewById(R.id.marbleLeftSpinner);
        mRightSpinner = findViewById(R.id.marbleRightSpinner);
        mBottomSpinner = findViewById(R.id.marbleBottomSpinner);

        ArrayAdapter<String> AmplitudeSignalAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        AmplitudeSignalAdapter.add("none");
        AmplitudeSignalAdapter.add(KickSignal.type);
        AmplitudeSignalAdapter.add(WaveSignal.type);
        AmplitudeSignalAdapter.add(WaveModFreq.type);

        mTopSpinner.setAdapter(AmplitudeSignalAdapter);
        mLeftSpinner.setAdapter(AmplitudeSignalAdapter);
        mRightSpinner.setAdapter(AmplitudeSignalAdapter);
        mBottomSpinner.setAdapter(AmplitudeSignalAdapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent == mTopSpinner){setSignal(AmplitudeSignalAdapter.getItem(position), 0);}
                else if (parent == mLeftSpinner) {setSignal(AmplitudeSignalAdapter.getItem(position), 1);}
                else if (parent == mRightSpinner) {setSignal(AmplitudeSignalAdapter.getItem(position), 2);}
                else if (parent == mBottomSpinner) {setSignal(AmplitudeSignalAdapter.getItem(position), 3);}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        mTopSpinner.setOnItemSelectedListener(itemSelectedListener);
        mLeftSpinner.setOnItemSelectedListener(itemSelectedListener);
        mRightSpinner.setOnItemSelectedListener(itemSelectedListener);
        mBottomSpinner.setOnItemSelectedListener(itemSelectedListener);

        marbleGame.reset();
    }

    private SignalWithAmplitude setSignal(String type, int direction){
        BiFunction<String, Synthesizer, SignalWithAmplitude> fn;
        String signalName = MarbleGamePrefix + directions[direction];
        switch (type) {
            case KickSignal.type:
                fn = KickSignal::new;
                break;
            case WaveSignal.type:
                fn = WaveSignal::new;
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
            if (old.getType() == type){
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