package com.gaminghub.musicplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gaminghub.musicplayer.SettingsViewModel

@Composable
fun MusicPlaybackSettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
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
                "Music & Playback",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        val musicLanguage by settingsViewModel.musicLanguage.collectAsState()
        val streamingQuality by settingsViewModel.streamingQuality.collectAsState()

        SettingsHeader("Music Language")
        SettingsDetailItem("Music Language", "To display songs on Home Screen", musicLanguage)
        SettingsDetailItem("Spotify Local Charts Location", "Country for Top Spotify Local Charts", "India")

        SettingsHeader("Streaming Quality")
        SettingsDropdownItem("Streaming Quality", "Higher quality uses more data", streamingQuality)
        SettingsDropdownItem("Streaming Quality (Wifi)", "This will be used whenever Wifi is connected", "320 kbps")
        SettingsDropdownItem("YouTube Streaming Quality", "Higher quality uses more data", "Low")

        SettingsHeader("Playback Options")
        SettingsSwitchItem("Load Last Session on App Start", "Automatically load last session when app starts", true)
        SettingsSwitchItem("Replay on Skip Previous", "Replay from start instead of skipping to previous song", false)
        SettingsSwitchItem("Enforce Repeating", "Keep the same repeat option for every session", false)
        SettingsSwitchItem("Autoplay", "Automatically add related songs to the queue", true)
        SettingsSwitchItem("Cache Songs", "Songs will be cached for future playback. Additional space on your device will be taken", false)
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        color = Color.Gray,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsDetailItem(title: String, subtitle: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Text(value, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
    }
}

@Composable
fun SettingsDropdownItem(title: String, subtitle: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun SettingsSwitchItem(title: String, subtitle: String, initialValue: Boolean) {
    var checked by remember { mutableStateOf(initialValue) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = com.gaminghub.musicplayer.JoytifyPink
            )
        )
    }
}
