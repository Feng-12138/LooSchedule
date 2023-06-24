package com.example.androidapp.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidapp.ui.theme.AndroidAppTheme
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(content: @Composable () -> Unit) {
    AndroidAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet { /* Drawer content */ }
                },
            ) {
                Scaffold(
                    topBar = { TopAppBar(
                        title = { Text("LooSchedule") },
                        navigationIcon = { IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Outlined.Menu, contentDescription = "")
                        }},
                    )},
                    content = {
                        Box(modifier = Modifier.padding(it)) {
                            content()
                        }},
                )
            }


        }
    }
}

@Composable
fun Greeting(navController: NavController, modifier: Modifier = Modifier) {
    Column() {
        Button(onClick = {
            navController.navigate(Screen.SelectDegree.route)
        }) {
            Text(text = "View Select Degree")
        }
        Button(onClick = {
            navController.navigate(Screen.ViewSchedule.route)
        }) {
            Text(text = "View Schedule")
        }
        Button(onClick = {
            navController.navigate(Screen.ApiPlayground.route)
        }) {
            Text(text = "PlayGround")
        }
    }
}
