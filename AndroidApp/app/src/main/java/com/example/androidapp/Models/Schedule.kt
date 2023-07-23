package com.example.androidapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.IgnoredOnParcel
import java.util.Date


@Parcelize
class Schedule(var terms: MutableMap<String, List<Course>>) : Parcelable {
    @SerializedName("time")
    var time: Date = Date()

    @SerializedName("term")
    var termSchedule: MutableMap<String, List<Course>> = terms
}