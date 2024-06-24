package com.example.myapplication.model

import com.example.myapplication.model.MockQuestion

data class Mock(
    var uid: String = System.currentTimeMillis().toString(),
    var title: String = "",
    var duration: String = "",
    var mockQuestion: List<MockQuestion> = emptyList()
)
