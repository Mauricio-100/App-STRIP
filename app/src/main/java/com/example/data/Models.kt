package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "is_verified") val isVerified: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String,
    @Json(name = "email") val email: String?,
    @Json(name = "phone_number") val phoneNumber: String?
)

@JsonClass(generateAdapter = true)
data class UserProfile(
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "bio") val bio: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "phone_number") val phoneNumber: String?,
    @Json(name = "is_verified") val isVerified: Boolean = false,
    @Json(name = "zodiac_sign") val zodiacSign: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "last_seen") val lastSeen: String? = null,
    @Json(name = "followers_count") val followersCount: Int = 0,
    @Json(name = "following_count") val followingCount: Int = 0,
    @Json(name = "likes_received") val likesReceived: Int = 0,
    @Json(name = "videos_count") val videosCount: Int = 0,
    @Json(name = "is_online") val isOnline: Boolean = false,
    @Json(name = "is_following") val isFollowing: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class UserMiniProfile(
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "bio") val bio: String?,
    @Json(name = "is_verified") val isVerified: Boolean = false,
    @Json(name = "zodiac_sign") val zodiacSign: String? = null,
    @Json(name = "is_following") val isFollowing: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class VideoItem(
    @Json(name = "id") val id: String,
    @Json(name = "video_url") val videoUrl: String,
    @Json(name = "thumbnail_url") val thumbnailUrl: String?,
    @Json(name = "description") val description: String,
    @Json(name = "likes") val likes: Int,
    @Json(name = "views") val views: Int,
    @Json(name = "duration") val duration: Double?,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "liked") val liked: Boolean = false,
    @Json(name = "is_verified") val isVerified: Boolean = false
)

@JsonClass(generateAdapter = true)
data class LiveStreamItem(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "thumbnail_url") val thumbnailUrl: String?,
    @Json(name = "viewer_count") val viewerCount: Int = 0,
    @Json(name = "is_live") val isLive: Boolean = false,
    @Json(name = "started_at") val startedAt: String?,
    @Json(name = "is_private") val isPrivate: Boolean = false,
    @Json(name = "stream_key") val streamKey: String? = null
)

@JsonClass(generateAdapter = true)
data class LiveStreamCreateRequest(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "is_private") val isPrivate: Boolean = false
)

@JsonClass(generateAdapter = true)
data class LiveStreamCreateResponse(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "stream_key") val streamKey: String,
    @Json(name = "is_private") val isPrivate: Boolean,
    @Json(name = "rtmp_url") val rtmpUrl: String,
    @Json(name = "started_at") val startedAt: String
)

@JsonClass(generateAdapter = true)
data class ChatConversation(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "last_message") val lastMessage: String?,
    @Json(name = "last_message_time") val lastMessageTime: String?,
    @Json(name = "unread_count") val unreadCount: Int = 0,
    @Json(name = "is_online") val isOnline: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
    @Json(name = "id") val id: String,
    @Json(name = "content") val content: String,
    @Json(name = "type") val type: String = "text",
    @Json(name = "sender_id") val senderId: String,
    @Json(name = "receiver_id") val receiverId: String,
    @Json(name = "read") val read: Boolean = false,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "sender_username") val senderUsername: String,
    @Json(name = "sender_avatar") val senderAvatar: String? = null
)

@JsonClass(generateAdapter = true)
data class SendMessageRequest(
    @Json(name = "content") val content: String,
    @Json(name = "receiver_id") val receiverId: String,
    @Json(name = "type") val type: String = "text"
)

@JsonClass(generateAdapter = true)
data class VerificationCriteria(
    @Json(name = "birth_date") val birthDate: String // Format YYYY-MM-DD
)

@JsonClass(generateAdapter = true)
data class VerificationResult(
    @Json(name = "verified") val verified: Boolean,
    @Json(name = "badge") val badge: Boolean,
    @Json(name = "reason") val reason: String?,
    @Json(name = "zodiac_sign") val zodiacSign: String?,
    @Json(name = "advantages") val advantages: Map<String, Boolean>?
)

@JsonClass(generateAdapter = true)
data class FollowResponse(
    @Json(name = "following") val following: Boolean,
    @Json(name = "message") val message: String,
    @Json(name = "followers_count") val followersCount: Int
)

@JsonClass(generateAdapter = true)
data class LikeResponse(
    @Json(name = "liked") val liked: Boolean
)

@JsonClass(generateAdapter = true)
data class AppStats(
    @Json(name = "total_users") val totalUsers: Int,
    @Json(name = "verified_users") val verifiedUsers: Int,
    @Json(name = "online_users") val onlineUsers: Int,
    @Json(name = "total_videos") val totalVideos: Int,
    @Json(name = "active_lives") val activeLives: Int,
    @Json(name = "total_messages") val totalMessages: Int,
    @Json(name = "timestamp") val timestamp: String
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    @Json(name = "bio") val bio: String?,
    @Json(name = "phone_number") val phoneNumber: String?
)

@JsonClass(generateAdapter = true)
data class UploadVideoResponse(
    @Json(name = "id") val id: String,
    @Json(name = "video_url") val videoUrl: String,
    @Json(name = "thumbnail_url") val thumbnailUrl: String?,
    @Json(name = "description") val description: String,
    @Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class WSIncomingMessage(
    @Json(name = "type") val type: String,
    @Json(name = "message_id") val messageId: String? = null,
    @Json(name = "sender_id") val senderId: String? = null,
    @Json(name = "content") val content: String? = null,
    @Json(name = "sender_username") val senderUsername: String? = null,
    @Json(name = "timestamp") val timestamp: String? = null,
    @Json(name = "is_typing") val isTyping: Boolean? = null,
    @Json(name = "live_id") val liveId: String? = null,
    @Json(name = "viewer_count") val viewerCount: Int? = null,
    @Json(name = "comment") val comment: LiveStreamComment? = null,
    @Json(name = "comments") val comments: List<LiveStreamComment>? = null
)

@JsonClass(generateAdapter = true)
data class LiveStreamComment(
    @Json(name = "user_id") val userId: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "message") val message: String,
    @Json(name = "timestamp") val timestamp: String
)

@JsonClass(generateAdapter = true)
data class VideoComment(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "content") val content: String,
    @Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class AddCommentRequest(
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class NotificationItem(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,
    @Json(name = "from_user_id") val fromUserId: String,
    @Json(name = "from_username") val fromUsername: String,
    @Json(name = "from_avatar") val fromAvatar: String?,
    @Json(name = "target_id") val targetId: String?,
    @Json(name = "message") val message: String,
    @Json(name = "read") val read: Boolean,
    @Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class TextPost(
    @Json(name = "id") val id: String,
    @Json(name = "content") val content: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "likes_count") val likes: Int = 0,
    @Json(name = "views_count") val viewsCount: Int = 0,
    @Json(name = "liked") val liked: Boolean = false,
    @Json(name = "is_verified") val isVerified: Boolean = false
)

@JsonClass(generateAdapter = true)
data class CreatePostRequest(
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class StoryItemUser(
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatar_url") val avatarUrl: String?
)

@JsonClass(generateAdapter = true)
data class StoryItemResponse(
    @Json(name = "id") val id: String,
    @Json(name = "media_url") val mediaUrl: String,
    @Json(name = "media_type") val mediaType: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "user") val user: StoryItemUser
)

data class CyberActivityFeedItem(
    val id: String,
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val type: String, // "AVATAR_CHANGE", "BIO_CHANGE", "STATUS_CHANGE"
    val previousValue: String? = null,
    val newValue: String? = null,
    val timestamp: String
)


