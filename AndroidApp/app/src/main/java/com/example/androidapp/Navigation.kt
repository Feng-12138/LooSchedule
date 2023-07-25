package com.example.androidapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.screens.AboutScreen
import com.example.androidapp.screens.ApiPlayGround
import com.example.androidapp.screens.CourseScreen
import com.example.androidapp.screens.ErrorScreen
import com.example.androidapp.screens.GetStartScreen
import com.example.androidapp.screens.HistoryScreen
import com.example.androidapp.screens.MainScreen
import com.example.androidapp.screens.Screen
import com.example.androidapp.screens.SearchCourseScreen
import com.example.androidapp.screens.SelectDegree
import com.example.androidapp.screens.ViewSchedule
import com.example.androidapp.viewModels.ScheduleViewModel
import com.example.androidapp.viewModels.SelectDegreeVM
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigation(){
    val navController = rememberNavController()
    val selectDegreeVM = SelectDegreeVM(LocalContext.current, navController = navController)

    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
        composable(route = Screen.SearchCourse.route){
            MainScreen (navController = navController, name = "SearchCourse") {
                val schedule = navController.previousBackStackEntry?.arguments?.getParcelable("schedule", Schedule::class.java)
                val term = navController.previousBackStackEntry?.arguments?.getString("term")
                val position = navController.previousBackStackEntry?.arguments?.getInt("position")
                if (term != null && schedule != null && position != null) {
                    SearchCourseScreen(
                        navController = navController,
                        term = term,
                        schedule = schedule,
                        position = position)
                }
            }
        }
        composable(route = Screen.About.route){
            MainScreen (navController = navController, name = "About") { AboutScreen() }
        }
        composable(route = Screen.MainScreen.route){
            MainScreen (navController = navController, name = "LooSchedule") { GetStartScreen(navController = navController) }
        }
        composable(route = Screen.SelectDegree.route){
            MainScreen (navController = navController, name = "Create Schedule") { SelectDegree(navController = navController,
                selectDegreeVM = selectDegreeVM) }
        }

        composable(route = Screen.ViewSchedule.route){
            val sharedPreferences = LocalContext.current.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
            val existingList = sharedPreferences.getString("scheduleList", "[]")
            val type = object : TypeToken<MutableList<Schedule>>() {}.type
            val scheduleList : MutableList<Schedule> = Gson().fromJson(existingList, type)
            if(scheduleList.isEmpty()){
                MainScreen(navController = navController, name = "Current Schedule") {
                    ErrorScreen(navController = navController)
                }
            }
            else{
                MainScreen (navController = navController, name = "Current Schedule") { ViewSchedule(navController = navController,
                    scheduleViewModel = ScheduleViewModel(scheduleList[0]), position = 0) }
            }
        }

        composable(route = Screen.ApiPlayground.route){
            MainScreen (navController = navController, name = "ApiPlayground") { ApiPlayGround(navController = navController) }
        }

        composable(route = Screen.CourseDetail.route) {
            val course = navController.previousBackStackEntry?.arguments?.getParcelable("course", Course::class.java)
            val schedule = navController.previousBackStackEntry?.arguments?.getParcelable("schedule", Schedule::class.java)
            val index = navController.previousBackStackEntry?.arguments?.getInt("index")
            val term = navController.previousBackStackEntry?.arguments?.getString("term")
            val position = navController.previousBackStackEntry?.arguments?.getInt("position")
            MainScreen(navController = navController, name = "") {
                if (index != null && term != null && schedule != null && position != null) {
                    CourseScreen(
                        course = course,
                        navController = navController,
                        index = index,
                        term = term,
                        schedule = schedule,
                        position = position
                    )
                }
            }
        }

        composable(route = Screen.ScheduleHistory.route){
            val sharedPreferences = LocalContext.current.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
            val existingList = sharedPreferences.getString("scheduleList", "[]")
            val type = object : TypeToken<MutableList<Schedule>>() {}.type
            val scheduleList : MutableList<Schedule> = Gson().fromJson(existingList, type)
            MainScreen (navController = navController, name = "History") { HistoryScreen(scheduleList, navController = navController) }
        }

        composable(route = Screen.OldSchedule.route) {
            val schedule = navController.previousBackStackEntry?.arguments?.getParcelable("schedule", Schedule::class.java)
            val index = navController.previousBackStackEntry?.arguments?.getInt("index")
            val listSize = navController.previousBackStackEntry?.arguments?.getInt("listSize")
            if(schedule != null){
                if (listSize != null) {
                    MainScreen(navController = navController, name = "Schedule ${listSize - index!!}") {
                        ViewSchedule(navController = navController,
                            scheduleViewModel = ScheduleViewModel(schedule), position = index
                        )
                    }
                }
            }
        }
    }
}
