package com.example.androidapp.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.androidapp.models.Communication
import com.example.androidapp.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun ApiPlayGround(navController: NavController) {
    val context = LocalContext.current
    Button(onClick = { getCommunications(context) }) {
        Text(text = "Try get Communications")
    }
}

private fun getCommunications(context: Context) {
    val api = RetrofitClient.create()
    val call = api.getCommunications()
    call?.enqueue(object : Callback<List<Communication>>{
        override fun onResponse(call: Call<List<Communication>>, response: Response<List<Communication>>) {
            if (response.isSuccessful) {
                val comList = response.body()
                Toast.makeText(context, comList?.get(0)?.subject ?: "fail to read", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "response unSuccessful", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onFailure(call: Call<List<Communication>>, t: Throwable) {
            Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            call.cancel()
        }
    })
}