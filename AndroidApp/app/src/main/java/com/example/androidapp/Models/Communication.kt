package com.example.androidapp.Models

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName

class Communication {
    @SerializedName("courseID")
    var courseID : String = ""

    @SerializedName("subject")
    var subject : String = ""

    @SerializedName("code")
    var code : String = ""

    @SerializedName("listNumber")
    var listNumber : String = ""

    @SerializedName("year")
    var year : String = ""
}