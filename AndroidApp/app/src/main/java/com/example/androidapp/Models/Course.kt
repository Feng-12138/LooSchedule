package com.example.androidapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Course() : Parcelable {
    @SerializedName("courseID")
    var courseID : String = "courseId"

    @SerializedName("courseName")
    var courseName : String = "name"

    @SerializedName("subject")
    var subject : String = ""

    @SerializedName("code")
    var code : String = ""

    @SerializedName("description")
    var description : String = "descript"

    @SerializedName("credit")
    var credit : Float = 0.0f

    @SerializedName("availability")
    var availability : String = ""

    @SerializedName("onlineTerms")
    var onlineTerm : String = ""

    @SerializedName("coreqs")
    var coreqs : String = ""

    @SerializedName("antireqs")
    var antireqs : String = ""

    @SerializedName("likedRating")
    var likedRating : Float = 0.0f

    @SerializedName("easyRating")
    var easyRating : Float = 0.0f

    @SerializedName("usefulRating")
    var usefulRating: Float = 0.0f

    lateinit var prereqs: Prerequisite
}