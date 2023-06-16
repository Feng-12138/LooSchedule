package com.example.androidapp.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClient {
    companion object{
        private const val baseUrl = "http://10.0.2.2:8080/"

        fun create(): Api {
            val retrofit: Retrofit = Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(Api::class.java)
        }
    }
}