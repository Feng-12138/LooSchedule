package com.example.androidapp.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.androidapp.Models.Course

class ScheduleViewModel {
    val termList = listOf("1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B")

    private val courseList1A = listOf(
        Course("CS 135"), Course("MATH 135"),
        Course("MATH 137"), Course("EMLS 129R"), Course("PHYS 111")
    )
    private val courseList1B = listOf(
        Course("1B 1"), Course("1B 2"), Course("1B 3"),
        Course("1B 4"), Course("1B 5")
    )
    private val courseList2A = listOf(
        Course("2A 1"), Course("2A 2"), Course("2A 3"),
        Course("2A 4"), Course("2A 5")
    )
    private val courseList2B = listOf(
        Course("2B 1"), Course("2B 2"), Course("2B 3"),
        Course("2B 4"), Course("2B 5")
    )
    private val courseList3A = listOf(
        Course("3A 1"), Course("3A 2"), Course("3A 3"),
        Course("3A 4"), Course("3A 5")
    )
    private val courseList3B = listOf(
        Course("3B 1"), Course("3B 2"), Course("3B 3"),
        Course("3B 4"), Course("3B 5")
    )
    private val courseList4A = listOf(
        Course("4A 1"), Course("4A 2"), Course("4A 3"),
        Course("4A 4"), Course("4A 5")
    )
    private val courseList4B = listOf(
        Course("4B 1"), Course("4B 2"), Course("4B 3"),
        Course("4B 4"), Course("4B 5")
    )
    private val _schedule = mapOf("1A" to courseList1A, "1B" to courseList1B,
        "2A" to courseList2A, "2B" to courseList2B,
        "3A" to courseList3A, "3B" to courseList3B,
        "4A" to courseList4A, "4B" to courseList4B)
    val schedule: Map<String, List<Course>> get() = _schedule

    private var _currentTerm = mutableStateOf("1A")
    val currentTerm: String get() = _currentTerm.value

    private val _courseList = mutableStateOf(courseList1A)
    val courseList: List<Course> get() = _courseList.value

    private val _selectedTabIndex = mutableStateOf(0)
    val selectedTabIndex: Int get() = _selectedTabIndex.value

    init {
        updateCourseList()
    }

    private fun updateCourseList() {
        _courseList.value = schedule[currentTerm] ?: emptyList()
    }

    fun onTermSelected(term: String) {
        _currentTerm.value = term
        _selectedTabIndex.value = termList.indexOf(term)
        _courseList.value = schedule[term]!!
    }
}