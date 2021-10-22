package com.albar.registration

object Constants {
    //db name
    const val dbName = "vaccination"

    //db Version
    const val dbVersion = 1

    //table Name
    const val tableName = "vaccinationTable"

    //columns/fields of table
    const val cId = "id"
    const val cNama = "nama"
    const val cAlamat = "alamat"
    const val cNoHp = "noHp"
    const val cJenisKelamin = "jenisKelamin"
    const val cLokasi = "lokasi"
    const val cImageUri = "imageUri"
    const val cAddedTimestamp = "addedTimeStamp"

    // create table query
    const val createTable = (
            "CREATE TABLE" + tableName + "("
                    + cId + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + cNama + " TEXT,"
                    + cAlamat + " TEXT,"
                    + cNoHp + " TEXT,"
                    + cJenisKelamin + " TEXT,"
                    + cLokasi + " TEXT,"
                    + cImageUri + " TEXT"
                    + cAddedTimestamp + " TEXT"
                    + ")"
            )
}