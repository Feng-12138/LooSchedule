package com.example.androidapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidapp.R
import com.example.androidapp.ui.theme.AndroidAppTheme
import kotlinx.coroutines.launch

@Composable
fun getTopBarColor(screen: String): Color {
    if (screen == "LooSchedule") {
        return Color(123, 142, 193)
    } else if (screen == "History") {
        return Color(51, 49, 72)
    } else if (screen == "Contact Us") {
        return Color(137, 179, 225)
    }

    return Color.White
}

@Composable
fun getTitleColor(screen: String): Color {
    if (screen == "LooSchedule") {
        return Color.White
    } else if (screen == "History") {
        return Color.White
    }

    return Color.Black
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, name: String, content: @Composable () -> Unit) {
    val indiaFlower = FontFamily(
        Font(R.font.indieflower, FontWeight.Light),
    )

    AndroidAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(123, 142, 193)
        ) {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {

                        Box {
                            Image(
                                painter = painterResource(id = R.drawable.contact2),
                                contentDescription = "get started",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.fillMaxSize()
                            )

                            Column(
                                modifier = Modifier
                                    .padding(top = 13.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(25.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "LooSchedule",
                                    modifier = Modifier.padding(top = 25.dp),
                                    fontFamily = indiaFlower,
                                    color = Color.White,
                                    style = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold),
                                )
                                Divider(color = Color.White, modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth(0.6f)
                                ) {
                                    NavigationDrawerItem(
                                        label = { Text(
                                            text = "Get Started",
                                            modifier = Modifier.align(Alignment.Center),
                                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        ) },
                                        modifier = Modifier.align(Alignment.Center),
                                        selected = name == "LooSchedule",
                                        shape = RoundedCornerShape(30.dp),
                                        onClick = { navController.navigate(Screen.MainScreen.route) }
                                    )
                                }
                                Box(
                                    modifier = Modifier.fillMaxWidth(0.6f)
                                ) {
                                    NavigationDrawerItem(
                                        label = { Text(
                                            text = "My Current Schedule",
                                            modifier = Modifier.align(Alignment.Center),
                                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        ) },
                                        selected = name == "Current Schedule",
                                        shape = RoundedCornerShape(30.dp),
                                        onClick = { navController.navigate(Screen.ViewSchedule.route) }
                                    )
                                }
                                Box(
                                    modifier = Modifier.fillMaxWidth(0.6f)
                                ) {
                                    NavigationDrawerItem(
                                        label = { Text(
                                            text = "Create My Schedule",
                                            modifier = Modifier.align(Alignment.Center),
                                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        ) },
                                        selected = name == "Create Schedule",
                                        shape = RoundedCornerShape(30.dp),
                                        onClick = { navController.navigate(Screen.SelectDegree.route) }
                                    )
                                }
                                Box(
                                    modifier = Modifier.fillMaxWidth(0.6f)
                                ) {
                                    NavigationDrawerItem(
                                        label = { Text(
                                            text = "Schedule History",
                                            modifier = Modifier.align(Alignment.Center),
                                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        ) },
                                        selected = name == "History",
                                        shape = RoundedCornerShape(30.dp),
                                        onClick = { navController.navigate(Screen.ScheduleHistory.route) }
                                    )
                                }
                                Box(
                                    modifier = Modifier.fillMaxWidth(0.6f)
                                ) {
                                    NavigationDrawerItem(
                                        label = { Text(
                                            text = "Contact Us",
                                            modifier = Modifier.align(Alignment.Center),
                                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        ) },
                                        selected = name == "About",
                                        shape = RoundedCornerShape(30.dp),
                                        onClick = { navController.navigate(Screen.About.route) }
                                    )
                                }
                            }
                        }
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = getTopBarColor(
                                    screen = name
                                )
                            ),
                            modifier = Modifier.padding(0.dp),
                            title = {
                                Text(
                                    style = TextStyle(fontSize = 30.sp),
                                    text = name,
                                    color = getTitleColor(screen = name),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(
                                    Font(R.font.indieflower, FontWeight.Light))) },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            drawerState.apply {
                                                if (isClosed) open() else close()
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Menu,
                                        contentDescription = "",
                                        tint = Color.White
                                    )
                                }
                            },
                        )
                    },
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        content()
                    }
                }
            }
        }
    }
}
