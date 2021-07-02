package com.example.musicplayer.data.remote

import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.other.Constants.SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class MusicDatabase {

    private val db = FirebaseFirestore.getInstance()

    private val songCollection = db.collection(SONG_COLLECTION)

    suspend fun getAllSongs(): List<Song> {
        return try {
            songCollection
                .get()
                .await()
                .toObjects(Song::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}