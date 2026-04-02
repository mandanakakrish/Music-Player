package com.gaminghub.musicplayer.ui

import android.content.Intent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.gaminghub.musicplayer.MusicViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(viewModel: MusicViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val upNextQueue by viewModel.upNextQueue.collectAsState()

    val isFavorite by (currentTrack?.audioUrl?.let { viewModel.isFavorite(it) }
        ?: flowOf(false)).collectAsState(initial = false)

    // UI state
    var showMenu by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var isShuffleOn by remember { mutableStateOf(false) }
    var isRepeatOn by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 72.dp,
        sheetContainerColor = Color(0xFF1A1A1A),
        sheetContentColor = Color.White,
        sheetDragHandle = null,
        containerColor = Color.Black,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContent = {
            UpNextPanel(
                currentTrack = currentTrack,
                upNextQueue = upNextQueue,
                viewModel = viewModel,
                onTrackClick = { index -> viewModel.playFromQueue(index) },
                onReorder = { from, to -> viewModel.reorderQueue(from, to) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 16.dp)
                .padding(bottom = innerPadding.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Top Bar ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.KeyboardArrowDown, "Back", tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showLyrics = !showLyrics }) {
                        Icon(Icons.Default.QueueMusic, "Lyrics", tint = if (showLyrics) Color.White else Color.White.copy(alpha = 0.8f))
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Check out this song!")
                            putExtra(Intent.EXTRA_TEXT, "🎵 ${currentTrack?.title ?: "Unknown"} by ${currentTrack?.artist ?: "Unknown"}\n${currentTrack?.audioUrl ?: ""}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                    }) {
                        Icon(Icons.Default.Share, "Share", tint = Color.White.copy(alpha = 0.8f))
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "More", tint = Color.White.copy(alpha = 0.8f))
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(Color(0xFF2B2B2B))) {
                            DropdownMenuItem(text = { Text("View Album", color = Color.White) }, onClick = { showMenu = false }, leadingIcon = { Icon(Icons.Default.Album, null, tint = Color.White) })
                            DropdownMenuItem(text = { Text("Add to Playlist", color = Color.White) }, onClick = { showMenu = false }, leadingIcon = { Icon(Icons.Default.PlaylistAdd, null, tint = Color.White) })
                            DropdownMenuItem(text = { Text("Sleep Timer", color = Color.White) }, onClick = { showMenu = false }, leadingIcon = { Icon(Icons.Default.Timer, null, tint = Color.White) })
                            DropdownMenuItem(text = { Text("Watch Video", color = Color.White) }, onClick = { showMenu = false }, leadingIcon = { Icon(Icons.Default.SmartDisplay, null, tint = Color.White) })
                            DropdownMenuItem(text = { Text("Playback Speed", color = Color.White) }, onClick = { showMenu = false; showSpeedDialog = true }, leadingIcon = { Icon(Icons.Default.Speed, null, tint = Color.White) })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Album Art or Lyrics Panel ─────────────────────────
            if (showLyrics) {
                Box(
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(Color(0xFF121212)).padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Maahiya, teri-meri ek jind-jaan hai\n\nTujhse gawaahi meri, tu hi pehchaan hai\n\nMaahiya, teri-meri ek jind-jaan hai\n\nTeri-meri ye prem-kahani...", color = Color.White, fontSize = 17.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium, lineHeight = 30.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Powered by Lrclib", color = Color.Gray, fontSize = 11.sp)
                    }
                }
            } else {
                AsyncImage(
                    model = currentTrack?.albumArtUrl, contentDescription = null,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Song Title ────────────────────────────────────────
            Text(currentTrack?.title ?: "Unknown Title", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(4.dp))
            Text(currentTrack?.artist ?: "Unknown Artist", color = Color.Gray, fontSize = 16.sp, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            // ── Speed Label ────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${String.format("%.2f", playbackSpeed).trimEnd('0').trimEnd('.')}x",
                    color = Color.Gray, fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterEnd).clickable { showSpeedDialog = true }.padding(4.dp)
                )
            }

            // ── Progress Slider ───────────────────────────────────
            Slider(
                value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                onValueChange = { if (duration > 0) viewModel.seekTo((it * duration).toLong()) },
                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White, inactiveTrackColor = Color.DarkGray),
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatTime(currentPosition), color = Color.Gray, fontSize = 12.sp)
                Text(formatTime(duration), color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Controls Row ──────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { isShuffleOn = !isShuffleOn; viewModel.setShuffleModeEnabled(isShuffleOn) }) {
                    Icon(Icons.Default.Shuffle, "Shuffle", tint = if (isShuffleOn) Color.White else Color.Gray, modifier = Modifier.size(24.dp))
                }
                IconButton(onClick = { viewModel.playPrevious() }) {
                    Icon(Icons.Default.SkipPrevious, "Previous", tint = Color.White, modifier = Modifier.size(36.dp))
                }
                IconButton(onClick = { viewModel.togglePlayPause() }, modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White)) {
                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "Play/Pause", tint = Color.Black, modifier = Modifier.size(32.dp))
                }
                IconButton(onClick = { viewModel.playNext() }) {
                    Icon(Icons.Default.SkipNext, "Next", tint = Color.White, modifier = Modifier.size(36.dp))
                }
                IconButton(onClick = { isRepeatOn = !isRepeatOn; viewModel.setRepeatMode(if (isRepeatOn) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF) }) {
                    Icon(if (isRepeatOn) Icons.Default.RepeatOne else Icons.Default.Repeat, "Repeat", tint = if (isRepeatOn) Color.White else Color.Gray, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Favorite Heart ──────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { currentTrack?.let { viewModel.toggleFavorite(it) } }) {
                    Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "Favorite", tint = if (isFavorite) Color.Red else Color.White, modifier = Modifier.size(28.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    // ── Speed Dialog ──────────────────────────────────────────
    if (showSpeedDialog) {
        SpeedControlDialog(
            currentSpeed = playbackSpeed,
            onDismiss = { showSpeedDialog = false },
            onSpeedSet = { speed -> playbackSpeed = speed; viewModel.setPlaybackSpeed(speed); showSpeedDialog = false }
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Up Next Panel — shown inside BottomSheetScaffold
// ─────────────────────────────────────────────────────────────
@Composable
fun UpNextPanel(
    currentTrack: com.gaminghub.musicplayer.TrackModel?,
    upNextQueue: List<com.gaminghub.musicplayer.TrackModel>,
    viewModel: MusicViewModel,
    onTrackClick: (Int) -> Unit,
    onReorder: (Int, Int) -> Unit
) {
    // Drag state
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.65f)
    ) {
        // ── Drag pill handle ──────────────────────────────
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Gray)
            )
        }

        // ── "Up Next" title ──────────────────────────────
        Text(
            "Up Next",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (upNextQueue.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No songs in queue", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            // ── Currently playing row (non-draggable) ────────
            currentTrack?.let { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2A2A2A))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = track.albumArtUrl, contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(track.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(track.artist, color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    // Sound bars icon for currently playing
                    Icon(Icons.Default.GraphicEq, contentDescription = "Playing", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // ── Queue list with drag-to-reorder ──────────────
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                itemsIndexed(upNextQueue, key = { index, track -> "${track.audioUrl}_$index" }) { index, track ->
                    val isDragging = draggedItemIndex == index
                    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "elevation")
                    val itemFav by (track.audioUrl?.let { viewModel.isFavorite(it) } ?: flowOf(false)).collectAsState(initial = false)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isDragging) Modifier
                                    .zIndex(1f)
                                    .graphicsLayer { translationY = dragOffsetY }
                                    .shadow(elevation, RoundedCornerShape(8.dp))
                                    .background(Color(0xFF333333), RoundedCornerShape(8.dp))
                                else Modifier.background(Color(0xFF1A1A1A))
                            )
                            .clickable { onTrackClick(index) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Thumbnail
                        AsyncImage(
                            model = track.albumArtUrl, contentDescription = null,
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        // Title + Artist
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(track.artist, color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        // Favorite toggle
                        IconButton(onClick = { viewModel.toggleFavorite(track) }, modifier = Modifier.size(36.dp)) {
                            Icon(
                                if (itemFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                "Favorite", tint = if (itemFav) Color.Red else Color.Gray, modifier = Modifier.size(20.dp)
                            )
                        }

                        // Drag handle — long press triggers reorder
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .pointerInput(upNextQueue.size) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            draggedItemIndex = index
                                            dragOffsetY = 0f
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffsetY += dragAmount.y
                                            // Calculate if we crossed a row boundary (~68dp per row)
                                            val rowHeight = with(density) { 68.dp.toPx() }
                                            val draggedIdx = draggedItemIndex ?: return@detectDragGesturesAfterLongPress
                                            val targetIndex = draggedIdx + (dragOffsetY / rowHeight).toInt()
                                            val clampedTarget = targetIndex.coerceIn(0, upNextQueue.size - 1)
                                            if (clampedTarget != draggedIdx) {
                                                onReorder(draggedIdx, clampedTarget)
                                                draggedItemIndex = clampedTarget
                                                dragOffsetY -= (clampedTarget - draggedIdx) * rowHeight
                                            }
                                        },
                                        onDragEnd = {
                                            draggedItemIndex = null
                                            dragOffsetY = 0f
                                        },
                                        onDragCancel = {
                                            draggedItemIndex = null
                                            dragOffsetY = 0f
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Menu, "Reorder", tint = Color.Gray, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Speed Control Dialog
// ─────────────────────────────────────────────────────────────
@Composable
fun SpeedControlDialog(
    currentSpeed: Float,
    onDismiss: () -> Unit,
    onSpeedSet: (Float) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var sliderSpeed by remember { mutableFloatStateOf(currentSpeed) }
    var textInput by remember { mutableStateOf(String.format("%.2f", currentSpeed)) }
    var isTextError by remember { mutableStateOf(false) }
    val speedPresets = listOf(0.50f, 0.75f, 1.0f, 1.25f, 1.50f, 1.75f, 2.0f)

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF1E1E1E)) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Playback Speed", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("${String.format("%.2f", sliderSpeed)}x", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Slider(
                    value = sliderSpeed,
                    onValueChange = { v ->
                        sliderSpeed = (Math.round(v / 0.05f) * 0.05f).coerceIn(0.50f, 2.0f)
                        textInput = String.format("%.2f", sliderSpeed)
                        isTextError = false
                    },
                    valueRange = 0.50f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White, inactiveTrackColor = Color.DarkGray),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("0.50x", color = Color.Gray, fontSize = 11.sp)
                    Text("2.0x", color = Color.Gray, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    speedPresets.forEach { preset ->
                        val active = Math.abs(sliderSpeed - preset) < 0.01f
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (active) Color.White else Color.Transparent,
                            modifier = Modifier.border(1.dp, if (active) Color.White else Color.Gray, RoundedCornerShape(20.dp)).clickable { sliderSpeed = preset; textInput = String.format("%.2f", preset); isTextError = false }
                        ) {
                            Text("${String.format("%.2f", preset).trimEnd('0').trimEnd('.')}x", color = if (active) Color.Black else Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { input ->
                        textInput = input
                        val parsed = input.toFloatOrNull()
                        if (parsed != null && parsed in 0.50f..2.0f) { sliderSpeed = parsed; isTextError = false } else { isTextError = true }
                    },
                    label = { Text("Enter speed (0.50 – 2.00)", color = Color.Gray, fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    isError = isTextError, singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.Gray, errorBorderColor = Color.Red, cursorColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isTextError) {
                    Text("Enter a value between 0.50 and 2.00", color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (!isTextError) onSpeedSet(sliderSpeed) }, colors = ButtonDefaults.buttonColors(containerColor = Color.White), enabled = !isTextError) {
                        Text("Apply", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
