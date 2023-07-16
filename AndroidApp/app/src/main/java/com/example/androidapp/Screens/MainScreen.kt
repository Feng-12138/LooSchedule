package com.example.androidapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
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
fun MainScreen(navController: NavController, name: String, content: @Composable () -> Unit) {
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
                    ModalDrawerSheet {
                        Text("LooSchedule", modifier = Modifier.padding(16.dp))
                        Divider()
                        NavigationDrawerItem(
                            label = { Text(text = "Get Start") },
                            selected = false,
                            shape = RoundedCornerShape(0.dp),
                            onClick = { navController.navigate(Screen.MainScreen.route) }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = "My Current Schedule") },
                            selected = false,
                            shape = RoundedCornerShape(0.dp),
                            onClick = { navController.navigate(Screen.ViewSchedule.route) }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = "Create My Schedule") },
                            selected = false,
                            shape = RoundedCornerShape(0.dp),
                            onClick = { navController.navigate(Screen.SelectDegree.route) }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = "Schedule History") },
                            selected = false,
                            shape = RoundedCornerShape(0.dp),
                            onClick = { navController.navigate(Screen.ScheduleHistory.route) }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = "About") },
                            selected = false,
                            shape = RoundedCornerShape(0.dp),
                            onClick = { navController.navigate(Screen.About.route) }
                        )
                        // For testing purpose
//                        NavigationDrawerItem(
//                            label = { Text(text = "PlayGround") },
//                            selected = false,
//                            onClick = { navController.navigate(Screen.ApiPlayground.route) }
//                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = { TopAppBar(
                        title = { Text(name) },
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
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        content()
                    }
                }
            }
        }
    }
}
