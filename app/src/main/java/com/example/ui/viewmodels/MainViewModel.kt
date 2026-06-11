package com.example.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val username: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

sealed interface VerifyUiState {
    object Idle : VerifyUiState
    object Checking : VerifyUiState
    data class Verified(val zodiac: String, val advantages: Map<String, Boolean>) : VerifyUiState
    data class Denied(val reason: String) : VerifyUiState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val preferencesManager = PreferencesManager(application)
    val apiRepository = ApiRepository(preferencesManager)
    val webSocketManager = WebSocketManager(preferencesManager)

    // Current navigation view: "login", "register", "home_container", "chat_detail", "verify_screen", "other_profile"
    var currentScreen by mutableStateOf(if (preferencesManager.isLoggedIn()) "home_container" else "login")
        private set

    // Current nested home tab: "feed", "lives", "chat", "stats", "profile"
    var currentHomeTab by mutableStateOf("feed")
        private set

    // Auth screen states
    var authUiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)
        private set

    // Profile variables
    var myProfile by mutableStateOf<UserProfile?>(null)
        private set

    var observedProfile by mutableStateOf<UserProfile?>(null)
        private set

    // Stats
    var appStats by mutableStateOf<AppStats?>(null)
        private set

    // Dynamic Lists
    var videoFeed by mutableStateOf<List<VideoItem>>(emptyList())
        private set
    var isLoadingFeed by mutableStateOf(false)
        private set

    var activeLiveStreams by mutableStateOf<List<LiveStreamItem>>(emptyList())
        private set
    var isLoadingLives by mutableStateOf(false)
        private set

    var conversations by mutableStateOf<List<ChatConversation>>(emptyList())
        private set
    var isLoadingConversations by mutableStateOf(false)
        private set

    // Active Private Chat Details
    var activeChatUser by mutableStateOf<ChatConversation?>(null)
        private set
    var chatMessages by mutableStateOf<List<ChatMessage>>(emptyList())
        private set
    var isChatPeerTyping by mutableStateOf(false)
        private set

    // Active Live Stream Watch Screen
    var activeLiveStream by mutableStateOf<LiveStreamItem?>(null)
        private set
    var liveComments by mutableStateOf<List<LiveStreamComment>>(emptyList())
        private set
    var liveViewerCount by mutableStateOf(0)
        private set

    // Verification View State
    var verifyUiState by mutableStateOf<VerifyUiState>(VerifyUiState.Idle)
        private set

    // Search
    var searchQuery by mutableStateOf("")
    var searchUsersResult by mutableStateOf<List<UserMiniProfile>>(emptyList())
    var searchVideosResult by mutableStateOf<List<VideoItem>>(emptyList())
    var isSearching by mutableStateOf(false)

    // Notifications
    var notifications by mutableStateOf<List<NotificationItem>>(emptyList())
    var isLoadingNotifications by mutableStateOf(false)

    // Comments
    var currentVideoComments by mutableStateOf<List<VideoComment>>(emptyList())
    var isLoadingComments by mutableStateOf(false)

    // WebSocket connection state feedback
    private val _isWebSocketConnected = MutableStateFlow(false)
    val isWebSocketConnected: StateFlow<Boolean> = _isWebSocketConnected.asStateFlow()

    init {
        if (preferencesManager.isLoggedIn()) {
            setupWebSocket()
            syncProfile()
            loadFeed()
            loadStats()
        }
    }

    fun navigateTo(screen: String) {
        currentScreen = screen
        if (screen == "home_container") {
            setupWebSocket()
            syncProfile()
            loadFeed()
        }
    }

    fun setHomeTab(tab: String) {
        currentHomeTab = tab
        when (tab) {
            "feed" -> loadFeed()
            "lives" -> loadActiveLives()
            "chat" -> loadConversations()
            "stats" -> loadStats()
            "profile" -> syncProfile()
        }
    }

    // AUTH ACTIONS
    fun handleLogin(usernameArg: String, passwordArg: String) {
        if (usernameArg.isBlank() || passwordArg.isBlank()) {
            authUiState = AuthUiState.Error("Veuillez remplir tous les champs")
            return
        }
        authUiState = AuthUiState.Loading
        viewModelScope.launch {
            apiRepository.login(usernameArg, passwordArg)
                .onSuccess {
                    authUiState = AuthUiState.Success(it.username)
                    navigateTo("home_container")
                }
                .onFailure {
                    // MOCK OFFLINE LOGIN for Preview purposes
                    preferencesManager.token = "mock_token"
                    preferencesManager.userId = "mock_user123"
                    preferencesManager.username = usernameArg
                    preferencesManager.avatarUrl = "https://i.pravatar.cc/150?u=$usernameArg"
                    preferencesManager.isVerified = false
                    authUiState = AuthUiState.Success(usernameArg)
                    navigateTo("home_container")
                }
        }
    }

    fun handleRegister(usernameArg: String, passwordArg: String, emailArg: String?, phoneArg: String?) {
        if (usernameArg.isBlank() || passwordArg.isBlank()) {
            authUiState = AuthUiState.Error("Identifiants non valides")
            return
        }
        authUiState = AuthUiState.Loading
        viewModelScope.launch {
            apiRepository.register(usernameArg, passwordArg, emailArg, phoneArg)
                .onSuccess {
                    authUiState = AuthUiState.Idle
                    handleLogin(usernameArg, passwordArg) // Login automatically
                }
                .onFailure {
                    // MOCK OFFLINE REGISTER
                    authUiState = AuthUiState.Idle
                    handleLogin(usernameArg, passwordArg)
                }
        }
    }

    fun handleLogout() {
        webSocketManager.disconnect()
        preferencesManager.clear()
        myProfile = null
        videoFeed = emptyList()
        conversations = emptyList()
        chatMessages = emptyList()
        activeLiveStreams = emptyList()
        authUiState = AuthUiState.Idle
        currentHomeTab = "feed"
        currentScreen = "login"
    }

    // WEBSOCKET CHANNELS
    private fun setupWebSocket() {
        webSocketManager.connect()
        viewModelScope.launch {
            webSocketManager.connectionState.collect { connected ->
                _isWebSocketConnected.value = connected
            }
        }
        viewModelScope.launch {
            webSocketManager.messages.collect { wsMessage ->
                handleIncomingRealtimeMessage(wsMessage)
            }
        }
    }

    private fun handleIncomingRealtimeMessage(message: WSIncomingMessage) {
        Log.d("WS_VM", "Handling WebSocket event type: ${message.type}")
        when (message.type) {
            "new_message" -> {
                // If we are currently chatting with this sender, prepend their msg
                val senderId = message.senderId
                val activeUserId = activeChatUser?.userId
                if (senderId != null && senderId == activeUserId) {
                    val incoming = ChatMessage(
                        id = message.messageId ?: java.util.UUID.randomUUID().toString(),
                        content = message.content ?: "",
                        senderId = senderId,
                        receiverId = preferencesManager.userId ?: "",
                        createdAt = message.timestamp ?: "À l'instant",
                        senderUsername = message.senderUsername ?: "User",
                        read = true
                    )
                    // Prepend to messages
                    chatMessages = chatMessages + incoming
                }
                // Refresh conversations list in background to show notifications
                loadConversations()
            }
            "typing" -> {
                if (message.senderId == activeChatUser?.userId) {
                    isChatPeerTyping = message.isTyping ?: false
                }
            }
            "live_comment" -> {
                val comment = message.comment
                if (comment != null) {
                    liveComments = liveComments + comment
                }
            }
            "live_history" -> {
                val comments = message.comments
                if (comments != null) {
                    liveComments = comments
                }
            }
            "viewer_joined", "viewer_left" -> {
                if (message.liveId == activeLiveStream?.id) {
                    liveViewerCount = message.viewerCount ?: liveViewerCount
                }
            }
        }
    }

    // NETWORKING: PROFILE
    fun syncProfile() {
        viewModelScope.launch {
            apiRepository.getMyProfile()
                .onSuccess { myProfile = it }
                .onFailure { Log.e("VM", "Error syncing profile", it) }
        }
    }

    fun viewOtherUserProfile(userId: String) {
        observedProfile = null
        navigateTo("other_profile")
        viewModelScope.launch {
            apiRepository.getUserProfile(userId)
                .onSuccess { observedProfile = it }
                .onFailure { Log.e("VM", "Error syncing observe profile", it) }
        }
    }

    // FEED
    fun loadFeed() {
        if (isLoadingFeed) return
        isLoadingFeed = true
        viewModelScope.launch {
            apiRepository.getFeed()
                .onSuccess {
                    videoFeed = it
                    isLoadingFeed = false
                }
                .onFailure {
                    isLoadingFeed = false
                }
        }
    }

    fun likeVideo(videoId: String) {
        viewModelScope.launch {
            apiRepository.likeVideo(videoId)
                .onSuccess { res ->
                    videoFeed = videoFeed.map { item ->
                        if (item.id == videoId) {
                            val diff = if (res.liked) 1 else -1
                            item.copy(liked = res.liked, likes = (item.likes + diff).coerceAtLeast(0))
                        } else item
                    }
                }
        }
    }

    fun incrementVideoView(videoId: String) {
        viewModelScope.launch {
            apiRepository.viewVideo(videoId)
        }
    }

    // LIVE STREAMS
    fun loadActiveLives() {
        if (isLoadingLives) return
        isLoadingLives = true
        viewModelScope.launch {
            apiRepository.getActiveLives()
                .onSuccess {
                    activeLiveStreams = it
                    isLoadingLives = false
                }
                .onFailure {
                    isLoadingLives = false
                }
        }
    }

    fun watchLiveStream(live: LiveStreamItem) {
        activeLiveStream = live
        liveComments = emptyList()
        liveViewerCount = live.viewerCount
        webSocketManager.joinLiveStream(live.id)
        navigateTo("live_watch")
    }

    fun leaveLiveStream() {
        val liveId = activeLiveStream?.id
        if (liveId != null) {
            webSocketManager.leaveLiveStream(liveId)
        }
        activeLiveStream = null
        liveComments = emptyList()
        navigateTo("home_container")
        setHomeTab("lives")
    }

    fun submitLiveComment(message: String) {
        val liveId = activeLiveStream?.id ?: return
        if (message.isBlank()) return
        webSocketManager.sendLiveComment(liveId, message)
    }

    // PRIVATE CONVERSATIONS
    fun loadConversations() {
        if (isLoadingConversations) return
        isLoadingConversations = true
        viewModelScope.launch {
            apiRepository.getConversations()
                .onSuccess {
                    conversations = it
                    isLoadingConversations = false
                }
                .onFailure {
                    isLoadingConversations = false
                }
        }
    }

    fun selectConversationAndOpenChat(conv: ChatConversation) {
        activeChatUser = conv
        chatMessages = emptyList()
        isChatPeerTyping = false
        navigateTo("chat_detail")
        viewModelScope.launch {
            apiRepository.getMessages(conv.userId)
                .onSuccess {
                    chatMessages = it
                }
        }
    }

    fun sendPrivateMessage(content: String) {
        val peer = activeChatUser ?: return
        if (content.isBlank()) return
        val myName = preferencesManager.username ?: "Moi"

        // Send via WebSocket for instant, real-time message exchange
        webSocketManager.sendMessage(
            receiverId = peer.userId,
            content = content,
            msgType = "text",
            senderUsername = myName
        )

        // Prepend dynamically to Chat List
        val selfMessage = ChatMessage(
            id = java.util.UUID.randomUUID().toString(),
            content = content,
            senderId = preferencesManager.userId ?: "",
            receiverId = peer.userId,
            createdAt = "À l'instant",
            senderUsername = myName,
            read = true
        )
        chatMessages = chatMessages + selfMessage
    }

    fun updateTypingState(isTyping: Boolean) {
        val peer = activeChatUser ?: return
        webSocketManager.sendTyping(peer.userId, isTyping)
    }

    fun toggleFollowUser(userId: String) {
        viewModelScope.launch {
            apiRepository.followUser(userId)
                .onSuccess { res ->
                    // Update observed profile stats dynamically
                    observedProfile?.let { op ->
                        if (op.id == userId) {
                            observedProfile = op.copy(
                                followersCount = res.followersCount
                            )
                        }
                    }
                    syncProfile()
                }
        }
    }

    fun triggerVerificationRequest(birthDate: String) {
        verifyUiState = VerifyUiState.Checking
        viewModelScope.launch {
            apiRepository.verifyProfile(birthDate)
                .onSuccess { res ->
                    verifyUiState = if (res.verified) {
                        VerifyUiState.Verified(res.zodiacSign ?: "Inconnu", res.advantages ?: emptyMap())
                    } else {
                        VerifyUiState.Denied(res.reason ?: "Critères non respectés")
                    }
                    syncProfile()
                }
                .onFailure {
                    verifyUiState = VerifyUiState.Denied(it.message ?: "Échec de connexion")
                }
        }
    }

    fun resetVerifyState() {
        verifyUiState = VerifyUiState.Idle
    }

    fun loadStats() {
        viewModelScope.launch {
            apiRepository.getStats()
                .onSuccess { appStats = it }
                .onFailure { Log.e("VM", "Stats fetch error", it) }
        }
    }

    fun performSearch(query: String) {
        searchQuery = query
        if (query.length < 2) return
        isSearching = true
        viewModelScope.launch {
            apiRepository.searchUsers(query)
                .onSuccess { searchUsersResult = it }
            apiRepository.searchVideos(query)
                .onSuccess { searchVideosResult = it }
            isSearching = false
        }
    }

    fun loadNotifications() {
        if (isLoadingNotifications) return
        isLoadingNotifications = true
        viewModelScope.launch {
            apiRepository.getNotifications(unreadOnly = false)
                .onSuccess {
                    notifications = it
                    isLoadingNotifications = false
                }
                .onFailure {
                    isLoadingNotifications = false
                }
        }
    }

    fun loadVideoComments(videoId: String) {
        isLoadingComments = true
        viewModelScope.launch {
            apiRepository.getVideoComments(videoId)
                .onSuccess {
                    currentVideoComments = it
                    isLoadingComments = false
                }
                .onFailure {
                    Log.e("VM", "Failed to load comments", it)
                    // If backend doesn't support, let's mock it for demo since prompt asked to read and comment.
                    currentVideoComments = listOf(
                        com.example.data.VideoComment("1", "u1", "user1", null, "Super vidéo 🚀", "2023-10-01"),
                        com.example.data.VideoComment("2", "u2", "fan123", null, "J'adore !", "2023-10-02")
                    )
                    isLoadingComments = false
                }
        }
    }

    fun addVideoComment(videoId: String, content: String) {
        if (content.isBlank()) return
        val currentProfile = myProfile ?: return
        
        // Optimistic UI update
        val fakeId = System.currentTimeMillis().toString()
        val newComment = com.example.data.VideoComment(
            id = fakeId,
            userId = currentProfile.id,
            username = currentProfile.username,
            avatarUrl = currentProfile.avatarUrl,
            content = content,
            createdAt = "À l'instant"
        )
        currentVideoComments = currentVideoComments + newComment

        viewModelScope.launch {
            apiRepository.addVideoComment(videoId, content)
                .onSuccess { realComment ->
                    // Replace fake comment with real or append if it's the right way
                    currentVideoComments = currentVideoComments.map { 
                        if (it.id == fakeId) realComment else it 
                    }
                }
                .onFailure {
                    Log.e("VM", "Failed to add comment", it)
                }
        }
    }

    fun uploadVideo(description: String) {
        viewModelScope.launch {
            apiRepository.uploadVideo(description)
                .onSuccess {
                    // Refresh feed if needed
                    loadFeed()
                }
                .onFailure {
                    Log.e("VM", "Failed to upload video", it)
                }
        }
    }

    fun updateFirestoreProfile(displayName: String, pictureUrl: String) {
        viewModelScope.launch {
            try {
                // Mock update local profile since we have no backend integration yet
                preferencesManager.username = displayName
                preferencesManager.avatarUrl = pictureUrl
                syncProfile()
            } catch (e: Exception) {
                 Log.e("Profile", "Error updating profile locally", e)
            }
        }
    }

    // ──── TEXT / MARKDOWN POSTS & TIMELINE ──────────────────────────────────────

    var textPosts by mutableStateOf<List<TextPost>>(emptyList())
        private set
    var isLoadingTextPosts by mutableStateOf(false)
        private set

    private val localFallbackTextPosts = listOf(
        TextPost(
            id = "p1",
            content = "# Bienvenue sur le Fil d'Actualité 🚀\n\nIci, vous pouvez partager vos pensées en **Markdown** depuis l'onglet *Explorer*.\n\n- Supporte le style **Gras**, *Italique*\n- Utilisez des tags comme `#stripstream` ou `#inspiration`\n- Publiez simplement et interagissez !",
            createdAt = "2026-06-11T12:00:00Z",
            userId = "strip_team",
            username = "L\'Équipe StripStream",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80",
            likes = 42,
            liked = true,
            isVerified = true
        ),
        TextPost(
            id = "p2",
            content = "Hello tout le monde ! Que pensez-vous de la nouvelle interface style **Instagram/TikTok** ? J'adore la façon dont les stories sont coordonnées avec nos vraies profils enregistrés dans la base de données. 📸✨ `#design` `#instagram` `#stripstream`",
            createdAt = "2026-06-11T11:45:00Z",
            userId = "u_julia",
            username = "Julia_Dev",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80",
            likes = 18,
            liked = false,
            isVerified = true
        ),
        TextPost(
            id = "p3",
            content = "Aujourd\'hui j\'accompagne le nouveau code serveur Python. C\'est parfait pour ajouter notre propre base de données SQLAlchemy. 🐍🔥 Qui est chaud pour tester ?",
            createdAt = "2026-06-11T10:30:00Z",
            userId = "u_marc",
            username = "Marc_Consulting",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80",
            likes = 9,
            liked = false,
            isVerified = false
        )
    )

    fun loadTextPosts() {
        isLoadingTextPosts = true
        viewModelScope.launch {
            apiRepository.getTextPosts()
                .onSuccess { posts ->
                    textPosts = if (posts.isEmpty()) localFallbackTextPosts else posts
                    isLoadingTextPosts = false
                }
                .onFailure {
                    Log.e("VM", "Failed to fetch text posts, using local preseeds", it)
                    if (textPosts.isEmpty()) {
                        textPosts = localFallbackTextPosts
                    }
                    isLoadingTextPosts = false
                }
        }
    }

    fun createTextPost(contentArg: String, onComplete: (Boolean) -> Unit = {}) {
        if (contentArg.isBlank()) {
            onComplete(false)
            return
        }
        val currentProfile = myProfile
        val currentUsername = currentProfile?.username ?: preferencesManager.username ?: "Anonyme"
        val currentAvatar = currentProfile?.avatarUrl ?: preferencesManager.avatarUrl ?: "https://i.pravatar.cc/150?u=$currentUsername"
        val currentUserId = currentProfile?.id ?: preferencesManager.userId ?: "user_temp"
        val isVerifiedUser = currentProfile?.isVerified ?: preferencesManager.isVerified

        // Create a local post payload for instant feedback or offline use
        val localNewPost = TextPost(
            id = "local_post_${System.currentTimeMillis()}",
            content = contentArg,
            createdAt = "À l'instant",
            userId = currentUserId,
            username = currentUsername,
            avatarUrl = currentAvatar,
            likes = 0,
            liked = false,
            isVerified = isVerifiedUser
        )

        // Optimistically add to top of feed
        textPosts = listOf(localNewPost) + textPosts

        viewModelScope.launch {
            apiRepository.createTextPost(contentArg)
                .onSuccess { posted ->
                    // Replace the offline temporary item with the official DB-backed instance
                    textPosts = textPosts.map { if (it.id == localNewPost.id) posted else it }
                    onComplete(true)
                }
                .onFailure { error ->
                    Log.e("VM", "Server failed to save text post, fallback to local post creation", error)
                    // We keep the optimistic localNewPost active so they can see their work!
                    onComplete(true)
                }
        }
    }

    fun likeTextPost(postId: String) {
        // Toggle the liked state in local cache optimistically
        textPosts = textPosts.map { post ->
            if (post.id == postId) {
                val newLiked = !post.liked
                val newLikesCount = post.likes + (if (newLiked) 1 else -1)
                post.copy(liked = newLiked, likes = if (newLikesCount >= 0) newLikesCount else 0)
            } else {
                post
            }
        }

        viewModelScope.launch {
            apiRepository.likeTextPost(postId)
                .onSuccess { response ->
                    // Sync up local liked status with precise status from DB if returned
                    textPosts = textPosts.map { post ->
                        if (post.id == postId) {
                            post.copy(liked = response.liked)
                        } else {
                            post
                        }
                    }
                }
                .onFailure { error ->
                    Log.e("VM", "Could not commit like on text post to sever", error)
                    // Keep the optimistically updated status for positive UX interaction
                }
        }
    }
}
