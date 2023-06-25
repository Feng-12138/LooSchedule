package com.example.androidapp

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidapp.Models.Course
import com.example.androidapp.Screens.ApiPlayGround
import com.example.androidapp.Screens.CourseScreen
import com.example.androidapp.Screens.Greeting
import com.example.androidapp.Screens.MainScreen
import com.example.androidapp.Screens.Screen
import com.example.androidapp.Screens.SelectDegree
import com.example.androidapp.Screens.ViewSchedule
import com.example.androidapp.ViewModels.SelectDegreeVM
import java.io.Serializable
import com.example.androidapp.ViewModels.ScheduleViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigation(){
    val navController = rememberNavController()
    val selectDegreeVM = SelectDegreeVM()
    val scheduleViewModel = ScheduleViewModel()

    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
        composable(route = Screen.MainScreen.route){
            MainScreen (navController = navController) { Greeting(navController = navController) }
        }
        composable(route = Screen.SelectDegree.route){
            MainScreen (navController = navController) { SelectDegree(navController = navController, viewModel = selectDegreeVM) }
        }
        composable(route = Screen.ViewSchedule.route){
            MainScreen (navController = navController) { ViewSchedule(navController = navController,
                scheduleViewModel = scheduleViewModel) }
        }
        composable(route = Screen.ApiPlayground.route){
            MainScreen (navController = navController) { ApiPlayGround(navController = navController) }
        }
        composable(route = "${Screen.CourseDetail.route}/{course}",
            arguments = listOf(navArgument("course") { NavType.SerializableType(Course::class.java) }))
        { backStackEntry ->
            val course = backStackEntry.arguments?.getSerializable("course", Course::class.java)
            MainScreen (navController = navController) { CourseScreen(course) }
        }
    }
}
