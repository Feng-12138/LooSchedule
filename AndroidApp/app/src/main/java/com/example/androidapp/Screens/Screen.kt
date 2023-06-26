package com.example.androidapp.screens

sealed class Screen(val route: String){
    object MainScreen: Screen("main")
    object ViewSchedule: Screen("viewSchedule")
    object SelectDegree: Screen("selectDegree")
    object CourseDetail: Screen("courseDetail")
    object ApiPlayground: Screen("apiPlayground")
    object ScheduleHistory: Screen("scheduleHistory")
    object OldSchedule: Screen("oldSchedule")
    object About: Screen("about")
}