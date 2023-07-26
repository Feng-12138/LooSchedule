package com.example.androidapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.androidapp.EverythingManager
import com.example.androidapp.enum.FieldType
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatgptScreen(schedule: Schedule){
    val terms: List<String> = schedule.termSchedule.keys.toList()
    val courses: List<String> = EverythingManager.getInstance().getCourses()?.map{ course -> "${course.courseID} ${course.courseName}" } ?: listOf()
    val context = LocalContext.current
    var selectedTerm by remember { mutableStateOf(terms.first()) }
    var expanded by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ){
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            Text(
                "Select Current Term: ",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp)
            )
            Box(Modifier.padding(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    },
                ) {
                    TextField(
                        value = selectedTerm,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        terms.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    TODO()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}