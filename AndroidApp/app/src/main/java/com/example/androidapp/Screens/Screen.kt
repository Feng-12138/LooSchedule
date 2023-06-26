package com.example.androidapp.screens

sealed class Screen(val route: String){
    object MainScreen: Screen("main")
    object AboutView: Screen("aboutView")
    object ViewSchedule: Screen("viewSchedule")
    object SelectDegree: Screen("selectDegree")
    object CourseDetail: Screen("courseDetail")
    object ApiPlayground: Screen("apiPlayground")
}