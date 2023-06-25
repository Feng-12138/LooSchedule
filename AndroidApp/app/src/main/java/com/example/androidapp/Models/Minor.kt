package com.example.androidapp.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Minor : Serializable {
    @SerializedName("requirementID")
    var requirementID : Int = 0

    @SerializedName("majorName")
    var majorName : String = ""

    @SerializedName("coopOnly")
    var coopOnly : Boolean = false

    @SerializedName("isDoubleDegree")
    var isDoubleDegree : Boolean = false
}