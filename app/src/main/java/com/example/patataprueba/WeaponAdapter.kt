package com.example.patataprueba

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.patataprueba.databinding.WeaponsBinding


class WeaponAdapter(private val weapons: List<Weapons>):
RecyclerView.Adapter<WeaponAdapter.WeaponViewHolder>(){

    private lateinit var context: Context
    private var onSelectWeapon: ((Weapons) -> Unit)? = null

    fun setOnSelectWeapon(callback: (Weapons) -> Unit) {
        this.onSelectWeapon = callback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        position: Int
    ): WeaponViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.weapons, parent, false)
        return WeaponViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: WeaponViewHolder,
        position: Int
    ) {
        val weapon = weapons.get(position)
        with(holder){
            binding.tvWeaponName.text = weapon.name
            binding.tvWeaponDescription.text = weapon.description
            Glide.with(context)
                .load(weapon.weaponURL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(binding.imgWeapon)

            binding.btnSelect.setOnClickListener {
                onSelectWeapon?.invoke(weapon)
            }
        }
    }


    override fun getItemCount(): Int = weapons.size

    class WeaponViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val binding = WeaponsBinding.bind(itemView)

    }
}