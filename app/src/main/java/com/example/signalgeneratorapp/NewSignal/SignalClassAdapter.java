package com.example.signalgeneratorapp.NewSignal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.signals.*;
import com.example.signalgeneratorapp.signals.presets.KickSignal;
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
        SignalClassNames.add(AddSignal.type);
        SignalConstructors.add(AddSignal::new);

        SignalClassNames.add(Compare.type);
        SignalConstructors.add(Compare::new);

        SignalClassNames.add(DivideSignal.type);
        SignalConstructors.add(DivideSignal::new);

        SignalClassNames.add(LinearRampSignal.type);
        SignalConstructors.add(LinearRampSignal::new);

        SignalClassNames.add(MaximumSignal.type);
        SignalConstructors.add(MaximumSignal::new);

        SignalClassNames.add(MinimumSignal.type);
        SignalConstructors.add(MinimumSignal::new);

        SignalClassNames.add(SawtoothSignal.type);
        SignalConstructors.add(SawtoothSignal::new);

        SignalClassNames.add(SchmidtTriggerSignal.type);
        SignalConstructors.add(SchmidtTriggerSignal::new);

        SignalClassNames.add(SineSignal.type);
        SignalConstructors.add(SineSignal::new);

        SignalClassNames.add(KickSignal.type);
        SignalConstructors.add(KickSignal::new);
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
        int selectedColor = holder.itemView.getResources().getColor(com.google.android.material.R.color.highlighted_text_material_dark, null);
        if (selectedIndex == position){
            holder.signalClassName.setBackgroundColor(selectedColor);
            selectedView = holder;
        }
        holder.itemView.setOnClickListener(v -> {
            if (selectedView != null && selectedView != holder){
                selectedView.signalClassName.setBackgroundColor(holder.itemView.getResources().getColor(R.color.white, null));
            }
            selectedView = holder;
            selectedIndex = holder.getAdapterPosition();
            holder.signalClassName.setBackgroundColor(selectedColor);
        });
    }

    @Override
    public int getItemCount() {
        return SignalClassNames.size();
    }

    public BiFunction<String, Synthesizer, Signal> getSelectedConstructor(){
        return SignalConstructors.get(selectedIndex);
    }

    public String getSelectedTypeName(){
        return SignalClassNames.get(selectedIndex);
    }
}
