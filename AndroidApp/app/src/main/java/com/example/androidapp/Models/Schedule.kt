package com.example.androidapp.models

import android.os.Parcelable
import com.example.androidapp.enum.OverallValidationResult
import com.example.androidapp.enum.ValidationResult
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.Date


@Parcelize
class Schedule(var terms: MutableMap<String, MutableList<Course>>,
               var myDegree: List<String>,
               var mySequence: String,
               var startYear: String,
) : Parcelable {
    @SerializedName("time")
    var time: Date = Date()

    @SerializedName("term")
    var termSchedule: MutableMap<String, MutableList<Course>> = terms

    @SerializedName("Validated")
    var validated: Boolean = true

    @SerializedName("ValidatedCourse")
    lateinit var validatedCourse: MutableMap<String, List<List<ValidateResults>>>

    @SerializedName("Degree")
    var degree: List<String> = myDegree

    @SerializedName("Sequence")
    var sequence: String = mySequence

    @SerializedName("Year")
    var year: String = startYear

    @SerializedName("Specialization")
    var specialization: List<String> = listOf()

    @SerializedName("Minor")
    var minor: List<String> = listOf()
}