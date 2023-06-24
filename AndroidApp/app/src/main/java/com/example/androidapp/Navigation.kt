package com.example.androidapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.Screens.ApiPlayGround
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
            MainScreen { Greeting(navController = navController) }
        }
        composable(route = Screen.SelectDegree.route){
            MainScreen { SelectDegree(navController = navController) }
        }
        composable(route = Screen.ViewSchedule.route){
            MainScreen { ViewSchedule(navController = navController) }
        }
        composable(route = Screen.ApiPlayground.route){
            MainScreen { ApiPlayGround(navController = navController) }
        }
    }
}


