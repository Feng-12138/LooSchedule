package com.example.androidapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Course(var courseId: String, var name: String, var rating: Float, var descript: String) : Parcelable {
    @SerializedName("courseID")
    var courseID : String = courseId

    @SerializedName("courseName")
    var courseName : String = name

    @SerializedName("subject")
    var subject : String = ""

    @SerializedName("code")
    var code : Int = 0

    @SerializedName("description")
    var description : String = descript

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

    @SerializedName("likeRating")
    var likeRating : Float = rating

    @SerializedName("easyRating")
    var easyRating : Float = 0.0f

    @SerializedName("usefulRating")
    var usefulRating: Float = 0.0f
}