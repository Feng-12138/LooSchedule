package com.example.androidapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidapp.models.Course
import com.example.androidapp.viewModels.ScheduleViewModel
import kotlinx.coroutines.launch
import java.lang.Integer.min

@Composable
private fun CourseDescription(course: Course, navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .height(150.dp)
    ) {
        Button(
            onClick = {
                navController.currentBackStackEntry?.arguments?.putParcelable("course", course)
                navController.navigate(Screen.CourseDetail.route)
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray
            )
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()) {
                Text(
                    text = course.courseID,
                    Modifier.align(Alignment.TopStart),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.Black
                )
                Text(
                    text = course.courseName,
                    Modifier.align(Alignment.CenterStart),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Text(
                    text = course.description,
                    Modifier.align(Alignment.BottomStart),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    color = Color.Black
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewSchedule(navController: NavController, scheduleViewModel: ScheduleViewModel){
    var currentTerm by remember { mutableStateOf(scheduleViewModel.currentTerm) }
    var pagerPage by remember { mutableStateOf(scheduleViewModel.selectedTabIndex) }
    var termList = scheduleViewModel.termList
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val pagerCourses = remember { mutableStateListOf<List<Course>>() }

    LaunchedEffect(key1 = scheduleViewModel.selectedTabIndex) {
        pagerPage = scheduleViewModel.selectedTabIndex
    }

    Surface(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            ScrollableTabRow(
                selectedTabIndex = min(termList.count(), pagerPage),
                edgePadding = 0.dp
            ) {
                scheduleViewModel.schedule.keys.forEachIndexed { index, tabName ->
                    Tab(
                        selected = index == pagerPage,
                        onClick = { scheduleViewModel.onTermSelected(tabName)
                                    currentTerm = scheduleViewModel.currentTerm
                                    pagerPage = scheduleViewModel.selectedTabIndex
                                    scope.launch { pagerState.animateScrollToPage(index) } },
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
                val courses = pagerCourses.getOrElse(page) {
                    val term = termList[page]
                    val courses = scheduleViewModel.schedule[term] ?: emptyList()
                    pagerCourses.add(page, courses)
                    courses
                }
                CourseSchedulePage(courses = courses, navController = navController)
            }
        }
    }
}

@Composable
private fun CourseSchedulePage(courses: List<Course>, navController: NavController) {
    if (courses.isEmpty()) return

    LazyColumn(
        state = rememberLazyListState()
    ) {
        items(courses) { course ->
            CourseDescription(course = course, navController = navController)
        }
    }
}
