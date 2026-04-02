package com.gaminghub.musicplayer

// The specific data for each song (Generic model used across the app)
data class TrackModel(
    val title: String,
    val artist: String,
    val audioUrl: String?,
    val albumArtUrl: String?,
    val album: String? = null,
    val genre: String? = null
)
