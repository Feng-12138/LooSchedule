package com.example.androidapp.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.EverythingManager
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCourseScreen(navController: NavController, term: String, schedule: Schedule, position: Int, swap: Boolean, courseIndex: Int){
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val courses: List<Course>? = EverythingManager.getInstance().getCourses()
    Column() {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = text,
            onQueryChange = { text = it},
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(text = "Find Course") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if(active){
                    Icon(
                        modifier = Modifier.clickable{
                              if(text.isNotEmpty()){
                                  text = ""
                              }else{
                                  active = false
                              }
                        },
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        ){
        }
        if (courses != null) {
            CourseList(navController = navController, courseList = courses, searchQuery = text, term = term, schedule = schedule, position = position, swap = swap, courseIndex = courseIndex)
        }
        else{
            Text("Nothing")
        }
    }
}


@Composable
fun CourseList(navController: NavController, courseList: List<Course>, searchQuery: String, term: String, schedule: Schedule, position: Int, swap: Boolean, courseIndex: Int) {
    var filteredCourses = remember { mutableStateListOf<Course>() }
    var showAlert by remember { mutableStateOf(false) }
    var selectedCourse = remember { mutableStateListOf<Course>() }
    val context = LocalContext.current
    LaunchedEffect(searchQuery) {
        filteredCourses.clear()
        if (searchQuery.isNotEmpty()) {
            filteredCourses.addAll(courseList.filter { course ->
                containsWithOrder(course.courseID.lowercase() + course.courseName.lowercase(), searchQuery.lowercase())
            })
        } else {
            filteredCourses.addAll(courseList)
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(filteredCourses) { course ->
            Surface(
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = {
                    selectedCourse.add(course)
                    showAlert = true
                }
            ) {
                Text(
                    text = course.courseID + "  " + course.courseName,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
    if (showAlert) {
        AlertDialog(
            onDismissRequest = {
                showAlert = !showAlert
            },
            title = {
                Text(text = "Warning")
            },
            text = {
                if(swap && selectedCourse.isNotEmpty()){
                    Text("Are you sure you want to swap with this course?")
                }
                else{
                    Text("Are you sure you want to add this course?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAlert = !showAlert

                        if(swap){
                            var updatedSchedule = schedule
                            updatedSchedule.termSchedule[term]?.add(selectedCourse[0])
                            updatedSchedule.termSchedule[term]?.removeAt(courseIndex)
                            selectedCourse.clear()
                            updatedSchedule.time = Date()
                            updatedSchedule.validated = false
                            val sharedPreferences = context.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
                            val existingList = sharedPreferences.getString("scheduleList", "[]")
                            val type = object : TypeToken<MutableList<Schedule>>() {}.type
                            val scheduleList : MutableList<Schedule> = Gson().fromJson(existingList, type)
                            scheduleList.removeAt(position)
                            scheduleList.add(0, updatedSchedule)

                            val jsonList = Gson().toJson(scheduleList)
                            val editor = sharedPreferences.edit()
                            editor.putString("scheduleList", jsonList)
                            editor.apply()

                            navController.navigate(route = Screen.ViewSchedule.route)
                        }
                        else{
                            var updatedSchedule = schedule
                            updatedSchedule.termSchedule[term]?.add(selectedCourse[0])
                            selectedCourse.clear()
                            updatedSchedule.time = Date()
                            updatedSchedule.validated = false
                            val sharedPreferences = context.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
                            val existingList = sharedPreferences.getString("scheduleList", "[]")
                            val type = object : TypeToken<MutableList<Schedule>>() {}.type
                            val scheduleList : MutableList<Schedule> = Gson().fromJson(existingList, type)
                            scheduleList.removeAt(position)
                            scheduleList.add(0, updatedSchedule)

                            val jsonList = Gson().toJson(scheduleList)
                            val editor = sharedPreferences.edit()
                            editor.putString("scheduleList", jsonList)
                            editor.apply()

                            navController.navigate(route = Screen.ViewSchedule.route)
                        }
                    }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showAlert = !showAlert
                    }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun containsWithOrder(text: String, query: String): Boolean {
    var j = 0
    for (i in text.indices) {
        if (j < query.length && text[i] == query[j]) {
            j++
        }
    }
    return j == query.length
}
