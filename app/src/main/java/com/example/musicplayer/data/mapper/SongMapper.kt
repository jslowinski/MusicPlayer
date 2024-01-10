package com.example.musicplayer.data.mapper

import androidx.media3.common.MediaItem
import com.example.musicplayer.data.dto.SongDto
import com.example.musicplayer.domain.model.Song

fun SongDto.toSong() =
    Song(
        mediaId = mediaId,
        title = title,
        subtitle = subtitle,
        songUrl = songUrl,
        imageUrl = imageUrl
    )

fun MusicDto.toSong() =
    Song(
        mediaId = id,
        title = title,
        subtitle = artist,
        songUrl = source,
        imageUrl = image
    )

fun MediaItem.toSong() =
    Song(
        mediaId = mediaId,
        title = mediaMetadata.title.toString(),
        subtitle = mediaMetadata.subtitle.toString(),
        songUrl = mediaId,
        imageUrl = mediaMetadata.artworkUri.toString()
    )