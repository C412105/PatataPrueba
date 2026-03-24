package com.example.patataprueba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.patataprueba.databinding.PresetListBinding

class PresetAdapter: RecyclerView.Adapter<PresetAdapter.PresetViewHolder>(){

    private var presetList: List<Preset> = ArrayList()
    
    private var onClickItem: ((Preset) -> Unit)? = null
    private var onClickDeleteItem: ((Preset) -> Unit)? = null

    fun addItems(presets: ArrayList<Preset>){
        this.presetList = presets
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback : (Preset) -> Unit){
        this.onClickItem = callback
    }

    fun setOnClickDeleteItem(callback : (Preset) -> Unit){
        this.onClickDeleteItem = callback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PresetViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.preset_list, parent, false)
    )

    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        val item = presetList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(item)
        }
        holder.binding.btnDelete.setOnClickListener {
            onClickDeleteItem?.invoke(item)
        }
    }

    override fun getItemCount(): Int = presetList.size

    inner class PresetViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = PresetListBinding.bind(view)
        
        fun bind(preset: Preset){
            with(binding){
                tvIdPreset.text = preset.id.toString()
                tvPresetName.text = preset.presetName
                tvAvatarName.text = preset.avatarName
                tvWeaponName.text = preset.weaponName
            }
        }
    }
}