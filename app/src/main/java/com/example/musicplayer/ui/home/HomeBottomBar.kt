package com.example.musicplayer.ui.home


import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicplayer.R
import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.exoplayer.isPlaying
import com.example.musicplayer.exoplayer.toSong
import com.example.musicplayer.ui.viewmodels.MainViewModel
import com.google.accompanist.coil.rememberCoilPainter

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {

    var offsetX by remember { mutableStateOf(0f) }
    val currentSong = viewModel.currentPlayingSong.value
    val playbackStateCompat by viewModel.playbackState.observeAsState()

    AnimatedVisibility(
        visible = currentSong != null,
        modifier = modifier
    ) {
        if (currentSong != null) {
            val song = currentSong.toSong()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                when {
                                    offsetX > 0 -> {
                                        viewModel.skipToPreviousSong()
                                    }
                                    offsetX < 0 -> {
                                        viewModel.skipToNextSong()
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consumeAllChanges()
                                val (x, y) = dragAmount
                                offsetX = x
                            }
                        )

                    }
                    .background(
                        if (!isSystemInDarkTheme()) {
                            Color.LightGray
                        } else Color.DarkGray
                    ),
            ) {
                HomeBottomBarItem(
                    song = song!!,
                    playbackStateCompat = playbackStateCompat,
                    viewModel = viewModel
                )
            }
        }
    }
}


@Composable
fun HomeBottomBarItem(
    song: Song,
    playbackStateCompat: PlaybackStateCompat?,
    viewModel: MainViewModel
) {


    Box(
        modifier = Modifier
            .height(64.dp)
            .clickable(onClick = {
                viewModel.showPlayerFullScreen = true
            })

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberCoilPainter(song.imageUrl),
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .offset(16.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 32.dp),
            ) {
                Text(
                    song.title,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    song.subtitle,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = 0.60f
                        }

                )
            }
            val painter = rememberCoilPainter(
                request = if (playbackStateCompat?.isPlaying == false) {
                    R.drawable.ic_round_play_arrow
                } else {
                    R.drawable.ic_round_pause
                }
            )

            Image(
                painter = painter,
                contentDescription = "Music",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(48.dp)
                    .clickable(
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 24.dp
                        )
                    ) { viewModel.playOrToggleSong(song, true) },
            )

        }
    }
}

