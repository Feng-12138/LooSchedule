package com.example.androidapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.androidapp.dataClass.Everything
import com.example.androidapp.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchServerData(this)
        setContent {
            Column(modifier = Modifier.fillMaxSize()) {
                Navigation()
            }
        }
    }

    private fun fetchServerData(context: Context) {
        val api = RetrofitClient.create()
        val call = api.getEverything()
        call.enqueue(object : Callback<Everything> {
            override fun onResponse(call: Call<Everything>, response: Response<Everything>) {
                if (response.isSuccessful) {
                    val dataManager = EverythingManager.getInstance()
                    response.body()?.let { dataManager.setEverything(it) }
                }
                else {
                    println(response.message())
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Everything>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                println(t.message)
                call.cancel()
            }
        })
    }
}