package com.example.signalgeneratorapp.SignalEdit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.signalgeneratorapp.ConnectionManager;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.SensorOutputManager;
import com.example.signalgeneratorapp.SignalManager;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class SignalEditInputPortAdapter extends RecyclerView.Adapter<SignalEditInputPortAdapter.ViewHolder> {

    private LinkedList<UnitInputPort> inputPorts;
    private LinkedList<UnitOutputPort> allOutputPorts = new LinkedList<>();
    private ArrayAdapter<String> adapterAllOutputPorts;

    public SignalEditInputPortAdapter(LinkedList<UnitInputPort> inputPorts, Context parentContext){

        this.inputPorts = inputPorts;

        adapterAllOutputPorts = new ArrayAdapter<>(parentContext, android.R.layout.simple_list_item_1);
        SignalManager.getInstance().getSignalList().forEach(signal -> signal.outputsPorts().forEach(unitOutputPort -> {
            allOutputPorts.add(unitOutputPort);
            String outputName = (unitOutputPort.getName() == "Output") ? signal.name : (signal.name + "." + unitOutputPort.getName());
            adapterAllOutputPorts.add(outputName);
        }));
        SensorOutputManager.getInstance().getSensorOutputList().forEach(
                sensorOutput -> sensorOutput.getSensorOutputDimensions().forEach(
                        sensorOutputDimension -> {
            adapterAllOutputPorts.add(sensorOutput.name + " " + sensorOutputDimension.dimension);
            allOutputPorts.add(sensorOutputDimension);
        }));

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.signal_edit_input_port_row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // replace content
        UnitInputPort uip = inputPorts.get(position);
        holder.InputPortName.setText(uip.getName());
        UnitOutputPort uop = ConnectionManager.getInstance().getConnected(uip);
        holder.SetConnected(uop != null);
        holder.EditConstantValue.setText(Double.toString(uip.get()));
        holder.EditConstantValue.setOnFocusChangeListener((v, hasFocus) -> {
            if (! hasFocus){
                uip.set(Double.valueOf(holder.EditConstantValue.getText().toString()));
            }
        });
        holder.ConnectedOutput.setAdapter(adapterAllOutputPorts);
        if (uop != null){
            holder.ConnectedOutput.setSelection(allOutputPorts.indexOf(uop));
        }
        holder.ConnectedOutput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UnitOutputPort olduop = ConnectionManager.getInstance().getConnected(uip);
                UnitOutputPort newuop = allOutputPorts.get(position);
                if (olduop != newuop){
                    ConnectionManager.getInstance().disconnect(uip);
                    ConnectionManager.getInstance().connect(uip, newuop);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                throw new RuntimeException("is nothing selected possible???");
            }
        });
        holder.InputPortName.setOnClickListener(v -> {
            holder.SetConnected(!holder.connected);
            if (holder.connected){
                int index = holder.ConnectedOutput.getSelectedItemPosition();
                ConnectionManager.getInstance().connect(uip, allOutputPorts.get(index));
            } else {
                ConnectionManager.getInstance().disconnect(uip);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inputPorts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView InputPortName;
        public final Spinner ConnectedOutput;
        public final EditText EditConstantValue;
        private boolean connected;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            InputPortName = itemView.findViewById(R.id.textViewInputPortName);
            ConnectedOutput = itemView.findViewById(R.id.spinnerConnectedOutput);
            EditConstantValue = itemView.findViewById(R.id.editTextConstantValue);
        }

        public boolean IsConnected(){return connected;}
        public void SetConnected(boolean connected){
            this.connected = connected;
            if (connected){
                ConnectedOutput.setVisibility(View.VISIBLE);
                EditConstantValue.setVisibility(View.GONE);
            } else {
                ConnectedOutput.setVisibility(View.GONE);
                EditConstantValue.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface OnClickListener {
        void onClick(int position, UnitInputPort unitInputPort);
    }
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) { this.onClickListener = onClickListener; }

}
