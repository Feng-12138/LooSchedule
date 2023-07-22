package com.example.androidapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.androidapp.models.Course

@Composable
fun CourseScreen(course: Course?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        course?.courseID?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        course?.courseName?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        if (course != null) {
            RatingBar(type = "Easy Rating", rating = course.easyRating)
            Spacer(modifier = Modifier.size(16.dp))
            RatingBar(type = "Like Rating", rating = course.likeRating)
        }

        Spacer(modifier = Modifier.size(16.dp))

        if (course != null) {
            Description(description = course.description)
        }
    }
}

@Composable
fun RatingBar(type: String, rating: Float) {
    Column{
        Text(
            text = type,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Row(horizontalArrangement = Arrangement.Center) {
            repeat(5) {index ->
                if (index < (rating * 5).toInt()){
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }

            }
        }
    }
}

@Composable
fun Description(description: String) {
    Column {
        Text(
            text = "Course Description",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 0.dp)
        )
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}
