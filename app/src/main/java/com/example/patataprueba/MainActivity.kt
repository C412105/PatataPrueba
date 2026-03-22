package com.example.patataprueba

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.patataprueba.databinding.ActivityMain2Binding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var avatarAdapter: AvatarAdapter
    private lateinit var weaponAdapter: WeaponAdapter
    private var screenHeight: Int = 0 // Variable para tamaño vertical de la pantalla

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sacar el tamaño de pantalla
        screenHeight = resources.displayMetrics.heightPixels

        // Ajuste altura de los RecyclerViews al tamaño de la pantalla
        binding.rvAvatar.layoutParams.height = screenHeight
        binding.rvWeapon.layoutParams.height = screenHeight

        // Configuracion de los RecyclerViews y el auto-scroll (Snap)
        setupRV()
        setupAutoScroll()

        avatarAdapter = AvatarAdapter(getAvatars())
        binding.rvAvatar.adapter = avatarAdapter

        weaponAdapter = WeaponAdapter(getWeapons())
        binding.rvWeapon.adapter = weaponAdapter
    }

    private fun setupRV() {
        // Configuración de Avatars y Weapons con PagerSnapHelper (página por página)
        binding.rvAvatar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        PagerSnapHelper().attachToRecyclerView(binding.rvAvatar)

        binding.rvWeapon.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        PagerSnapHelper().attachToRecyclerView(binding.rvWeapon)
    }

    private fun setupAutoScroll() {
        // Snap vertical para el NestedScrollView al soltar el toque
        binding.nestedScrollView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Pequeño delay para dejar que el scroll inercial termine antes de snappear
                binding.nestedScrollView.postDelayed({
                    snapToNearestView(binding.nestedScrollView.scrollY)
                }, 100)
            }
            false
        }
    }

    private fun snapToNearestView(currentScrollY: Int) {
        // Lógica: Redondea el scroll actual a la posición de la pantalla más cercana
        val nearestPosition = ((currentScrollY + screenHeight / 2) / screenHeight) * screenHeight
        binding.nestedScrollView.smoothScrollTo(0, nearestPosition)
    }

    private fun getAvatars(): MutableList<Avatars>{
        val avatars = mutableListOf<Avatars>()
        val optimus = Avatars(1,getString(R.string.optimus_name) ,(getString(R.string.optimus_description)), getString(R.string.optimus_url))
        val megatron = Avatars(2,getString(R.string.megatron_name) , (getString(R.string.megatron_description)), getString(R.string.megatron_url))

        avatars.add(optimus)
        avatars.add(megatron)

        return avatars
    }

    private fun getWeapons(): MutableList<Weapons>{
        val weapons = mutableListOf<Weapons>()
        val fm = Weapons(1, getString(R.string.fm_name), getString(R.string.fm_description), getString(R.string.fm_url))
        val es = Weapons(2, getString(R.string.es_name), getString(R.string.es_description), getString(R.string.es_url))

        weapons.add(fm)
        weapons.add(es)

        return weapons
    }
}