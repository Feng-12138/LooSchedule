package com.example.androidapp.screens

import android.content.Context
import android.view.Surface
import android.widget.Toast
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.androidapp.R
import com.example.androidapp.dataClass.TermSchedule
import com.example.androidapp.models.Course
import com.example.androidapp.models.Schedule
import com.example.androidapp.services.RetrofitClient
import com.example.androidapp.viewModels.AcademicPlan
import com.example.androidapp.viewModels.RequestData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Indicator() {
    val strokeWidth = 5.dp

    CircularProgressIndicator(
        modifier = Modifier.drawBehind {
            drawCircle(
                Color.Red,
                radius = size.width / 2 - strokeWidth.toPx() / 2,
                style = Stroke(strokeWidth.toPx())
            )
        },
        color = Color.LightGray,
        strokeWidth = strokeWidth
    )
}

@Composable
fun DialogBoxLoading(
    cornerRadius: Dp = 16.dp,
    paddingStart: Dp = 56.dp,
    paddingEnd: Dp = 56.dp,
    paddingTop: Dp = 32.dp,
    paddingBottom: Dp = 32.dp,
    progressIndicatorColor: Color = Color(0xFF35898f),
    progressIndicatorSize: Dp = 80.dp
) {

    Dialog(
        onDismissRequest = {
        }
    ) {
        Column(
            modifier = Modifier
                .padding(start = paddingStart, end = paddingEnd, top = paddingTop),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProgressIndicatorLoading(
                progressIndicatorSize = progressIndicatorSize,
                progressIndicatorColor = progressIndicatorColor
            )

            // Gap between progress indicator and text
            Spacer(modifier = Modifier.height(32.dp))

            // Please wait text
            Text(
                modifier = Modifier
                    .padding(bottom = paddingBottom),
                text = "Please wait...",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun ProgressIndicatorLoading(progressIndicatorSize: Dp, progressIndicatorColor: Color) {

    val infiniteTransition = rememberInfiniteTransition()

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600
            }
        )
    )

    CircularProgressIndicator(
        progress = 1f,
        modifier = Modifier
            .size(progressIndicatorSize)
            .rotate(angle)
            .border(
                12.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        Color.White, // add background color first
                        progressIndicatorColor.copy(alpha = 0.1f),
                        progressIndicatorColor
                    )
                ),
                shape = CircleShape
            ),
        strokeWidth = 1.dp,
        color = Color.White // Set background color
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealChatgptScreen(major: String, minor: String, specialization: String, year: String, sequence: String, navController: NavController){
    var value = remember {
        mutableStateOf("")
    }
    var showAlert by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    var popUpMessage = remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    Image(
        painter = painterResource(id = R.drawable.career_background),
        contentDescription = "career",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showLoading) {
            DialogBoxLoading()
        }
        Text(
            text = "What do you want to work in the future?",
            modifier = Modifier.padding(start = 11.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "(Please enter Career name)",
            modifier = Modifier.padding(start = 11.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(6.dp))


        Text(text = "eg. accountant, software developer and etc.")

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = value.value,
            onValueChange = { newText ->
                value.value = newText
            }
        )

        Button(
            onClick = {
                showLoading = true
                val inputMajor = listOf(major)
                val inputMinor = if (minor != "Select your minor"){
                    listOf(minor)
                } else{
                    emptyList()
                }
                val inputSpecialization = if (specialization != "Select your specialization"){
                    listOf(specialization)
                } else{
                    emptyList()
                }

                val requestData = RecommendationPlan(
                    position = value.value,
                    academicPlan = AcademicPlan(
                        majors = inputMajor,
                        startYear = year,
                        sequence = sequence,
                        minors = inputMinor,
                        specializations = inputSpecialization
                    )
                )

                val gson = Gson()
                val jsonBody = gson.toJson(requestData)
                println(jsonBody)

                val api = RetrofitClient.create()
                val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody)

                val call = api.getRecommendation(requestBody)
                call.enqueue(object : Callback<TermSchedule> {
                    override fun onResponse(
                        call: Call<TermSchedule>,
                        response: Response<TermSchedule>
                    ) {
                        if (response.isSuccessful) {
                            val output = response.body()
                            val sharedPreferences = context.getSharedPreferences("MySchedules", Context.MODE_PRIVATE)
                            val existingList = sharedPreferences.getString("scheduleList", "[]")
                            val type = object : TypeToken<MutableList<Schedule>>() {}.type
                            val scheduleList : MutableList<Schedule> = Gson().fromJson(existingList, type)
                            var position = 0
                            val schedule = output?.let {
                                popUpMessage.value = it.message.toString()
                                Schedule(it.schedule as MutableMap<String, MutableList<Course>>, myDegree = inputMajor, mySequence = sequence, startYear = year)
                            }
                            if (schedule != null) {
                                schedule.minor = inputMinor
                                schedule.specialization = inputSpecialization
                                scheduleList.add(position , schedule)
                            }
                            val editor = sharedPreferences.edit()
                            val jsonList = Gson().toJson(scheduleList)
                            editor.putString("scheduleList", jsonList)
                            editor.apply()

                            showAlert = true
                        } else {
                            println(response.message())
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<TermSchedule>, t: Throwable) {
                        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                        println(t.message)
                        call.cancel()
                    }
                })
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Gray
            ),
            modifier = Modifier
                .padding(16.dp)) {
            Text("Done!")
        }
    }

    if (showAlert) {
        showLoading = false
        AlertDialog(
            onDismissRequest = {
                showAlert = false
            },
            title = {
                Text(text = "Notice")
            },
            text = {
                Text(popUpMessage.value)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAlert = false
                        navController.navigate(Screen.ViewSchedule.route)
                    }) {
                    Text("Confirm")
                }
            }
        )
    }
}

data class RecommendationPlan(
    val position: String,
    val academicPlan: AcademicPlan
)


