package com.albar.registration

import android.net.Uri
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


        binding.txtNama.setText(data.mNama)
        binding.txtAlamat.setText(data.mAlamat)
        binding.txtHp.setText(data.mNoHp)
        binding.tvLokasi.setText(data.mLokasi)
        binding.tvJenisKelamin.setText(data.mJenisKelamin)
        binding.profile.setImageURI(Uri.parse(data.mImageUri))

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}