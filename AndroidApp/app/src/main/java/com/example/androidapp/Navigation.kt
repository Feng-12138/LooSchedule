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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigation(){
    val navController = rememberNavController()
    val selectDegreeVM = SelectDegreeVM(LocalContext.current, navController = navController)

    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
        composable(route = Screen.SearchCourse.route){
            MainScreen (navController = navController, name = "SearchCourse") { SearchCourseScreen(navController = navController) }
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
            val existingList = sharedPreferences.getStringSet("scheduleList", emptySet())?.toList()
            val scheduleList = existingList?.map { Gson().fromJson(it, Schedule::class.java) } ?: emptyList()
            if(scheduleList.isEmpty()){
                MainScreen(navController = navController, name = "Current Schedule") {
                    ErrorScreen(navController = navController)
                }
            }
            else{
                val scheduleViewModel = ScheduleViewModel(scheduleList[0])
                MainScreen (navController = navController, name = "Current Schedule") { ViewSchedule(navController = navController,
                    scheduleViewModel = scheduleViewModel) }
            }

        }
        composable(route = Screen.ApiPlayground.route){
            MainScreen (navController = navController, name = "ApiPlayground") { ApiPlayGround(navController = navController) }
        }
        composable(route = Screen.CourseDetail.route) {
            val course = navController.previousBackStackEntry?.arguments?.getParcelable("course", Course::class.java)
            MainScreen(navController = navController, name = "") {
                CourseScreen(course)
            }
        }
        composable(route = Screen.ScheduleHistory.route){
            val sharedPreferences = LocalContext.current.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
            val existingList = sharedPreferences.getStringSet("scheduleList", emptySet())?.toList()
            val scheduleList = existingList?.map { Gson().fromJson(it, Schedule::class.java) } ?: emptyList()

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
                            scheduleViewModel = ScheduleViewModel(schedule))
                    }
                }
            }
        }
    }
}
