package com.example.signalgeneratorapp.SignalEdit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.signalgeneratorapp.ConnectionManager;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.SignalManager;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class SignalEditInputPortAdapter extends RecyclerView.Adapter<SignalEditInputPortAdapter.ViewHolder> {

    private LinkedList<UnitInputPort> inputPorts;

    public SignalEditInputPortAdapter(LinkedList<UnitInputPort> inputPorts){
        this.inputPorts = inputPorts;
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
        if (uop != null){
            String signalName = SignalManager.getInstance().getSignal(uop).name;
            String outputName = (uop.getName() == "Output") ? signalName : (signalName + "." + uop.getName());
            holder.ConnectedOutput.setText(outputName);
        }
    }

    @Override
    public int getItemCount() {
        return inputPorts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView InputPortName, ConnectedOutput;
        public final EditText EditConstantValue;
        private boolean connected;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            InputPortName = itemView.findViewById(R.id.textViewInputPortName);
            ConnectedOutput = itemView.findViewById(R.id.textViewConnectedOutput);
            EditConstantValue = itemView.findViewById(R.id.editTextConstantValue);
            InputPortName.setOnClickListener(v -> SetConnected(!connected));
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
