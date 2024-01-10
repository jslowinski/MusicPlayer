package com.example.musicplayer.di

import android.content.Context
import com.example.musicplayer.data.remotedatabase.MusicRemoteDatabase
import com.example.musicplayer.data.repository.MusicRepositoryImpl
import com.example.musicplayer.data.service.MusicControllerImpl
import com.example.musicplayer.domain.repository.MusicRepository
import com.example.musicplayer.domain.service.MusicController
import com.example.musicplayer.other.Constants
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCollection() = FirebaseFirestore.getInstance().collection(Constants.SONG_COLLECTION)

    @Singleton
    @Provides
    fun provideMusicDatabase(songCollection: CollectionReference) =
        MusicRemoteDatabase(songCollection)


    @Singleton
    @Provides
    fun provideMusicRepository(
        musicRemoteDatabase: MusicRemoteDatabase,
    ): MusicRepository =
        MusicRepositoryImpl(musicRemoteDatabase)

    @Singleton
    @Provides
    fun provideMusicController(@ApplicationContext context: Context): MusicController =
        MusicControllerImpl(context)
}