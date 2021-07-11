package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.request.ImageRequest
import com.example.musicplayer.ui.home.HomeBottomBar
import com.example.musicplayer.ui.home.HomeScreen
import com.example.musicplayer.ui.navigation.Destination
import com.example.musicplayer.ui.navigation.Navigator
import com.example.musicplayer.ui.navigation.ProvideNavHostController
import com.example.musicplayer.ui.songscreen.SongScreen
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.google.accompanist.coil.rememberCoilPainter
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale

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