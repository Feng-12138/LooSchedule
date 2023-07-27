package com.example.androidapp.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidapp.EverythingManager
import com.example.androidapp.dataClass.TermSchedule
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.services.RetrofitClient
import com.example.androidapp.viewModels.RequestData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermFilterScreen(schedule: Schedule, position: Int, navController: NavController){
    val terms: List<String> = schedule.termSchedule.keys.toList()
    val courses: List<Course> = EverythingManager.getInstance().getCourses()?.map{ course -> course } ?: listOf()
    val context = LocalContext.current
    var selectedTerm by remember { mutableStateOf(terms.first()) }
    var expanded by remember { mutableStateOf(false) }

    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var filteredCourses = remember { mutableStateListOf<Course>() }
    var selectedCourses = remember { mutableStateListOf<Course>() }

    LaunchedEffect(text) {
        filteredCourses.clear()
        if (text.isNotEmpty()) {
            filteredCourses.addAll(courses.filter { course ->
                containsWithOrder("${course.courseID} ${course.courseName}".lowercase(), text.lowercase())
            })
        } else {
            filteredCourses.addAll(courses)
        }
    }
    Column() {
        Row(
            verticalAlignment = CenterVertically,
        ) {
            Text(
                "Select Current Term: ",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
            Box(Modifier.padding(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    },
                ) {
                    TextField(
                        value = selectedTerm,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        terms.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    selectedTerm = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = text,
            onQueryChange = { text = it },
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(text = "Select taken courses") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if(active){
                    Icon(
                        modifier = Modifier.clickable{
                            if(text.isNotEmpty()){
                                text = ""
                            }else{
                                active = false
                            }
                        },
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        ){
            LazyColumn(modifier = Modifier.fillMaxSize()){
                items(filteredCourses) { course ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = {
                            if(!selectedCourses.contains(course)){
                                selectedCourses.add(course)
                                active = false
                            }
                        }
                    ) {
                        Text(
                            text = "${course.courseID} ${course.courseName}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

        }
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth(1.0f)
        ) {
            items(selectedCourses) { course ->
                Row(verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(16.dp)) {
                    Text(
                        text = "${course.courseID} ${course.courseName}",
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    Checkbox(
                        checked = true,
                        onCheckedChange = { selectedCourses.remove(course) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = Color.Gray
                        )
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center // Align the button to the center horizontally and vertically
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(90, 118, 142),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(100.dp)
                    .padding(16.dp),
                onClick = {
                    val requestData = RequestData(
                        majors = schedule.degree,
                        startYear = schedule.startYear,
                        sequence = schedule.sequence,
                        minors = schedule.minor,
                        specializations = schedule.specialization,
                        currentTerm = selectedTerm,
                        coursesTaken = selectedCourses.map { course -> course.courseID }
                    )

                    val gson = Gson()
                    val jsonBody = gson.toJson(requestData)
                    println(jsonBody)

                    val api = RetrofitClient.create()
                    val requestBody =
                        RequestBody.create(MediaType.parse("application/json"), jsonBody)

                    val call = api.getCourseSchedule(requestBody)
                    call.enqueue(object : Callback<TermSchedule> {
                        override fun onResponse(
                            call: Call<TermSchedule>,
                            response: Response<TermSchedule>
                        ) {
                            if (response.isSuccessful) {
                                val output = response.body()
                                val sharedPreferences = context.getSharedPreferences(
                                    "MySchedules",
                                    Context.MODE_PRIVATE
                                )
                                val existingList = sharedPreferences.getString("scheduleList", "[]")
                                val type = object : TypeToken<MutableList<Schedule>>() {}.type
                                val scheduleList: MutableList<Schedule> =
                                    Gson().fromJson(existingList, type)
                                var position = 0
                                val newSchedule = output?.let {
                                    Schedule(
                                        it.schedule as MutableMap<String, MutableList<Course>>,
                                        myDegree = schedule.degree,
                                        mySequence = schedule.sequence,
                                        startYear = schedule.startYear
                                    )
                                }
                                if (newSchedule != null) {
                                    newSchedule.minor = schedule.minor
                                    newSchedule.specialization = schedule.specialization
                                    scheduleList.add(position, newSchedule)
                                }
                                val editor = sharedPreferences.edit()
                                val jsonList = Gson().toJson(scheduleList)
                                editor.putString("scheduleList", jsonList)
                                editor.apply()
                                navController.navigate(Screen.ViewSchedule.route)
                            } else {
                                println(response.message())
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<TermSchedule>, t: Throwable) {
                            Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                            println(t.message)
                            call.cancel()
                        }
                    })
                }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modify"
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Modify Course Schedule", fontSize = 16.sp)
            }

        }
    }
}

private fun containsWithOrder(text: String, query: String): Boolean {
    var j = 0
    for (i in text.indices) {
        if (j < query.length && text[i] == query[j]) {
            j++
        }
    }
    return j == query.length
}