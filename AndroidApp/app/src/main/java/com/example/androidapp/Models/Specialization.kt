package com.example.androidapp.Models

import com.google.gson.annotations.SerializedName

class Specialization {
    @SerializedName("requirementId")
    var requirementId : Int = 0

    @SerializedName("specializationName")
    var specialization : String = ""
}