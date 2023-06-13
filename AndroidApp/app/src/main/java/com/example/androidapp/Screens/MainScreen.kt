package com.example.androidapp.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.Screen
import com.example.androidapp.ui.theme.AndroidAppTheme

@Composable
fun MainScreen(navController: NavController){
    AndroidAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Greeting("Android", navController)
        }
    }
}

@Composable
fun Greeting(name: String, navController: NavController, modifier: Modifier = Modifier, ) {
    Column() {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = {
            navController.navigate(Screen.SelectDegree.route)
        }) {
            Text(text = "Test Navigate")
        }
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = {
            navController.navigate(Screen.ViewSchedule.route)
        }) {
            Text(text = "View Schedule")
        }
    }

}