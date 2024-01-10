package com.example.musicplayer.data.remotedatabase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot

class MusicRemoteDatabase(private val songCollection: CollectionReference) {
    fun getAllSongs(): Task<QuerySnapshot> = songCollection.get()
}