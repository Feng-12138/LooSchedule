package com.example.androidapp.viewModels

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.androidapp.dataClass.ValidationResults
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.services.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class ValidateData(
    val schedule: MutableMap<String, MutableList<Course>>,
    val academicPlan: AcademicPlan
)

data class AcademicPlan(
    val majors: List<String>,
    val startYear: String,
    val sequence: String,
    val minors: List<String>,
    val specializations: List<String>,
)

class ScheduleViewModel(input: Schedule) : ViewModel() {

    private var _schedule = MutableStateFlow(input)
    val schedule: Schedule get() = _schedule.value

    private val _termList = schedule.termSchedule.keys.toList()
    val termList : List<String> get() = _termList

    private var _currentTerm = schedule.termSchedule.keys.first()
    val currentTerm: String get() = _currentTerm

    private val _courseList = MutableStateFlow(schedule.termSchedule.getValue(schedule.termSchedule.keys.first()))
    val courseList: MutableList<Course> get() = _courseList.value

    init {
        updateCourseList()
    }

    private fun updateCourseList() {
        _courseList.value = (schedule.termSchedule[currentTerm] ?: emptyList()) as MutableList<Course>
    }

    fun onTermSelected(term: String) {
        _currentTerm = term
        _courseList.value = schedule.termSchedule[term]!!
    }


    fun validateCourseSchedule(
        schedule: Schedule,
        context: Context
    ){
        val validateData = ValidateData(
            schedule = schedule.termSchedule,
            academicPlan = AcademicPlan(
                majors = schedule.degree,
                sequence = schedule.mySequence,
                minors = schedule.minor,
                specializations = schedule.specialization,
                startYear = schedule.year
            )
        )

        val gson = Gson()
        val jsonBody = gson.toJson(validateData)
        println(jsonBody)


        val api = RetrofitClient.create()
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody)
        val call = api.validateSchedule(requestBody)

        call.enqueue(object: Callback<ValidationResults>{
            override fun onResponse(
                call: Call<ValidationResults>,
                response: Response<ValidationResults>
            ) {
                if (response.isSuccessful) {
                    val output = response.body()

                } else {
                    println(response.message())
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ValidationResults>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                println(t.message)
                call.cancel()
            }

        })
    }
}