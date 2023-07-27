package com.example.androidapp.services

import com.example.androidapp.dataClass.UWData
import com.example.androidapp.dataClass.TermSchedule
import com.example.androidapp.dataClass.ValidationResults
import com.example.androidapp.models.Communication
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {
    @GET("api/everything")
    fun getUWData(): Call<UWData>

    @POST("api/schedule")
    fun getCourseSchedule(@Body request: RequestBody): Call<TermSchedule>

    @GET("api/Communications")
    fun getCommunications(): Call<List<Communication>>

    @POST("api/validate")
    suspend fun validateSchedule(@Body requestBody: RequestBody): Response<ValidationResults>

    @POST("api/recommendations")
    fun getRecommendation(@Body request: RequestBody): Call<TermSchedule>
}