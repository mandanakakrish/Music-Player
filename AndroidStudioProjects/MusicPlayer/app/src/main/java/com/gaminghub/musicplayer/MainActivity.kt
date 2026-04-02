package com.gaminghub.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.gaminghub.musicplayer.ui.AboutSettingsScreen
import com.gaminghub.musicplayer.ui.AppUISettingsScreen
import com.gaminghub.musicplayer.ui.BackupRestoreSettingsScreen
import com.gaminghub.musicplayer.ui.FavoritesScreen
import com.gaminghub.musicplayer.ui.HomeScreen
import com.gaminghub.musicplayer.ui.ImportPlaylistScreen
import com.gaminghub.musicplayer.ui.LibraryScreen
import com.gaminghub.musicplayer.ui.MusicPlaybackSettingsScreen
import com.gaminghub.musicplayer.ui.MyMusicScreen
import com.gaminghub.musicplayer.ui.NowPlayingScreen
import com.gaminghub.musicplayer.ui.OthersSettingsScreen
import com.gaminghub.musicplayer.ui.PlaylistsScreen
import com.gaminghub.musicplayer.ui.SettingsScreen
import com.gaminghub.musicplayer.ui.StatsScreen
import com.gaminghub.musicplayer.ui.ThemeSettingsScreen
import com.gaminghub.musicplayer.ui.TopChartsScreen
import com.gaminghub.musicplayer.ui.YouTubeScreen
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

val JoytifyPink = Color(0xFFE91E63)

class MainActivity : ComponentActivity() {
    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

            MaterialTheme(
                colorScheme = if (isDarkMode) {
                    darkColorScheme(
                        primary = JoytifyPink,
                        surface = Color(0xFF121212),
                        background = Color(0xFF000000)
                    )
                } else {
                    lightColorScheme(
                        primary = JoytifyPink,
                        surface = Color(0xFFFFFFFF),
                        background = Color(0xFFF5F5F5)
                    )
                }
            ) {
                val musicViewModel: MusicViewModel = viewModel()
                JoytifyMainScreen(viewModel = musicViewModel, settingsViewModel = settingsViewModel)
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoytifyMainScreen(viewModel: MusicViewModel, settingsViewModel: SettingsViewModel) {
    val realTracks by viewModel.tracks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    "Joytify",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(24.dp))
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = currentRoute == "home",
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate("home") { launchSingleTop = true }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                NavigationDrawerItem(
                    label = { Text("My Music") },
                    selected = currentRoute == "my_music",
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate("my_music") { launchSingleTop = true }
                    },
                    icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                NavigationDrawerItem(
                    label = { Text("Playlists") },
                    selected = currentRoute == "playlists",
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate("playlists") { launchSingleTop = true }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.PlaylistPlay, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = currentRoute == "settings",
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate("settings") { launchSingleTop = true }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                NavigationDrawerItem(
                    label = { Text("Help us by rating") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Go Premium now", color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("👑")
                    }
                }
            }
        }
    ) {
        val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }
        Scaffold(
            bottomBar = {
                if (currentRoute != "now_playing" && currentRoute != "search") {
                    Column {
                        MiniPlayer(viewModel = viewModel, settingsViewModel = settingsViewModel, onClick = { navController.navigate("now_playing") })
                        JoytifyBottomNavBar(navController = navController)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(paddingValues)) {
                composable("home") { HomeScreen(tracks = realTracks, viewModel = viewModel, settingsViewModel = settingsViewModel, navController = navController, onMenuClick = openDrawer) }
                composable("top_charts") { TopChartsScreen(viewModel = viewModel) }
                composable("youtube") { YouTubeScreen(viewModel = viewModel, onMenuClick = openDrawer) }
                composable("library") { LibraryScreen(viewModel = viewModel, navController = navController, onMenuClick = openDrawer) }
                composable("search") { com.gaminghub.musicplayer.ui.SearchScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) }
                composable("my_music") { MyMusicScreen(viewModel = viewModel, navController = navController) }
                composable("my_music/{playlistName}") { backStackEntry ->
                    val playlistName = backStackEntry.arguments?.getString("playlistName")
                    MyMusicScreen(viewModel = viewModel, navController = navController, playlistName = playlistName)
                }
                composable("playlists") { PlaylistsScreen(navController = navController, viewModel = viewModel) }
                composable("import_playlist") { ImportPlaylistScreen(navController = navController, viewModel = viewModel) }
                composable("stats") { StatsScreen(navController = navController) }
                composable("settings") { SettingsScreen(navController = navController) }
                composable("music_playback_settings") { MusicPlaybackSettingsScreen(navController = navController, settingsViewModel = settingsViewModel) }
                composable("about_settings") { AboutSettingsScreen(navController = navController) }
                composable("others_settings") { OthersSettingsScreen(navController = navController) }
                composable("backup_restore_settings") { BackupRestoreSettingsScreen(navController = navController) }
                composable("theme_settings") { ThemeSettingsScreen(navController = navController, settingsViewModel = settingsViewModel) }
                composable("app_ui_settings") { AppUISettingsScreen(navController = navController, settingsViewModel = settingsViewModel) }
                
                composable("subscriptions") { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Subscriptions Coming Soon") } }
                composable("now_playing") { NowPlayingScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) }
                composable("last_session") { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Last Session Coming Soon") } }
                composable("favorites") { FavoritesScreen(viewModel = viewModel, navController = navController) }
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun MiniPlayer(viewModel: MusicViewModel, settingsViewModel: SettingsViewModel, onClick: () -> Unit) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val useDenseMiniplayer by settingsViewModel.useDenseMiniplayer.collectAsState()

    currentTrack?.let { track ->
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable { onClick() },
            color = Color(0xFF1A1A1A), // Sleek dark grey
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = track.albumArtUrl,
                    contentDescription = "Currently Playing",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
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
                
                // Heart icon next to play controls
                IconButton(onClick = { viewModel.toggleFavorite(track) }) {
                    val isFavorite by (track.audioUrl?.let { viewModel.isFavorite(it) } ?: flowOf(false)).collectAsState(initial = false)
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) JoytifyPink else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = JoytifyPink,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { viewModel.togglePlayPause() }) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                IconButton(onClick = { viewModel.playNext() }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun JoytifyBottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.Black,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Pill
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { 
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                color = if (currentRoute == "home") Color(0xFF2B2B2B) else Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (currentRoute == "home") Icons.Default.Home else Icons.Outlined.Home,
                        contentDescription = "Home",
                        tint = if (currentRoute == "home") Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    if (currentRoute == "home") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Home", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // Charts
            IconButton(onClick = { 
                navController.navigate("top_charts") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = if (currentRoute == "top_charts") Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Outlined.TrendingUp,
                    contentDescription = "Charts",
                    tint = if (currentRoute == "top_charts") Color.White else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // YouTube
            IconButton(onClick = { 
                navController.navigate("youtube") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = if (currentRoute == "youtube") Icons.Filled.PlayCircleFilled else Icons.Outlined.PlayCircleOutline,
                    contentDescription = "YouTube",
                    tint = if (currentRoute == "youtube") Color.White else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Library Pill
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { 
                        navController.navigate("library") { 
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                color = if (currentRoute?.startsWith("my_music") == true || currentRoute == "playlists" || currentRoute == "library") Color(0xFF2B2B2B) else Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (currentRoute == "my_music" || currentRoute == "playlists" || currentRoute == "library") Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic,
                        contentDescription = "Library",
                        tint = if (currentRoute == "my_music" || currentRoute == "playlists" || currentRoute == "library") Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    if (currentRoute == "my_music" || currentRoute == "playlists" || currentRoute == "library") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Library", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
