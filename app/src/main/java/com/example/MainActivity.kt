package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.di.DatabaseModule
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.HealthHubViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HealthHubApp()
            }
        }
    }
}

@Composable
fun HealthHubApp(
    viewModel: HealthHubViewModel = viewModel(
        factory = DatabaseModule.provideViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    MainScreen(viewModel = viewModel)
}
