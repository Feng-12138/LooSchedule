package com.example.androidapp.Models

import com.google.gson.annotations.SerializedName

class Requirement {
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