package com.example.musicplayer.ui.songscreen

import android.graphics.Bitmap
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.bitmap.BitmapPool
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.Transformation
import com.example.musicplayer.R
import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.exoplayer.toSong
import com.example.musicplayer.ui.theme.roundedShape
import com.example.musicplayer.ui.viewmodels.MainViewModel
import com.example.musicplayer.ui.viewmodels.SongViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.systemBarsPadding
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SongScreen(
    backPressedDispatcher: OnBackPressedDispatcher,
    mainViewModel: MainViewModel = hiltViewModel(),
    songViewModel: SongViewModel = hiltViewModel()
) {
    val song = mainViewModel.currentPlayingSong.value
    AnimatedVisibility(
        visible = song != null && mainViewModel.showPlayerFullScreen,
        enter = slideInVertically(
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            targetOffsetY = { it }
        )) {
        if (song != null) {
            SongScreenBody(
                song = song.toSong()!!,
                backPressedDispatcher = backPressedDispatcher,
                mainViewModel = mainViewModel,
                songViewModel = songViewModel
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SongScreenBody(
    song: Song,
    backPressedDispatcher: OnBackPressedDispatcher,
    mainViewModel: MainViewModel,
    songViewModel: SongViewModel
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val endAnchor = LocalConfiguration.current.screenHeightDp * LocalDensity.current.density
    val anchors = mapOf(
        0f to 0,
        endAnchor to 1
    )

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainViewModel.showPlayerFullScreen = false
            }
        }
    }

    val backgroundColor = MaterialTheme.colors.background

    var dominantColor by remember {
        mutableStateOf(backgroundColor)
    }

    val context = LocalContext.current

    val imageLoader = ImageLoader(context)

    val request = ImageRequest.Builder(context)
        .data(song.imageUrl)
        .build()

    val imagePainter = rememberCoilPainter(
        request = request,
        requestBuilder = {
            transformations(
                object : Transformation {
                    override fun key(): String {
                        return song.imageUrl
                    }

                    override suspend fun transform(
                        pool: BitmapPool,
                        input: Bitmap,
                        size: Size
                    ): Bitmap {
                        songViewModel.calculateColorPalette(input) {
                            dominantColor = it
                        }
                        return input
                    }
                }
            )
        },
        fadeIn = true
    )

    val iconResId =
        if (mainViewModel.songIsPlaying) R.drawable.ic_round_pause else R.drawable.ic_round_play_arrow

    val isSongPlaying = mainViewModel.songIsPlaying

    var sliderIsChanging by remember { mutableStateOf(false) }

    var localSliderValue by remember { mutableStateOf(0f) }

    var localSongValue by remember {
        mutableStateOf(0f)
    }

    val sliderProgress =
        if (sliderIsChanging) localSliderValue else songViewModel.currentPlayerPosition

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.34f) },
                orientation = Orientation.Vertical
            )
    ) {
        if (swipeableState.currentValue >= 1) {
            LaunchedEffect("key") {
                mainViewModel.showPlayerFullScreen = false
            }
        }
        SongScreenContent(
            song = song,
            isSongPlaying = isSongPlaying,
            imagePainter = imagePainter,
            dominantColor = dominantColor,
            playbackProgress = sliderProgress,
            currentTime = songViewModel.currentPlaybackFormattedPosition,
            totalTime = songViewModel.currentSongFormattedPosition,
            playPauseIcon = iconResId,
            yOffset = swipeableState.offset.value.roundToInt(),
            playOrToggleSong = { mainViewModel.playOrToggleSong(song, true) },
            playNextSong = { mainViewModel.skipToNextSong() },
            playPreviousSong = { mainViewModel.skipToPreviousSong() },
            onSliderChange = { newPosition ->
                localSliderValue = newPosition
                sliderIsChanging = true
            },
            onSliderChangeFinished = {
                mainViewModel.seekTo(songViewModel.currentSongDuration * localSliderValue)
                sliderIsChanging = false
            },
            onForward = {
                songViewModel.currentPlaybackPosition.let { currentPosition ->
                    mainViewModel.seekTo(currentPosition + 10 * 1000f)
                }
            },
            onRewind = {
                songViewModel.currentPlaybackPosition.let { currentPosition ->
                    mainViewModel.seekTo(if (currentPosition - 10 * 1000f < 0) 0f else currentPosition - 10 * 1000f)
                }
            },
            onClose = {
                mainViewModel.showPlayerFullScreen = false
            }
        )
    }

    LaunchedEffect("playbackPosition") {
        songViewModel.updateCurrentPlaybackPosition()
    }

    DisposableEffect(backPressedDispatcher) {
        backPressedDispatcher.addCallback(backCallback)

        onDispose {
            backCallback.remove()
            mainViewModel.showPlayerFullScreen = false
        }
    }
}

@Composable
fun SongScreenContent(
    song: Song,
    isSongPlaying: Boolean,
    imagePainter: Painter,
    dominantColor: Color,
    playbackProgress: Float,
    currentTime: String,
    totalTime: String,
    @DrawableRes playPauseIcon: Int,
    yOffset: Int,
    playOrToggleSong: () -> Unit,
    playNextSong: () -> Unit,
    playPreviousSong: () -> Unit,
    onSliderChange: (Float) -> Unit,
    onSliderChangeFinished: () -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onClose: () -> Unit

) {


    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(
            dominantColor,
            MaterialTheme.colors.background
        )
    } else {
        listOf(
            MaterialTheme.colors.background,
            MaterialTheme.colors.background
        )
    }

    val sliderColors = if (isSystemInDarkTheme()) {
        SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.onBackground,
            activeTrackColor = MaterialTheme.colors.onBackground,
            inactiveTrackColor = MaterialTheme.colors.onBackground.copy(
                alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity
            ),
        )
    } else SliderDefaults.colors(
        thumbColor = dominantColor,
        activeTrackColor = dominantColor,
        inactiveTrackColor = dominantColor.copy(
            alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Surface {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = gradientColors,
                            endY = LocalConfiguration.current.screenHeightDp.toFloat() * LocalDensity.current.density
                        )
                    )
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                Column {
                    IconButton(
                        onClick = onClose
                    ) {
                        Image(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Close",
                            colorFilter = ColorFilter.tint(LocalContentColor.current)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 32.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .weight(1f, fill = false)
                                .aspectRatio(1f)

                        ) {
//                            Image(
//                                painter = imagePainter,
//                                contentDescription = "Song thumbnail",
//                                contentScale = ContentScale.Crop,
//                                modifier = Modifier
//                                    .fillMaxSize()
//                            )
                            VinylAnimation(painter = imagePainter, isSongPlaying = isSongPlaying)
                        }

                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.h5,
                            color = MaterialTheme.colors.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            song.subtitle,
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.graphicsLayer {
                                alpha = 0.60f
                            }
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Slider(
                                value = playbackProgress,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = sliderColors,
                                onValueChange = onSliderChange,
                                onValueChangeFinished = onSliderChangeFinished
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                    Text(
                                        currentTime,
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                    Text(
                                        totalTime,
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SkipPrevious,
                                contentDescription = "Skip Previous",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = playPreviousSong)
                                    .padding(12.dp)
                                    .size(32.dp)
                            )
                            Icon(
                                imageVector = Icons.Rounded.Replay10,
                                contentDescription = "Replay 10 seconds",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = onRewind)
                                    .padding(12.dp)
                                    .size(32.dp)
                            )
                            Icon(
                                painter = painterResource(playPauseIcon),
                                contentDescription = "Play",
                                tint = MaterialTheme.colors.background,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colors.onBackground)
                                    .clickable(onClick = playOrToggleSong)
                                    .size(64.dp)
                                    .padding(8.dp)
                            )
                            Icon(
                                imageVector = Icons.Rounded.Forward10,
                                contentDescription = "Forward 10 seconds",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = onForward)
                                    .padding(12.dp)
                                    .size(32.dp)
                            )
                            Icon(
                                imageVector = Icons.Rounded.SkipNext,
                                contentDescription = "Skip Next",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = playNextSong)
                                    .padding(12.dp)
                                    .size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Vinyl(
    modifier: Modifier = Modifier,
    rotationDegrees: Float = 0f,
    painter: Painter
) {
    Box(
        modifier = modifier
            .aspectRatio(1.0f)
            .clip(roundedShape)
    ) {
        // Vinyl background
        Image(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationDegrees),
            painter = painterResource(id = R.drawable.vinyl_background),
            contentDescription = "Vinyl Background"
        )

        // Vinyl song cover
        Image(
            modifier = Modifier
                .fillMaxSize(0.5f)
                .rotate(rotationDegrees)
                .aspectRatio(1.0f)
                .align(Alignment.Center)
                .clip(roundedShape),
            painter = painter,
            contentDescription = "Song cover"
        )
    }
}

@Composable
fun VinylAnimation(
    modifier: Modifier = Modifier,
    isSongPlaying: Boolean = true,
    painter: Painter
) {
    var currentRotation by remember {
        mutableStateOf(0f)
    }

    val rotation = remember {
        Animatable(currentRotation)
    }

    LaunchedEffect(isSongPlaying) {
        if (isSongPlaying) {
            rotation.animateTo(
                targetValue = currentRotation + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            ) {
                currentRotation = value
            }
        } else {
            if (currentRotation > 0f) {
                rotation.animateTo(
                    targetValue = currentRotation + 50,
                    animationSpec = tween(
                        1250,
                        easing = LinearOutSlowInEasing
                    )
                ) {
                    currentRotation = value
                }
            }
        }
    }

    Vinyl(painter = painter, rotationDegrees = rotation.value)
}


