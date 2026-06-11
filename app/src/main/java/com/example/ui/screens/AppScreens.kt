package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.VideoView
import android.net.Uri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.*
import com.example.ui.viewmodels.AuthUiState
import com.example.ui.viewmodels.MainViewModel
import com.example.ui.viewmodels.VerifyUiState
import kotlinx.coroutines.delay
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isUnspecified

// DESIGN THEME COLORS
val NeonPink = Color(0xFFFF2D55)
val NeonCyan = Color(0xFF00F0FF)
val DeepMidnight = Color(0xFF09090F)
val DarkSurface = Color(0xFF161622)
val GoldAccent = Color(0xFFFFCC00)
val TextPrimary = Color(0xFFF2F2F7)
val TextSecondary = Color(0xFF8E8E93)

// ──── LOGIN SCREEN ────────────────────────────────────────────────────────
@Composable
fun LoginScreen(viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMidnight)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title & App Banner
            Surface(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                color = NeonPink.copy(alpha = 0.15f),
                border = BorderStroke(2.dp, NeonPink)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Stream,
                        contentDescription = "Logo",
                        tint = NeonPink,
                        modifier = Modifier.size(45.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "STRIP STREAM",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = 2.sp
            )
            Text(
                text = "Temps Réel & Live Interactif",
                fontSize = 14.sp,
                color = NeonCyan,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Inputs Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "CONNEXION",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nom d'utilisateur") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = NeonPink,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPink) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = NeonPink,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.handleLogin(username, password) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button")
                    ) {
                        Text(
                            text = "Se Connecter",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = viewModel.authUiState) {
                is AuthUiState.Loading -> CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(30.dp))
                is AuthUiState.Error -> Text(
                    text = state.message,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                is AuthUiState.Success -> Text(
                    text = "Bienvenue ${state.username} !",
                    color = Color.Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                else -> {}
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = { viewModel.navigateTo("register") },
                modifier = Modifier.testTag("go_register_btn")
            ) {
                Text(
                    text = "Pas de compte ? Inscrivez-vous !",
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ──── REGISTER SCREEN ───────────────────────────────────────────────────────
@Composable
fun RegisterScreen(viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMidnight)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "REJOINDRE STRIP",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = "Créez votre profil en quelques secondes",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Pseudo unique") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = NeonCyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        leadingIcon = { Icon(Icons.Default.LockOpen, contentDescription = null, tint = NeonPink) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email (optionnel)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Numéro de téléphone (optionnel)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.handleRegister(username, password, email.ifBlank { null }, phoneNumber.ifBlank { null }) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_register_btn")
                    ) {
                        Text(
                            text = "Créer mon Compte",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepMidnight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = viewModel.authUiState) {
                is AuthUiState.Loading -> CircularProgressIndicator(color = NeonPink, modifier = Modifier.size(30.dp))
                is AuthUiState.Error -> Text(text = state.message, color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                else -> {}
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = { viewModel.navigateTo("login") }) {
                Text(
                    text = "Compte existant ? Connectez-vous",
                    color = NeonPink,
                    fontSize = 14.sp
                )
            }
        }
    }
}


// ──── MAIN CONTAINER (WITH BOTTOM NAV BAR) ─────────────────────────────────
@Composable
fun MainContainer(viewModel: MainViewModel) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DeepMidnight,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = viewModel.currentHomeTab == "feed",
                    onClick = { viewModel.setHomeTab("feed") },
                    icon = { Icon(if (viewModel.currentHomeTab == "feed") Icons.Default.Home else Icons.Outlined.Home, contentDescription = "Feed") },
                    label = { Text("Feed", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonPink,
                        selectedTextColor = NeonPink,
                        indicatorColor = NeonPink.copy(alpha = 0.12f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = viewModel.currentHomeTab == "lives",
                    onClick = { viewModel.setHomeTab("lives") },
                    icon = { Icon(if (viewModel.currentHomeTab == "lives") Icons.Default.Tv else Icons.Outlined.Tv, contentDescription = "Lives") },
                    label = { Text("Lives", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan.copy(alpha = 0.12f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = viewModel.currentHomeTab == "chat",
                    onClick = { viewModel.setHomeTab("chat") },
                    icon = { Icon(if (viewModel.currentHomeTab == "chat") Icons.Default.ChatBubble else Icons.Outlined.ChatBubble, contentDescription = "Messagerie") },
                    label = { Text("Chat", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonPink,
                        selectedTextColor = NeonPink,
                        indicatorColor = NeonPink.copy(alpha = 0.12f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = viewModel.currentHomeTab == "upload",
                    onClick = { viewModel.setHomeTab("upload") },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Upload", modifier = Modifier.size(36.dp)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.White
                    )
                )

                NavigationBarItem(
                    selected = viewModel.currentHomeTab == "stats",
                    onClick = { viewModel.setHomeTab("stats") },
                    icon = { Icon(if (viewModel.currentHomeTab == "stats") Icons.Default.BarChart else Icons.Outlined.BarChart, contentDescription = "Server Stats") },
                    label = { Text("Stats", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan.copy(alpha = 0.12f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = viewModel.currentHomeTab == "profile",
                    onClick = { viewModel.setHomeTab("profile") },
                    icon = { Icon(if (viewModel.currentHomeTab == "profile") Icons.Default.AccountBox else Icons.Outlined.AccountBox, contentDescription = "Profile") },
                    label = { Text("Moi", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoldAccent,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent.copy(alpha = 0.12f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepMidnight)
                .padding(paddingValues)
        ) {
            when (viewModel.currentHomeTab) {
                "feed" -> FeedTabScreen(viewModel)
                "lives" -> LivesTabScreen(viewModel)
                "chat" -> ChatTabScreen(viewModel)
                "upload" -> UploadTabScreen(viewModel)
                "stats" -> StatsTabScreen(viewModel)
                "profile" -> ProfileTabScreen(viewModel)
            }
        }
    }
}

// ──── UPLOAD TAB SCREEN ───────────────────────────────────────────────────────
@Composable
fun UploadTabScreen(viewModel: MainViewModel) {
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }
    var hasOriginalSound by remember { mutableStateOf(true) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Publier", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(24.dp))
        
        // Video Preview Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .clickable { /* Simulate picking video */ },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CloudUpload, contentDescription = "Upload", tint = NeonPink, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sélectionner une vidéo...", color = TextSecondary)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Description", color = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            maxLines = 4
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Vidéo publique", color = TextPrimary)
            Switch(
                checked = isPublic,
                onCheckedChange = { isPublic = it },
                colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.5f))
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Son original", color = TextPrimary)
            Switch(
                checked = hasOriginalSound,
                onCheckedChange = { hasOriginalSound = it },
                colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = NeonCyan.copy(alpha = 0.5f))
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = {
                viewModel.uploadVideo(description)
                viewModel.setHomeTab("feed")
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Publier maintenant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ──── INSTAGRAM STORIES & SHORTS REELS ──────────────────────────────────────────

data class StoryItem(
    val id: String,
    val username: String,
    val avatarUrl: String,
    val mediaUrl: String,
    val caption: String = ""
)

val mockStories = listOf(
    StoryItem("s1", "Jean_d", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80", "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=800&q=80", caption = "Départ en vacances ! 🏝️☀️"),
    StoryItem("s2", "Alice_v", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=800&q=80", caption = "Nouveau projet incroyable 🎬✨"),
    StoryItem("s3", "Marc_b", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80", "https://images.unsplash.com/photo-1488161628813-04466f872be2?auto=format&fit=crop&w=800&q=80", caption = "Gros entraînement aujourd'hui 💪🚿"),
    StoryItem("s4", "Sophie_t", "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=200&q=80", "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=800&q=80", caption = "Coucher de soleil magique 🌅🧡"),
    StoryItem("s5", "David_k", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=800&q=80", caption = "Dîner de rêve entre amis 🥂✨")
)

@Composable
fun StoriesRow(stories: List<StoryItem>, onStoryClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "Votre story" button (like Instagram)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { /* Simule add story */ }
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                        .border(1.5.dp, Color.Gray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(NeonPink)
                        .border(1.5.dp, DeepMidnight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Votre story", color = TextSecondary, fontSize = 11.sp)
        }

        // Active stories
        stories.forEachIndexed { idx, story ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onStoryClick(idx) }
            ) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .background(
                            brush = Brush.sweepGradient(
                                listOf(NeonPink, NeonCyan, GoldAccent, NeonPink)
                            ),
                            shape = CircleShape
                        )
                        .padding(2.5.dp)
                        .background(DeepMidnight, CircleShape)
                        .padding(3.dp)
                ) {
                    AsyncImage(
                        model = story.avatarUrl,
                        contentDescription = story.username,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(story.username, color = TextPrimary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun StoryViewer(
    stories: List<StoryItem>,
    initialIndex: Int,
    onClose: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val currentStory = stories.getOrNull(currentIndex) ?: return
    
    var progress by remember(currentIndex) { mutableStateOf(0f) }
    
    LaunchedEffect(currentIndex) {
        progress = 0f
        val steps = 100
        val delayTime = 50L // 50 * 100 = 5000ms = 5s
        for (i in 1..steps) {
            delay(delayTime)
            progress = i / 100f
        }
        if (currentIndex < stories.lastIndex) {
            currentIndex++
        } else {
            onClose()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val context = LocalContext.current
        val isVideo = currentStory.mediaUrl.endsWith(".mp4", ignoreCase = true) || 
                      currentStory.mediaUrl.contains("video", ignoreCase = true) || 
                      currentStory.mediaUrl.contains(".m3u8", ignoreCase = true)

        if (isVideo) {
            val exoPlayer = remember(currentStory.id) {
                androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
                    setMediaItem(androidx.media3.common.MediaItem.fromUri(currentStory.mediaUrl))
                    repeatMode = androidx.media3.common.Player.REPEAT_MODE_OFF
                    prepare()
                    playWhenReady = true
                }
            }
            DisposableEffect(currentStory.id) {
                onDispose {
                    exoPlayer.release()
                }
            }
            AndroidView(
                factory = { ctx ->
                    androidx.media3.ui.PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = currentStory.mediaUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Navigation sectors
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) {
                        if (currentIndex > 0) {
                            currentIndex--
                        } else {
                            onClose()
                        }
                    }
            )
            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) {
                        if (currentIndex < stories.lastIndex) {
                            currentIndex++
                        } else {
                            onClose()
                        }
                    }
            )
        }

        // Top progress indicator
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 12.dp, end = 12.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stories.forEachIndexed { idx, _ ->
                    val segmentProgress = when {
                        idx < currentIndex -> 1f
                        idx == currentIndex -> progress
                        else -> 0f
                    }
                    LinearProgressIndicator(
                        progress = segmentProgress,
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp),
                        color = NeonPink,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = currentStory.avatarUrl,
                        contentDescription = currentStory.username,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        currentStory.username,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        if (currentStory.caption.isNotEmpty()) {
            Text(
                text = currentStory.caption,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 40.dp)
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun TikTokShortsPageItem(
    video: VideoItem,
    isActive: Boolean,
    onLike: () -> Unit,
    onProfileClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    var isPlaying by remember(isActive) { mutableStateOf(isActive) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (video.videoUrl.isNotEmpty()) {
            val context = LocalContext.current
            val exoPlayer = remember {
                androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
                    setMediaItem(androidx.media3.common.MediaItem.fromUri(video.videoUrl))
                    repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL
                    prepare()
                }
            }

            LaunchedEffect(isPlaying, isActive) {
                exoPlayer.playWhenReady = isPlaying && isActive
            }

            DisposableEffect(Unit) {
                onDispose {
                    exoPlayer.release()
                }
            }

            AndroidView(
                factory = { ctx ->
                    androidx.media3.ui.PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // Touch overlay to toggle play/pause
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) { isPlaying = !isPlaying }
            )
        } else {
            AsyncImage(
                model = video.thumbnailUrl ?: "android.resource://com.example/drawable/ic_launcher_background",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Info overlay (bottom-left)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 86.dp, bottom = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onProfileClick() }
            ) {
                AsyncImage(
                    model = video.avatarUrl ?: "https://i.pravatar.cc/150?u=${video.username}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, NeonPink, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = video.username,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (video.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = "Verifié", tint = NeonCyan, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = video.description,
                color = TextPrimary,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Vertical action icons (right side)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.BottomCenter) {
                AsyncImage(
                    model = video.avatarUrl ?: "https://i.pravatar.cc/150?u=${video.username}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color.White, CircleShape)
                        .clickable { onProfileClick() },
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .offset(y = 6.dp)
                        .size(16.dp)
                        .background(NeonPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onLike,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = if (video.liked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (video.liked) NeonPink else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "${video.likes}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onCommentClick,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comments",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "${video.views / 20}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Large Play button indicator overlay (center)
        if (!isPlaying) {
            IconButton(
                onClick = { isPlaying = true },
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(60.dp)
                    .background(NeonPink.copy(alpha = 0.82f), CircleShape)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TikTokShortsPager(
    viewModel: MainViewModel,
    onCommentClick: (String) -> Unit
) {
    val items = viewModel.videoFeed
    if (items.isEmpty()) return

    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = 0,
        pageCount = { items.size }
    )

    androidx.compose.foundation.pager.VerticalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) { page ->
        val video = items.getOrNull(page)
        if (video != null) {
            val isPageActive = pagerState.currentPage == page
            TikTokShortsPageItem(
                video = video,
                isActive = isPageActive,
                onLike = { viewModel.likeVideo(video.id) },
                onProfileClick = { viewModel.viewOtherUserProfile(video.userId) },
                onCommentClick = { onCommentClick(video.id) }
            )
        }
    }
}

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    val annotatedString = remember(text) {
        buildAnnotatedString {
            val lines = text.split("\n")
            lines.forEachIndexed { lineIdx, line ->
                var stylizedLine = line
                var isHeader = false
                
                if (stylizedLine.startsWith("# ")) {
                    isHeader = true
                    stylizedLine = stylizedLine.substring(2)
                    withStyle(style = SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = NeonPink)) {
                        append(stylizedLine)
                    }
                } else if (stylizedLine.startsWith("## ")) {
                    isHeader = true
                    stylizedLine = stylizedLine.substring(3)
                    withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonCyan)) {
                        append(stylizedLine)
                    }
                } else if (stylizedLine.startsWith("- ")) {
                    append("  • ")
                    stylizedLine = stylizedLine.substring(2)
                }
                
                if (!isHeader) {
                    var pos = 0
                    while (pos < stylizedLine.length) {
                        val nextBold = stylizedLine.indexOf("**", pos)
                        val nextItalic = stylizedLine.indexOf("*", pos)
                        val nextCode = stylizedLine.indexOf("`", pos)
                        val nextTag = stylizedLine.indexOf("#", pos)
                        
                        val foundIndex = listOf(nextBold, nextItalic, nextCode, nextTag)
                            .filter { it >= pos }
                            .minOrNull() ?: -1
                            
                        if (foundIndex == -1) {
                            append(stylizedLine.substring(pos))
                            break
                        }
                        
                        if (foundIndex > pos) {
                            append(stylizedLine.substring(pos, foundIndex))
                            pos = foundIndex
                        }
                        
                        when (stylizedLine[pos]) {
                            '*' -> {
                                if (stylizedLine.startsWith("**", pos)) {
                                    val closing = stylizedLine.indexOf("**", pos + 2)
                                    if (closing != -1) {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(stylizedLine.substring(pos + 2, closing))
                                        }
                                        pos = closing + 2
                                    } else {
                                        append("**")
                                        pos += 2
                                    }
                                } else {
                                    val closing = stylizedLine.indexOf("*", pos + 1)
                                    if (closing != -1) {
                                        withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                            append(stylizedLine.substring(pos + 1, closing))
                                        }
                                        pos = closing + 1
                                    } else {
                                        append("*")
                                        pos += 1
                                    }
                                }
                            }
                            '`' -> {
                                val closing = stylizedLine.indexOf("`", pos + 1)
                                if (closing != -1) {
                                    withStyle(style = SpanStyle(fontFamily = FontFamily.Monospace, background = Color.DarkGray.copy(alpha = 0.4f), color = NeonCyan)) {
                                        append(stylizedLine.substring(pos + 1, closing))
                                    }
                                    pos = closing + 1
                                } else {
                                    append("`")
                                    pos += 1
                                }
                            }
                            '#' -> {
                                var endIdx = pos + 1
                                while (endIdx < stylizedLine.length && (stylizedLine[endIdx].isLetterOrDigit() || stylizedLine[endIdx] == '_')) {
                                    endIdx++
                                }
                                if (endIdx > pos + 1) {
                                    val tag = stylizedLine.substring(pos, endIdx)
                                    withStyle(style = SpanStyle(color = NeonPink, fontWeight = FontWeight.SemiBold)) {
                                        append(tag)
                                    }
                                    pos = endIdx
                                } else {
                                    append("#")
                                    pos += 1
                                }
                            }
                            else -> {
                                append(stylizedLine[pos].toString())
                                pos += 1
                            }
                        }
                    }
                }
                
                if (lineIdx < lines.lastIndex) {
                    append("\n")
                }
            }
        }
    }
    
    val finalColor = if (color == Color.Unspecified) TextPrimary else color
    val finalFontSize = if (fontSize == TextUnit.Unspecified) 14.sp else fontSize
    
    Text(
        text = annotatedString,
        modifier = modifier,
        color = finalColor,
        fontSize = finalFontSize,
        fontWeight = fontWeight,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun TextPostCard(
    post: TextPost,
    onLike: () -> Unit,
    onProfileClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: User Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProfileClick() }
            ) {
                AsyncImage(
                    model = post.avatarUrl ?: "https://i.pravatar.cc/150?u=${post.username}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, NeonPink, CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.username,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (post.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Vérifié",
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = post.createdAt, 
                        color = TextSecondary, 
                        fontSize = 11.sp
                    )
                }

                IconButton(onClick = { /* Plus options */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body content: Styled Markdown text
            MarkdownText(
                text = post.content,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                color = TextPrimary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Action footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onLike() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (post.liked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.liked) NeonPink else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.likes}",
                        color = if (post.liked) NeonPink else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { /* Simule commentaires */ }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Répondre",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Share Button
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Partager",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTabScreen(viewModel: MainViewModel) {
    val items = viewModel.videoFeed
    var selectedVideoForComments by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var selectedSubTab by remember { mutableStateOf("explore") }
    var activeStoryIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(selectedSubTab) {
        if (selectedSubTab == "explore") {
            viewModel.loadTextPosts()
        }
    }

    val derivedStories = remember(items) {
        if (items.isEmpty()) {
            mockStories
        } else {
            items.map { video ->
                StoryItem(
                    id = video.id,
                    username = video.username,
                    avatarUrl = video.avatarUrl ?: "https://i.pravatar.cc/150?u=${video.username}",
                    mediaUrl = video.videoUrl,
                    caption = video.description.ifEmpty { "Ma story StripStream !" }
                )
            }.distinctBy { it.username }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedSubTab == "shorts") {
            // "Shorts" Tab: Immersive full screen vertical TikTok style pager
            if (viewModel.isLoadingFeed && items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonPink)
                }
            } else if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MovieFilter, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Aucune vidéo trouvée pour les Shorts.", color = TextSecondary, fontSize = 16.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = { viewModel.loadFeed() }, colors = ButtonDefaults.buttonColors(containerColor = NeonPink)) {
                            Text("Recharger")
                        }
                    }
                }
            } else {
                TikTokShortsPager(
                    viewModel = viewModel,
                    onCommentClick = { videoId ->
                        selectedVideoForComments = videoId
                        viewModel.loadVideoComments(videoId)
                    }
                )
            }

            // Floating Header over Shorts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                        )
                    ),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("explore" to "Explorer", "foryou" to "Pour toi", "shorts" to "Shorts").forEach { (id, label) ->
                    val isSelected = selectedSubTab == id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedSubTab = id }
                            .padding(horizontal = 14.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        if (isSelected) {
                            Box(modifier = Modifier.height(3.dp).width(30.dp).background(NeonPink, RoundedCornerShape(1.dp)))
                        } else {
                            Box(modifier = Modifier.height(3.dp).width(30.dp).background(Color.Transparent))
                        }
                    }
                }
            }

        } else {
            // Standard feed layout with Instagram Stories row
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stream, contentDescription = "Logo", tint = NeonPink, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("STRIP", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.navigateTo("search_screen") }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
                        }
                        IconButton(onClick = {
                            viewModel.loadNotifications()
                            viewModel.navigateTo("notifications_screen")
                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextPrimary)
                        }
                    }
                }

                // Subtabs Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("explore" to "Explorer", "foryou" to "Pour toi", "shorts" to "Shorts").forEach { (id, label) ->
                        val isSelected = selectedSubTab == id
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { selectedSubTab = id }) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            if (isSelected) {
                                Box(modifier = Modifier.height(3.dp).width(30.dp).background(NeonPink, RoundedCornerShape(1.dp)))
                            } else {
                                Box(modifier = Modifier.height(3.dp).width(30.dp).background(Color.Transparent))
                            }
                        }
                    }
                }

                if (selectedSubTab == "explore") {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Instagram Stories at the top of Explorer
                        item {
                            StoriesRow(stories = derivedStories, onStoryClick = { idx ->
                                activeStoryIndex = idx
                            })
                            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        }

                        // Publication Composer for Markdown text posts
                        item {
                            var postContent by remember { mutableStateOf("") }
                            var isPublishing by remember { mutableStateOf(false) }
                            val myProfile = viewModel.myProfile
                            val myAvatar = myProfile?.avatarUrl ?: "https://i.pravatar.cc/150?u=${viewModel.preferencesManager.username ?: "me"}"

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = BorderStroke(1.dp, Brush.linearGradient(listOf(NeonPink, NeonCyan))),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        AsyncImage(
                                            model = myAvatar,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .border(1.5.dp, NeonPink, CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        
                                        Column(modifier = Modifier.weight(1f)) {
                                            OutlinedTextField(
                                                value = postContent,
                                                onValueChange = { if (it.length <= 1000) postContent = it },
                                                placeholder = { Text("Partagez une publication en Markdown... (**gras**, *italique*, # titre, `code`, `#tag`)", color = TextSecondary, fontSize = 13.5.sp) },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color.Transparent,
                                                    unfocusedBorderColor = Color.Transparent,
                                                    focusedContainerColor = Color.Transparent,
                                                    unfocusedContainerColor = Color.Transparent,
                                                    focusedTextColor = Color.White,
                                                    unfocusedTextColor = Color.White
                                                ),
                                                modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 200.dp)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Markdown formatting info
                                        Box(
                                            modifier = Modifier
                                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "Markdown supporté 📝",
                                                color = NeonCyan,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${postContent.length}/1000",
                                                color = if (postContent.length >= 900) NeonPink else TextSecondary,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(end = 12.dp)
                                            )
                                            
                                            Button(
                                                onClick = {
                                                    if (postContent.isNotBlank() && !isPublishing) {
                                                        isPublishing = true
                                                        viewModel.createTextPost(postContent) { success ->
                                                            isPublishing = false
                                                            if (success) {
                                                                postContent = ""
                                                            }
                                                        }
                                                    }
                                                },
                                                enabled = postContent.isNotBlank() && !isPublishing,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = NeonPink,
                                                    disabledContainerColor = NeonPink.copy(alpha = 0.3f)
                                                ),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                                shape = RoundedCornerShape(18.dp),
                                                modifier = Modifier.height(34.dp).testTag("publish_post_button")
                                            ) {
                                                if (isPublishing) {
                                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp)
                                                } else {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Publier", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Fil d'actualité list of text posts
                        val posts = viewModel.textPosts
                        if (viewModel.isLoadingTextPosts && posts.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = NeonPink)
                                }
                            }
                        } else if (posts.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Aucune publication trouvée.", color = TextSecondary, fontSize = 14.sp)
                                }
                            }
                        } else {
                            items(posts) { post ->
                                TextPostCard(
                                    post = post,
                                    onLike = { viewModel.likeTextPost(post.id) },
                                    onProfileClick = { viewModel.viewOtherUserProfile(post.userId) }
                                )
                            }
                        }
                    }
                } else {
                    // Standard visual feed for "foryou" subtab
                    if (viewModel.isLoadingFeed && items.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NeonPink)
                        }
                    } else if (items.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.MovieFilter, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(60.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Aucune vidéo trouvée sur le serveur.", color = TextSecondary, fontSize = 16.sp, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(onClick = { viewModel.loadFeed() }, colors = ButtonDefaults.buttonColors(containerColor = NeonPink)) {
                                    Text("Recharger")
                                }
                            }
                        }
                    } else {
                        val displayItems = when (selectedSubTab) {
                            "foryou" -> items.shuffled()
                            else -> items
                        }

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Instagram Stories at the top of ForYou
                            item {
                                StoriesRow(stories = derivedStories, onStoryClick = { idx ->
                                    activeStoryIndex = idx
                                })
                                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                            }

                            items(displayItems) { video ->
                                VideoFeedCard(
                                    video = video,
                                    onLike = { viewModel.likeVideo(video.id) },
                                    onProfileClick = { viewModel.viewOtherUserProfile(video.userId) },
                                    onView = { viewModel.incrementVideoView(video.id) },
                                    onCommentClick = {
                                        selectedVideoForComments = video.id
                                        viewModel.loadVideoComments(video.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedVideoForComments != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedVideoForComments = null },
                sheetState = sheetState,
                containerColor = DeepMidnight
            ) {
                CommentsSheetContent(viewModel = viewModel, videoId = selectedVideoForComments!!)
            }
        }

        // Animated full screen story viewer
        if (activeStoryIndex != null) {
            StoryViewer(
                stories = derivedStories,
                initialIndex = activeStoryIndex!!,
                onClose = { activeStoryIndex = null }
            )
        }
    }
}

@Composable
fun CommentsSheetContent(viewModel: MainViewModel, videoId: String) {
    var newCommentText by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.75f).padding(16.dp)) {
        Text("Commentaires", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        if (viewModel.isLoadingComments) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan)
            }
        } else if (viewModel.currentVideoComments.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Aucun commentaire. Soyez le premier !", color = TextSecondary)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.currentVideoComments) { comment ->
                    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(comment.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${comment.username}")
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.Gray),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("@${comment.username}", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(comment.createdAt, color = TextSecondary.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(comment.content, color = TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ajouter un commentaire...", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(20.dp),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (newCommentText.isNotBlank()) {
                        viewModel.addVideoComment(videoId, newCommentText)
                        newCommentText = ""
                    }
                },
                modifier = Modifier.background(NeonPink, CircleShape)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

@Composable
fun VideoFeedCard(video: VideoItem, onLike: () -> Unit, onProfileClick: () -> Unit, onView: () -> Unit, onCommentClick: () -> Unit) {
    // Increment view once on presentation simulates video playback
    LaunchedEffect(video.id) {
        onView()
    }

    var isPlaying by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Column {
            // Video viewport over DarkSurface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color.Black)
                    .clickable { isPlaying = !isPlaying }
            ) {
                if (video.videoUrl.isNotEmpty()) {
                    val context = LocalContext.current
                    val exoPlayer = remember {
                        androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
                            setMediaItem(androidx.media3.common.MediaItem.fromUri(video.videoUrl))
                            repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL
                            prepare()
                        }
                    }
                    LaunchedEffect(isPlaying) {
                        exoPlayer.playWhenReady = isPlaying
                    }
                    DisposableEffect(Unit) {
                        onDispose {
                            exoPlayer.release()
                        }
                    }
                    AndroidView(
                        factory = { ctx ->
                            androidx.media3.ui.PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    // Intercept clicks before they reach PlayerView
                    Box(modifier = Modifier.matchParentSize().clickable { isPlaying = !isPlaying })
                } else {
                    // Simulated pulsing visual element if no videoUrl
                    val infiniteTransition = rememberInfiniteTransition(label = "simulated_playback")
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 0.8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse"
                    )

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(video.thumbnailUrl ?: "android.resource://com.example/drawable/ic_launcher_background")
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().alpha(pulseAlpha),
                        contentScale = ContentScale.Crop
                    )
                }

                // Interaction actions overlayed on bottom right
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onLike,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .size(42.dp)
                    ) {
                        Icon(
                            imageVector = if (video.liked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (video.liked) NeonPink else Color.White
                        )
                    }

                    IconButton(
                        onClick = onCommentClick,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .size(42.dp)
                    ) {
                        Icon(Icons.Default.Comment, contentDescription = "Comments", tint = Color.White)
                    }

                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .size(42.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                }

                // Player play overlay symbol if paused
                if (!isPlaying) {
                    IconButton(
                        onClick = { isPlaying = true },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(54.dp)
                            .background(NeonPink.copy(alpha = 0.8f), CircleShape)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }

            // Description block
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onProfileClick() }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(video.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${video.username}")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, NeonCyan, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "@${video.username}",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (video.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Profile",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text("Cliquez pour voir le profil", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = video.description,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${video.likes} likes", color = TextSecondary, fontSize = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RemoveRedEye, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${video.views} vues", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}


// ──── LIVES TAB SCREEN ──────────────────────────────────────────────────────
@Composable
fun LivesTabScreen(viewModel: MainViewModel) {
    val items = viewModel.activeLiveStreams

    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement. someRowMatchingSpaceBetween() ?: Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LIVES EN COURS",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            IconButton(onClick = { viewModel.loadActiveLives() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh Lives", tint = NeonCyan)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (viewModel.isLoadingLives && items.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan)
            }
        } else if (items.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.TvOff, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(60.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Aucun live stream actif programmé.", color = TextSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadActiveLives() }, colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)) {
                        Text("Actualiser", color = DeepMidnight, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { stream ->
                    LiveStreamCard(stream = stream, onWatch = { viewModel.watchLiveStream(stream) })
                }
            }
        }
    }
}

private fun Arrangement.someRowMatchingSpaceBetween() = Arrangement.SpaceBetween

@Composable
fun LiveStreamCard(stream: LiveStreamItem, onWatch: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onWatch() }
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Simulated preview
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(stream.thumbnailUrl ?: "https://api.dicebear.com/7.x/identicon/svg?seed=${stream.title}")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                        .background(NeonPink, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stream.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Par @${stream.username}",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                stream.description?.let {
                    Text(
                        text = it,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.People, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${stream.viewerCount} spectateurs", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}


// ──── LIVE STREAM WATCH SCREEN ──────────────────────────────────────────────
@Composable
fun LiveStreamWatchScreen(viewModel: MainViewModel) {
    val live = viewModel.activeLiveStream ?: return
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMidnight)
    ) {
        // Upper Viewport Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                IconButton(onClick = { viewModel.leaveLiveStream() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Leave stream", tint = TextPrimary)
                }

                Spacer(modifier = Modifier.width(6.dp))

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(live.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${live.username}")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(live.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Diffuseur: @${live.username}", color = NeonCyan, fontSize = 11.sp)
                }
            }

            // Real-time viewer badge with WebSocket dynamic state
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.Red.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .border(1.dp, NeonPink, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
                Spacer(modifier = Modifier.width(6.dp))
                // WebSocket manages this count in real-time
                Text("${viewModel.liveViewerCount} vue(s)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Live Simulated screen with rolling chat messages
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            // Simulated video output graphics
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // Dynamic glowing grids background
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonPink.copy(alpha = 0.25f), Color.Transparent),
                                center = Offset(size.width / 2, size.height / 3),
                                radius = size.minDimension / 1.5f
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LiveTv, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(80.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("FLUX VIDÉO EN COURS", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Retransmission temps réel STRIP", color = TextSecondary, fontSize = 12.sp)
                }
            }

            // Bottom overlay chats
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .padding(14.dp)
            ) {
                Text(
                    text = "Discussion en Direct (WebSocket)",
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    reverseLayout = true
                ) {
                    // Reverse list or keep index standard, let's reverse to show newest on bottom
                    items(viewModel.liveComments.reversed()) { comment ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = "${comment.username}: ",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = comment.message,
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Send comments input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Lâcher un commentaire...", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 1,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (commentText.isNotBlank()) {
                        viewModel.submitLiveComment(commentText)
                        commentText = ""
                    }
                },
                modifier = Modifier
                    .background(NeonCyan, CircleShape)
                    .size(46.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = DeepMidnight)
            }
        }
    }
}


// ──── CHAT TAB SCREEN (MESSAGES LIST & CONVERSATIONS) ──────────────────────
@Composable
fun ChatTabScreen(viewModel: MainViewModel) {
    val items = viewModel.conversations

    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MESSAGERIE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Tiny WebSocket indicator lights up green when live sync is established
                val isConnected by viewModel.isWebSocketConnected.collectAsState()
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isConnected) Color.Green else Color.Gray)
                )
            }

            IconButton(onClick = { viewModel.loadConversations() }) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = NeonPink)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (viewModel.isLoadingConversations && items.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonPink)
            }
        } else if (items.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Forum, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(60.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Aucune conversation active trouvée.", color = TextSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadConversations() }, colors = ButtonDefaults.buttonColors(containerColor = NeonPink)) {
                        Text("Recharger")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items) { conv ->
                    ConversationItemView(conv = conv, onClick = { viewModel.selectConversationAndOpenChat(conv) })
                }
            }
        }
    }
}

@Composable
fun ConversationItemView(conv: ChatConversation, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(conv.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${conv.username}")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Online indicator
                if (conv.isOnline) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.Green)
                            .border(2.dp, DarkSurface, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "@${conv.username}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = conv.lastMessage ?: "Ouvrir la discussion",
                    color = if (conv.unreadCount > 0) NeonCyan else TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    fontWeight = if (conv.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (conv.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(NeonPink),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conv.unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


// ──── PRIVATE CHAT DETAIL VIEW ──────────────────────────────────────────────
@Composable
fun ChatDetailScreen(viewModel: MainViewModel) {
    val user = viewModel.activeChatUser ?: return
    var textMsg by remember { mutableStateOf("") }

    // Implement real-time typing state updates based on user inputs
    LaunchedEffect(textMsg) {
        if (textMsg.isNotBlank()) {
            viewModel.updateTypingState(true)
            delay(1500)
            viewModel.updateTypingState(false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMidnight)
    ) {
        // App header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("home_container") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${user.username}")
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "@${user.username}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                // Real-time typing indicators received directly via events
                if (viewModel.isChatPeerTyping) {
                    Text("En train d'écrire...", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                } else if (user.isOnline) {
                    Text("En ligne", color = Color.Green, fontSize = 11.sp)
                } else {
                    Text("Hors ligne", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        // Messages output scroll container
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.chatMessages) { message ->
                val isMe = message.senderId == viewModel.preferencesManager.userId
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isMe) 12.dp else 0.dp,
                            bottomEnd = if (isMe) 0.dp else 12.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) NeonPink else DarkSurface
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = message.content,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = message.createdAt,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 9.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Typing inputs footer bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .navigationBarsPadding()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textMsg,
                onValueChange = { textMsg = it },
                placeholder = { Text("Écrire un message en direct...", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPink,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 3,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            IconButton(
                onClick = {
                    if (textMsg.isNotBlank()) {
                        viewModel.sendPrivateMessage(textMsg)
                        textMsg = ""
                    }
                },
                modifier = Modifier
                    .background(NeonPink, CircleShape)
                    .size(46.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}


// ──── STATS TAB SCREEN ──────────────────────────────────────────────────────
@Composable
fun StatsTabScreen(viewModel: MainViewModel) {
    val stats = viewModel.appStats
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "DIAGNOSTICS SERVEUR",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Mesures de CMO-Streaming en temps réel",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 20.dp)
        )

        if (stats == null) {
            Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan)
            }
        } else {
            // Big diagnostics dashboard cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(title = "Utilisateurs", value = stats.totalUsers.toString(), icon = Icons.Default.People, tint = NeonPink, modifier = Modifier.weight(1f))
                StatCard(title = "En Ligne (Live)", value = stats.onlineUsers.toString(), icon = Icons.Default.Wifi, tint = Color.Green, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(title = "Vidéos", value = stats.totalVideos.toString(), icon = Icons.Default.Movie, tint = NeonCyan, modifier = Modifier.weight(1f))
                StatCard(title = "Lives Actifs", value = stats.activeLives.toString(), icon = Icons.Default.LiveTv, tint = GoldAccent, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            StatCard(
                title = "Total Messages Transmis",
                value = stats.totalMessages.toString(),
                icon = Icons.Default.Message,
                tint = NeonPink,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("API Host: hoosthubs-g.onrender.com", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Synchronisé le: ${stats.timestamp}", color = TextSecondary, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.loadStats() },
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Rafraîchir les métriques", color = DeepMidnight, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = title, fontSize = 11.sp, color = TextSecondary)
        }
    }
}


// ──── PROFILE TAB SCREEN (OWN PROFILE & REQ VERIFICATION) ───────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTabScreen(viewModel: MainViewModel) {
    val profile = viewModel.myProfile
    var showEditDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        if (profile == null) {
            CircularProgressIndicator(color = NeonPink, modifier = Modifier.padding(40.dp))
        } else {
            Box(contentAlignment = Alignment.Center) {
                // Cover visual brush
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.horizontalGradient(listOf(NeonPink, NeonCyan)))
                )

                // Avatar
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profile.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${profile.username}")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(DeepMidnight)
                        .border(3.dp, DeepMidnight, CircleShape)
                        .border(1.dp, NeonCyan, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@${profile.username}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (profile.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Logo",
                        tint = GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            profile.zodiacSign?.let { sign ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .background(GoldAccent.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, GoldAccent, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Astro: $sign", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = profile.bio ?: "Aucune description.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStat(title = "Abonnés", value = profile.followersCount)
                MiniStat(title = "Abonnements", value = profile.followingCount)
                MiniStat(title = "Vidéos", value = profile.videosCount)
                MiniStat(title = "Likes reçus", value = profile.likesReceived)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showEditDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Text("Éditer le profil", color = TextPrimary)
                }
                IconButton(
                    onClick = { /* Settings */ },
                    modifier = Modifier.size(40.dp).background(DarkSurface, RoundedCornerShape(8.dp)).border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // VERIFICATION CARD trigger (C.M.O secure.self().verify requirements)
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("BADGE DE VÉRIFICATION", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Le système automatique secure.self().verify vérifie que vous avez un profil complet (numéro, bio, photo), 10k+ abonnées, 100k+ vues cumulées, 18+ ans et au moins 1 vidéo publique avec son original.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (profile.isVerified) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.Green.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                                .fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compte Vérifié ! Boost de portée & Monétisation Actifs.", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.navigateTo("verify_screen") },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Lancer secure.self().verify", color = DeepMidnight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text("Mes dernières vidéos", color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f).height(120.dp).background(DarkSurface, RoundedCornerShape(8.dp)).border(1.dp, NeonPink, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NeonPink, modifier = Modifier.size(32.dp))
                }
                Box(modifier = Modifier.weight(1f).height(120.dp).background(DarkSurface, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(32.dp))
                }
                Box(modifier = Modifier.weight(1f).height(120.dp).background(DarkSurface, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Logout action Button
            Button(
                onClick = { viewModel.handleLogout() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, Color.Red),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Déconnexion de l'application", color = Color.Red, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
            Text("MES VIDÉOS", color = NeonPink, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            
            val myVideos = viewModel.videoFeed.filter { it.userId == profile.id }
            if (myVideos.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    Text("Aucune vidéo publiée.", color = TextSecondary)
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    myVideos.chunked(3).forEach { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            rowItems.forEach { video ->
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(video.thumbnailUrl ?: "android.resource://com.example/drawable/ic_launcher_background")
                                        .crossfade(true).build(),
                                    contentDescription = null,
                                    modifier = Modifier.weight(1f).aspectRatio(1f).background(Color.DarkGray).clickable { 
                                        viewModel.setHomeTab("feed")
                                    },
                                    contentScale = ContentScale.Crop
                                )
                            }
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
        
    if (showEditDialog && profile != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ModalBottomSheet(
            onDismissRequest = { showEditDialog = false },
            sheetState = sheetState,
            containerColor = DeepMidnight
        ) {
            EditProfileSheetContent(
                currentName = profile.username,
                currentAvatarUrl = profile.avatarUrl ?: "",
                onClose = { showEditDialog = false },
                onSave = { newName, newUrl ->
                    viewModel.updateFirestoreProfile(newName, newUrl)
                    showEditDialog = false
                }
            )
        }
    }
}
}

@Composable
fun EditProfileSheetContent(
    currentName: String,
    currentAvatarUrl: String,
    onClose: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var avatarUrl by remember { mutableStateOf(currentAvatarUrl) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Éditer le profil (Firestore)", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nom d'affichage", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                focusedBorderColor = NeonCyan, unfocusedBorderColor = Color.DarkGray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = avatarUrl,
            onValueChange = { avatarUrl = it },
            label = { Text("URL de la photo de profil", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                focusedBorderColor = NeonCyan, unfocusedBorderColor = Color.DarkGray
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.weight(1f)
            ) {
                Text("Annuler", color = Color.White)
            }
            Button(
                onClick = { onSave(name, avatarUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                modifier = Modifier.weight(1f)
            ) {
                Text("Sauvegarder", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun MiniStat(title: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (value >= 1000) String.format("%.1fk", value / 1000.0) else value.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(text = title, fontSize = 11.sp, color = TextSecondary)
    }
}


// ──── SEARCH SCREEN ─────────────────────────────────────────────────────────
@Composable
fun SearchScreen(viewModel: MainViewModel) {
    var query by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().background(DeepMidnight).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo("home_container") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it; viewModel.performSearch(it) },
                placeholder = { Text("Rechercher..", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (viewModel.isSearching) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("Utilisateurs", color = NeonCyan, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                }
                items(viewModel.searchUsersResult) { user ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.viewOtherUserProfile(user.id) }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("@${user.username}", color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Vidéos", color = NeonPink, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                }
                items(viewModel.searchVideosResult) { video ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(video.description, color = TextPrimary, modifier = Modifier.padding(12.dp))
                    }
                }
            }
        }
    }
}

// ──── NOTIFICATIONS SCREEN ──────────────────────────────────────────────────
@Composable
fun NotificationsScreen(viewModel: MainViewModel) {
    Column(modifier = Modifier.fillMaxSize().background(DeepMidnight).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo("home_container") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text("NOTIFICATIONS", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (viewModel.isLoadingNotifications) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonPink)
            }
        } else if (viewModel.notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune notification", color = TextSecondary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(viewModel.notifications) { notif ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(notif.type.uppercase(), color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(notif.message, color = TextPrimary, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ──── VERIFICATION DIALOG SCREEN ────────────────────────────────────────────
@Composable
fun VerificationDialogScreen(viewModel: MainViewModel) {
    var birthDateText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMidnight)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape),
                color = GoldAccent.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("VÉRIFICATION SÉCURISÉE", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Text(
                "Entrez votre date de naissance pour valider vos critères",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = birthDateText,
                        onValueChange = { birthDateText = it },
                        label = { Text("Date de naissance (YYYY-MM-DD)") },
                        placeholder = { Text("Ex: 1995-08-25") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GoldAccent) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("birth_date_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.triggerVerificationRequest(birthDateText) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_verification_btn")
                    ) {
                        Text("Vérifier Mes Données", color = DeepMidnight, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (val state = viewModel.verifyUiState) {
                is VerifyUiState.Checking -> {
                    CircularProgressIndicator(color = GoldAccent)
                    Text("Calcul astrologique et analyse des critères...", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
                is VerifyUiState.Verified -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3D2F)),
                        border = BorderStroke(1.dp, Color.Green),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("BADGE ACCORDÉ !", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Votre signe Astro: ${state.zodiac}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Boost d'abonnés de +50% et gains publicitaires debloqués.", color = TextPrimary, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
                is VerifyUiState.Denied -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3F1316)),
                        border = BorderStroke(1.dp, Color.Red),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("CRITÈRES MANQUANTS", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(state.reason, color = TextPrimary, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = {
                    viewModel.resetVerifyState()
                    viewModel.navigateTo("home_container")
                    viewModel.setHomeTab("profile")
                }
            ) {
                Text("Retour au Profil", color = NeonCyan, fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ──── OBSERVING OTHER PROFILES SCREEN ──────────────────────────────────────
@Composable
fun OtherProfileScreen(viewModel: MainViewModel) {
    val profile = viewModel.observedProfile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(DeepMidnight)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("home_container") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text("Profil de l'auteur", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (profile == null) {
            CircularProgressIndicator(color = NeonCyan, modifier = Modifier.padding(40.dp))
        } else {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.horizontalGradient(listOf(NeonCyan, NeonPink)))
                )

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profile.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${profile.username}")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(DeepMidnight)
                        .border(3.dp, DeepMidnight, CircleShape)
                        .border(1.dp, NeonPink, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@${profile.username}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (profile.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Logo",
                        tint = GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            profile.zodiacSign?.let { sign ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .background(GoldAccent.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, GoldAccent, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Astro: $sign", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = profile.bio ?: "Aucune description.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStat(title = "Abonnés", value = profile.followersCount)
                MiniStat(title = "Abonnements", value = profile.followingCount)
                MiniStat(title = "Vidéos", value = profile.videosCount)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action: toggle follow buttons
            Button(
                onClick = { viewModel.toggleFollowUser(profile.id) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("S'abonner / Se Désabonner", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val conv = ChatConversation(
                        id = profile.id,
                        userId = profile.id,
                        username = profile.username,
                        avatarUrl = profile.avatarUrl,
                        lastMessage = null,
                        lastMessageTime = null,
                        unreadCount = 0,
                        isOnline = profile.isOnline
                    )
                    viewModel.selectConversationAndOpenChat(conv)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ChatBubble, contentDescription = null, tint = DeepMidnight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Contacter en Messagerie Directe", color = DeepMidnight, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("VIDÉOS", color = NeonPink, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(10.dp))
            
            val theirVideos = viewModel.videoFeed.filter { it.userId == profile.id }
            if (theirVideos.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    Text("Aucune vidéo.", color = TextSecondary)
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    theirVideos.chunked(3).forEach { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            rowItems.forEach { video ->
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(video.thumbnailUrl ?: "android.resource://com.example/drawable/ic_launcher_background")
                                        .crossfade(true).build(),
                                    contentDescription = null,
                                    modifier = Modifier.weight(1f).aspectRatio(1f).background(Color.DarkGray).clickable { 
                                        viewModel.setHomeTab("feed")
                                    },
                                    contentScale = ContentScale.Crop
                                )
                            }
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
