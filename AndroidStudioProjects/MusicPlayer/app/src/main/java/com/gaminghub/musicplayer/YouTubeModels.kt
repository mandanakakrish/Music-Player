package com.gaminghub.musicplayer
import com.google.gson.annotations.SerializedName

data class YouTubeSearchResponse(
    val items: List<YouTubeSearchItem>
)

data class YouTubeSearchItem(
    val id: VideoId,
    val snippet: VideoSnippet
)

data class VideoId(
    val videoId: String // We need this to play the track later
)

data class VideoSnippet(
    val title: String,
    val channelTitle: String, // Acts as the Artist Name
    val thumbnails: Thumbnails
)

data class Thumbnails(
    val high: ThumbnailDetail
)

data class ThumbnailDetail(
    val url: String // The album artwork
)