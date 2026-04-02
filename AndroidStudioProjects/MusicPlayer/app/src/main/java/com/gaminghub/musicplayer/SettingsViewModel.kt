package com.gaminghub.musicplayer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    // Theme Settings
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _useSystemTheme = MutableStateFlow(false)
    val useSystemTheme: StateFlow<Boolean> = _useSystemTheme

    // Music & Playback Settings
    private val _musicLanguage = MutableStateFlow("None")
    val musicLanguage: StateFlow<String> = _musicLanguage

    private val _streamingQuality = MutableStateFlow("96 kbps")
    val streamingQuality: StateFlow<String> = _streamingQuality

    // App UI Settings
    private val _playerBackground = MutableStateFlow("Default")
    val playerBackground: StateFlow<String> = _playerBackground

    private val _useDenseMiniplayer = MutableStateFlow(false)
    val useDenseMiniplayer: StateFlow<Boolean> = _useDenseMiniplayer

    private val _userName = MutableStateFlow("Krish")
    val userName: StateFlow<String> = _userName

    // Actions
    fun setDarkMode(enabled: Boolean) { _isDarkMode.value = enabled }
    fun setUseSystemTheme(enabled: Boolean) { _useSystemTheme.value = enabled }
    fun setMusicLanguage(language: String) { _musicLanguage.value = language }
    fun setStreamingQuality(quality: String) { _streamingQuality.value = quality }
    fun setPlayerBackground(background: String) { _playerBackground.value = background }
    fun setUseDenseMiniplayer(enabled: Boolean) { _useDenseMiniplayer.value = enabled }
    fun setUserName(name: String) { _userName.value = name }
}
