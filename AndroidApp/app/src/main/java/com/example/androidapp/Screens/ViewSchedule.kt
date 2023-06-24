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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

val termList = listOf("1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B")

val courseList1A = listOf("CS 135", "MATH 135", "MATH 137", "EMLS 129R", "PHYS 111")
val courseList1B = listOf("1B 1", "1B 2", "1B 3", "1B 4", "1B 5")
val courseList2A = listOf("2A 1", "2A 2", "2A 3", "2A 4", "2A 5")
val courseList2B = listOf("2B 1", "2B 2", "2B 3", "2B 4", "2B 5")
val courseList3A = listOf("3A 1", "3A 2", "3A 3", "3A 4", "3A 5")
val courseList3B = listOf("3B 1", "3B 2", "3B 3", "3B 4", "3B 5")
val courseList4A = listOf("4A 1", "4A 2", "4A 3", "4A 4", "4A 5")
val courseList4B = listOf("4B 1", "4B 2", "4B 3", "4B 4", "4B 5")
val schedule = mapOf("1A" to courseList1A, "1B" to courseList1B,
    "2A" to courseList2A, "2B" to courseList2B,
    "3A" to courseList3A, "3B" to courseList3B,
    "4A" to courseList4A, "4B" to courseList4B)

@Composable
private fun TermButton(term : String, updateTerm : () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .height(50.dp)
            .width(100.dp)
    ) {
        Button(
            onClick = updateTerm
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
fun ViewSchedule(navController: NavController){
    var currentTerm by remember { mutableStateOf("1B") }
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
                    TermButton(term = term, updateTerm = {currentTerm = term})
                }
            }

            Spacer(modifier = Modifier.size(20.dp))

            // Course schedule for the each term
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                for (course in schedule[currentTerm]!!) {
                    CourseDescription(course = course)
                }
            }
        }
    }
}