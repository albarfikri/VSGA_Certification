package com.albar.registration

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

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

    // get All Data
    fun getAllData(orderBy: String): ArrayList<RecordModel> {
        val recordList = ArrayList<RecordModel>()
        val selectQuery = "SELECT * FROM ${Constants.tableName} ORDER BY $orderBy"

        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val modelRecord = RecordModel(
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cId)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cNama)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cAlamat)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cNoHp)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cJenisKelamin)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cLokasi)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cImageUri)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cAddedTimestamp))
                )
                //add record to list
                recordList.add(modelRecord)
            } while (cursor.moveToNext())
        }
        // close db connection
        db.close()
        // return the queried result list
        return recordList
    }

    // search data
    fun searchRecord(query: String): ArrayList<RecordModel> {
        val recordList = ArrayList<RecordModel>()
        val selectQuery = "SELECT * FROM ${Constants.tableName} WHERE ${Constants.cNama} LIKE'% $query %"

        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val modelRecord = RecordModel(
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cId)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cNama)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cAlamat)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cNoHp)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cJenisKelamin)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cLokasi)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cImageUri)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.cAddedTimestamp))
                )
                //add record to list
                recordList.add(modelRecord)
            } while (cursor.moveToNext())
        }
        // close db connection
        db.close()
        // return the queried result list
        return recordList
    }
}