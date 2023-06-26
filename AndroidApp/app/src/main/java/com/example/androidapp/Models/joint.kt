package com.example.androidapp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class joint : Serializable {
    @SerializedName("requirementID")
    var requirementID : Int = 0

    @SerializedName("jointName")
    var jointName : String = ""
}