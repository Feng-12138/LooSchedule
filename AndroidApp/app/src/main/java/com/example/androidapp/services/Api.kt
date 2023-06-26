package com.example.androidapp.services

import com.example.androidapp.models.Communication
import com.example.androidapp.models.Course
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @POST("api/schedule")
    fun getCourseSchedule(@Body request: RequestBody): Call<Map<String, List<Course>>>

    @GET("api/Communications")
    fun getCommunications(): Call<List<Communication>>

}