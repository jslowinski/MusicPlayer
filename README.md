# Music Player
<p align="center">
  <a href="https://android-arsenal.com/api?level=26"><img alt="API" src="https://img.shields.io/badge/API-26%2B-brightgreen"/></a>
  <a href="https://github.com/jslowinski"><img alt="Profile" src="https://jslowinski.github.io/Website/badges/jslowinski.svg"/></a> 
</p>

Music Player is an Android application created for playing with Jetpack Compose framework and ExoPlayer.

## Result

### Dark/Light Mode and Notification
| ![list](previews/darkList.png) | ![player](previews/darkPlayer.png) |![list](previews/lightList.png) |![player](previews/lightPlayer.png) | ![notification](previews/notificationPlayer.png) |
|----------|:----------:|:--------:|:---------:|:---------:|

## Tech stack & Open-source libraries

- [Kotlin](https://kotlinlang.org/) based
- [ExoPlayer](https://github.com/google/ExoPlayer) for playing multimedia files
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for asynchronous.
- [JetPack](https://developer.android.com/jetpack)
  - [Compose](https://developer.android.com/jetpack/compose) - Modern toolkit for building native UI.
  - [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle) - Create a UI that automatically responds to lifecycle events.
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Notify domain layer data to views.
  - [Navigation](https://developer.android.com/jetpack/compose/navigation) - Handle everything needed for in-app navigation.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - UI related data holder, lifecycle aware.
- [Hilt](https://dagger.dev/hilt/) - For [dependency injection](https://developer.android.com/training/dependency-injection/hilt-android).
- [Coil](https://github.com/coil-kt/coil) - An image loading library for Android backed by Kotlin Coroutines.
- [Pallete API](https://developer.android.com/training/material/palette-colors)
- [Firebase](https://firebase.google.com/) - Firestore Database and Storage

## Features

- Jetpack Compose UI. Custom animations, transiations, light/dark theme and layouts.
- Jetpack Compose Navigation
- MVVM Architecutre
- Dependency injection with Hilt
- Retrieves songs metadata from the network specifically from firebase
- Allows background playback using a foreground service
- Media style notifications
- Uses a MediaBrowserService to control and expose the current media session
- Controls the current playback state with actions such as: play/pause, swipe bottom bar to skip next/previous, skip to next/previous, shuffle, repeat and stop



