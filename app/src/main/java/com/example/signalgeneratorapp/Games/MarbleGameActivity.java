package com.example.signalgeneratorapp.Games;

import android.app.Activity;

import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.SensorOutputManager;
import com.example.signalgeneratorapp.signals.SensorOutput;

import static android.hardware.Sensor.*;

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
}