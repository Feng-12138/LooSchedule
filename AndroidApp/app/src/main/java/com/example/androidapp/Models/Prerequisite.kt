package com.example.androidapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Prerequisite : Parcelable {
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