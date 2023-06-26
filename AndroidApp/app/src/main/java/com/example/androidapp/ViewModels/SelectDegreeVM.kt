package com.example.androidapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.androidapp.dataClass.MyDegree
import com.example.androidapp.enum.CoopSequence
import com.example.androidapp.enum.MyMajor
import com.example.androidapp.enum.MyMinor
import com.example.androidapp.enum.MySpecialization
import com.example.androidapp.enum.MyYear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectDegreeVM : ViewModel() {
    private val _uiState = MutableStateFlow(MyDegree())
    val uiState: StateFlow<MyDegree> = _uiState.asStateFlow()

    fun generateSchedule(
        major: MyMajor,
        year: MyYear,
        sequence: CoopSequence,
        minor: MyMinor,
        specialization: MySpecialization
    ) {
        println(major.toString())
        println(year.toString())
        println(sequence.toString())
        println(minor.toString())
        println(specialization.toString())
        if (major.toString() == "Select your degree") {

        }
    }
}