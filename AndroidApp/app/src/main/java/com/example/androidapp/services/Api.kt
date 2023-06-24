package com.example.androidapp.services

import com.example.androidapp.Models.Communication
import retrofit2.Call
import retrofit2.http.GET

interface Api {
    @GET("api/Communications")
    fun getCommunications(): Call<List<Communication>>
}