package com.gaminghub.musicplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun OthersSettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Text(
                "Others",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        SettingsDetailItem("Language", "App Text Language", "English")
        SettingsDetailItem("Include/Exclude Folders", "Includes/Excludes selected folders from 'My Music' Section", "")
        SettingsDetailItem("Min Audio Length to search music", "Audios with length smaller than this will not be shown in 'My Music' Section", "")
        
        SettingsSwitchItem("Live Search", "Search songs as soon as user stops typing", true)
        SettingsSwitchItem("Stream Downloaded Songs, If available", "If song is already downloaded, downloaded song will be played instead of streaming online", false)
        SettingsSwitchItem("Search lyrics of local songs", "Search online if lyrics aren't available/downloaded for any offline song", true)
        SettingsSwitchItem("Support Equalizer", "Keep this off if you are unable to play songs (in both online and offline mode)", false)
        SettingsSwitchItem("Stop music on App Close", "If turned off, music won't stop even after app is 'closed', until you press stop button.", true)
        SettingsSwitchItem("Use Proxy", "Turn this on if you are not from India and having issues with search...", false)
        
        SettingsDetailItem("Proxy Settings", "Change Proxy IP and Port", "103.47.67.134:8080")
        SettingsDetailItem("Clear Cached Details", "Deletes Cached details including Homepage, Spotify Top Charts...", "0.55 MB")

        Spacer(modifier = Modifier.height(100.dp))
    }
}
