package com.example.androidapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.Models.Course
import com.example.androidapp.Screens.ApiPlayGround
import com.example.androidapp.Screens.CourseScreen
import com.example.androidapp.Screens.Greeting
import com.example.androidapp.Screens.MainScreen
import com.example.androidapp.Screens.Screen
import com.example.androidapp.Screens.SelectDegree
import com.example.androidapp.Screens.ViewSchedule

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
        composable(route = Screen.MainScreen.route){
            MainScreen (navController = navController) { Greeting(navController = navController) }
        }
        composable(route = Screen.SelectDegree.route){
            MainScreen (navController = navController) { SelectDegree(navController = navController) }
        }
        composable(route = Screen.ViewSchedule.route){
            MainScreen (navController = navController) { ViewSchedule(navController = navController) }
        }
        composable(route = Screen.ApiPlayground.route){
            MainScreen (navController = navController) { ApiPlayGround(navController = navController) }
        }
        composable(route = Screen.CourseDetail.route) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val course = arguments.getParcelable("course", Course::class.java)
            if (course != null) {
                CourseScreen(course)
            }
        }
    }
}
