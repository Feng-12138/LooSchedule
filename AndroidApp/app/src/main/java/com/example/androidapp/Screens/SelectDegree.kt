package com.example.androidapp.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.EverythingManager
import com.example.androidapp.enum.FieldType
import com.example.androidapp.viewModels.SelectDegreeVM


@SuppressLint("StateFlowValueCalledInComposition")

@Composable
fun SelectDegree(navController: NavController, selectDegreeVM: SelectDegreeVM) {
    var showAlert by remember { mutableStateOf(selectDegreeVM.showDialog) }
    val viewModel: SelectDegreeVM = selectDegreeVM
    val everythingManager: EverythingManager = EverythingManager.getInstance()
    val sequence: List<String> = listOf("Select your coop sequence", "Regular", "Sequence 1", "Sequence 2", "Sequence 3", "Sequence 4")
    val year: List<String> = listOf("Select your academic year", "2023", "2022", "2021", "2020", "2019")
    val context = LocalContext.current

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    )
    {
        Column(
                modifier = Modifier.align(Alignment.TopCenter),
        ) {
            Text("Degree: ",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp))
            SelectList(everythingManager.getMajors().toTypedArray(), context, FieldType.MAJOR, viewModel)
            Text("Academic Year: ",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp))
            SelectList(year.toTypedArray(), context, FieldType.YEAR, viewModel)
            Text("Coop Sequence: ",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp))
            SelectList(sequence.toTypedArray(), context, FieldType.SEQUENCE, viewModel)
            Text("Minor (Optional): ",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp))
            SelectList(everythingManager.getMinors().toTypedArray(), context, FieldType.MINOR, viewModel)
            Text("Specialization (Optional): ",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp))
            SelectList(everythingManager.getSpecializations().toTypedArray(), context, FieldType.SPECIALIZATION, viewModel)
        }

        Column(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 16.dp)) {
            Button(onClick =
            {
                if (viewModel.uiState.value.major == "Select your degree" ||
                    viewModel.uiState.value.year == "Select your academic year" ||
                    viewModel.uiState.value.sequence == "Select your Coop sequence") {
                    showAlert = true
                }
                viewModel.generateSchedule(
                    viewModel.uiState.value.major,
                    viewModel.uiState.value.year,
                    viewModel.uiState.value.sequence,
                    viewModel.uiState.value.minor,
                    viewModel.uiState.value.specialization,
                    context,
                    navController)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create"
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text("Create Course Schedule")
            }

            Button(
                onClick = {
                    if (viewModel.uiState.value.major == "Select your degree" ||
                        viewModel.uiState.value.year == "Select your academic year" ||
                        viewModel.uiState.value.sequence == "Select your Coop sequence") {
                        showAlert = true
                    }  else{
                        navController.navigate(Screen.RealChatgptScreen.route)
                    }
                },
                modifier = Modifier.width(230.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "modify"
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text("Build Your Career Plan")
            }
        }


        if (showAlert) {
            AlertDialog(
                onDismissRequest = {
                    showAlert = false
                },
                title = {
                    Text(text = "Warning")
                },
                text = {
                    Text("You must select degree, academic year and sequence!")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAlert = false
                        }) {
                        Text("Confirm")
                    }
                }
            )
        }
    }
}

@SuppressLint("RememberReturnType", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectList(choices: Array<String>, context: Context, type: FieldType, viewModel: SelectDegreeVM) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }

    if(type == FieldType.MAJOR){
        selectedText = viewModel.uiState.value.major
    }
    if(type == FieldType.MINOR){
        selectedText = viewModel.uiState.value.minor
    }
    if(type == FieldType.SPECIALIZATION){
        selectedText = viewModel.uiState.value.specialization
    }
    if(type == FieldType.YEAR){
        selectedText = viewModel.uiState.value.year
    }
    if(type == FieldType.SEQUENCE){
        selectedText = viewModel.uiState.value.sequence
    }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
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
                            if(type == FieldType.MAJOR){
                                viewModel.uiState.value.major = item
                            }
                            if(type == FieldType.MINOR){
                                viewModel.uiState.value.minor = item
                            }
                            if(type == FieldType.SPECIALIZATION){
                                viewModel.uiState.value.specialization = item
                            }
                            if(type == FieldType.YEAR){
                                viewModel.uiState.value.year = item
                            }
                            if(type == FieldType.SEQUENCE){
                                viewModel.uiState.value.sequence = item
                            }
                        }
                    )
                }
            }
        }
    }
}



