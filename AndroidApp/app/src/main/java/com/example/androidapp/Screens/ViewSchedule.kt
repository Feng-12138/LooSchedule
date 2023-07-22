package com.example.androidapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseDescription(course: Course, navController: NavController) {
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
fun ViewSchedule(navController: NavController, scheduleViewModel: ScheduleViewModel){
    var currentTerm by remember { mutableStateOf(scheduleViewModel.currentTerm) }
    var pagerPage by remember { mutableStateOf(scheduleViewModel.selectedTabIndex) }
    println("pagerPage: $pagerPage")
    println("selectedtab: ${scheduleViewModel.selectedTabIndex}")
    var termList = scheduleViewModel.termList
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val pagerCourses = remember { mutableStateMapOf<Int, List<Course>>() }


    LaunchedEffect(key1 = pagerState.currentPage) {
        pagerPage = pagerState.currentPage
        scheduleViewModel.setSelectedTabIndex(pagerPage)
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
                val courses = pagerCourses[page]
                    ?: scheduleViewModel.schedule[termList[page]].also {
                        it?.let { pagerCourses[page] = it }
                    } ?: emptyList()
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
