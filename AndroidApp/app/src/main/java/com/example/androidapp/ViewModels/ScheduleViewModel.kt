package com.example.androidapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import kotlinx.coroutines.flow.MutableStateFlow

class ScheduleViewModel(input: Schedule) : ViewModel() {

    private val _schedule = input
    val schedule: Map<String, List<Course>> get() = _schedule.term

    private val _currentTerm = MutableStateFlow(schedule.keys.first())
    val currentTerm: String get() = _currentTerm.value

    private val _courseList = MutableStateFlow(schedule.getValue(schedule.keys.first()))
    val courseList: List<Course> get() = _courseList.value

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: Int get() = _selectedTabIndex.value

    init {
        updateCourseList()
    }

    private fun updateCourseList() {
        _courseList.value = schedule[currentTerm] ?: emptyList()
    }

    fun onTermSelected(term: String) {
        _currentTerm.value = term
        _selectedTabIndex.value = schedule.keys.indexOf(term)
        _courseList.value = schedule[term]!!
    }
}