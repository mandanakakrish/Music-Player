package com.gaminghub.musicplayer.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gaminghub.musicplayer.JoytifyPink
import com.gaminghub.musicplayer.MusicViewModel
import com.gaminghub.musicplayer.SettingsViewModel
import com.gaminghub.musicplayer.TrackModel
import kotlinx.coroutines.flow.flowOf

@OptIn(UnstableApi::class)
@Composable
fun HomeScreen(
    tracks: List<TrackModel>,
    viewModel: MusicViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavController,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recentTracks by viewModel.recentTracks.collectAsState()
    val userName by settingsViewModel.userName.collectAsState()
    val scrollState = androidx.compose.foundation.lazy.rememberLazyListState()
    
    // Calculate scroll fraction for animation (0f to 1f)
    // We want the animation to complete when the user has scrolled about 150dp
    val scrollFraction = remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0) 1f
            else {
                val firstItemOffset = scrollState.firstVisibleItemScrollOffset.toFloat()
                val targetOffset = 300f // Adjust as needed for smoothness
                (firstItemOffset / targetOffset).coerceIn(0f, 1f)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Content Area
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {
            // Spacer for the top bar area (initially)
            item { Spacer(modifier = Modifier.height(64.dp)) }

            // Greetings
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .graphicsLayer {
                            alpha = 1f - scrollFraction.value
                            translationY = -scrollFraction.value * 50f
                        }
                ) {
                    Text(
                        text = "Hi There,",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = userName,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Placeholder for Search Bar to reserve space in the list
            item {
                Spacer(modifier = Modifier.height(110.dp))
            }

            // Your Playlists
            item {
                HorizontalPlaylistSection(
                    title = "Your Playlists",
                    playlists = listOf(
                        PlaylistData("Favorite Songs", "3 Songs", tracks.take(4)),
                        PlaylistData("#", "46 Songs", tracks.drop(4).take(4))
                    ),
                    onPlaylistClick = {}
                )
            }

            // Last Session
            if (recentTracks.isNotEmpty()) {
                item { SectionHeader("Last Session") }
                items(recentTracks.take(4)) { track ->
                    TrackListItem(track = track, viewModel = viewModel, queue = recentTracks)
                }
            }

            // Quick Picks
            item { SectionHeader("Quick picks", showPlayAll = true) }
            items(tracks.take(4)) { track ->
                TrackListItem(track = track, viewModel = viewModel, subtitle = "${(1..5).random()}.${(0..9).random()}B plays", queue = tracks)
            }

            // Trending Community Playlists
            item { 
                HorizontalGridPlaylistSection("Trending community playlists", tracks.shuffled().take(minOf(tracks.size, 6)))
            }

            // New Releases
            item {
                HorizontalSection("New releases", tracks.shuffled().take(minOf(tracks.size, 6)))
            }

            // Genres
            item {
                HorizontalGenreSection("Old School Windows", tracks.shuffled().take(minOf(tracks.size, 6)))
            }
            item {
                HorizontalGenreSection("Dancing on your own", tracks.shuffled().take(minOf(tracks.size, 6)))
            }
            item {
                HorizontalGenreSection("Brb, Being Nostalgic!", tracks.shuffled().take(minOf(tracks.size, 6)))
            }
            
            // Bottom spacer to ensure scrolling past the last item comfortably
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        // Animated Header Overlay
        val headerBackgroundAlpha = scrollFraction.value * 0.9f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp) // Large enough to cover the initial greeting/search transition area
        ) {
            // Background that fades in on scroll
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.Black.copy(alpha = headerBackgroundAlpha))
            )

            // Menu Icon (Stays at the top left)
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }

            // Moving Search Bar
            val searchBarY = lerp(190f, 12f, scrollFraction.value)
            val searchBarPaddingStart = lerp(16f, 60f, scrollFraction.value)
            val searchBarPaddingEnd = lerp(16f, 16f, scrollFraction.value)
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = searchBarPaddingStart.dp, end = searchBarPaddingEnd.dp)
                    .offset(y = searchBarY.dp)
                    .height(54.dp)
                    .clickable { navController.navigate("search") },
                color = Color(0xFF151515).copy(alpha = 0.9f + (scrollFraction.value * 0.1f)),
                shape = RoundedCornerShape(27.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Songs, albums or artists", color = Color.Gray, fontSize = 15.sp)
                }
            }
        }
    }
}


// Helper for linear interpolation
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}

data class PlaylistData(
    val name: String,
    val songCount: String,
    val tracks: List<TrackModel>
)

@Composable
fun HorizontalPlaylistSection(
    title: String,
    playlists: List<PlaylistData>,
    onPlaylistClick: (PlaylistData) -> Unit
) {
    SectionHeader(title)
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(playlists) { playlist ->
            PlaylistCard(playlist = playlist, onClick = { onPlaylistClick(playlist) })
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun PlaylistCard(playlist: PlaylistData, onClick: () -> Unit) {
    val cardSize = 180.dp
    val halfSize = 90.dp

    Column(
        modifier = Modifier
            .width(cardSize)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(cardSize)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E1E1E))
        ) {
            if (playlist.tracks.size >= 4) {
                // 2x2 grid of album art
                Column {
                    Row(modifier = Modifier.height(halfSize)) {
                        AsyncImage(
                            model = playlist.tracks[0].albumArtUrl,
                            contentDescription = null,
                            modifier = Modifier.size(halfSize),
                            contentScale = ContentScale.Crop
                        )
                        AsyncImage(
                            model = playlist.tracks[1].albumArtUrl,
                            contentDescription = null,
                            modifier = Modifier.size(halfSize),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Row(modifier = Modifier.height(halfSize)) {
                        AsyncImage(
                            model = playlist.tracks[2].albumArtUrl,
                            contentDescription = null,
                            modifier = Modifier.size(halfSize),
                            contentScale = ContentScale.Crop
                        )
                        AsyncImage(
                            model = playlist.tracks[3].albumArtUrl,
                            contentDescription = null,
                            modifier = Modifier.size(halfSize),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else if (playlist.tracks.isNotEmpty()) {
                // Single image cover
                AsyncImage(
                    model = playlist.tracks.first().albumArtUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Empty placeholder with music note icon
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = playlist.name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = playlist.songCount,
            color = Color.Gray,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
fun OverlayCard(track: TrackModel, category: String, color: Color = JoytifyPink, onClick: () -> Unit = {}) {
    Column(modifier = Modifier.width(160.dp).clickable { onClick() }) {
        Box(modifier = Modifier.size(160.dp).clip(RoundedCornerShape(12.dp))) {
            AsyncImage(
                model = track.albumArtUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 100f
                        )
                    )
            )
            Surface(
                modifier = Modifier.padding(8.dp).size(24.dp),
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.padding(4.dp))
            }
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
            ) {
                Text(track.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(category, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(track.title, color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(track.artist, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun HorizontalGridPlaylistSection(title: String, tracks: List<TrackModel>) {
    SectionHeader(title)
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tracks) { track ->
            Column(modifier = Modifier.width(160.dp)) {
                Box(modifier = Modifier.size(160.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF222222))) {
                    AsyncImage(
                        model = track.albumArtUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(track.title, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp, maxLines = 1)
                Text(track.artist, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}

@Composable
fun HorizontalGenreSection(title: String, tracks: List<TrackModel>) {
    SectionHeader(title)
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tracks) { track ->
            OverlayCard(track, "Dance", onClick = { })
        }
    }
}

@Composable
fun SectionHeader(title: String, showPlayAll: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (showPlayAll) {
            Surface(
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(
                    text = "Play all",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun HorizontalSection(title: String, tracks: List<TrackModel>, showPlayAll: Boolean = false) {
    SectionHeader(title, showPlayAll)
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tracks) { track ->
            Column(modifier = Modifier.width(140.dp)) {
                AsyncImage(
                    model = track.albumArtUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = track.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun TrackListItem(track: TrackModel, viewModel: MusicViewModel, subtitle: String? = null, queue: List<TrackModel> = emptyList()) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.playTrack(track, queue)
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.albumArtUrl,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle ?: track.artist,
                color = Color.Gray,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        val isFavorite by (track.audioUrl?.let { viewModel.isFavorite(it) } ?: flowOf(false)).collectAsState(initial = false)
        
        IconButton(onClick = { viewModel.toggleFavorite(track) }) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) Color.Red else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        var showMenu by remember { mutableStateOf(false) }
        IconButton(onClick = { showMenu = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(20.dp))
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color(0xFF2B2B2B))
            ) {
                DropdownMenuItem(
                    text = { Text("Remove", color = Color.White) },
                    onClick = { showMenu = false },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White) }
                )
                DropdownMenuItem(
                    text = { Text("Play Next", color = Color.White) },
                    onClick = { showMenu = false },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.PlaylistPlay, contentDescription = null, tint = Color.White) }
                )
                DropdownMenuItem(
                    text = { Text("Add to Queue", color = Color.White) },
                    onClick = { showMenu = false },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = null, tint = Color.White) }
                )
                DropdownMenuItem(
                    text = { Text("Add to Playlist", color = Color.White) },
                    onClick = { showMenu = false },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = null, tint = Color.White) }
                )
                DropdownMenuItem(
                    text = { Text("View Album", color = Color.White) },
                    onClick = { showMenu = false },
                    leadingIcon = { Icon(Icons.Default.Album, contentDescription = null, tint = Color.White) }
                )
                DropdownMenuItem(
                    text = { Text("View Artist", color = Color.White) },
                    onClick = { showMenu = false },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) }
                )
                DropdownMenuItem(
                    text = { Text("Play Radio", color = Color.White) },
                    onClick = { showMenu = false },
                    leadingIcon = { Icon(Icons.Default.Radio, contentDescription = null, tint = Color.White) }
                )
                DropdownMenuItem(
                    text = { Text("Share", color = Color.White) },
                    onClick = { showMenu = false },
                    leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = Color.White) }
                )
            }
        }
    }
}


