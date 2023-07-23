package com.example.androidapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.runtime.snapshotFlow
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
//    val courseList1A = listOf(
//        Course("CS 135"), Course("MATH 135"),
//        Course("MATH 137"), Course("EMLS 129R"), Course("PHYS 111")
//    )
//    val courseList1B = listOf(
//        Course("1B 1"), Course("1B 2"), Course("1B 3"),
//        Course("1B 4"), Course("1B 5")
//    )
//    val courseList2A = listOf(
//        Course("2A 1"), Course("2A 2"), Course("2A 3"),
//        Course("2A 4"), Course("2A 5")
//    )
//    val courseList2B = listOf(
//        Course("2B 1"), Course("2B 2"), Course("2B 3"),
//        Course("2B 4"), Course("2B 5")
//    )
//    val courseList3A = listOf(
//        Course("3A 1"), Course("3A 2"), Course("3A 3"),
//        Course("3A 4"), Course("3A 5")
//    )
//    val courseList3B = listOf(
//        Course("3B 1"), Course("3B 2"), Course("3B 3"),
//        Course("3B 4"), Course("3B 5")
//    )
//    val courseList4A = listOf(
//        Course("4A 1"), Course("4A 2"), Course("4A 3"),
//        Course("4A 4"), Course("4A 5")
//    )
//    val courseList4B = listOf(
//        Course("4B 1"), Course("4B 2"), Course("4B 3"),
//        Course("4B 4"), Course("4B 5")
//    )
//
//    val test = Schedule(mapOf("1A" to courseList1A, "1B" to courseList1B,
//        "2A" to courseList2A, "2B" to courseList2B,
//        "3A" to courseList3A, "3B" to courseList3B,
//        "4A" to courseList4A, "4B" to courseList4B))
//
//    val test2 = Schedule(mapOf("1A" to courseList1A, "1B" to courseList1B,
//        "2A" to courseList2A, "2B" to courseList2B,
//        "3A" to courseList3A, "3B" to courseList3B,
//        "4A" to courseList4A))


//    var testList: List<Schedule> = listOf(test, test2)

//    val sharedPreferences = LocalContext.current.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
//
//    val editor = sharedPreferences.edit()
//    val jsonList = testList.map { Gson().toJson(it) }.toSet()
//    editor.putStringSet("scheduleList", jsonList)
//    editor.apply()
//
//    val existingList = sharedPreferences.getStringSet("scheduleList", emptySet())?.toList()
//    val scheduleList = existingList?.map { Gson().fromJson(it, Schedule::class.java) } ?: emptyList()
//    val scheduleViewModel = ScheduleViewModel(scheduleList[0])
    val sharedPreferences = LocalContext.current.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
    val existingList = sharedPreferences.getStringSet("scheduleList", emptySet())?.toList()
    val scheduleList = existingList?.map { Gson().fromJson(it, Schedule::class.java) } ?: emptyList()
    val scheduleViewModel = ScheduleViewModel(scheduleList[0])

    val context = LocalContext.current
    LaunchedEffect(scheduleViewModel) {
        snapshotFlow { scheduleViewModel.schedule }
            .collect { updatedSchedule ->
                val sharedPreferences = context.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val jsonUpdatedSchedule = Gson().toJson(Schedule(updatedSchedule))
                val existingSchedules = sharedPreferences.getStringSet("scheduleList", emptySet())?.toMutableSet() ?: mutableSetOf()
                existingSchedules.remove(Gson().toJson(scheduleList[0]))  // remove the old one
                existingSchedules.add(jsonUpdatedSchedule) // add the updated one
                editor.putStringSet("scheduleList", existingSchedules)
                editor.apply()
            }
    }

    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
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


            if(scheduleList.isEmpty()){
                MainScreen(navController = navController, name = "Current Schedule") {
                    ErrorScreen(navController = navController)
                }
            }
            else{
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
                CourseScreen(course = course, navController = navController, viewModel = scheduleViewModel)
            }
        }
        composable(route = Screen.ScheduleHistory.route){
//            val sharedPreferences = LocalContext.current.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
//            val existingList = sharedPreferences.getStringSet("scheduleList", emptySet())?.toList()
//            val scheduleList = existingList?.map { Gson().fromJson(it, Schedule::class.java) } ?: emptyList()

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
