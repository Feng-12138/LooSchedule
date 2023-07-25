package com.example.androidapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidapp.R

@Composable
fun GetStartScreen(navController: NavController) {
    val indiaFlower = FontFamily(
        Font(R.font.indieflower, FontWeight.Light),
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to LooSchedule!",
            style = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = indiaFlower
        )

        Button(
            onClick = { navController.navigate(Screen.SelectDegree.route) },
            modifier = Modifier
                .height(48.dp)
        ) {
            Text(text = "Schedule Your Courses!")
        }
    }
}