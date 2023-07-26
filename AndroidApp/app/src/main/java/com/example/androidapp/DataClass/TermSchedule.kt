package com.example.androidapp.dataClass

import com.example.androidapp.models.Course

data class TermSchedule(
    val schedule: Map<String, List<Course>>,
    val successRecCount: Int = 0,
    val recommendedCourses: List<String> = listOf(),
    val message: String? = ""
)
