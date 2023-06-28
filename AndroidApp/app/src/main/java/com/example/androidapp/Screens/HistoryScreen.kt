package com.example.androidapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule

@Composable
fun HistoryScreen(scheduleItems: List<Schedule>, navController: NavController) {
    LazyColumn {
        itemsIndexed(scheduleItems) { index, item ->
            CourseScheduleItem(scheduleItem = item, navController = navController, index = index, listSize = scheduleItems.size)
        }
    }
}

@Composable
fun CourseScheduleItem(scheduleItem: Schedule, navController: NavController, index : Int, listSize: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 20.dp)
            .height(60.dp)
    ) {
        Button(
            onClick = {
                navController.currentBackStackEntry?.arguments?.putParcelable("schedule", scheduleItem)
                navController.currentBackStackEntry?.arguments?.putInt("index", index)
                navController.navigate(Screen.OldSchedule.route)
            },
            shape = RoundedCornerShape(0.dp),
        ) {
            Column() {
                Text(text = "Schedule: ${listSize - index}")
                Text(text = "Time: ${scheduleItem.time}")
            }

        }
    }
}