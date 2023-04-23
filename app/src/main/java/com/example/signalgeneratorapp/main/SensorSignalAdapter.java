package com.example.signalgeneratorapp.main;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.signalgeneratorapp.R;
import com.example.signalgeneratorapp.SignalManager;
import com.example.signalgeneratorapp.signals.Signal;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;

public class SensorSignalAdapter extends RecyclerView.Adapter<SensorSignalAdapter.ViewHolder> {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final Button buttonDelete;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = view.findViewById(R.id.textViewSignalName);
            buttonDelete = view.findViewById(R.id.buttonDelete);
        }

        public TextView getTextView() {
            return textView;
        }
        public Button getButtonDelete() { return buttonDelete; }
    }
    public interface OnClickListener {
        void onClick(int position, Signal signal);
    }

    private LinkedList<Signal> signalCollection;
    private OnClickListener onClickListener;
    private OnClickListener onDeleteClickListener;
    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    public void setOnDeleteClickListener(OnClickListener onClickListener){
        this.onDeleteClickListener = onClickListener;
    }
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
        holder.itemView.setOnClickListener(v -> {
            if (onClickListener != null){
                onClickListener.onClick(position, s);
            }
        });
        holder.getButtonDelete().setOnClickListener(v -> {
            if (onDeleteClickListener != null){
                onDeleteClickListener.onClick(position, s);
            }
        });
    }

    @Override
    public int getItemCount() {
        return signalCollection.size();
    }
}
