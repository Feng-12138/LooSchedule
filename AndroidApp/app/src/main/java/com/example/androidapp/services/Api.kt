package com.example.androidapp.services

import com.example.androidapp.Models.Communication
import com.example.androidapp.Models.Course
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.Dictionary

interface Api {
    @GET("api/CourseSchedules")
    fun getCourseSchedule(): Call<List<Pair<String, List<Course>>>>

    @GET("api/Communications")
    fun getCommunications(): Call<List<Communication>>

    @GET("api/Courses/{courseId}")
    fun getCourseDetails(@Path("courseId") courseId: String): Call<Course>
}