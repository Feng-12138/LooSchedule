package com.example.androidapp.services

import com.example.androidapp.dataClass.Everything
import com.example.androidapp.dataClass.ValidationResults
import com.example.androidapp.models.Communication
import com.example.androidapp.models.Course
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @GET("api/everything")
    fun getEverything(): Call<Everything>

    @POST("api/schedule")
    fun getCourseSchedule(@Body request: RequestBody): Call<Map<String, List<Course>>>

    @GET("api/Communications")
    fun getCommunications(): Call<List<Communication>>

    @POST("api/validate")
    fun validateSchedule(@Body requestBody: RequestBody): Call<ValidationResults>
}