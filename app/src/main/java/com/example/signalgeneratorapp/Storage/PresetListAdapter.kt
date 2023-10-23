package com.example.signalgeneratorapp.Storage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.signalgeneratorapp.R
import com.example.signalgeneratorapp.StorageManager

class PresetListAdapter : RecyclerView.Adapter<PresetListAdapter.ViewHolder>() {
    private val mList: ArrayList<PresetViewModel> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.storage_preset_row_item, parent, false)
        val vh = ViewHolder(view)
        vh.delete.setOnClickListener {
            StorageManager.getInstance().deletePreset(vh.prefName.text.toString())
            presetsChanged()
        }
        vh.load.setOnClickListener {
            StorageManager.getInstance().loadFromPreset(vh.prefName.toString())
        }
        vh.save.setOnClickListener {
            StorageManager.getInstance().storeToPreset(vh.prefName.toString())
        }
        return vh
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val PresetViewModel: PresetViewModel = mList[position]
        holder.prefName.text = PresetViewModel.name
    }

    fun presetsChanged() {
        mList.clear()
        for (name in StorageManager.getInstance().storedPreferenceNames) {
            mList.add(PresetViewModel(name))
        }
        notifyDataSetChanged()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val prefName: TextView = ItemView.findViewById(R.id.textViewPresetName)
        val delete: Button = ItemView.findViewById(R.id.buttonDeletePreset)
        val save: Button = ItemView.findViewById(R.id.buttonSaveToPreset)
        val load: Button = ItemView.findViewById(R.id.buttonLoadFromPreset)
    }
}