package com.example.musicplayer.ui.home

import com.example.musicplayer.domain.model.Song

sealed class HomeEvent {
    object PlaySong : HomeEvent()
    object PauseSong : HomeEvent()
    object ResumeSong : HomeEvent()
    object FetchSong : HomeEvent()
    object SkipToNextSong : HomeEvent()
    object SkipToPreviousSong : HomeEvent()
    data class OnSongSelected(val selectedSong: Song) : HomeEvent()
}