package com.example.androidapp.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.dataClass.TermSchedule
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.services.RetrofitClient
import com.example.androidapp.viewModels.AcademicPlan
import com.example.androidapp.viewModels.RequestData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealChatgptScreen(major: String, minor: String, specialization: String, year: String, sequence: String, navController: NavController){
    var value = remember {
        mutableStateOf("")
    }
    var showAlert by remember { mutableStateOf(false) }
    var popUpMessage = remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "What do you want to work in the future? (Please enter Career name)")
        TextField(
            value = value.value,
            onValueChange = { newText ->
                value.value = newText
            }
        )
        Text(text = "eg. accountant, software developer and etc.")
        Button(onClick = {
            val inputMajor = listOf(major)
            val inputMinor = if (minor != "Select your minor"){
                listOf(minor)
            } else{
                emptyList()
            }
            val inputSpecialization = if (specialization != "Select your specialization"){
                listOf(specialization)
            } else{
                emptyList()
            }

            val requestData = RecommendationPlan(
                position = value.value,
                academicPlan = AcademicPlan(
                    majors = inputMajor,
                    startYear = year,
                    sequence = sequence,
                    minors = inputMinor,
                    specializations = inputSpecialization
                )
            )

            val gson = Gson()
            val jsonBody = gson.toJson(requestData)
            println(jsonBody)

            val api = RetrofitClient.create()
            val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody)

            val call = api.getRecommendation(requestBody)
            call.enqueue(object : Callback<TermSchedule> {
                override fun onResponse(
                    call: Call<TermSchedule>,
                    response: Response<TermSchedule>
                ) {
                    if (response.isSuccessful) {
                        val output = response.body()
                        val sharedPreferences = context.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
                        val existingList = sharedPreferences.getString("scheduleList", "[]")
                        val type = object : TypeToken<MutableList<Schedule>>() {}.type
                        val scheduleList : MutableList<Schedule> = Gson().fromJson(existingList, type)
                        var position = 0
                        val schedule = output?.let {
                            popUpMessage.value = it.message.toString()
                            Schedule(it.schedule as MutableMap<String, MutableList<Course>>, myDegree = inputMajor, mySequence = sequence, startYear = year)
                        }
                        if (schedule != null) {
                            schedule.minor = inputMinor
                            schedule.specialization = inputSpecialization
                            scheduleList.add(position , schedule)
                        }
                        val editor = sharedPreferences.edit()
                        val jsonList = Gson().toJson(scheduleList)
                        editor.putString("scheduleList", jsonList)
                        editor.apply()

                        showAlert = true
                    } else {
                        println(response.message())
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TermSchedule>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    println(t.message)
                    call.cancel()
                }
            })
        },
            modifier = Modifier
                .padding(16.dp)) {
            Text("Done!")
        }
    }

    if (showAlert) {
        AlertDialog(
            onDismissRequest = {
                showAlert = false
            },
            title = {
                Text(text = "Notice")
            },
            text = {
                Text(popUpMessage.value)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAlert = false
                        navController.navigate(Screen.ViewSchedule.route)
                    }) {
                    Text("Confirm")
                }
            }
        )
    }
}

data class RecommendationPlan(
    val position: String,
    val academicPlan: AcademicPlan
)


