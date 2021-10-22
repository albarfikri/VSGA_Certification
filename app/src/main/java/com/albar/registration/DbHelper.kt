package com.albar.registration

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(
    context,
    Constants.dbName,
    null,
    Constants.dbVersion
) {
    override fun onCreate(db: SQLiteDatabase?) {
        //create table on db
        db?.execSQL(Constants.createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //upgrage database(if there is any structure change, change db version)
        //drop older table if exists
        db?.execSQL("DROP TABLE IF EXISTS " + Constants.tableName)
        onCreate(db)
    }

    // insert record
    fun insertData(
        nama: String,
        alamat: String,
        noHp: String,
        jenisKelamin: String,
        lokasi: String,
        imageUri: String,
        addedTimeStamp: String
    ): Long {
        // get writable database as we're about to write data
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(Constants.cNama, nama)
        values.put(Constants.cAlamat, alamat)
        values.put(Constants.cNoHp, noHp)
        values.put(Constants.cJenisKelamin, jenisKelamin)
        values.put(Constants.cLokasi, lokasi)
        values.put(Constants.cImageUri, imageUri)
        values.put(Constants.cAddedTimestamp, addedTimeStamp)

        //insert row, it will return record id of saved record
        val id = db.insert(Constants.tableName, null, values)
        //close db connection
        db.close()
        //return id of inserted record
        return id
    }
}