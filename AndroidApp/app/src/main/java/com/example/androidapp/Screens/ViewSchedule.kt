package com.example.androidapp.Screens

import android.widget.Button
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.Screen

val termList = listOf("1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B")

val courseList = listOf("CS 135", "MATH 135", "MATH 137", "EMLS 129R", "PHYS 111")

@Composable
private fun TermButton(term : String) {
    Surface(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Button(onClick = { /*TODO*/ }) {
            Text(text = term)
        }
    }
}

@Composable
private fun CourseDescription(course : String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        TextButton(onClick = { /*TODO*/ }) {
            Text(text = course)
        }
    }
}

@Composable
fun ViewSchedule(navController: NavController){
    Surface(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)){
            Row(modifier = Modifier
                .padding(0.dp)
                .horizontalScroll(rememberScrollState())) {
                for (term in termList) {
                    TermButton(term = term)
                }
            }

            // Course schedule for the each term
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                for (course in courseList) {
                    CourseDescription(course = course)
                }
            }
        }
    }
}

