package com.example.androidapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidapp.R
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule

@Composable
fun HistoryScreen(scheduleItems: List<Schedule>, navController: NavController) {
    Image(
        painter = painterResource(id = R.drawable.hist2),
        contentDescription = "",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(scheduleItems) { index, item ->
            CourseScheduleItem(
                scheduleItem = item,
                navController = navController,
                index = index,
                listSize = scheduleItems.size
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScheduleItem(scheduleItem: Schedule, navController: NavController, index : Int, listSize: Int) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .width(288.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation =  5.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor =  Color.White,
        ),
        shape = RoundedCornerShape(30.dp),
        onClick = {
            navController.currentBackStackEntry?.arguments?.putParcelable("schedule", scheduleItem)
            navController.currentBackStackEntry?.arguments?.putInt("index", index)
            navController.currentBackStackEntry?.arguments?.putInt("listSize", listSize)
            navController.navigate(Screen.OldSchedule.route)
        },
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = scheduleItem.myDegree.last(),
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Time: ${scheduleItem.time}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    )
}