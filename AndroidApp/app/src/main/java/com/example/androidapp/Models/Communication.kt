package com.example.androidapp.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Communication : Serializable {
    @SerializedName("courseID")
    var courseID : String = ""

    @SerializedName("subject")
    var subject : String = ""

    @SerializedName("code")
    var code : String = ""

    @SerializedName("listNumber")
    var listNumber : Int = 0

    @SerializedName("year")
    var year : String = ""
}