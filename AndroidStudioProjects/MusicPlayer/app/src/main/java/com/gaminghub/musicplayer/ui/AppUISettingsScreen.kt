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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gaminghub.musicplayer.SettingsViewModel

@Composable
fun AppUISettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    val useDenseMiniplayer by settingsViewModel.useDenseMiniplayer.collectAsState()

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
                "App UI",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        SettingsDetailItem("Player Screen Background", "Selected Background will be shown in Player Screen", "")
        
        SettingsSwitchItemFunctional("Use Dense Miniplayer", "Miniplayer height will be reduced (You need to restart app)", useDenseMiniplayer) {
            settingsViewModel.setUseDenseMiniplayer(it)
        }

        SettingsDetailItem("Buttons to show in Mini Player", "Tap to change buttons shown in the Mini Player", "")
        SettingsDetailItem("Compact Notification Buttons", "Buttons to show in Compact Notification View", "")
        SettingsDetailItem("Blacklisted Home Sections", "Sections with these titles won't be shown on Home Screen", "")
        
        SettingsSwitchItemFunctional("Show Playlists on Home Screen", "", true) {}
        SettingsSwitchItemFunctional("Show Last Session", "Show Last session on Home Screen", true) {}
        SettingsDetailItem("Navigation Bar Tabs", "Tabs to be shown in bottom navigation bar", "")
        
        SettingsSwitchItemFunctional("Enable Artwork Gestures", "Enables tap, longpress, swipe, etc on the Artwork in Player Screen", true) {}
        SettingsSwitchItemFunctional("Enable Volume Gesture Controls", "Use vertical swipe on the Artwork in Player Screen to control volume instead of sliding player down", false) {}
        SettingsSwitchItemFunctional("Use Less Data for Images", "This will reduce the quality of images in the app, but will save your data", false) {}

        Spacer(modifier = Modifier.height(100.dp))
    }
}
