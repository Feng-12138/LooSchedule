package com.example.androidapp.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.androidapp.enum.ValidationResult
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.screens.Screen
import com.example.androidapp.services.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody

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

class ScheduleViewModel(input: Schedule, navController: NavController) : ViewModel() {

    private val _navController = navController
    val navController: NavController get() = _navController

    private var _schedule = MutableStateFlow(input)
    val schedule: Schedule get() = _schedule.value

    private val _termList = schedule.termSchedule.keys.toList()
    val termList : List<String> get() = _termList

    private var _currentTerm = schedule.termSchedule.keys.first()
    val currentTerm: String get() = _currentTerm

    private val _courseList = MutableStateFlow(schedule.termSchedule.getValue(schedule.termSchedule.keys.first()))
    val courseList: MutableList<Course> get() = _courseList.value

    private var _showAlert = MutableStateFlow(false)
    val showAlert: StateFlow<Boolean> get() = _showAlert

    private var _isValidated = MutableStateFlow(_schedule.value.validated)
    val isValidated: StateFlow<Boolean> get() = _isValidated

    private var _message = MutableStateFlow("hahaha")
    val message: StateFlow<String> get() = _message

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


    fun validateCourseSchedule (
        schedule: Schedule,
        context: Context,
        position: Int
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

        viewModelScope.launch {
            try {
                val response = api.validateSchedule(requestBody)
                if (response.isSuccessful) {
                    val output = response.body()
                    println(output)
                    var newSchedule = schedule
                    if (output != null) {
                        newSchedule.validated = output.overallResult
                        newSchedule.courseValidation = output.courseValidationResult
                        newSchedule.degreeValidation = output.degreeValidationResult

                        _schedule.value.validated = output.overallResult
                        _schedule.value.courseValidation = output.courseValidationResult
                        _schedule.value.degreeValidation = output.degreeValidationResult
                        _isValidated.value = output.overallResult

                        val sharedPreferences = context.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
                        val existingList = sharedPreferences.getString("scheduleList", "[]")
                        val type = object : TypeToken<MutableList<Schedule>>() {}.type
                        val scheduleList : MutableList<Schedule> = Gson().fromJson(existingList, type)
                        scheduleList.removeAt(position)
                        scheduleList.add(position, newSchedule)
                        val editor = sharedPreferences.edit()
                        val jsonList = Gson().toJson(scheduleList)
                        editor.putString("scheduleList", jsonList)
                        editor.apply()

                        if(output.overallResult){
                            _message.value = "The schedule is valid"
                        }
                        else{
                            _message.value = "The schedule is not valid because: \n"
                            output.courseValidationResult.forEach{ (key, value) ->
                                value.forEachIndexed { index, value ->
                                    if(value.isNotEmpty()){
                                        val temp = value[0]
                                        val thisCourse = _schedule.value.termSchedule[key]?.get(index)?.courseID
                                        val thisMessage = when (temp) {
                                            ValidationResult.CommunicationCourseTooLate -> " is taken too late as Communication Course.\n"
                                            ValidationResult.Success -> ""
                                            ValidationResult.TermUnavailable -> " is not available in that term.\n"
                                            ValidationResult.NotMeetMinLvl -> " is taken too early.\n"
                                            ValidationResult.NotMeetPreReq -> " has missing prerequisites.\n"
                                            ValidationResult.NotMeetCoReq -> " has missing co-requisites.\n"
                                            ValidationResult.NotMeetAntiReq -> " has anti-requisites.\n"
                                            ValidationResult.NoSuchCourse -> " does not exist.\n"
                                            ValidationResult.NoSuchMajor -> " is not available for your major.\n"
                                            else -> ""
                                        }
                                        _message.value += "In term $key $thisCourse$thisMessage"
                                    }
                                }
                            }
                        }


                        toggleAlert()
                    }
                }
                    else {
                    println(response.message())
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                println(e.message)
            }
        }
    }

    fun toggleAlert() {
        _showAlert.value = !_showAlert.value
    }

    fun addCourse(position: Int) {
        navController.currentBackStackEntry?.arguments?.putParcelable("schedule", schedule)
        navController.currentBackStackEntry?.arguments?.putString("term", currentTerm)
        navController.currentBackStackEntry?.arguments?.putInt("position", position)
        navController.currentBackStackEntry?.arguments?.putBoolean("swap", false)
        navController.currentBackStackEntry?.arguments?.putInt("index", 0)
        navController.navigate(Screen.SearchCourse.route)
    }

    fun modifySchedule(position: Int) {
        navController.currentBackStackEntry?.arguments?.putParcelable("schedule", schedule)
        navController.currentBackStackEntry?.arguments?.putInt("position", position)
        navController.navigate(Screen.ChatgptScreen.route)
    }
}