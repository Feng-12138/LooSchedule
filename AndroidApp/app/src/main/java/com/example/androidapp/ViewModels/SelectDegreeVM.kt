package com.example.androidapp.ViewModels

import androidx.lifecycle.ViewModel
import com.example.androidapp.DataClass.MyDegree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectDegreeVM : ViewModel() {
    private val _uiState = MutableStateFlow(MyDegree())
    val uiState: StateFlow<MyDegree> = _uiState.asStateFlow()

}