package com.example.androidapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.models.Course
import com.example.androidapp.screens.ApiPlayGround
import com.example.androidapp.screens.CourseScreen
import com.example.androidapp.screens.GetStartScreen
import com.example.androidapp.screens.MainScreen
import com.example.androidapp.screens.Screen
import com.example.androidapp.screens.SelectDegree
import com.example.androidapp.screens.ViewSchedule
import com.example.androidapp.viewModels.ScheduleViewModel
import com.example.androidapp.viewModels.SelectDegreeVM

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigation(){
    val navController = rememberNavController()
    val selectDegreeVM = SelectDegreeVM()
    val scheduleViewModel = ScheduleViewModel()

    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
        composable(route = Screen.MainScreen.route){
            MainScreen (navController = navController) { GetStartScreen(navController = navController) }
        }
        composable(route = Screen.SelectDegree.route){
            MainScreen (navController = navController) { SelectDegree(navController = navController,
                selectDegreeVM = selectDegreeVM) }
        }
        composable(route = Screen.ViewSchedule.route){
            MainScreen (navController = navController) { ViewSchedule(navController = navController,
                scheduleViewModel = scheduleViewModel) }
        }
        composable(route = Screen.ApiPlayground.route){
            MainScreen (navController = navController) { ApiPlayGround(navController = navController) }
        }
        composable(route = Screen.CourseDetail.route) {
            val course = navController.previousBackStackEntry?.arguments?.getParcelable("course", Course::class.java)
            println(course.toString())
            MainScreen(navController = navController) {
                CourseScreen(course)
            }
        }
    }
}
