package com.example.androidapp.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.androidapp.enum.ValidationResults
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.services.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class ValidateData(
    val schedule: MutableMap<String, MutableList<Course>>,
    val degree: String,
    val sequence: String
)

class ScheduleViewModel(input: Schedule) : ViewModel() {

    private val _schedule = MutableStateFlow(input)
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

    var onCourseDeleted: ((String, Course) -> Unit)? = null

    fun deleteCourse(term: String, course: Course) {
        val updatedCourseList = schedule.termSchedule[term]?.filterNot { it == course } ?: emptyList()
        schedule.termSchedule[term] = updatedCourseList as MutableList<Course>
        updateCourseList()

        // Notify external listeners about the course deletion
        onCourseDeleted?.invoke(term, course)
    }

    private fun validateCourseSchedule(
        context: Context,
        navController: NavController,
        schedule: Schedule
    ){
        val validateData = ValidateData(
            schedule = schedule.termSchedule,
            degree = schedule.degree[0],
            sequence = schedule.sequence
        )

        val gson = Gson()
        val jsonBody = gson.toJson(validateData)
        println(jsonBody)

        val api = RetrofitClient.create()
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody)
        val call = api.validateSchedule(requestBody)

        call.enqueue(object: Callback<Map<String, MutableSet<ValidationResults>>>{
            override fun onResponse(
                call: Call<Map<String, MutableSet<ValidationResults>>>,
                response: Response<Map<String, MutableSet<ValidationResults>>>
            ) {
                if (response.isSuccessful) {
                    val output = response.body()

                } else {
                    println(response.message())
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<Map<String, MutableSet<ValidationResults>>>,
                t: Throwable
            ) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                println(t.message)
                call.cancel()
            }

        })
    }
}