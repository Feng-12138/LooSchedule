package com.example.androidapp.ViewModels

import androidx.lifecycle.ViewModel
import com.example.androidapp.DataClass.MyDegree
import com.example.androidapp.Enum.CoopSequence
import com.example.androidapp.Enum.MyMajor
import com.example.androidapp.Enum.MyMinor
import com.example.androidapp.Enum.MySpecialization
import com.example.androidapp.Enum.MyYear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectDegreeVM : ViewModel() {
    private val _uiState = MutableStateFlow(MyDegree())
    val uiState: StateFlow<MyDegree> = _uiState.asStateFlow()

}