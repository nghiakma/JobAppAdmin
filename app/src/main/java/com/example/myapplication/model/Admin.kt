package com.example.myapplication.model

import com.example.myapplication.util.Constants.Companion.ROLE_TYPE_ADMIN

data class Admin(
    var uid : String = "",
    var email : String = "",
    var username : String = "",
    var imageUrl : String = "",
    val roleType : String = ROLE_TYPE_ADMIN
)