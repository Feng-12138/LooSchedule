package com.example.androidapp.screens

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.viewModels.ScheduleViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.lang.Integer.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseDescription(course: Course, navController: NavController, schedule: Schedule, term: String, index: Int) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(10.dp,5.dp,10.dp,10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation =  10.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor =  MaterialTheme.colorScheme.primaryContainer,
        ),
        onClick = {
            navController.currentBackStackEntry?.arguments?.putParcelable("course", course)
            navController.currentBackStackEntry?.arguments?.putParcelable("schedule", schedule)
            navController.currentBackStackEntry?.arguments?.putInt("index", index)
            navController.currentBackStackEntry?.arguments?.putString("term", term)
            navController.navigate(Screen.CourseDetail.route)
        },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = course.courseID,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = course.courseName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = course.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewSchedule(navController: NavController, scheduleViewModel: ScheduleViewModel, position: Int){
    val pagerState = rememberPagerState()
    var pagerPage by remember { mutableStateOf(pagerState.currentPage) }
    var currentTerm by remember { mutableStateOf(scheduleViewModel.termList[pagerPage]) }
    scheduleViewModel.onTermSelected(currentTerm)
    var termList = scheduleViewModel.termList
    val scope = rememberCoroutineScope()
    val pagerCourses = remember { mutableStateMapOf<Int, List<Course>>() }
    var isValidated = remember { mutableStateOf(scheduleViewModel.schedule.validated) }

    LaunchedEffect(key1 = pagerState.currentPage) {
        pagerPage = pagerState.currentPage
        currentTerm = scheduleViewModel.termList[pagerPage]
        scheduleViewModel.onTermSelected(currentTerm)
    }

//    val sharedPreferences = LocalContext.current.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
//    val existingList = sharedPreferences.getStringSet("scheduleList", emptySet())?.toList()
//    val scheduleList = existingList?.map { Gson().fromJson(it, Schedule::class.java) } ?: emptyList()
//    val scheduleViewModel = ScheduleViewModel(scheduleList[0])
//
//    LaunchedEffect(scheduleViewModel) {
//        snapshotFlow { scheduleViewModel.schedule }
//            .collect { updatedSchedule ->
//                val sharedPreferences = context.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
//                val jsonUpdatedSchedule = Gson().toJson(updatedSchedule)
//                val existingSchedules = sharedPreferences.getStringSet("scheduleList", emptySet())?.toMutableList() ?: mutableListOf()
//                existingSchedules.remove(Gson().toJson(scheduleList[updatedSchedule.position]))
//                existingSchedules.add(updatedSchedule.position, jsonUpdatedSchedule)
//                val jsonList = existingSchedules.map { Gson().toJson(it) }.toSet()
//                val editor = sharedPreferences.edit()
//                editor.putStringSet("scheduleList", jsonList)
//                editor.apply()
//            }
//    }

    Surface(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            ScrollableTabRow(
                selectedTabIndex = min(termList.count(), pagerPage),
                edgePadding = 0.dp
            ) {
                scheduleViewModel.schedule.termSchedule.keys.forEachIndexed { index, tabName ->
                    Tab(
                        selected = index == pagerPage,
                        onClick = {
                            println("tabName: $tabName")
                            scheduleViewModel.onTermSelected(tabName)
                            currentTerm = scheduleViewModel.currentTerm
                            pagerPage = scheduleViewModel.schedule.termSchedule.keys.indexOf(currentTerm)
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(tabName) }
                    )
                }
            }

            Spacer(modifier = Modifier.size(10.dp))

            HorizontalPager(
                pageCount = termList.count(),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                state = pagerState,
                verticalAlignment = Alignment.Top
            ) { page ->
                val courses = pagerCourses[page]
                    ?: scheduleViewModel.schedule.termSchedule[termList[page]].also {
                        it?.let { pagerCourses[page] = it }
                    } ?: emptyList()
                CourseSchedulePage(courses = courses, navController = navController, schedule = scheduleViewModel.schedule, term = termList[page], position = position)
            }
        }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .padding(vertical = 24.dp, horizontal = 12.dp)
        ) {
            Column() {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 12.dp),
                    onClick = { /* Handle button click here */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }

                FloatingActionButton(
                    containerColor = if (isValidated.value) MaterialTheme.colorScheme.secondary else Color.Red,
                    contentColor = Color.White,
                    shape = CircleShape,
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Validate"
                    )
                }
            }
        }
    }
}


@Composable
private fun CourseSchedulePage(courses: List<Course>, navController: NavController, schedule: Schedule, term: String, position: Int) {
    if (courses.isEmpty()) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 56.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "No courses warning",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No courses selected for this term",
                    style = MaterialTheme.typography.displayMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)),
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    Box(
       modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = rememberLazyListState()
        ) {
            items(courses) { course, ->
                val index = courses.indexOf(course)
                CourseDescription(course = course, navController = navController, schedule = schedule, term = term, index = index)
            }
        }
    }
}