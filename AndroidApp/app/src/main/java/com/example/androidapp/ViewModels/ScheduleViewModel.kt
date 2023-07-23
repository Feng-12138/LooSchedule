package com.example.androidapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScheduleViewModel(input: Schedule) : ViewModel() {

    private val _schedule = MutableStateFlow(input.termSchedule)
    val schedule: MutableMap<String, List<Course>> get() = _schedule.value

    private val _termList = schedule.keys.toList()
    val termList : List<String> get() = _termList

    private var _currentTerm = schedule.keys.first()
    val currentTerm: String get() = _currentTerm

    private val _courseList = MutableStateFlow(schedule.getValue(schedule.keys.first()))
    val courseList: List<Course> get() = _courseList.value

    init {
        updateCourseList()
    }

    private fun updateCourseList() {
        _courseList.value = schedule[currentTerm] ?: emptyList()
    }

    fun onTermSelected(term: String) {
        _currentTerm = term
        _courseList.value = schedule[term]!!
    }

    var onCourseDeleted: ((String, Course) -> Unit)? = null

    fun deleteCourse(term: String, course: Course) {
        val updatedCourseList = schedule[term]?.filterNot { it == course } ?: emptyList()
        schedule[term] = updatedCourseList
        updateCourseList()

        // Notify external listeners about the course deletion
        onCourseDeleted?.invoke(term, course)
    }
}