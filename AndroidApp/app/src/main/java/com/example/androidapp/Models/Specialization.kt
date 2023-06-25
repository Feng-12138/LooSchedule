package com.example.androidapp.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Specialization : Serializable {
    @SerializedName("requirementID")
    var requirementID : Int = 0

    @SerializedName("specializationName")
    var specialization : String = ""
}