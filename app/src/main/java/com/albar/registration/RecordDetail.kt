package com.albar.registration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.albar.registration.databinding.ActivityRecordDetailBinding

class RecordDetail : AppCompatActivity() {
    companion object {
        const val data = "data"
    }

    private lateinit var binding: ActivityRecordDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // return back to the previous activity
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val data = intent.getParcelableExtra<RecordModel>(data) as RecordModel

        binding.txtNama.text = data.mNama
    }

    private fun setDetail() {
        binding.txtNama.text = nama
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}