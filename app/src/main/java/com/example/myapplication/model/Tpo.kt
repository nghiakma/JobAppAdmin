package com.example.myapplication.model

import android.os.Parcelable
import com.example.myapplication.util.Constants.Companion.ROLE_TYPE_TPO
import kotlinx.parcelize.Parcelize

//đóng gói (parcel) để truyền giữa các thành phần của ứng dụng Android
@Parcelize
data class Tpo(
    var uid : String = "",
    var email : String = "",
    var username : String = "",
    var mobile : String = "",
    var dob : String = "",
    var gender : String = "",
    var imageUri : String = "",
    var stream : String = "",
    var qualification : String = "",
    var experience : String = "",
    var biography : String = "",
    val roleType : String = ROLE_TYPE_TPO
): Parcelable