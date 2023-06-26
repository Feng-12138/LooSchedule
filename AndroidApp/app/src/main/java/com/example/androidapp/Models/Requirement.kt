package com.example.androidapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Requirement : Parcelable {
    @SerializedName("requirementID")
    var requirementID: Int = 0

    @SerializedName("type")
    var type: String = ""

    @SerializedName("year")
    var year: String = ""

    @SerializedName("courses")
    var courses: String = ""

    @SerializedName("additionalRequirements")
    var additionalRequirements: String = ""

    @SerializedName("link")
    var link: String =""
}