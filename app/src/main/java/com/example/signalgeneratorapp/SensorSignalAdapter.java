package com.example.signalgeneratorapp;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.signalgeneratorapp.signals.Signal;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;

public class SensorSignalAdapter extends RecyclerView.Adapter<SensorSignalAdapter.ViewHolder> {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.textViewSignalName);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    private LinkedList<Signal> signalCollection;
    public SensorSignalAdapter()
    {
        signalCollection = new LinkedList<>(SignalManager.getInstance().getSignalList());
        SignalManager.getInstance().addSignalsChangedListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                signalCollection = new LinkedList<>(SignalManager.getInstance().getSignalList());
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_signal_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Signal s = signalCollection.get(position);
        holder.getTextView().setText(s.name);
    }

    @Override
    public int getItemCount() {
        return signalCollection.size();
    }
}
