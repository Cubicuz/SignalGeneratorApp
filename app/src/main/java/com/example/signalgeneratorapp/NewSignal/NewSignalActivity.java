package com.example.signalgeneratorapp.NewSignal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.signalgeneratorapp.R;

public class NewSignalActivity extends Activity {
    private EditText editTextSignalName;
    private RecyclerView recyclerViewSignalOptions;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_signal_layout);

        editTextSignalName = findViewById(R.id.editTextNewSignalName);
        recyclerViewSignalOptions = findViewById(R.id.recyclerViewSignalOptions);

        recyclerViewSignalOptions.setLayoutManager(new LinearLayoutManager(this));
        SignalClassAdapter signalClassAdapter = new SignalClassAdapter();
        recyclerViewSignalOptions.setAdapter(signalClassAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        editTextSignalName.clearFocus();
    }
}
