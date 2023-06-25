package com.example.androidapp.Screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.Enum.CoopSequence
import com.example.androidapp.Enum.MyMajor
import com.example.androidapp.Enum.MyMinor
import com.example.androidapp.Enum.MySpecialization
import com.example.androidapp.Enum.MyYear
import com.example.androidapp.ViewModels.SelectDegreeVM


@SuppressLint("StateFlowValueCalledInComposition")

@Composable
fun SelectDegree(navController: NavController, viewModel: SelectDegreeVM) {
    val viewModel: SelectDegreeVM = viewModel
    val context = LocalContext.current
    Box(
            Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
    )
    {
        Column(
                modifier = Modifier
                        .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally) {
            SelectList(MyMajor.values().map { it.major }.toTypedArray(), context, viewModel.uiState.value.major)
            SelectList(MyYear.values().map { it.year }.toTypedArray(), context, viewModel.uiState.value.year)
            SelectList(CoopSequence.values().map { it.sequence }.toTypedArray(), context, viewModel.uiState.value.sequence)
            SelectList(MyMinor.values().map { it.minor }.toTypedArray(), context, viewModel.uiState.value.minor)
            SelectList(MySpecialization.values().map { it.specialization }.toTypedArray(), context, viewModel.uiState.value.specialization)
        }
        Button(onClick = { viewModel.generateSchedule(
                                viewModel.uiState.value.major,
                                viewModel.uiState.value.year, viewModel.uiState.value.sequence,
                                viewModel.uiState.value.minor,
                                viewModel.uiState.value.specialization) },
                modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)) {
            Text("Generate Course Schedules")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> SelectList(choices: Array<String>, context: Context, enum : T) {
    var expanded by remember { mutableStateOf(false) }
    // var selectedText by remember { mutableStateOf(choices[0]) }
    var selectedText by remember { mutableStateOf(choices[enum.ordinal]) }

    Box(Modifier.padding(12.dp)) {
        ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
        ) {
            TextField(
                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
            )

            ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
            ) {
                choices.forEach { item ->
                    DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                selectedText = item
                                expanded = false
                                Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                            }
                    )
                }
            }
        }
    }
}