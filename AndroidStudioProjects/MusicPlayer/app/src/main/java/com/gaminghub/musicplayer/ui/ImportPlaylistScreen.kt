package com.gaminghub.musicplayer.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gaminghub.musicplayer.JoytifyPink
import com.gaminghub.musicplayer.MusicViewModel
import androidx.media3.common.util.UnstableApi

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun ImportPlaylistScreen(navController: NavController, viewModel: MusicViewModel? = null) {
    var showYoutubeDialog by remember { mutableStateOf(false) }
    var showRessoDialog by remember { mutableStateOf(false) }
    
    // File Picker for local imports
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        // Handle imported music files
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
                Text("Import Playlist", color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ImportActionItem("Import from File", Icons.AutoMirrored.Filled.ExitToApp) {
                filePicker.launch(arrayOf("audio/*"))
            }
            ImportActionItem("Import from YouTube", Icons.Filled.PlayCircleFilled) {
                showYoutubeDialog = true
            }
            ImportActionItem("Import from Resso", Icons.Default.MusicNote) {
                showRessoDialog = true
            }
        }
    }

    if (showYoutubeDialog) {
        ImportLinkDialog(
            title = "Enter YouTube Playlist Link",
            description = "Enter the full YouTube playlist link (e.g. https://www.youtube.com/playlist?list=...). \n\nTo obtain these links, go to the playlist page inside the YouTube app and tap the \"Share\" or \"Copy Link\" button. \n\nPlease make sure the playlist is public.",
            onDismiss = { showYoutubeDialog = false },
            onConfirm = { link ->
                val name = link.substringAfterLast("list=").take(20).ifBlank { "YouTube Playlist" }
                viewModel?.createPlaylist(name)
                navController.popBackStack()
            }
        )
    }

    if (showRessoDialog) {
        ImportLinkDialog(
            title = "Enter Resso Playlist Link",
            onDismiss = { showRessoDialog = false },
            onConfirm = { link ->
                val name = link.substringAfterLast("/").ifBlank { "Resso Playlist" }
                viewModel?.createPlaylist(name)
                navController.popBackStack()
            }
        )
    }
}

@Composable
fun ImportActionItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(24.dp))
        Text(title, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontSize = 16.sp)
    }
}

@Composable
fun ImportLinkDialog(title: String, description: String? = null, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2B2B2B),
        title = { Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium) },
        text = {
            Column {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    singleLine = true
                )
                if (description != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(description, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text); onDismiss() }) {
                Text("Ok", color = JoytifyPink, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        },
        shape = RoundedCornerShape(8.dp)
    )
}
