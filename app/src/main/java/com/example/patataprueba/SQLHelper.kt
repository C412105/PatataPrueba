package com.example.patataprueba

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLHelper(context: MainActivity): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    
    companion object{
        private const val DB_VERSION = 1
        private const val DB_NAME = "patata_presets.db"
        private const val DB_TABLE = "tbl_presets"
        private const val ID = "id"
        private const val PRESET_NAME = "preset_name"
        private const val AVATAR_NAME = "avatar_name"
        private const val WEAPON_NAME = "weapon_name"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = (
                "CREATE TABLE $DB_TABLE(" +
                        "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$PRESET_NAME TEXT," +
                        "$AVATAR_NAME TEXT," +
                        "$WEAPON_NAME TEXT)"
                )
        db?.execSQL(createTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL("DROP TABLE IF EXISTS $DB_TABLE")
        onCreate(db)
    }

    fun insertPreset(preset: Preset): Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, preset.id)
        contentValues.put(PRESET_NAME, preset.presetName)
        contentValues.put(AVATAR_NAME, preset.avatarName)
        contentValues.put(WEAPON_NAME, preset.weaponName)

        val success = db.insert(DB_TABLE, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getPresets(): ArrayList<Preset>{
        val listPreset: ArrayList<Preset> = ArrayList()

        val db = this.readableDatabase
        val sql = "SELECT * FROM $DB_TABLE ORDER BY $ID DESC"
        val cursor: Cursor

        try {
            cursor = db.rawQuery(sql, null)
        } catch (err: Exception){
            err.printStackTrace()
            return ArrayList()
        }

        if(cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex(ID))
                val presetName = cursor.getString(cursor.getColumnIndex(PRESET_NAME))
                val avatarName = cursor.getString(cursor.getColumnIndex(AVATAR_NAME))
                val weaponName = cursor.getString(cursor.getColumnIndex(WEAPON_NAME))

                val preset = Preset(id, presetName, avatarName, weaponName)
                listPreset.add(preset)

            } while (cursor.moveToNext())
        }
        cursor.close()
        return listPreset
    }

    fun updatePreset(presetUpd: Preset): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(PRESET_NAME, presetUpd.presetName)
        contentValues.put(AVATAR_NAME, presetUpd.avatarName)
        contentValues.put(WEAPON_NAME, presetUpd.weaponName)

        val success = db.update(DB_TABLE, contentValues, "$ID=${presetUpd.id}", null)
        db.close()
        return success
    }

    fun deletePreset(id: Int?) {
        val db = writableDatabase
        db.delete(DB_TABLE, "$ID=${id}", null)
        db.close()
    }
}