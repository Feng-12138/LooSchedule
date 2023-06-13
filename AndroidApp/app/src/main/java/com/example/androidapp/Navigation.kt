package com.example.androidapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.Screens.MainScreen
import com.example.androidapp.Screens.SelectDegree
import com.example.androidapp.Screens.ViewSchedule

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
        composable(route = Screen.MainScreen.route){
            MainScreen(navController = navController)
        }
        composable(route = Screen.SelectDegree.route){
            SelectDegree(navController = navController)
        }
        composable(route = Screen.ViewSchedule.route){
            ViewSchedule(navController = navController)
        }
    }
}
