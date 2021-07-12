package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.ui.home.HomeBottomBar
import com.example.musicplayer.ui.home.HomeScreen
import com.example.musicplayer.ui.navigation.Destination
import com.example.musicplayer.ui.songscreen.SongScreen
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerApp(
                backPressedDispatcher = onBackPressedDispatcher,
                startDestination = Destination.home
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MusicPlayerApp(
    startDestination: String = Destination.home,
    backPressedDispatcher: OnBackPressedDispatcher
) {
    val systemUiController = rememberSystemUiController()

    val useDarkIcons = !isSystemInDarkTheme()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }
    MusicPlayerTheme {
        val navController = rememberNavController()
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            NavHost(navController = navController, startDestination = startDestination) {
                composable(Destination.home) {
                    HomeScreen()
                }
            }
            HomeBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)

            )
            SongScreen(backPressedDispatcher = backPressedDispatcher)
        }

    }
}

