package com.example.androidapp.viewModels

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.androidapp.dataClass.MyDegree
import com.example.androidapp.enum.CoopSequence
import com.example.androidapp.enum.MyMajor
import com.example.androidapp.enum.MyMinor
import com.example.androidapp.enum.MySpecialization
import com.example.androidapp.enum.MyYear
import com.example.androidapp.models.Communication
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.screens.Screen
import com.example.androidapp.services.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectDegreeVM(context: Context, navController: NavController) : ViewModel() {
    private val _uiState = MutableStateFlow(MyDegree())
    val uiState: StateFlow<MyDegree> = _uiState.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog : Boolean get() = _showDialog.value

    private fun getCourseSchedule(context: Context, navController: NavController) {
        val api = RetrofitClient.create()
        val call = api.getCourseSchedule()
        call?.enqueue(object : Callback<Map<String, List<Course>>> {
            override fun onResponse(
                call: Call<Map<String, List<Course>>>,
                response: Response<Map<String, List<Course>>>
            ) {
                if (response.isSuccessful) {
                    val output = response.body()
                    val schedule = output?.let { Schedule(it) }

                    val sharedPreferences = context.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
                    val existingList = sharedPreferences.getStringSet("scheduleList", emptySet())?.toList()
                    val scheduleList =
                        (existingList?.map { Gson().fromJson(it, Schedule::class.java) } ?: emptyList()).toMutableList()
                    scheduleList += schedule
                    val editor = sharedPreferences.edit()
                    val jsonList = scheduleList.map { Gson().toJson(it) }.toSet()
                    editor.putStringSet("scheduleList", jsonList)
                    editor.apply()

                    navController.navigate(Screen.ViewSchedule.route)
                } else {
                    Toast.makeText(context, "Response unSuccessful", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, List<Course>>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }

    fun toggleDialog() {
        _showDialog.value = !_showDialog.value
    }

    fun generateSchedule(
        major: MyMajor,
        year: MyYear,
        sequence: CoopSequence,
        minor: MyMinor,
        specialization: MySpecialization,
        context: Context,
        navController: NavController
    ) {
        if (major.major == "Select your degree" ||
            year.year == "Select your academic year" ||
            sequence.sequence == "Select your Coop sequence") {
            toggleDialog()
            return
        }
        getCourseSchedule(context, navController)
    }
}