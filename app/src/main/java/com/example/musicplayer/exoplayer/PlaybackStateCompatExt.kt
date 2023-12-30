package com.example.musicplayer.exoplayer

import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED

inline val PlaybackStateCompat.isPrepared
    get() = state == STATE_BUFFERING ||
            state == STATE_PLAYING ||
            state == STATE_PAUSED

inline val PlaybackStateCompat.isPlaying
    get() = state == STATE_BUFFERING ||
            state == STATE_PLAYING

inline val PlaybackStateCompat.isPlayEnabled
    get() = actions and ACTION_PLAY != 0L ||
            (actions and ACTION_PLAY_PAUSE != 0L &&
                    state == STATE_PAUSED)

inline val PlaybackStateCompat.currentPlaybackPosition: Long
    get() = if (state == STATE_PLAYING) {
        val timeDelta = SystemClock.elapsedRealtime() - lastPositionUpdateTime
        (position + (timeDelta * playbackSpeed)).toLong()
    } else position