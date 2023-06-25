package com.example.androidapp.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidapp.Models.Course
import com.example.androidapp.ViewModels.ScheduleViewModel

//val termList = listOf("1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B")
//
//val courseList1A = listOf(Course("CS 135"), Course("MATH 135"),
//    Course("MATH 137"), Course("EMLS 129R"), Course("PHYS 111"))
//val courseList1B = listOf(Course("1B 1"), Course("1B 2"), Course("1B 3"),
//    Course("1B 4"), Course("1B 5"))
//val courseList2A = listOf(Course("2A 1"), Course("2A 2"), Course("2A 3"),
//    Course("2A 4"), Course("2A 5"))
//val courseList2B = listOf(Course("2B 1"), Course("2B 2"), Course("2B 3"),
//    Course("2B 4"), Course("2B 5"))
//val courseList3A = listOf(Course("3A 1"), Course("3A 2"), Course("3A 3"),
//    Course("3A 4"), Course("3A 5"))
//val courseList3B = listOf(Course("3B 1"), Course("3B 2"), Course("3B 3"),
//    Course("3B 4"), Course("3B 5"))
//val courseList4A = listOf(Course("4A 1"), Course("4A 2"), Course("4A 3"),
//    Course("4A 4"), Course("4A 5"))
//val courseList4B = listOf(Course("4B 1"), Course("4B 2"), Course("4B 3"),
//    Course("4B 4"), Course("4B 5"))
//val schedule = mapOf("1A" to courseList1A, "1B" to courseList1B,
//    "2A" to courseList2A, "2B" to courseList2B,
//    "3A" to courseList3A, "3B" to courseList3B,
//    "4A" to courseList4A, "4B" to courseList4B
//)

@Composable
private fun CourseDescription(course: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .height(150.dp)
    ) {
        Button(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray
            )
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()) {
                Text(
                    text = course,
                    Modifier.align(Alignment.TopStart),
                    color = Color.Black, fontSize = 25.sp
                )
            }
        }
    }
}

//@Composable
//fun ViewSchedule(navController: NavController, scheduleViewModel: ScheduleViewModel) {
//    Surface(
//        modifier = Modifier.padding(horizontal = 10.dp)
//    ) {
//        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
//            ScrollableTabRow(
//                selectedTabIndex = scheduleViewModel.selectedTabIndex,
//                edgePadding = 0.dp
//            ) {
//                scheduleViewModel.termList.forEachIndexed { index, term ->
//                    Tab(
//                        selected = index == scheduleViewModel.selectedTabIndex,
//                        onClick = { scheduleViewModel.onTermSelected(term) },
//                        text = { Text(term) }
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.size(10.dp))
//
//            LazyColumn {
//                items(scheduleViewModel.courseList) { course ->
//                    CourseDescription(course = course.courseID)
//                }
//            }
//        }
//    }
//}
@Composable
fun ViewSchedule(navController: NavController, scheduleViewModel: ScheduleViewModel){
    var currentTerm by remember { mutableStateOf(scheduleViewModel.currentTerm) }
    var selectedTabIndex by remember { mutableStateOf(scheduleViewModel.selectedTabIndex) }
    var listState : LazyListState = rememberLazyListState()

    Surface(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp
            ) {
                scheduleViewModel.termList.forEachIndexed { index, term ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = { scheduleViewModel.onTermSelected(term)
                                    currentTerm = scheduleViewModel.currentTerm
                                    selectedTabIndex = scheduleViewModel.selectedTabIndex },
                        text = { Text(term) }
                    )
                }
            }

            Spacer(modifier = Modifier.size(10.dp))

            LazyColumn(
                state = listState
            ) {
                items(scheduleViewModel.schedule[currentTerm]!!) { course ->
                    CourseDescription(course = course.courseID)
                }
            }
        }
    }
}

