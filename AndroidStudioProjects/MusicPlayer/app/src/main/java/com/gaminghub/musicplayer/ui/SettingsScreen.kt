package com.gaminghub.musicplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    val settingsItems = listOf(
        SettingsItemData("Theme", "Dark Mode, Accent Color & Hue, Use System Theme", Icons.Default.BrightnessMedium, "theme_settings"),
        SettingsItemData("App UI", "Player Screen Background, Buttons to show in Mini Player", Icons.Default.Edit, "app_ui_settings"),
        SettingsItemData("Music & Playback", "Music Language, Streaming Quality", Icons.Default.MusicNote, "music_playback_settings"),
        SettingsItemData("Others", "Language, Include/Exclude Folders, Min Audio Length", Icons.Default.Settings, "others_settings"),
        SettingsItemData("Backup & Restore", "Create Backup, Restore, Auto Backup", Icons.Default.Restore, "backup_restore_settings"),
        SettingsItemData("About", "Version, Share App, Contact Us", Icons.Default.Info, "about_settings")
    )

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
            }
            Text("Settings", color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Search", color = Color.Gray, fontSize = 16.sp)
            }
        }

        LazyColumn {
            items(settingsItems) { item ->
                SettingsItem(item) {
                    navController.navigate(item.route)
                }
            }
        }
    }
}

data class SettingsItemData(val title: String, val subtitle: String, val icon: ImageVector, val route: String)

@Composable
fun SettingsItem(item: SettingsItemData, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(24.dp))
        Column {
            Text(item.title, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(item.subtitle, color = Color.Gray, fontSize = 13.sp)
        }
    }
}
