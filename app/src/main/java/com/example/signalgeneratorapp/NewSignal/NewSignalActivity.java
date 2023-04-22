package com.example.signalgeneratorapp.NewSignal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.SignalManager;

public class NewSignalActivity extends Activity {
    private EditText editTextSignalName;
    private RecyclerView recyclerViewSignalOptions;
    private Button buttonApproveNewSignal, buttonSuggestSignalName;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_signal_layout);

        editTextSignalName = findViewById(R.id.editTextNewSignalName);
        recyclerViewSignalOptions = findViewById(R.id.recyclerViewSignalOptions);
        buttonApproveNewSignal = findViewById(R.id.buttonApproveNewSignal);
        buttonSuggestSignalName = findViewById(R.id.buttonSuggestSignalName);

        recyclerViewSignalOptions.setLayoutManager(new LinearLayoutManager(this));
        SignalClassAdapter signalClassAdapter = new SignalClassAdapter();
        recyclerViewSignalOptions.setAdapter(signalClassAdapter);

        editTextSignalName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputValidation(s.toString());
            }
        });

        buttonSuggestSignalName.setOnClickListener(v -> {
            String baseName = signalClassAdapter.getSelectedTypeName();
            String signalName = baseName;
            int i=0;
            while (SignalManager.getInstance().signalNameExists(signalName)){
                i++;
                signalName = baseName+i;
            }
            editTextSignalName.setText(signalName);
        });
        buttonApproveNewSignal.setOnClickListener(v -> {
            SignalManager.getInstance().addSignal(editTextSignalName.getText().toString(), signalClassAdapter.getSelectedConstructor());
            finish();
        });

        inputValidation(editTextSignalName.getText().toString());
    }

    private void inputValidation(String signalName){
        if (SignalManager.getInstance().signalNameExists(signalName)) {
            editTextSignalName.setError("Name already exists!");
            buttonApproveNewSignal.setEnabled(false);
            //buttonApproveNewSignal.setEnabled(false);
        } else {
            buttonApproveNewSignal.setEnabled(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        editTextSignalName.clearFocus();
    }
}
