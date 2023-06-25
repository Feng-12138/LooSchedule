package com.example.androidapp.Screens

import android.net.Uri
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
import androidx.navigation.NavController
import com.example.androidapp.ViewModels.ScheduleViewModel
import com.google.gson.Gson

@Composable
private fun CourseDescription(course: String, navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .height(150.dp)
    ) {
        Button(
            onClick = {
                val json = Uri.encode(Gson().toJson(course))
                val route = "${Screen.CourseDetail.route}/$json"
                navController.navigate(route)
            },
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
                    CourseDescription(course = course.courseID, navController = navController)
                }
            }
        }
    }
}