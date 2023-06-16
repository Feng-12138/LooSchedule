package com.example.androidapp.Screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

val termList = listOf("1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B")

val courseList = listOf("CS 135", "MATH 135", "MATH 137", "EMLS 129R", "PHYS 111")

@Composable
private fun TermButton(term: String) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .height(50.dp)
            .width(100.dp)
    ) {
        Button(
            onClick = { /*TODO*/ },
        ) {
            Text(text = term)
        }
    }
}

@Composable
private fun CourseDescription(course: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .height(150.dp)
    ) {
        TextButton(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray
            )
        ) {
            Text(
                text = course, color = Color.Black, fontSize = 25.sp, textAlign = TextAlign.Left
            )

//            Text(text = "Description", textAlign = TextAlign.Right)
        }
    }
}

@Composable
fun ViewSchedule(navController: NavController) {
    Surface(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                for (term in termList) {
                    TermButton(term = term)
                }
            }

            Spacer(modifier = Modifier.size(20.dp))

            // Course schedule for the each term
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                for (course in courseList) {
                    CourseDescription(course = course)
                }
            }
        }
    }
}

