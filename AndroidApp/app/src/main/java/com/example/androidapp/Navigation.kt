package com.example.androidapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.widget.Toast
import android.widget.Toolbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.Screens.MainScreen
import com.example.androidapp.Screens.SelectDegree
import kotlinx.coroutines.launch
import com.example.androidapp.Screens.ViewSchedule

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
        composable(route = Screen.MainScreen.route){
            MainScreen(navController = navController)
        }
        composable(route = Screen.SelectDegree.route){
            SelectDegree(navController = navController)
        }
        composable(route = Screen.ViewSchedule.route){
            ViewSchedule(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolBar() {
    val contextForToast = LocalContext.current.applicationContext

    TopAppBar(
        title = {
            Text(text = "LooSchedule")
        },
        navigationIcon = {
            IconButton(onClick = {
                Toast.makeText(contextForToast, "Navigation Icon Click", Toast.LENGTH_SHORT)
                    .show()
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Navigation icon")
            }
        },
    )
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestMain() {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val selectedRoute = remember { mutableStateOf("Home") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "LooSchedule") },
                navigationIcon = {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } }
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                // Your main content here

                if (drawerState.isOpen) {
                    // Overlay to capture clicks outside the drawer
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { scope.launch { drawerState.close() } },
                        content = {}
                    )
                }

                DrawerContent(
                    selectedRoute = selectedRoute.value,
                    onRouteSelected = { route ->
                        selectedRoute.value = route
                        scope.launch { drawerState.close() }
                    },
                )
            }
        }
    )
}

@Composable
fun DrawerContent(selectedRoute: String, onRouteSelected: (String) -> Unit) {
    Column {
        // Drawer items
        DrawerItem(
            text = "Home",
            selected = selectedRoute == "Home",
            onClick = { onRouteSelected("Home") }
        )
        DrawerItem(
            text = "Profile",
            selected = selectedRoute == "Profile",
            onClick = { onRouteSelected("Profile") }
        )
        // Add more drawer items as needed
    }
}

@Composable
fun DrawerItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp)
        )
    }
}
