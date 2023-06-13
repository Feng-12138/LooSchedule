package com.example.androidapp.Screens

import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.Screen

val termList = listOf("1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B")

val selectedButton = mutableStateOf("")

@Composable
fun ViewSchedule(navController: NavController){
    Column{
        // Terms
//        Row(
//
//        )

        Text(
            text = "View Schedule"
        )

        Text(
            text = "View Schedule2"
        )
    }
}

