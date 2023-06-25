package com.example.androidapp.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Prerequisite : Serializable {
    @SerializedName("courseID")
    var courseID: String = ""

    @SerializedName("consentRequired")
    var consentRequired: Boolean = false

    @SerializedName("courses")
    var courses: String = ""

    @SerializedName("minimumLevel")
    var minimumLevel: String = ""

    @SerializedName("onlyOpenTo")
    var onlyOpenTo: String = ""

    @SerializedName("notOpenTo")
    var notOpenTo: String = ""
}