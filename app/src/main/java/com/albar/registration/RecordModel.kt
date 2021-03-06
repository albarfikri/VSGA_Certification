package com.albar.registration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RecordModel(
    var mId: String,
    var mNama: String,
    var mAlamat: String,
    var mNoHp: String,
    var mJenisKelamin: String,
    var mLokasi: String,
    var mImageUri: String,
    var mAddedTimeStamp: String
):Parcelable
