package com.gaminghub.musicplayer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    // ── Tracks ────────────────────────────────────────────────────────────────
    @Query("SELECT * FROM tracks WHERE isFavorite = 1")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE lastPlayedTimestamp IS NOT NULL ORDER BY lastPlayedTimestamp DESC LIMIT 20")
    fun getRecentHistory(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE audioUrl = :url LIMIT 1")
    suspend fun getTrackByUrl(url: String): TrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Transaction
    suspend fun toggleFavorite(url: String, trackRequest: TrackEntity) {
        val existing = getTrackByUrl(url)
        if (existing == null) {
            insertTrack(trackRequest.copy(isFavorite = true))
        } else {
            insertTrack(existing.copy(isFavorite = !existing.isFavorite))
        }
    }

    @Transaction
    suspend fun recordHistory(url: String, trackRequest: TrackEntity) {
        val existing = getTrackByUrl(url)
        val timestamp = System.currentTimeMillis()
        if (existing == null) {
            insertTrack(trackRequest.copy(lastPlayedTimestamp = timestamp))
        } else {
            insertTrack(existing.copy(lastPlayedTimestamp = timestamp))
        }
    }

    // ── Playlists ─────────────────────────────────────────────────────────────
    @Query("SELECT * FROM playlists ORDER BY createdAt ASC")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylist(id: Int)

    @Query("UPDATE playlists SET name = :newName WHERE id = :id")
    suspend fun renamePlaylist(id: Int, newName: String)
}
