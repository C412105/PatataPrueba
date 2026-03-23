package com.example.patataprueba

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.patataprueba.databinding.AvataresBinding

class AvatarAdapter(private val avatars: List<Avatars>):
RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>(){

    private lateinit var context: Context
    private var onSelectAvatar: ((Avatars) -> Unit)? = null

    fun setOnSelectAvatar(callback: (Avatars) -> Unit) {
        this.onSelectAvatar = callback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvatarViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.avatares, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(
    holder: AvatarViewHolder,
    position: Int
    ) {
        val avatar = avatars.get(position)
        with(holder){
            binding.tvAvatarName.text = avatar.name
            binding.tvAvatarDescription.text = avatar.description
            Glide.with(context)
                .load(avatar.avatarURL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(binding.imgAvatar)
                
            binding.btnSelect.setOnClickListener {
                onSelectAvatar?.invoke(avatar)
            }
        }
    }

    override fun getItemCount(): Int = avatars.size

    class AvatarViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding = AvataresBinding.bind(itemView)
    }
}