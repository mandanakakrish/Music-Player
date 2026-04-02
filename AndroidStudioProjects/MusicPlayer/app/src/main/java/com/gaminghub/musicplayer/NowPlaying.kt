package com.gaminghub.musicplayer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(tracks: List<TrackModel>, viewModel: MusicViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Set this to a dark color in your theme
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Discover Music",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // A placeholder for a list of songs
            SongItemRow(title = "Neon Lights", artist = "Synthwave Master")
            SongItemRow(title = "Coding Focus", artist = "LoFi Beats")

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Player Control Placeholder
            BottomPlayerBar()
        }
    }
}

@Composable
fun SongItemRow(title: String, artist: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = artist, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun BottomPlayerBar() {
    Button(
        onClick = { /* TODO: Toggle Play/Pause via ExoPlayer */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Play / Pause")
    }
}