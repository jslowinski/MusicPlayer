package com.example.musicplayer.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.musicplayer.R
import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.other.Resource
import com.example.musicplayer.ui.viewmodels.MainViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.statusBarsHeight
import timber.log.Timber

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {

    val items by viewModel.mediaItems.observeAsState()

    println(viewModel.mediaItemsTest.value.toString())
    Surface(modifier = Modifier.fillMaxSize()) {
        HomeContent(
            modifier = Modifier.fillMaxSize(),
            music = viewModel.mediaItemsTest.value
        )

    }
}


@Composable
fun HomeAppBar(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = null
                )
                Text(text = "MusicPlayer")
            }
        },
        backgroundColor = backgroundColor,
        actions = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {


            }
        },
        modifier = modifier
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    music: Resource<List<Song>>
) {
    Column(modifier = modifier) {
        val appBarColor = MaterialTheme.colors.surface.copy(alpha = 0.87f)

        Spacer(
            Modifier
                .background(appBarColor)
                .fillMaxWidth()
                .statusBarsHeight()
        )

        HomeAppBar(
            backgroundColor = appBarColor,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        val scrollState = rememberScrollState()
        when (music) {
            is Resource.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .verticalScroll(state = scrollState)
                ) {
                    music.data?.forEach {
                        MusicListItem(
                            song = it,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                }
            }
            is Resource.Loading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .fillMaxHeight()
                        .padding(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                )
            }
        }
    }
}

@Composable
fun MusicListItem(
    viewModel: MainViewModel = hiltViewModel(),
    song: Song,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = Modifier
            .clickable {
                viewModel.playOrToggleSong(song)
            }
            .fillMaxWidth()
    ) {
        val (
            divider, songTitle, songSubtitle, image, playIcon,
            date
        ) = createRefs()

        Divider(
            Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)

                width = Dimension.fillToConstraints
            }
        )

        Image(
            painter = rememberCoilPainter(song.imageUrl, fadeIn = true),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium)
                .constrainAs(image) {
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(parent.top, 16.dp)
                }
        )

        Text(
            text = song.title,
            maxLines = 2,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.constrainAs(songTitle) {
                linkTo(
                    start = parent.start,
                    end = image.start,
                    startMargin = 24.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, 16.dp)

                width = Dimension.preferredWrapContent
            }
        )

        val titleImageBarrier = createBottomBarrier(songSubtitle, image)

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = song.subtitle,
                maxLines = 2,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.constrainAs(songSubtitle) {
                    linkTo(
                        start = parent.start,
                        end = image.start,
                        startMargin = 24.dp,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(songTitle.bottom, 6.dp)

                    width = Dimension.preferredWrapContent
                }
            )
        }

        Image(
            imageVector = Icons.Rounded.PlayCircleFilled,
            contentDescription = stringResource(id = R.string.cd_play),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            modifier = Modifier
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = rememberRipple(
                        bounded = false,
                        radius = 24.dp
                    )
                ) { /* TODO */ }
                .size(36.dp)
                .constrainAs(playIcon) {
                    start.linkTo(parent.start, 24.dp)
                    top.linkTo(titleImageBarrier, margin = 16.dp)
                    bottom.linkTo(parent.bottom, 16.dp)
                }
        )
    }
}