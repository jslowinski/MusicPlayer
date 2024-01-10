package com.example.musicplayer.domain.repository

import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.other.Resource
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getSongs(): Flow<Resource<List<Song>>>
}