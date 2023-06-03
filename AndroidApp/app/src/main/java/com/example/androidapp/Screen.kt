package com.example.androidapp

sealed class Screen(val route: String){
    object MainScreen: Screen("main")
    object AboutView: Screen("aboutView")
    object ViewSchedule: Screen("viewSchedule")
    object SelectDegree: Screen("selectDegree")
    object CourseDetail: Screen("courseDetail")

}