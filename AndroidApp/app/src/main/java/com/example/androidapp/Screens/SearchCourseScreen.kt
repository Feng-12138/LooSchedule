package com.example.androidapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.navigation.NavController
import com.example.androidapp.models.Schedule

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//, term: String, schedule: Schedule, position: Int
fun SearchCourse(navController: NavController){
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    Scaffold {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = text,
            onQueryChange = { text = it},
            onSearch = { active = false},
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

        )
        {
        }
    }
}

