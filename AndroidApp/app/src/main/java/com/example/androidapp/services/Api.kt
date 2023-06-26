package com.example.androidapp.services

import com.example.androidapp.models.Communication
import com.example.androidapp.models.Course
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("api/CourseSchedules")
    fun getCourseSchedule(): Call<Map<String, List<Course>>>

    @GET("api/Communications")
    fun getCommunications(): Call<List<Communication>>

}