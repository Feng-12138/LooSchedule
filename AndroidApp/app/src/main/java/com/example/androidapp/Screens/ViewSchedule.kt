package com.example.androidapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.R
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.viewModels.ScheduleViewModel
import kotlinx.coroutines.launch
import java.lang.Integer.min

@Composable
fun getCourseColor(color: String?): Painter {
    if (color == null) {
        return painterResource(id = R.drawable.card_recommend)
    }

    return when (color) {
        "red" -> painterResource(id = R.drawable.card_red)
        "green" -> painterResource(id = R.drawable.card_green)
        "blue" -> painterResource(id = R.drawable.card_blue)
        else -> painterResource(id = R.drawable.card_recommend) // default background if recommend
    }
}

@Composable
fun getSelectedColor(selected: Boolean): Pair<Color, Color> {
    if (selected) {
        val textColor = Color.White
        val backGroundColor = Color(130, 156, 173)
        return Pair(backGroundColor, textColor)
    }

    return Pair(Color.White, Color(130, 156, 173))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseDescription(course: Course, navController: NavController, schedule: Schedule, term: String, index: Int, position: Int) {
    println(course.courseName)
    println(course.color)
    Card(
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier.padding(10.dp,5.dp,10.dp,10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation =  10.dp,
        ),
        onClick = {
            navController.currentBackStackEntry?.arguments?.putParcelable("course", course)
            navController.currentBackStackEntry?.arguments?.putParcelable("schedule", schedule)
            navController.currentBackStackEntry?.arguments?.putInt("index", index)
            navController.currentBackStackEntry?.arguments?.putString("term", term)
            navController.currentBackStackEntry?.arguments?.putInt("position", position)
            navController.navigate(Screen.CourseDetail.route)
        },
    ) {
        Box(modifier = Modifier.height(140.dp)) {
            Image(
                painter = getCourseColor(color = course.color),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

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

    val context = LocalContext.current
    val isValidated = scheduleViewModel.isValidated.collectAsState().value

    LaunchedEffect(key1 = pagerState.currentPage) {
        pagerPage = pagerState.currentPage
        currentTerm = scheduleViewModel.termList[pagerPage]
        scheduleViewModel.onTermSelected(currentTerm)
    }

    Surface(
        modifier = Modifier
            .padding(horizontal = 0.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.current_schedule),
            contentDescription = "current schedule",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.padding(horizontal = 10.dp)) {

            ScrollableTabRow(
                selectedTabIndex = min(termList.count(), pagerPage),
                edgePadding = 0.dp,
                containerColor = Color.White
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
                        modifier = Modifier.background(color = getSelectedColor(selected = index == pagerPage).first),
                        text = { Text(text = tabName, color = getSelectedColor(selected = index == pagerPage).second) }
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
                CourseSchedulePage(courses = courses, navController = navController, schedule = scheduleViewModel.schedule, term = termList[page], position = position, scheduleViewModel = scheduleViewModel)
            }
        }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .padding(vertical = 24.dp, horizontal = 8.dp)
                .fillMaxSize(),
        ) {
            Column() {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 12.dp),
                    onClick = {
                        scheduleViewModel.modifySchedule(position)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Filter"
                    )
                }

                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 12.dp),
                    onClick = {
                        scheduleViewModel.addCourse(position)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }

                FloatingActionButton(
                    containerColor = if (isValidated) MaterialTheme.colorScheme.secondary else Color.Red,
                    contentColor = Color.White,
                    shape = CircleShape,
                    onClick = {
                        scheduleViewModel.validateCourseSchedule(schedule = scheduleViewModel.schedule, context = context, position = position)
                    }
                ) {
                    if(isValidated){
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Validate"
                        )
                    }
                    else {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Validate"
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun CourseSchedulePage(courses: List<Course>, navController: NavController, schedule: Schedule, term: String, position: Int, scheduleViewModel: ScheduleViewModel) {
    val context = LocalContext.current
    val showAlert = scheduleViewModel.showAlert.collectAsState().value
    val isValidated = scheduleViewModel.isValidated.collectAsState().value
    val message = scheduleViewModel.message.collectAsState().value
    if (courses.isEmpty()) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(88.dp))

                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "No courses warning",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No courses selected for this term",
                    style = MaterialTheme.typography.displayMedium.copy(color = Color.White),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showAlert) {
        AlertDialog(
            onDismissRequest = {
                scheduleViewModel.toggleAlert()
            },
            title = {
                Text(text = "Validation Result")
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                Button(
                    onClick = {
                        scheduleViewModel.toggleAlert()
                    }) {
                    Text("Confirm")
                }
            }
        )
    }

    Box(
       modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp)
        ) {
            items(courses) { course, ->
                val index = courses.indexOf(course)
                CourseDescription(course = course, navController = navController, schedule = schedule, term = term, index = index, position = position)
            }
        }
    }
}