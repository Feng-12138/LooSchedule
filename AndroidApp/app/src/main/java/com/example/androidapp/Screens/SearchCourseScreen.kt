package com.example.androidapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.EverythingManager

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//, term: String, schedule: Schedule, position: Int
fun SearchCourseScreen(navController: NavController){
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val courses: List<String>? = EverythingManager.getInstance().getCourses()
    Column() {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = text,
            onQueryChange = { text = it},
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(text = "Find Course")},
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

        }
        if (courses != null) {
            CourseList(courseList = courses, searchQuery = text)
        }
        else{
            Text("Nothing")
        }
    }
}


@Composable
fun CourseList(courseList: List<String>, searchQuery: String) {
    var filteredCourses = remember { mutableStateListOf<String>() }
    LaunchedEffect(searchQuery) {
        filteredCourses.clear()
        if (searchQuery.isNotEmpty()) {
            filteredCourses.addAll(courseList.filter { course ->
                containsWithOrder(course.lowercase(), searchQuery.lowercase())
            })
        } else {
            filteredCourses.addAll(courseList)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(filteredCourses) { course ->
            Surface(
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = course,
                    modifier = Modifier.padding(16.dp)
                )
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
