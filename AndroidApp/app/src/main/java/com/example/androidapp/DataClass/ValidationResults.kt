<<<<<<< HEAD
=======
package com.example.androidapp.dataClass

import com.example.androidapp.enum.OverallValidationResult
import com.example.androidapp.enum.ValidationResult

data class ValidationResults(
    var overallResult: Boolean,
    var courseValidationResult: Map<String, MutableList<List<ValidationResult?>>>,
    var degreeValidationResult: List<OverallValidationResult?>
)
>>>>>>> 7584a9d (validate api works)
