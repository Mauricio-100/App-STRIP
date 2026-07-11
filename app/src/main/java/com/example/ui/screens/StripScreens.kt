package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*
import com.example.ui.viewmodels.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(text: String, modifier: Modifier = Modifier, color: Color = TextPrimary, fontSize: TextUnit = 14.sp) {
    var displayedText by remember { mutableStateOf("") }
    LaunchedEffect(text) {
        displayedText = ""
        for (i in text.indices) {
            displayedText += text[i]
            delay(15) // Speed of typing
        }
    }
    Text(
        text = displayedText + "█",
        color = color,
        fontFamily = FontFamily.Monospace,
        fontSize = fontSize,
        modifier = modifier
    )
}

@Composable
fun FloatingDock(currentTab: String, onTabSelected: (String) -> Unit) {
    val tabs = listOf("feed", "actfile", "upload", "live", "chat", "profile")
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .background(Color(0x22FFFFFF), RoundedCornerShape(24.dp))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(24.dp))
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = currentTab == tab
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.25f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "scale"
                )
                val color by animateColorAsState(
                    targetValue = if (isSelected) ElectricCyan else Color(0x88FFFFFF),
                    animationSpec = tween(300),
                    label = "color"
                )
                
                Icon(
                    imageVector = when(tab) {
                        "feed" -> Icons.Default.ViewDay
                        "actfile" -> Icons.Default.Code
                        "upload" -> Icons.Default.AddCircle
                        "live" -> Icons.Default.Sensors
                        "chat" -> Icons.Default.Forum
                        "profile" -> Icons.Default.Person
                        else -> Icons.Default.Home
                    },
                    contentDescription = tab,
                    tint = color,
                    modifier = Modifier
                        .size(26.dp * scale)
                        .clickable(
                            indication = null, 
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onTabSelected(tab)
                        }
                )
            }
        }
    }
}

@Composable
fun CyberOtherProfileScreen(viewModel: MainViewModel) {
    val profile = viewModel.observedProfile

    Box(modifier = Modifier.fillMaxSize().background(AbsoluteBlack)) {
        if (profile == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricCyan)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Back Button Bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateTo("home_container") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ElectricCyan)
                    }
                    Text("PUBLIC_LEDGER // @${profile.username}", color = ElectricCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                // Profile Header Grid
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Background gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                            .background(Brush.verticalGradient(listOf(ElectricViolet.copy(alpha = 0.4f), Color.Transparent)))
                    )
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = profile.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${profile.username}",
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(AbsoluteBlack)
                                    .border(2.dp, ElectricCyan, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            if (profile.isVerified) {
                                Icon(
                                    Icons.Default.VerifiedUser,
                                    contentDescription = "Verified",
                                    tint = ElectricCyan,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(28.dp)
                                        .offset(x = (-4).dp, y = (-4).dp)
                                        .background(AbsoluteBlack, CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("@${profile.username}", color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        
                        if (profile.bio != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(profile.bio, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                        }
                        
                        if (profile.zodiacSign != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("SIGN: ${profile.zodiacSign}", color = ElectricViolet, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x11FFFFFF), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatMatrix("FOLLOWERS", profile.followersCount.toString())
                    ProfileStatMatrix("FOLLOWING", profile.followingCount.toString())
                    ProfileStatMatrix("VIDEOS", profile.videosCount.toString())
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val isFollowing = profile.isFollowing ?: false
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isFollowing) Color.DarkGray else ElectricCyan, RoundedCornerShape(12.dp))
                            .clickable { viewModel.toggleFollowUser(profile.id) }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFollowing) "UNFOLLOW" else "FOLLOW",
                            color = if (isFollowing) Color.White else AbsoluteBlack,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Transparent, RoundedCornerShape(12.dp))
                            .border(1.dp, ElectricCyan, RoundedCornerShape(12.dp))
                            .clickable {
                                val conv = com.example.data.ChatConversation(
                                    id = profile.id, // usually conversation id is synthetic or just other user id
                                    userId = profile.id,
                                    username = profile.username,
                                    avatarUrl = profile.avatarUrl,
                                    lastMessage = null,
                                    lastMessageTime = null,
                                    unreadCount = 0,
                                    isOnline = profile.isOnline
                                )
                                viewModel.selectConversationAndOpenChat(conv)
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MESSAGE", color = ElectricCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CyberProfileTabScreen(viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        if (viewModel.myProfile == null) {
            viewModel.initializeFallbackProfile()
        }
        viewModel.syncProfile()
    }

    val context = LocalContext.current
    val profile = viewModel.myProfile

    var showEditDialog by remember { mutableStateOf(false) }
    var editUsername by remember(profile) { mutableStateOf(profile?.username ?: "") }
    var editBio by remember(profile) { mutableStateOf(profile?.bio ?: "") }
    var capturedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var pickedUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            pickedUri = null
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            pickedUri = uri
            capturedBitmap = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (profile == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricCyan)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 40.dp, bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // Profile Header Grid
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Background gradient
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                                .background(Brush.verticalGradient(listOf(ElectricViolet.copy(alpha = 0.4f), Color.Transparent)))
                        )
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = profile.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${profile.username}",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(AbsoluteBlack)
                                        .border(2.dp, ElectricCyan, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                if (profile.isVerified) {
                                    Icon(
                                        Icons.Default.VerifiedUser,
                                        contentDescription = "Verified",
                                        tint = ElectricCyan,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(28.dp)
                                            .offset(x = (-4).dp, y = (-4).dp)
                                            .background(AbsoluteBlack, CircleShape)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("@${profile.username}", color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            
                            if (profile.bio != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(profile.bio, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                            }
                            
                            if (profile.zodiacSign != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("SIGN: ${profile.zodiacSign}", color = ElectricViolet, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                            }
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
                
                item {
                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x11FFFFFF), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatMatrix("FOLLOWERS", profile.followersCount.toString())
                        ProfileStatMatrix("FOLLOWING", profile.followingCount.toString())
                        ProfileStatMatrix("VIDEOS", profile.videosCount.toString())
                        ProfileStatMatrix("LIKES", profile.likesReceived.toString())
                    }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
                
                item {
                    // System actions
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CyberButton("EDIT PARAMETERS", Icons.Default.Settings) {
                            editUsername = profile.username
                            editBio = profile.bio ?: ""
                            capturedBitmap = null
                            pickedUri = null
                            showEditDialog = true
                        }
                        CyberButton("SECURE.SELF().VERIFY", Icons.Default.Security) { /* TODO */ }
                        CyberButton("SYSTEM LOGOUT", Icons.Default.ExitToApp, isDanger = true) { viewModel.logout() }
                    }
                }
            }
        }

        // EDIT PARAMETERS CYBER DIALOG
        if (showEditDialog && profile != null) {
            AlertDialog(
                onDismissRequest = { if (!isSaving) showEditDialog = false },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .border(2.dp, ElectricCyan, RoundedCornerShape(16.dp)),
                containerColor = AbsoluteBlack,
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "EDITION // PROFIL",
                            color = ElectricCyan,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showEditDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Photo Preview & Selection Container
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(110.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, ElectricViolet, CircleShape)
                                    .background(Color(0x22FFFFFF))
                            ) {
                                if (capturedBitmap != null) {
                                    AsyncImage(
                                        model = capturedBitmap,
                                        contentDescription = "Portrait photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (pickedUri != null) {
                                    AsyncImage(
                                        model = pickedUri,
                                        contentDescription = "Gallery photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    AsyncImage(
                                        model = profile.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${profile.username}",
                                        contentDescription = "Current avatar",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 4.dp, y = 4.dp)
                                    .background(AbsoluteBlack, CircleShape)
                                    .border(1.dp, ElectricCyan, CircleShape)
                                    .padding(4.dp)
                            )
                        }

                        // Photo Source Selection Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Camera launch button
                            Button(
                                onClick = { cameraLauncher.launch(null) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FF007F)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("CAMERA", color = NeonPink, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            
                            // Gallery pick button
                            Button(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300E5FF)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("GALERIE", color = ElectricCyan, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        // Form input: Display Name / Pseudo
                        OutlinedTextField(
                            value = editUsername,
                            onValueChange = { editUsername = it },
                            label = { Text("Pseudo / Nom", color = TextSecondary, fontFamily = FontFamily.Monospace) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = Color(0x44FFFFFF),
                                focusedLabelColor = ElectricCyan,
                                unfocusedLabelColor = TextSecondary
                            ),
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Form input: Bio
                        OutlinedTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            label = { Text("Votre Bio", color = TextSecondary, fontFamily = FontFamily.Monospace) },
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = Color(0x44FFFFFF),
                                focusedLabelColor = ElectricCyan,
                                unfocusedLabelColor = TextSecondary
                            ),
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth().height(90.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isSaving = true
                            // Capture photo bytes internally
                            val bytesStream = java.io.ByteArrayOutputStream()
                            val imageBytes: ByteArray? = when {
                                capturedBitmap != null -> {
                                    capturedBitmap?.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, bytesStream)
                                    bytesStream.toByteArray()
                                }
                                pickedUri != null -> {
                                    try {
                                        context.contentResolver.openInputStream(pickedUri!!)?.use { it.readBytes() }
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                                else -> null
                            }
                            
                            viewModel.updateCyberProfile(
                                context = context,
                                newUsername = editUsername.trim(),
                                newBio = editBio.trim(),
                                avatarBytes = imageBytes
                            ) { success ->
                                isSaving = false
                                if (success) {
                                    showEditDialog = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isSaving && editUsername.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = AbsoluteBlack, modifier = Modifier.size(18.dp))
                        } else {
                            Text(
                                "ENREGISTRER // CONFIG",
                                color = AbsoluteBlack,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileStatMatrix(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = ElectricCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun CyberButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isDanger: Boolean = false, onClick: () -> Unit) {
    val tint = if (isDanger) NeonPink else ElectricCyan
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tint.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .border(1.dp, tint.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = tint, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ActionIconHUD(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0x22FFFFFF), CircleShape)
                .border(1.dp, Color(0x44FFFFFF), CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun UploadTabScreen(viewModel: MainViewModel) {
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }
    var hasOriginalSound by remember { mutableStateOf(true) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        videoUri = uri
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Publier (Cyber Upload)", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .border(1.dp, if (videoUri != null) ElectricCyan else Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .clickable { launcher.launch("video/*") },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(if (videoUri != null) Icons.Default.CheckCircle else Icons.Default.CloudUpload, contentDescription = "Upload", tint = if (videoUri != null) ElectricCyan else ElectricViolet, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(if (videoUri != null) "Vidéo sélectionnée" else "Sélectionner une vidéo...", color = TextSecondary, fontFamily = FontFamily.Monospace)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Description", color = TextSecondary, fontFamily = FontFamily.Monospace) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            maxLines = 4,
            textStyle = TextStyle(fontFamily = FontFamily.Monospace)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Vidéo publique", color = TextPrimary, fontFamily = FontFamily.Monospace)
            Switch(
                checked = isPublic,
                onCheckedChange = { isPublic = it },
                colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan, checkedTrackColor = ElectricCyan.copy(alpha = 0.5f))
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Son original", color = TextPrimary, fontFamily = FontFamily.Monospace)
            Switch(
                checked = hasOriginalSound,
                onCheckedChange = { hasOriginalSound = it },
                colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan, checkedTrackColor = ElectricCyan.copy(alpha = 0.5f))
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = {
                if (videoUri != null) {
                    viewModel.uploadVideo(context, videoUri!!, description, isPublic, hasOriginalSound)
                }
            },
            enabled = videoUri != null,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet, disabledContainerColor = Color.DarkGray),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Publier maintenant", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun ImmersiveVideoFeed(viewModel: MainViewModel) {
    val pagerState = rememberPagerState(pageCount = { viewModel.videoFeed.size.coerceAtLeast(1) })
    
    if (viewModel.videoFeed.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(AbsoluteBlack), contentAlignment = Alignment.Center) {
            TypewriterText("SYSTEM:// NO_FEED_DATA_FOUND", color = ElectricCyan)
        }
        return
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize().background(AbsoluteBlack)
    ) { page ->
        val video = viewModel.videoFeed[page]
        Box(modifier = Modifier.fillMaxSize()) {
            val context = LocalContext.current
            val isPageActive = pagerState.currentPage == page
            
            if (isPageActive) {
                val exoPlayer = remember(video.videoUrl) {
                    androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
                        setMediaItem(androidx.media3.common.MediaItem.fromUri(video.videoUrl))
                        repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL
                        prepare()
                        playWhenReady = true
                    }
                }
                DisposableEffect(video.videoUrl) {
                    onDispose {
                        exoPlayer.release()
                    }
                }
                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { ctx ->
                        androidx.media3.ui.PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Box(modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AbsoluteBlack.copy(alpha=0.4f), Color.Transparent, Color.Transparent, AbsoluteBlack.copy(alpha=0.9f)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp, bottom = 80.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "@${video.username}",
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            if (video.isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Verified",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TypewriterText(text = video.description, color = TextSecondary, fontSize = 14.sp)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Original Audio - sys.stream()", color = ElectricViolet, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 80.dp)
                    ) {
                        ActionIconHUD(
                            icon = Icons.Outlined.FavoriteBorder,
                            value = video.likes.toString(),
                            onClick = { viewModel.likeVideo(video.id) }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        ActionIconHUD(
                            icon = Icons.Outlined.ChatBubbleOutline,
                            value = "cmd",
                            onClick = { }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        ActionIconHUD(
                            icon = Icons.Outlined.Share,
                            value = "link",
                            onClick = { }
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        AsyncImage(
                            model = video.avatarUrl ?: "https://i.pravatar.cc/150?u=${video.userId}",
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(2.dp, ElectricCyan, CircleShape)
                                .clickable { viewModel.viewOtherUserProfile(video.userId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CyberStoriesRow(
    stories: List<com.example.ui.screens.StoryItem>,
    onStoryClick: (Int) -> Unit,
    onAddStoryClick: () -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "Votre story" button (cyber themed)
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onAddStoryClick() }
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray)
                            .border(1.5.dp, ElectricCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ajouter",
                            tint = ElectricCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(ElectricViolet)
                            .border(1.dp, AbsoluteBlack, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Votre story",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Active stories
        items(stories.size) { idx ->
            val story = stories[idx]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onStoryClick(idx) }
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                        .border(2.dp, Brush.linearGradient(listOf(ElectricCyan, ElectricViolet)), CircleShape)
                        .padding(3.dp)
                ) {
                    AsyncImage(
                        model = story.avatarUrl,
                        contentDescription = story.username,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.username,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun CyberActFilesTabScreen(viewModel: MainViewModel) {
    var newPostContent by remember { mutableStateOf("") }
    var activeStoryIndex by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    
    var selectedStoryUri by remember { mutableStateOf<Uri?>(null) }
    var selectedStoryEffect by remember { mutableStateOf<String?>("none") }
    var isUploadingStory by remember { mutableStateOf(false) }

    val storyLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedStoryUri = uri
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadTextPosts()
        viewModel.loadStories()
    }

    val derivedStories = remember(viewModel.activeStories, viewModel.videoFeed) {
        if (viewModel.activeStories.isEmpty()) {
            if (viewModel.videoFeed.isEmpty()) {
                com.example.ui.screens.mockStories
            } else {
                viewModel.videoFeed.map { video ->
                    com.example.ui.screens.StoryItem(
                        id = video.id,
                        username = video.username,
                        avatarUrl = video.avatarUrl ?: "https://i.pravatar.cc/150?u=${video.username}",
                        mediaUrl = video.videoUrl,
                        caption = video.description.ifEmpty { "Ma story StripStream !" }
                    )
                }.distinctBy { it.username }
            }
        } else {
            viewModel.activeStories.map { storyRes ->
                com.example.ui.screens.StoryItem(
                    id = storyRes.id,
                    username = storyRes.user.username,
                    avatarUrl = storyRes.user.avatarUrl ?: "https://i.pravatar.cc/150?u=${storyRes.user.username}",
                    mediaUrl = storyRes.mediaUrl,
                    caption = "Story par @${storyRes.user.username}"
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            Text("ACTFILES // PUBLIC LEDGER", color = ElectricCyan, fontFamily = FontFamily.Monospace, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Stories Row at the top
            CyberStoriesRow(
                stories = derivedStories,
                onStoryClick = { activeStoryIndex = it },
                onAddStoryClick = { storyLauncher.launch("image/* video/*") }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Input Area
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newPostContent,
                    onValueChange = { newPostContent = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Log entry...", color = TextSecondary, fontFamily = FontFamily.Monospace) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = ElectricCyan
                    ),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(ElectricViolet, RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.createTextPost(newPostContent) {
                                if (it) newPostContent = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = AbsoluteBlack)
                }
            }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (viewModel.isLoadingTextPosts && viewModel.textPosts.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricCyan)
            }
        } else {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.textPosts) { post ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x11FFFFFF), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = post.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${post.username}",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, ElectricCyan, CircleShape)
                                        .clickable { viewModel.viewOtherUserProfile(post.userId) }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("@${post.username}", color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        if (post.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.VerifiedUser, contentDescription = "Verified", tint = ElectricCyan, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text(post.createdAt.take(10), color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(post.content, color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { viewModel.likeTextPost(post.id) }
                                ) {
                                    Icon(
                                        imageVector = if (post.liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = if (post.liked) NeonPink else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${post.likes}", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Visibility, contentDescription = "Views", tint = TextSecondary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${post.viewsCount}", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                }
                                
                                Icon(Icons.Default.Forum, contentDescription = "Comment", tint = TextSecondary, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
        }

        if (activeStoryIndex != null) {
            StoryViewer(
                stories = derivedStories,
                initialIndex = activeStoryIndex!!,
                onClose = { activeStoryIndex = null }
            )
        }

        if (selectedStoryUri != null) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { selectedStoryUri = null }
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PUBLIER UNE STORY",
                            color = ElectricCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Story media preview
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = selectedStoryUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Effect / Filter Selector Row
                        Text(
                            text = "FILTRE D'EFFETS OPENCV / CLOUDINARY :",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val effects = listOf(
                            "none" to "Standard",
                            "cyberpunk" to "Cyberpunk",
                            "sketch" to "Sketch Art",
                            "vintage" to "Vintage",
                            "cartoon" to "Cartoon",
                            "vignette" to "Vignette"
                        )

                        androidx.compose.foundation.lazy.LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(effects.size) { index ->
                                val eff = effects[index]
                                val isSelected = selectedStoryEffect == eff.first
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) ElectricCyan else Color.Transparent
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricCyan else Color.Gray),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier
                                        .clickable { selectedStoryEffect = eff.first }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = eff.second,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.Black else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (isUploadingStory) {
                            CircularProgressIndicator(color = ElectricCyan, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Traitement & Publication...", color = ElectricCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { selectedStoryUri = null },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Annuler", color = TextPrimary, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        isUploadingStory = true
                                        val effVal = if (selectedStoryEffect == "none") null else selectedStoryEffect
                                        viewModel.uploadStory(context, selectedStoryUri!!, effVal) { success ->
                                            isUploadingStory = false
                                            selectedStoryUri = null
                                            if (success) {
                                                android.widget.Toast.makeText(context, "Story publiée avec succès !", android.widget.Toast.LENGTH_SHORT).show()
                                            } else {
                                                android.widget.Toast.makeText(context, "Erreur lors de l'upload de la story", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Publier", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class UserStatus {
    ACTIVE, AWAY, OFFLINE
}

@Composable
fun CyberAvatarWithStatus(
    avatarUrl: String?,
    username: String,
    status: UserStatus,
    size: androidx.compose.ui.unit.Dp = 48.dp,
    borderWidth: androidx.compose.ui.unit.Dp = 1.5.dp,
    onClickAvatar: () -> Unit
) {
    val indicatorColor = when (status) {
        UserStatus.ACTIVE -> Color(0xFF00FF66) // Electric Green
        UserStatus.AWAY -> Color(0xFFFFCC00)   // Electric Amber/Yellow
        UserStatus.OFFLINE -> Color(0xFF777777) // Medium Gray
    }
    
    val statusText = when (status) {
        UserStatus.ACTIVE -> "En ligne"
        UserStatus.AWAY -> "Absent"
        UserStatus.OFFLINE -> "Hors-ligne"
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        AsyncImage(
            model = avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=$username",
            contentDescription = "$username - $statusText",
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(borderWidth, indicatorColor, CircleShape)
                .clickable { onClickAvatar() },
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .size((size * 0.28f).coerceAtLeast(10.dp))
                .clip(CircleShape)
                .background(indicatorColor)
                .border(1.5.dp, AbsoluteBlack, CircleShape)
        )
    }
}

@Composable
fun CyberChatTabScreen(viewModel: MainViewModel) {
    val items = viewModel.conversations
    var friendQuery by remember { mutableStateOf("") }

    val filteredConversations = remember(items, friendQuery) {
        if (friendQuery.isEmpty()) items
        else items.filter { it.username.contains(friendQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SECURE.COMM",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ElectricCyan,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Tiny WebSocket indicator lights up green when live sync is established
                val isConnected by viewModel.isWebSocketConnected.collectAsState()
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isConnected) ElectricCyan else Color.DarkGray)
                        .border(1.dp, if (isConnected) Color.White else Color.Transparent, CircleShape)
                )
            }
            IconButton(onClick = { 
                viewModel.loadConversations() 
                viewModel.startActivityFeedSimulation()
            }) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = ElectricViolet)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Custom segmented cyber tab buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(Color(0x18FFFFFF), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0x15FFFFFF), RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            val sections = listOf(
                "discussions" to "CHATS & AMIS",
                "activities" to "ALERTE ACTIVITÉ"
            )
            sections.forEach { (key, title) ->
                val isSelected = viewModel.currentChatSubTab == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ElectricCyan else Color.Transparent)
                        .clickable { viewModel.currentChatSubTab = key }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) AbsoluteBlack else TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.currentChatSubTab == "discussions") {
            // FIND FRIENDS & FILTER SEARCH BAR (Trouve des amis & Filtre en temps réel)
            OutlinedTextField(
                value = friendQuery,
                onValueChange = {
                    friendQuery = it
                    if (it.length >= 2) {
                        viewModel.performSearch(it)
                    }
                },
                placeholder = { Text("Filtrer ou rechercher un ami...", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = ElectricCyan
                ),
                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                leadingIcon = { Icon(Icons.Default.PersonSearch, contentDescription = null, tint = ElectricCyan) },
                trailingIcon = {
                    if (friendQuery.isNotEmpty()) {
                        IconButton(onClick = { friendQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            
            if (viewModel.isLoadingConversations && items.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElectricCyan)
                }
            } else if (items.isEmpty() && friendQuery.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        TypewriterText(text = "NO ENCRYPTED CHANNELS OPEN.", color = TextSecondary)
                    }
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // SECTION 1: NETWORK SEARCH RESULTS (If searching/typed)
                    if (friendQuery.isNotEmpty()) {
                        item {
                            Text(
                                text = "UTILISATEURS RÉSEAU // TROUVÉS (${viewModel.searchUsersResult.size})",
                                color = ElectricViolet,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        
                        if (viewModel.isSearching) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = ElectricCyan, modifier = Modifier.size(24.dp))
                                }
                            }
                        } else if (viewModel.searchUsersResult.isEmpty()) {
                            item {
                                Text(
                                    text = "Aucun utilisateur trouvé sur le réseau",
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        } else {
                            items(viewModel.searchUsersResult) { user ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0x18FFFFFF), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val status = remember(user.id) {
                                        val hash = user.id.hashCode().coerceAtLeast(0)
                                        when (hash % 3) {
                                            0 -> UserStatus.ACTIVE
                                            1 -> UserStatus.AWAY
                                            else -> UserStatus.OFFLINE
                                        }
                                    }
                                    CyberAvatarWithStatus(
                                        avatarUrl = user.avatarUrl,
                                        username = user.username,
                                        status = status,
                                        size = 44.dp,
                                        borderWidth = 1.5.dp,
                                        onClickAvatar = { viewModel.viewOtherUserProfile(user.id) }
                                    )
                                    
                                    Spacer(modifier = Modifier.width(12.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("@${user.username}", color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            if (user.isVerified) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(Icons.Default.VerifiedUser, contentDescription = "Verified", tint = ElectricCyan, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        Text("Bio: ${user.bio ?: "Actif sur StripStream"}", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp, maxLines = 1)
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(ElectricViolet)
                                                .clickable { viewModel.toggleFollowUser(user.id) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("SUIVRE", color = AbsoluteBlack, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(ElectricCyan)
                                                .clickable {
                                                    val conv = com.example.data.ChatConversation(
                                                        id = user.id,
                                                        userId = user.id,
                                                        username = user.username,
                                                        avatarUrl = user.avatarUrl,
                                                        lastMessage = null,
                                                        lastMessageTime = null,
                                                        unreadCount = 0,
                                                        isOnline = false
                                                    )
                                                    viewModel.selectConversationAndOpenChat(conv)
                                                }
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("PARLER", color = AbsoluteBlack, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "CHATS ACTIFS FILTRÉS (${filteredConversations.size})",
                                color = ElectricCyan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "DISCUSSIONS RÉCENTES",
                                color = ElectricCyan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    // SECTION 2: CONVERSATIONS/CHATS
                    if (filteredConversations.isEmpty() && friendQuery.isNotEmpty()) {
                        item {
                            Text(
                                text = "Aucun ami correspondant localement",
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    } else {
                        items(filteredConversations) { conv ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0x11FFFFFF), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                                    .clickable { viewModel.selectConversationAndOpenChat(conv) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val status = remember(conv.userId) {
                                    if (conv.isOnline) {
                                        UserStatus.ACTIVE
                                    } else {
                                        val hash = conv.userId.hashCode().coerceAtLeast(0)
                                        when (hash % 2) {
                                            0 -> UserStatus.AWAY
                                            else -> UserStatus.OFFLINE
                                        }
                                    }
                                }
                                CyberAvatarWithStatus(
                                    avatarUrl = conv.avatarUrl,
                                    username = conv.username,
                                    status = status,
                                    size = 48.dp,
                                    borderWidth = 1.5.dp,
                                    onClickAvatar = { viewModel.viewOtherUserProfile(conv.userId) }
                                )
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("@${conv.username}", color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        Text(conv.lastMessageTime?.take(10) ?: "", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = conv.lastMessage ?: "",
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                                
                                if (conv.unreadCount > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(NeonPink, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(conv.unreadCount.toString(), color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // REAL TIME ACTIVITY FEED (FLUX D'ACTIVITÉ EN DIRECT DES AMIS)
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (viewModel.activityFeed.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.NotificationsOff,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                TypewriterText(
                                    text = "AUCUNE ACTIVITÉ RÉSEAU ACTIVE.",
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(viewModel.activityFeed) { activity ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x12FFFFFF), RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            viewModel.viewOtherUserProfile(activity.userId)
                                        }
                                    ) {
                                        AsyncImage(
                                            model = activity.avatarUrl ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${activity.username}",
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .border(1.5.dp, ElectricCyan, CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "@${activity.username}",
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 13.sp
                                            )
                                            val typeLabel = when (activity.type) {
                                                "AVATAR_CHANGE" -> "PHOTO DE PROFIL CHANGÉE"
                                                "BIO_CHANGE" -> "BIO MODIFIÉE"
                                                "STATUS_CHANGE" -> "STATUT SPECIAL MIS À JOUR"
                                                else -> "ACTIVITÉ RÉSEAU AMIS"
                                            }
                                            Text(
                                                text = typeLabel,
                                                color = ElectricViolet,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    // Time indicator
                                    Text(
                                        text = activity.timestamp,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Visual changes display block
                                when (activity.type) {
                                    "AVATAR_CHANGE" -> {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0x1800E5FF), RoundedCornerShape(8.dp))
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            // Old Avatar
                                            AsyncImage(
                                                model = activity.previousValue ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${activity.username}_old",
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                                                alpha = 0.6f,
                                                contentScale = ContentScale.Crop
                                            )

                                            Spacer(modifier = Modifier.width(16.dp))

                                            Icon(
                                                Icons.Default.ArrowForward,
                                                contentDescription = "Updated to",
                                                tint = ElectricCyan,
                                                modifier = Modifier.size(20.dp)
                                            )

                                            Spacer(modifier = Modifier.width(16.dp))

                                            // New Avatar
                                            AsyncImage(
                                                model = activity.newValue ?: "https://api.dicebear.com/7.x/pixel-art/svg?seed=${activity.username}",
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clip(CircleShape)
                                                    .border(2.dp, ElectricCyan, CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Configuration d'avatar d'urgence connectée.",
                                            color = TextSecondary,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                    }

                                    "BIO_CHANGE" -> {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0x188A2BE2), RoundedCornerShape(8.dp))
                                                .border(1.dp, Color(0x338A2BE2), RoundedCornerShape(8.dp))
                                                .padding(12.dp)
                                        ) {
                                            if (!activity.previousValue.isNullOrBlank()) {
                                                Text(
                                                    text = "ANCIEN: ${activity.previousValue}",
                                                    color = TextSecondary,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 11.sp,
                                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                                                    maxLines = 1,
                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                            }
                                            Text(
                                                text = "NOUVEAU: \"${activity.newValue}\"",
                                                color = ElectricCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    "STATUS_CHANGE" -> {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0x0FFFFFFF), RoundedCornerShape(8.dp))
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(ElectricCyan)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = activity.newValue ?: "CONNECTED",
                                                color = TextPrimary,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Quick Actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Chat button
                                    Button(
                                        onClick = {
                                            val conv = com.example.data.ChatConversation(
                                                id = activity.userId,
                                                userId = activity.userId,
                                                username = activity.username,
                                                avatarUrl = activity.avatarUrl,
                                                lastMessage = null,
                                                lastMessageTime = null,
                                                unreadCount = 0,
                                                isOnline = true
                                            )
                                            viewModel.selectConversationAndOpenChat(conv)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text(
                                            text = "PARLER IM DIREC",
                                            color = ElectricCyan,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Profile button
                                    Button(
                                        onClick = {
                                            viewModel.viewOtherUserProfile(activity.userId)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text(
                                            text = "VOIR PROFIL",
                                            color = TextPrimary,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StripMainLayout(viewModel: MainViewModel) {
    Box(modifier = Modifier.fillMaxSize().background(AbsoluteBlack)) {
        AnimatedContent(
            targetState = viewModel.currentHomeTab,
            transitionSpec = {
                (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f)) togetherWith
                (fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 1.05f))
            },
            label = "tab_transition",
            modifier = Modifier.fillMaxSize()
        ) { targetTab ->
            when (targetTab) {
                "feed" -> ImmersiveVideoFeed(viewModel)
                "upload" -> UploadTabScreen(viewModel)
                "actfile" -> CyberActFilesTabScreen(viewModel)
                "live" -> LivesTabScreen(viewModel) // Should be Cyber component ideally
                "chat" -> CyberChatTabScreen(viewModel)
                "profile" -> CyberProfileTabScreen(viewModel)
                else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TypewriterText(text = "MODULE[$targetTab] = NOT_LOADED", color = ElectricViolet)
                }
            }
        }
        
        FloatingDock(
            currentTab = viewModel.currentHomeTab,
            onTabSelected = { tab ->
                viewModel.setHomeTab(tab)
            }
        )
    }
}
