package com.example.androidapp.viewModels

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.androidapp.dataClass.MyDegree
import com.example.androidapp.dataClass.TermSchedule
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.screens.Screen
import com.example.androidapp.services.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    val specializations: List<String>,
    var coursesTaken: List<String> = listOf(),
    var currentTerm: String? = null,
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
            listOf(specialization)
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
                    val schedule = output?.let { Schedule(it.schedule as MutableMap<String, MutableList<Course>>, myDegree = inputMajor, mySequence = sequence, startYear = year) }
                    if (schedule != null) {
                        schedule.minor = inputMinor
                        schedule.specialization = inputSpecialization
                        scheduleList.add(position , schedule)
                    }
                    val editor = sharedPreferences.edit()
                    val jsonList = Gson().toJson(scheduleList)
                    editor.putString("scheduleList", jsonList)
                    editor.apply()
                    navController.navigate(Screen.ViewSchedule.route)
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