package com.gaminghub.musicplayer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gaminghub.musicplayer.TrackModel

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val audioUrl: String, // YouTube URL as identifier
    val title: String,
    val artist: String,
    val albumArtUrl: String?,
    val isFavorite: Boolean = false,
    val lastPlayedTimestamp: Long? = null
)

fun TrackEntity.toModel() = TrackModel(
    title = title,
    artist = artist,
    audioUrl = audioUrl,
    albumArtUrl = albumArtUrl
)

fun TrackModel.toEntity(isFavorite: Boolean = false, lastPlayed: Long? = null) = TrackEntity(
    audioUrl = audioUrl ?: "",
    title = title,
    artist = artist,
    albumArtUrl = albumArtUrl,
    isFavorite = isFavorite,
    lastPlayedTimestamp = lastPlayed
)
