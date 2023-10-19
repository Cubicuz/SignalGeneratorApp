package com.example.signalgeneratorapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.signalgeneratorapp.*;
import com.example.signalgeneratorapp.Games.MarbleGameActivity;
import com.example.signalgeneratorapp.NewSignal.NewSignalActivity;
import com.example.signalgeneratorapp.SensorEdit.SensorEditActivity;
import com.example.signalgeneratorapp.SignalEdit.SignalEditActivity;
import com.example.signalgeneratorapp.Storage.StorageActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private RecyclerView listViewMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        StorageManager.createInstance(getApplicationContext());
        SensorOutputManager.createInstance(getApplicationContext());

        SignalManager.getInstance().loadFromPreferences();
        ConnectionManager.getInstance().loadFromPreferences();

        //SineSynth mSineSynth = new SineSynth();
        listViewMain = findViewById(R.id.linearLayoutMain);
        FloatingActionButton addNewSignalButton = findViewById(R.id.floatingActionButtonNewSignal);

        SensorSignalAdapter ssa = new SensorSignalAdapter();
        ssa.setOnClickListener((position, signal) -> {
            Intent intent = new Intent(MainActivity.this, SignalEditActivity.class);
            // Passing the data to the SignalEditActivity
            intent.putExtra(util.INTENT_SIGNAL_NAME, signal.name);
            startActivity(intent);
        });
        ssa.setOnDeleteClickListener((position, signal) -> {
            SignalManager.getInstance().removeSignal(signal.name);
        });

        listViewMain.setLayoutManager(new LinearLayoutManager(this));
        listViewMain.setAdapter(ssa);

        addNewSignalButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewSignalActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_menu:
                startActivity(new Intent(MainActivity.this, SensorEditActivity.class));
                return true;
            case R.id.add_signal_menu:
                startActivity(new Intent(MainActivity.this, NewSignalActivity.class));
                return true;
            case R.id.marble_game_menu:
                startActivity(new Intent(MainActivity.this, MarbleGameActivity.class));
                return true;
            case R.id.storage_menu:
                startActivity(new Intent(MainActivity.this, StorageActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
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
