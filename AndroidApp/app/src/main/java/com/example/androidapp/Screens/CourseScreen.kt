package com.example.androidapp.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidapp.R

import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.viewModels.ScheduleViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

@Composable
fun CourseScreen(
    course: Course?,
    navController: NavController,
    index: Int,
    term: String,
    schedule: Schedule,
    position: Int
) {
    val context = LocalContext.current
    Box(
        Modifier
            .fillMaxWidth()
            .background(color = Color(107, 119, 151))
            .fillMaxHeight(),
    )
    {
//        Image(
//            painter = painterResource(id = R.drawable.course_card),
//            contentDescription = "",
//            contentScale = ContentScale.FillBounds,
//            modifier = Modifier.fillMaxSize()
//        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
        ) {
            course?.courseID?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.White
                )
            }

            course?.courseName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = Color.White
                )
            }

            if (course != null) {
                RatingBar(rating = course.easyRating)
                Spacer(modifier = Modifier.size(16.dp))
                Description(description = course.description)
            }
        }

        SwapAndDelete(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            onSwap = {
                course?.let {
                    navController.currentBackStackEntry?.arguments?.putParcelable("schedule", schedule)
                    navController.currentBackStackEntry?.arguments?.putString("term", term)
                    navController.currentBackStackEntry?.arguments?.putInt("position", position)
                    navController.currentBackStackEntry?.arguments?.putBoolean("swap", true)
                    navController.currentBackStackEntry?.arguments?.putInt("index", index)
                    navController.navigate(Screen.SearchCourse.route)
                }

            },
            onDelete = {
                course?.let {
                    var updatedSchedule = schedule
                    updatedSchedule.termSchedule[term]?.removeAt(index)
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
            }
        )
    }
}

@Composable
fun RatingBar(rating: Float) {
    Row {
        Text(
            text = "Rating",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp),
            color = Color.White
        )

        Spacer(modifier = Modifier.size(12.dp))

        Row {
            repeat(5) {index ->
                if (index < (rating * 5).toInt()){
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(top = 5.dp),
                        tint = Color.White
                    )
                }

            }
        }
    }
}

@Composable
fun Description(description: String) {
    Column {
        Row {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "description",
                tint = Color.White,
                modifier = Modifier.padding(top = 7.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "description:",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 0.dp),
                color = Color.White
            )
        }
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp),
            color = Color.White
        )
    }
}


@Composable
fun SwapAndDelete(modifier: Modifier = Modifier, onSwap: () -> Unit, onDelete: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(90, 118, 142)
                ),
                modifier = Modifier.height(56.dp),
                onClick = { onSwap() }
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Swap"
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text("Swap Course")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(90, 118, 142)
                ),
                modifier = Modifier.height(56.dp),
                onClick = { onDelete() }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text("Delete Course")
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}