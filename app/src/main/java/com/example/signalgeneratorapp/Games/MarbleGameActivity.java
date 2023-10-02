package com.example.signalgeneratorapp.Games;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import com.example.signalgeneratorapp.ConnectionManager;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.SensorOutputManager;
import com.example.signalgeneratorapp.SignalManager;
import com.example.signalgeneratorapp.signals.SensorOutput;
import com.example.signalgeneratorapp.signals.SineSignal;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;

public class MarbleGameActivity extends Activity {

    private MarbleGame marbleGame;
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

        SineSignal ss = SignalManager.getInstance().addOrGetSignal("bottomNoises", SineSignal::new);
        ss.frequency().set(300);
        ConnectionManager.getInstance().connect(ss.amplitude(), marbleGame.getXLow());
        ConnectionManager.getInstance().connectLineout(ss.firstOutputPort(), 0);

        Button reset = findViewById(R.id.marbeGameResetButton);
        reset.setOnClickListener(v -> marbleGame.reset());
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