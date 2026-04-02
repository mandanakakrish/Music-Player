package com.gaminghub.musicplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gaminghub.musicplayer.SettingsViewModel

@Composable
fun ThemeSettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val useSystemTheme by settingsViewModel.useSystemTheme.collectAsState()

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
                "Theme",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        SettingsSwitchItemFunctional("Dark Mode", "", isDarkMode) { settingsViewModel.setDarkMode(it) }
        SettingsSwitchItemFunctional("Use System Theme", "", useSystemTheme) { settingsViewModel.setUseSystemTheme(it) }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Accent Color & Hue", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
                Text("White, 100", color = Color.Gray, fontSize = 12.sp)
            }
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(if (isDarkMode) Color.White else Color.Black))
        }

        ThemeCircleItem("Background Gradient", "Gradient used as background everywhere")
        ThemeCircleItem("Card Gradient", "Gradient used in Cards")
        ThemeCircleItem("Bottom Sheets Gradient", "Gradient used in Bottom Sheets")

        SettingsDropdownItem("Canvas Color", "Color of Background Canvas", "Grey")
        SettingsDropdownItem("Card Color", "Color of Search Bar, Alert Dialogs, Cards", "Grey900")
        
        SettingsSwitchItemFunctional("Use Amoled Dark Mode Settings", "", false) {}
        SettingsDropdownItem("Current Theme", "", "Custom")
        
        Text(
            text = "Save Theme",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun ThemeCircleItem(title: String, subtitle: String) {
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
        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.DarkGray))
    }
}

@Composable
fun SettingsSwitchItemFunctional(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
            if (subtitle.isNotEmpty()) {
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = com.gaminghub.musicplayer.JoytifyPink
            )
        )
    }
}
