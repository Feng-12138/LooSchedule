package com.example.androidapp.Models

import com.google.gson.annotations.SerializedName

class Prerequisite {
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