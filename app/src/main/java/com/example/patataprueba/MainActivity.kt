package com.example.patataprueba

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.patataprueba.databinding.ActivityMain2Binding
import io.github.muddz.styleabletoast.StyleableToast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var avatarAdapter: AvatarAdapter
    private lateinit var weaponAdapter: WeaponAdapter
    private lateinit var presetAdapter: PresetAdapter
    private lateinit var sqliteHelper: SQLHelper
    
    private var screenHeight: Int = 0
    private var presetModel: Preset? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        sqliteHelper = SQLHelper(this)
        screenHeight = resources.displayMetrics.heightPixels

        // Ajustar alturas de las secciones para que ocupen toda la pantalla
        binding.clPresets.layoutParams.height = screenHeight
        binding.rvAvatar.layoutParams.height = screenHeight
        binding.rvWeapon.layoutParams.height = screenHeight

        initRecyclerViews()
        setupAutoScroll()
        getPresets()

        // Listeners para los botones de la sección de Presets
        with(binding) {
            btnSave.setOnClickListener {
                val name = etPresetName.text.toString()
                val avatar = etPresetAvatar.text.toString()
                val weapon = etPresetWeapon.text.toString()
                addPreset(name, avatar, weapon)
            }

            btnClear.setOnClickListener {
                clearText()
            }

            btnUpdate.setOnClickListener {
                val name = etPresetName.text.toString()
                val avatar = etPresetAvatar.text.toString()
                val weapon = etPresetWeapon.text.toString()
                updatePreset(name, avatar, weapon)
            }
        }

        // Manejar clicks en los items de la lista de presets guardados
        presetAdapter.setOnClickItem { preset ->
            with(binding) {
                etPresetName.setText(preset.presetName)
                etPresetAvatar.setText(preset.avatarName)
                etPresetWeapon.setText(preset.weaponName)
                presetModel = preset
                btnUpdate.isEnabled = true
                btnSave.isEnabled = false
            }
        }

        // Manejar el borrado de un preset
        presetAdapter.setOnClickDeleteItem { preset ->
            preset.id?.let { deletePreset(it) }
        }
    }

    private fun initRecyclerViews() {
        // Configuración de Avatares
        avatarAdapter = AvatarAdapter(getAvatars())
        binding.rvAvatar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvAvatar.adapter = avatarAdapter
        PagerSnapHelper().attachToRecyclerView(binding.rvAvatar)
        
        // Al darle a "Select" en un avatar, se autocompleta el cuadro de texto
        avatarAdapter.setOnSelectAvatar { avatar ->
            binding.etPresetAvatar.setText(avatar.name)
            StyleableToast.makeText(this, "${avatar.name} selected", R.style.success).show()
        }

        // Configuración de Armas
        weaponAdapter = WeaponAdapter(getWeapons())
        binding.rvWeapon.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvWeapon.adapter = weaponAdapter
        PagerSnapHelper().attachToRecyclerView(binding.rvWeapon)

        // Al darle a "Select" en un arma, se autocompleta el cuadro de texto
        weaponAdapter.setOnSelectWeapon { weapon ->
            binding.etPresetWeapon.setText(weapon.name)
            StyleableToast.makeText(this, "${weapon.name} selected", R.style.success).show()
        }

        // Configuración de la lista de Presets guardados (vertical)
        binding.rvPresets.layoutManager = LinearLayoutManager(this)
        presetAdapter = PresetAdapter()
        binding.rvPresets.adapter = presetAdapter
    }

    private fun addPreset(name: String, avatar: String, weapon: String) {
        if (name.isEmpty() || avatar.isEmpty() || weapon.isEmpty()) {
            StyleableToast.makeText(this, "All fields are required", R.style.error).show()
            return
        }
        val preset = Preset(null, name, avatar, weapon)
        val status = sqliteHelper.insertPreset(preset)

        if (status > -1) {
            StyleableToast.makeText(this, "Preset saved", R.style.success).show()
            getPresets()
            clearText()
        } else {
            StyleableToast.makeText(this, "Error saving preset", R.style.error).show()
        }
    }

    private fun updatePreset(name: String, avatar: String, weapon: String) {
        if (presetModel == null) return
        val presetUpd = Preset(presetModel?.id, name, avatar, weapon)
        val status = sqliteHelper.updatePreset(presetUpd)
        if (status > -1) {
            StyleableToast.makeText(this, "Preset updated", R.style.success).show()
            getPresets()
            clearText()
        }
    }

    private fun deletePreset(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Preset")
        builder.setMessage("Are you sure?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            sqliteHelper.deletePreset(id)
            getPresets()
            dialog.dismiss()
            StyleableToast.makeText(this, "Successfully deleted", R.style.success).show()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun getPresets() {
        val list = sqliteHelper.getPresets()
        presetAdapter.addItems(list)
    }

    private fun clearText() {
        with(binding) {
            etPresetName.setText("")
            etPresetAvatar.setText("")
            etPresetWeapon.setText("")
            etPresetName.requestFocus()
            presetModel = null
            btnUpdate.isEnabled = false
            btnSave.isEnabled = true
        }
    }

    private fun setupAutoScroll() {
        // Snap vertical mejorado para el NestedScrollView
        binding.nestedScrollView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                binding.nestedScrollView.postDelayed({
                    val scrollY = binding.nestedScrollView.scrollY
                    
                    // Calculamos las posiciones top de cada sección
                    val presetsTop = binding.clPresets.top
                    val avatarTop = binding.rvAvatar.top
                    val weaponTop = binding.rvWeapon.top
                    
                    // Encontramos la sección más cercana al scroll actual
                    val distances = listOf(
                        Math.abs(scrollY - presetsTop),
                        Math.abs(scrollY - avatarTop),
                        Math.abs(scrollY - weaponTop)
                    )
                    
                    val minDistance = distances.minOrNull()
                    val targetY = when (minDistance) {
                        distances[0] -> presetsTop
                        distances[1] -> avatarTop
                        else -> weaponTop
                    }
                    
                    binding.nestedScrollView.smoothScrollTo(0, targetY)
                }, 100)
            }
            false
        }
    }

    private fun getAvatars(): MutableList<Avatars>{
        val avatars = mutableListOf<Avatars>()
        avatars.add(Avatars(1, getString(R.string.optimus_name), getString(R.string.optimus_description), getString(R.string.optimus_url)))
        avatars.add(Avatars(2, getString(R.string.megatron_name), getString(R.string.megatron_description), getString(R.string.megatron_url)))
        avatars.add(Avatars(3, getString(R.string.chief_name), getString(R.string.chief_description), getString(R.string.chief_url)))
        avatars.add(Avatars(4, getString(R.string.uriel_name), getString(R.string.uriel_description), getString(R.string.uriel_url)))
        avatars.add(Avatars(5, getString(R.string.shepard_name), getString(R.string.shepard_description), getString(R.string.shepard_url)))
        avatars.add(Avatars(6, getString(R.string.armstrong_name), getString(R.string.armstrong_description), getString(R.string.armstrong_url)))
        avatars.add(Avatars(7, getString(R.string.helldiver_name), getString(R.string.helldiver_description), getString(R.string.helldiver_url)))
        avatars.add(Avatars(8, getString(R.string.radahn_name), getString(R.string.radahn_description), getString(R.string.radahn_url)))
        avatars.add(Avatars(9, getString(R.string.guilliman_name), getString(R.string.guilliman_description), getString(R.string.guilliman_url)))
        avatars.add(Avatars(10, getString(R.string.voldemort_name), getString(R.string.voldemort_description), getString(R.string.voldemort_url)))
        avatars.add(Avatars(11, getString(R.string.sauron_name), getString(R.string.sauron_description), getString(R.string.sauron_url)))
        avatars.add(Avatars(12, getString(R.string.mordekaiser_name), getString(R.string.mordekaiser_description), getString(R.string.mordekaiser_url)))
        avatars.add(Avatars(13, getString(R.string.robocop_name), getString(R.string.robocop_description), getString(R.string.robocop_url)))
        avatars.add(Avatars(14, getString(R.string.jj_name), getString(R.string.jj_description), getString(R.string.jj_url)))
        avatars.add(Avatars(15, getString(R.string.hitler_name), getString(R.string.hitler_description), getString(R.string.hitler_url)))
        return avatars
    }

    private fun getWeapons(): MutableList<Weapons>{
        val weapons = mutableListOf<Weapons>()
        weapons.add(Weapons(1, getString(R.string.fm_name), getString(R.string.fm_description), getString(R.string.fm_url)))
        weapons.add(Weapons(2, getString(R.string.es_name), getString(R.string.es_description), getString(R.string.es_url)))
        weapons.add(Weapons(3, getString(R.string.exec_name), getString(R.string.exec_description), getString(R.string.exec_url)))
        weapons.add(Weapons(4, getString(R.string.riptide_name), getString(R.string.riptide_description), getString(R.string.riptide_url)))
        weapons.add(Weapons(5, getString(R.string.hellbomb_name), getString(R.string.hellbomb_description), getString(R.string.hellbomb_url)))
        weapons.add(Weapons(6, getString(R.string.armor_breaker_name), getString(R.string.armor_breaker_description), getString(R.string.armor_breaker_url)))
        weapons.add(Weapons(7, getString(R.string.emperor_sword_name), getString(R.string.emperor_sword_description), getString(R.string.emperor_sword_url)))
        weapons.add(Weapons(8, getString(R.string.maliketh_blade_name), getString(R.string.maliketh_blade_description), getString(R.string.maliketh_blade_url)))
        weapons.add(Weapons(9, getString(R.string.elder_wand_name), getString(R.string.elder_wand_description), getString(R.string.elder_wand_url)))
        weapons.add(Weapons(10, getString(R.string.zorg_name), getString(R.string.zorg_description), getString(R.string.zorg_url)))
        weapons.add(Weapons(11, getString(R.string.sauron_mace_name), getString(R.string.sauron_mace_description), getString(R.string.sauron_mace_url)))
        weapons.add(Weapons(12, getString(R.string.nightfall_name), getString(R.string.nightfall_description), getString(R.string.nightfall_url)))
        weapons.add(Weapons(12, getString(R.string.ray_gun_name), getString(R.string.ray_gun_description), getString(R.string.ray_gun_url)))
        return weapons
    }
}