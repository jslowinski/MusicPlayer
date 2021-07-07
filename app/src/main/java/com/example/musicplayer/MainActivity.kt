package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.ui.home.HomeScreen
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.example.musicplayer.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home_screen") {
                    composable("home_screen") {
                        HomeScreen(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}