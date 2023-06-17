package com.example.androidapp.Models

import com.google.gson.annotations.SerializedName

class Breadth {
    @SerializedName("courseId")
    var courseId : String = ""

    @SerializedName("subject")
    var subject : String = ""

    @SerializedName("code")
    var code : String = ""

    @SerializedName("category")
    var category: String = ""
}