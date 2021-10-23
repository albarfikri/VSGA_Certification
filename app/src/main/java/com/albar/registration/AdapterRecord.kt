package com.albar.registration

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.albar.registration.databinding.ItemRecordBinding

class AdapterRecord():RecyclerView.Adapter<AdapterRecord.HolderRecord>(){

    private var context: Context? = null
    private var recordList: ArrayList<RecordModel>? = null

    constructor(context: Context?, recordList: ArrayList<RecordModel>?) : this() {
        this.context = context
        this.recordList = recordList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecord {
        return HolderRecord(
            LayoutInflater.from(context).inflate(R.layout.item_record, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return recordList!!.size
    }

    override fun onBindViewHolder(holder: HolderRecord, position: Int) {
        // get data, set data, handle clicks

        // get data
        val model = recordList!![position]

        // set data to views
        holder.bind(model)

    }

    inner class HolderRecord(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRecordBinding.bind(itemView)

        fun bind(model: RecordModel) {
            binding.namaItem.text = model.mNama
            binding.hpItem.text = model.mNoHp
            binding.alamatItem.text = model.mAlamat
        }
    }
}
