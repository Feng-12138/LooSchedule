package com.example.androidapp.dataClass

import com.example.androidapp.enum.OverallValidationResult
import com.example.androidapp.enum.ValidateResults

data class ValidationResults(
    var validated: Boolean,
    var validatedCourses: Map<String, MutableList<List<ValidateResults>>>,
    var validatedDegree: List<OverallValidationResult>
)
