package com.example.signalgeneratorapp.SignalEdit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.signalgeneratorapp.R;
import com.jsyn.ports.UnitInputPort;
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
        

    }

    @Override
    public int getItemCount() {
        return inputPorts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView InputPortName, SelectedOutput;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            InputPortName = itemView.findViewById(R.id.textViewInputPortName);
            SelectedOutput = itemView.findViewById(R.id.textViewSelectedOutput);
        }
    }

    public interface OnClickListener {
        void onClick(int position, UnitInputPort unitInputPort);
    }
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) { this.onClickListener = onClickListener; }

}
