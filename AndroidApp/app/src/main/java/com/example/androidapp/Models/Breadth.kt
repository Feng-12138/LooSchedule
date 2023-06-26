package com.example.androidapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Breadth : Parcelable {
    @SerializedName("courseID")
    var courseID : String = ""

    @SerializedName("subject")
    var subject : String = ""

    @SerializedName("code")
    var code : String = ""

    @SerializedName("category")
    var category: String = ""
}