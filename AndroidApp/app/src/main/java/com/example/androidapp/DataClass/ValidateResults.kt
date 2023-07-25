package com.example.androidapp.dataClass

data class ValidateResults(
    var validated: Boolean,
    var coursesValidated: List<List<ValidateResults>>
)
