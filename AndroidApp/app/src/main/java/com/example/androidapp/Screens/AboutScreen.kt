package com.example.androidapp.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import com.example.androidapp.R


@SuppressLint("QueryPermissionsNeeded", "IntentReset")
@Composable
fun AboutScreen() {
    val indiaFlower = FontFamily(
        Font(R.font.indieflower, FontWeight.Light),
    )
    var isDialogVisible by remember { mutableStateOf(false) }
    var title by remember {
        mutableStateOf("")
    }
    var feedback by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "LooSchedule",
            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(bottom = 64.dp)
                .align(CenterHorizontally)
        )

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("App Version: ")
                }
                append("1.0.1")
            },
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Group: ")
                }
                append("20")
            },
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Developers: ")
                }
                append("Joyce Dai, Kevin Jin, Kevin Ke, Michael Zhang, Steven Tian, Yiran Sun")
            },
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = {
//                isDialogVisible = true
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    type = "plain/text"
                    data = Uri.parse("mailto: tian_ruian@163.com")
//                    putExtra(Intent.EXTRA_SUBJECT, title)
//                    putExtra(Intent.EXTRA_TEXT, feedback)
//                    putExtra(Intent.EXTRA_EMAIL, title)
                }
                startActivity(context, Intent.createChooser(emailIntent, "test"), null)
            }
        ){
            Text("Contact us")
        }

//        if (isDialogVisible) {
//            MyAlertDialog(
//                emailTitle = {
//                    Text(text = "Enter your Title:")
//                },
//                titleBox = {
//                    OutlinedTextField(
//                        value = title,
//                        onValueChange = { title = it },
//                    )
//                },
//                messageTitle = {
//                    Text(text = "Enter your feedback:")
//                },
//                messageBox = {
//                    OutlinedTextField(
//                        value = feedback,
//                        onValueChange = { feedback = it },
//                        minLines = 10
//                    )
//                },
//                cancelButton = {
//                    TextButton(
//                        onClick = { isDialogVisible = false },
//                        content = { Text("Cancel") },
//                    )
//                },
//                sendButton = {
//                    TextButton(
//                        onClick = {
//                            val emailIntent = Intent(Intent.ACTION_SEND).apply {
//                                putExtra(Intent.EXTRA_SUBJECT, title)
//                                putExtra(Intent.EXTRA_TEXT, feedback)
//                                type = "plain/text"
////                                data = Uri.parse("mailto: q4ke@uwaterloo.ca")
//                                putExtra(Intent.EXTRA_EMAIL, title)
//                            }
//                            startActivity(context, Intent.createChooser(emailIntent, "test"), null)
//                            isDialogVisible = false
//                        },
//                        content = { Text("Send") },
//                    )
//                },
//                onDismiss = {
//                    isDialogVisible = false
//                },
//            )
//        }

    }
}

@Composable
fun MyAlertDialog(
    emailTitle: @Composable () -> Unit,
    titleBox: @Composable () -> Unit,
    messageTitle: @Composable () -> Unit,
    messageBox: @Composable () -> Unit,
    cancelButton: @Composable () -> Unit,
    sendButton: @Composable () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                Column(Modifier.padding(24.dp)) {
                    emailTitle.invoke()
                    Spacer(Modifier.size(16.dp))
                    titleBox.invoke()
                    Spacer(Modifier.size(16.dp))
                    messageTitle.invoke()
                    Spacer(Modifier.size(16.dp))
                    messageBox.invoke()
                }
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    cancelButton.invoke()
                    sendButton.invoke()
                }
            }
        }
    }
}