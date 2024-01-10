package com.example.musicplayer.data.repository

import com.example.musicplayer.data.dto.SongDto
import com.example.musicplayer.data.mapper.toSong
import com.example.musicplayer.data.remotedatabase.MusicRemoteDatabase
import com.example.musicplayer.domain.repository.MusicRepository
import com.example.musicplayer.other.Resource
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicRemoteDatabase: MusicRemoteDatabase,
) :
    MusicRepository {
    override fun getSongs() =
        flow {
            val songs = musicRemoteDatabase.getAllSongs().await().toObjects<SongDto>()

            if (songs.isNotEmpty()) {
                emit(Resource.Success(songs.map { it.toSong() }))
            }

        }

}