package com.example.musicplayer.ui.viewmodels


import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.exoplayer.MusicServiceConnection
import com.example.musicplayer.exoplayer.isPlayEnabled
import com.example.musicplayer.exoplayer.isPlaying
import com.example.musicplayer.exoplayer.isPrepared
import com.example.musicplayer.other.Constants.MEDIA_ROOT_ID
import com.example.musicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkFailure = musicServiceConnection.networkFailure
    val nowPlayingSong = musicServiceConnection.nowPlaying
    val playbackState = musicServiceConnection.playbackState

    init {
        _mediaItems.postValue(Resource.Loading(null))
        musicServiceConnection.subscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = children.map { child ->
                    Song(
                        child.mediaId!!,
                        child.description.title.toString(),
                        child.description.subtitle.toString(),
                        child.description.mediaUri.toString(),
                        child.description.iconUri.toString()
                    )
                }
                _mediaItems.postValue(Resource.Success(items))
            }
        })
    }

    fun skipToNextSong() {
        musicServiceConnection.transportController.skipToNext()
    }

    fun skipToPreviousSong() {
        musicServiceConnection.transportController.skipToPrevious()
    }

    fun seekTo(pos: Long) {
        musicServiceConnection.transportController.seekTo(pos)
    }

    fun playMedia(mediaItem: Song, pauseAllowed: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaId == nowPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if (pauseAllowed) musicServiceConnection.transportController.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportController.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportController.playFromMediaId(mediaItem.mediaId, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback() {})
    }
}