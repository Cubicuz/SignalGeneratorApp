package com.example.signalgeneratorapp.NewSignal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.signals.*;
import com.jsyn.Synthesizer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class SignalClassAdapter extends RecyclerView.Adapter<SignalClassAdapter.ViewHolder>{
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView signalClassName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            signalClassName = itemView.findViewById(R.id.textViewSignalClassName);
        }

        public TextView getSignalClassName() {
            return signalClassName;
        }
    }

    private final ArrayList<String> SignalClassNames = new ArrayList<>();
    private final ArrayList<BiFunction<String, Synthesizer, Signal>> SignalConstructors = new ArrayList<>();
    private int selectedIndex = 0;
    private ViewHolder selectedView=null;


    public SignalClassAdapter(){
        SignalClassNames.add("Add Signal");
        SignalConstructors.add(AddSignal::new);

        SignalClassNames.add("Compare Signal");
        SignalConstructors.add(Compare::new);

        SignalClassNames.add("Divide Signal");
        SignalConstructors.add(DivideSignal::new);

        SignalClassNames.add("Linear Ramp Signal");
        SignalConstructors.add(LinearRampSignal::new);

        SignalClassNames.add("Maximum Signal");
        SignalConstructors.add(MaximumSignal::new);

        SignalClassNames.add("Minimum Signal");
        SignalConstructors.add(MinimumSignal::new);

        SignalClassNames.add("Sawtooth Signal");
        SignalConstructors.add(SawtoothSignal::new);

        SignalClassNames.add("Schmidt Trigger Signal");
        SignalConstructors.add(SchmidtTriggerSignal::new);

        SignalClassNames.add("Sine Signal");
        SignalConstructors.add(SineSignal::new);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_signal_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.getSignalClassName().setText(SignalClassNames.get(position));
        if (selectedIndex == position){
            holder.getSignalClassName().setSelected(true);

        }
        holder.itemView.setOnClickListener(v -> {
            if (selectedView != null && selectedView != holder){
                selectedView.signalClassName.setBackgroundColor(holder.itemView.getResources().getColor(R.color.white));
            }
            selectedView = holder;
            selectedIndex = position;
            holder.signalClassName.setBackgroundColor(holder.itemView.getResources().getColor(com.google.android.material.R.color.abc_color_highlight_material));
        });
    }

    @Override
    public int getItemCount() {
        return SignalClassNames.size();
    }


}
