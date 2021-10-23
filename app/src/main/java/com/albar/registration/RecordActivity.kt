package com.albar.registration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.albar.registration.databinding.ActivityRecordBinding

class RecordActivity : AppCompatActivity() {
    // db helper
    lateinit var dbHelper: DbHelper

    // order by/sor queries
    private val latestFirst = "${Constants.cAddedTimestamp} DESC"

    private lateinit var binding: ActivityRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init db helper
        dbHelper = DbHelper(this)

        //load Records
        loadRecords()

        // return back to the previous activity
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        loadRecords()
    }

    private fun loadRecords() {
        val adapterRecord = AdapterRecord(this, dbHelper.getAllData(latestFirst))
        binding.rvRecord.adapter = adapterRecord
    }
}