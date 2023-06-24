package com.example.androidapp.Screens

sealed class Screen(val route: String){
    object MainScreen: Screen("main")
    object AboutView: Screen("aboutView")
    object ViewSchedule: Screen("viewSchedule")
    object SelectDegree: Screen("selectDegree")
    object CourseDetail: Screen("courseDetail/{CourseID}")
    object ApiPlayground: Screen("apiPlayground")
}