package com.example.androidapp.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.androidapp.dataClass.MyDegree
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.screens.Screen
import com.example.androidapp.services.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


data class RequestData(
    val majors: List<String>,
    val startYear: String,
    val sequence: String,
    val minors: List<String>,
    val specializations: List<String>
)

class SelectDegreeVM(context: Context, navController: NavController) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MyDegree(
            major = "Select your degree",
            minor = "Select your minor",
            specialization = "Select your specialization",
            year = "Select your academic year",
            sequence = "Select your Coop sequence"
        )
    )
    val uiState: StateFlow<MyDegree> = _uiState.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog : Boolean get() = _showDialog.value

    private fun getCourseSchedule(
        context: Context,
        navController: NavController,
        major: String,
        year: String,
        sequence: String,
        minor: String,
        specialization: String
    ) {
        val inputMajor = listOf(major)
        val inputMinor = if (minor != "Select your minor"){
            listOf(minor)
        } else{
            emptyList()
        }
        val inputSpecialization = if (specialization != "Select your specialization"){
            listOf(minor)
        } else{
            emptyList()
        }

        val requestData = RequestData(
            majors = inputMajor,
            startYear = year,
            sequence = sequence,
            minors = inputMinor,
            specializations = inputSpecialization
        )

        val gson = Gson()
        val jsonBody = gson.toJson(requestData)
        println(jsonBody)

        val api = RetrofitClient.create()
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody)


        val call = api.getCourseSchedule(requestBody)
        call.enqueue(object : Callback<Map<String, List<Course>>> {
            override fun onResponse(
                call: Call<Map<String, List<Course>>>,
                response: Response<Map<String, List<Course>>>
            ) {
                if (response.isSuccessful) {
                    val output = response.body()
                    val schedule = output?.let { Schedule(it as MutableMap<String, List<Course>>) }

                    val sharedPreferences = context.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
                    val existingList = sharedPreferences.getStringSet("scheduleList", emptySet())?.toList()
                    val scheduleList =
                        (existingList?.map { Gson().fromJson(it, Schedule::class.java) } ?: emptyList()).toMutableList()
                    var position = 0
                    if(scheduleList.size > 0){
                        position = scheduleList.size
                    }
                    scheduleList.add(position , schedule)
                    val editor = sharedPreferences.edit()
                    val jsonList = scheduleList.map { Gson().toJson(it) }.toSet()
                    editor.putStringSet("scheduleList", jsonList)
                    editor.apply()

                    navController.navigate(Screen.ViewSchedule.route)
                } else {
                    println(response.message())
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, List<Course>>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                println(t.message)
                call.cancel()
            }
        })
    }

    fun toggleDialog() {
        _showDialog.value = !_showDialog.value
    }

    fun generateSchedule(
        major: String,
        year: String,
        sequence: String,
        minor: String,
        specialization: String,
        context: Context,
        navController: NavController
    ) {
        if (major == "Select your degree" ||
            year == "Select your academic year" ||
            sequence == "Select your Coop sequence") {
            toggleDialog()
            return
        }

        getCourseSchedule(context, navController, major, year, sequence, minor, specialization)
    }
}