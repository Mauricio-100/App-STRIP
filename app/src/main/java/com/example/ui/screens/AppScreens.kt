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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTabScreen(viewModel: MainViewModel) {
    val items = viewModel.videoFeed
    var selectedVideoForComments by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var selectedSubTab by remember { mutableStateOf("explore") }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    "shorts" -> items.reversed()
                    else -> items
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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

        if (selectedVideoForComments != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedVideoForComments = null },
                sheetState = sheetState,
                containerColor = DeepMidnight
            ) {
                CommentsSheetContent(viewModel = viewModel, videoId = selectedVideoForComments!!)
            }
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

    var isPlaying by remember { mutableStateOf(true) }

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
                    AndroidView(
                        factory = { context ->
                            VideoView(context).apply {
                                setVideoURI(Uri.parse(video.videoUrl))
                                setOnPreparedListener { mp ->
                                    mp.isLooping = true
                                    start()
                                }
                            }
                        },
                        update = { view ->
                            if (isPlaying) view.start() else view.pause()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
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
