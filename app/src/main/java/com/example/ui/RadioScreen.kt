package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DefaultStations
import com.example.model.RadioStation
import com.example.player.PlayerState
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioScreen(viewModel: RadioViewModel) {
    val context = LocalContext.current
    val stations by viewModel.radioStations.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()
    val playerState by viewModel.playerState.collectAsState()

    var showAboutSheet by remember { mutableStateOf(false) }
    var showPlayerSheet by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val aboutSheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                onAboutClicked = { showAboutSheet = true },
                searchQuery = searchQuery,
                onSearchQueryChanged = { viewModel.setSearchQuery(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Category Slider
                CategorySlider(
                    categories = listOf("Tümü", "Favoriler") + DefaultStations.list.map { it.category }.distinct(),
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )

                // Main Stations List
                if (stations.isEmpty()) {
                    EmptyState(
                        isSearching = searchQuery.isNotBlank(),
                        isFavorites = selectedCategory == "Favoriler"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(bottom = if (currentStation != null) 30.dp else 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
                    ) {
                        items(stations, key = { it.id }) { station ->
                            RadioStationCard(
                                station = station,
                                isPlaying = currentStation?.id == station.id && playerState !is PlayerState.Paused,
                                isCurrent = currentStation?.id == station.id,
                                isFavorite = favoriteIds.contains(station.id),
                                playerState = playerState,
                                onPlayClicked = {
                                    if (currentStation?.id == station.id) {
                                        viewModel.togglePlayPause()
                                    } else {
                                        viewModel.playStation(station)
                                    }
                                },
                                onFavoriteToggled = {
                                    viewModel.toggleFavorite(station.id)
                                }
                            )
                        }
                    }
                }
            }

            // Beautiful Bottom MiniPlayer Bar
            AnimatedVisibility(
                visible = currentStation != null,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                currentStation?.let { station ->
                    MiniPlayerBar(
                        station = station,
                        playerState = playerState,
                        onPlayPauseClicked = { viewModel.togglePlayPause() },
                        onStopClicked = { viewModel.stopPlayback() },
                        onBarClicked = { showPlayerSheet = true }
                    )
                }
            }
        }
    }

    // 1. Hakkında Bottom Sheet Dialog (Serhat Güner details)
    if (showAboutSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAboutSheet = false },
            sheetState = aboutSheetState,
            containerColor = LightCardBg,
            contentColor = TextDark
        ) {
            AboutScreenContent(
                onDismiss = { showAboutSheet = false }
            )
        }
    }

    // 2. Full-Screen Audio Player Experience Sheet
    if (showPlayerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPlayerSheet = false },
            sheetState = bottomSheetState,
            containerColor = VibrantPurple,
            contentColor = Color.White
        ) {
            currentStation?.let { station ->
                FullPlayerContent(
                    station = station,
                    playerState = playerState,
                    isFavorite = favoriteIds.contains(station.id),
                    onPlayPauseClicked = { viewModel.togglePlayPause() },
                    onFavoriteToggled = { viewModel.toggleFavorite(station.id) },
                    onStopClicked = {
                        viewModel.stopPlayback()
                        showPlayerSheet = false
                    },
                    onDismiss = { showPlayerSheet = false }
                )
            }
        }
    }
}

// ---------------- UI COMPONENTS -----------------

@Composable
fun TopAppBar(
    onAboutClicked: () -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp, bottom = 10.dp, start = 12.dp, end = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Elegant Radio Icon styled as Crescent + Waves
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "CANLI YAYIN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Radyo Türkiye",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // About Screen Button Tappable
            IconButton(
                onClick = onAboutClicked,
                modifier = Modifier
                    .testTag("about_button")
                    .background(CardHighlightBg, CircleShape)
                    .size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Hakkında",
                    tint = ActivePurpleText
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Outlined Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("search_input"),
            placeholder = { Text("Radyo adı veya frekans ara...", color = Color.Gray, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara", tint = Color.Gray) },
            trailingIcon = if (searchQuery.isNotBlank()) {
                {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Temizle", tint = Color.Gray)
                    }
                }
            } else null,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = BottomNavBorder,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun CategorySlider(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else BottomNavBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (category == "Favoriler") {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun RadioStationCard(
    station: RadioStation,
    isPlaying: Boolean,
    isCurrent: Boolean,
    isFavorite: Boolean,
    playerState: PlayerState,
    onPlayClicked: () -> Unit,
    onFavoriteToggled: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else BottomNavBorder,
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) CardHighlightBg.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel Play Circle
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPlaying) MaterialTheme.colorScheme.primary else CardHighlightBg
                    )
                    .clickable { onPlayClicked() },
                contentAlignment = Alignment.Center
            ) {
                if (isCurrent && playerState is PlayerState.Buffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (isPlaying) {
                    PauseIcon(tint = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Oynat",
                        tint = ActivePurpleText,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Text Metadata Detail
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPlayClicked() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = station.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${station.frequency} MHz",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ActivePurpleText,
                        modifier = Modifier
                            .background(CardHighlightBg, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = station.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = station.category,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Favorite Right Heart Button
            IconButton(
                onClick = onFavoriteToggled,
                modifier = Modifier.testTag("fav_toggle_${station.id}")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favori",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}

@Composable
fun EmptyState(isSearching: Boolean, isFavorites: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val titleText = if (isFavorites) "Favori İstasyon Bulunmuyor" else "Sonuç Bulunamadı"
        val subtitleText = if (isFavorites) {
            "Beğendiğiniz radyo kanallarının yanındaki kalp simgesine tıklayarak daha hızlı erişim sağlayabilirsiniz."
        } else {
            "Arama teriminizi kontrol edin veya farklı bir kelime yazmayı deneyin."
        }

        Box(
            modifier = Modifier
                .size(72.dp)
                .background(CardHighlightBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = ActivePurpleText,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = titleText,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitleText,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

// ---------------- MINI PLAYER BAR -----------------

@Composable
fun MiniPlayerBar(
    station: RadioStation,
    playerState: PlayerState,
    onPlayPauseClicked: () -> Unit,
    onStopClicked: () -> Unit,
    onBarClicked: () -> Unit
) {
    val isPlaying = playerState is PlayerState.Playing
    val isBuffering = playerState is PlayerState.Buffering

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BottomNavBg)
            .border(1.dp, BottomNavBorder, RoundedCornerShape(16.dp))
            .clickable { onBarClicked() }
            .padding(vertical = 10.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rotating Pulsing Visual Container
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CardHighlightBg),
            contentAlignment = Alignment.Center
        ) {
            LiveEqualizerAnimation(isPlaying = isPlaying)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Station Metadata
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = station.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${station.frequency} MHz",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActivePurpleText
                )
            }
            Text(
                text = when (playerState) {
                    is PlayerState.Buffering -> "Bağlanıyor / Arabelleğe alınıyor..."
                    is PlayerState.Playing -> "Yayın Açık • Canlı Dinliyorsunuz"
                    is PlayerState.Paused -> "Duraklatıldı"
                    is PlayerState.Error -> "Hata oluştu"
                    else -> "Yayın hazır"
                },
                fontSize = 11.sp,
                color = if (playerState is PlayerState.Error) MaterialTheme.colorScheme.primary else TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Action Buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onPlayPauseClicked,
                modifier = Modifier.testTag("mini_play_pause")
            ) {
                if (isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (isPlaying) {
                    PauseIcon(tint = ActivePurpleText, modifier = Modifier.size(20.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Oynat/Duraklat",
                        tint = ActivePurpleText
                    )
                }
            }

            IconButton(
                onClick = onStopClicked,
                modifier = Modifier.testTag("mini_stop")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Yayını Kapat",
                    tint = TextMuted
                )
            }
        }
    }
}

// ---------------- LIVE EQUALIZER ANIMATION COMPOSE -----------------

@Composable
fun LiveEqualizerAnimation(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val heightsPercent = listOf(0.4f, 0.75f, 0.5f, 0.9f, 0.35f)
    
    val animators = heightsPercent.mapIndexed { idx, heightVal ->
        if (isPlaying) {
            infiniteTransition.animateFloat(
                initialValue = 0.15f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 350 + (idx * 110),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "eq_anim_$idx"
            )
        } else {
            remember { mutableStateOf(0.15f) }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxHeight()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        animators.forEach { animator ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(animator.value)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

// ---------------- FULL PLAYER MODAL BOTTOM SHEET CONTENT -----------------

@Composable
fun FullPlayerContent(
    station: RadioStation,
    playerState: PlayerState,
    isFavorite: Boolean,
    onPlayPauseClicked: () -> Unit,
    onFavoriteToggled: () -> Unit,
    onStopClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isPlaying = playerState is PlayerState.Playing
    val isBuffering = playerState is PlayerState.Buffering

    // Album Art pulsing animation matching music state
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by if (isPlaying) {
        infiniteTransition.animateFloat(
            initialValue = 0.96f,
            targetValue = 1.04f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        VibrantPurple,
                        VibrantPurpleDark
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 10.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Şu Anda Oynatılıyor",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f)
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White.copy(alpha = 0.7f))
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Giant Vinyl Disc representation / Pulse Ring
        Box(
            modifier = Modifier
                .size(190.dp)
                .scale(pulseScale)
                .drawBehind {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.2f),
                        radius = size.minDimension / 2 + 15.dp.toPx(),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.1f),
                        radius = size.minDimension / 2 + 35.dp.toPx(),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Radio tower emblem or large text
                Icon(
                    painter = painterResource(android.R.drawable.presence_audio_online),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = station.frequency,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = Color.White
                )
                Text(
                    text = "MHz",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Station Metadata Text Title
        Text(
            text = station.name,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = station.category,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = station.description,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Status Banner Indication
        Text(
            text = when (playerState) {
                is PlayerState.Buffering -> "ARABELLEK ALINIYOR • FREKANS KURULUYOR"
                is PlayerState.Playing -> "CANLI • YAYIN KESİNTİSİZ DEVAM EDİYOR"
                is PlayerState.Paused -> "YAYIN BEKLEMEDE"
                is PlayerState.Error -> "YAYIN AKIŞI ÇEVRİMDIŞI"
                else -> ""
            },
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Main Control Buttons Deck
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favorite Button
            IconButton(
                onClick = onFavoriteToggled,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                    .size(50.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorilere Ekle",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Big Play/Pause Ring Button
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onPlayPauseClicked() },
                contentAlignment = Alignment.Center
            ) {
                if (isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(34.dp),
                        strokeWidth = 3.dp,
                        color = VibrantPurple
                    )
                } else if (isPlaying) {
                    PauseIcon(tint = VibrantPurple, modifier = Modifier.size(36.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Oynat/Duraklat",
                        tint = VibrantPurple,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Stop Button
            IconButton(
                onClick = onStopClicked,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                    .size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Yayını Kapat",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Web launch button if website exists
        if (station.website.isNotBlank()) {
            Button(
                onClick = {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(station.website))
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Tarayıcı açılamadı.", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(painter = painterResource(android.R.drawable.ic_menu_help), contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Web Sitesini Ziyaret Et", color = Color.White, fontSize = 13.sp)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

// ---------------- HAKKINDA (ABOUT) SCREEN PANEL CONTENT -----------------

@Composable
fun AboutScreenContent(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Geliştirici Hakkında",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large Developer Avatar styled
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(VibrantPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SG",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Developer Name
        Text(
            text = "Serhat Güner",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = TextDark
        )
        Text(
            text = "Yazılım & Uygulama Geliştiricisi",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(22.dp))
        Divider(color = BottomNavBorder)
        Spacer(modifier = Modifier.height(22.dp))

        // Interactive List Taps
        // 1. Phone Button
        AboutContactRow(
            icon = Icons.Default.Phone,
            title = "Telefon",
            value = "0542 890 98 04",
            onRowClicked = {
                try {
                    val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:05428909804"))
                    context.startActivity(phoneIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Arama başlatılamadı.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Email button
        AboutContactRow(
            icon = Icons.Default.Email,
            title = "E-posta",
            value = "cezali.1genc@gmail.com",
            onRowClicked = {
                try {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:cezali.1genc@gmail.com")
                    }
                    context.startActivity(Intent.createChooser(emailIntent, "E-posta Gönder"))
                } catch (e: Exception) {
                    // Try direct generic send in case mailto fails
                    try {
                        val emailGeneric = Intent(Intent.ACTION_SEND).apply {
                            type = "message/rfc822"
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("cezali.1genc@gmail.com"))
                        }
                        context.startActivity(Intent.createChooser(emailGeneric, "E-posta Gönder"))
                    } catch (e2: Exception) {
                        Toast.makeText(context, "E-posta uygulaması bulunamadı.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Website button
        AboutContactRow(
            icon = Icons.Default.Share,
            title = "Web Sitesi",
            value = "modlu-1apk.xo.je",
            onRowClicked = {
                try {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://modlu-1apk.xo.je"))
                    context.startActivity(webIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "İnternet sayfası açılamadı.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Elegant version branding
        Text(
            text = "Radyo TR • Sürüm 1.0.0",
            fontSize = 11.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun AboutContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onRowClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BottomNavBorder, RoundedCornerShape(16.dp))
            .clickable { onRowClicked() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VibrantPurple,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = if (title == "Web Sitesi") Color(0xFF2563EB) else TextDark,
                    fontWeight = FontWeight.SemiBold,
                    style = if (title == "Web Sitesi") androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline) else androidx.compose.ui.text.TextStyle.Default
                )
            }
        }
    }
}

@Composable
fun PauseIcon(tint: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.5.dp)
                .fillMaxHeight(0.55f)
                .background(tint, RoundedCornerShape(1.5.dp))
        )
        Box(
            modifier = Modifier
                .width(4.5.dp)
                .fillMaxHeight(0.55f)
                .background(tint, RoundedCornerShape(1.5.dp))
        )
    }
}

