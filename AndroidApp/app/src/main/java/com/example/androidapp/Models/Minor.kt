package com.example.androidapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Minor : Parcelable {
    @SerializedName("requirementID")
    var requirementID : Int = 0

    @SerializedName("majorName")
    var majorName : String = ""

    @SerializedName("coopOnly")
    var coopOnly : Boolean = false

    @SerializedName("isDoubleDegree")
    var isDoubleDegree : Boolean = false
}