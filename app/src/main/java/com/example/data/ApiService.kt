package com.example.data

import retrofit2.http.*
import retrofit2.Response

interface ApiService {

    @FormUrlEncoded
    @POST("api/token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("api/users/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<Map<String, Any>> // returns user details like {"id": "...", "username": "..."}

    @GET("api/users/me")
    suspend fun getMyProfile(): Response<UserProfile>

    @GET("api/users/{user_id}")
    suspend fun getUserProfile(
        @Path("user_id") userId: String
    ): Response<UserProfile>

    @GET("api/feed/random") // fallback or search
    suspend fun getFeedRandom(
        @Query("limit") limit: Int = 10
    ): Response<List<VideoItem>>

    @GET("api/feed")
    suspend fun getFeed(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<List<VideoItem>>

    @GET("api/feed/random")
    suspend fun getFeedRandomApi(
        @Query("limit") limit: Int = 20
    ): Response<List<VideoItem>>

    @POST("api/videos/{video_id}/like")
    suspend fun likeVideo(
        @Path("video_id") videoId: String
    ): Response<LikeResponse>

    @POST("api/videos/{video_id}/view")
    suspend fun viewVideo(
        @Path("video_id") videoId: String
    ): Response<Map<String, String>>

    @GET("api/messages/conversations")
    suspend fun getConversations(): Response<List<ChatConversation>>

    @GET("api/messages/{user_id}")
    suspend fun getMessages(
        @Path("user_id") userId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<List<ChatMessage>>

    @POST("api/messages/send")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): Response<ChatMessage>

    @POST("api/users/{user_id}/follow")
    suspend fun followUser(
        @Path("user_id") userId: String
    ): Response<FollowResponse>

    @GET("api/lives/active")
    suspend fun getActiveLives(): Response<List<LiveStreamItem>>

    @POST("api/lives/start")
    suspend fun startLive(
        @Body request: LiveStreamCreateRequest
    ): Response<LiveStreamCreateResponse>

    @POST("api/lives/{live_id}/stop")
    suspend fun stopLive(
        @Path("live_id") liveId: String
    ): Response<Map<String, String>>

    @POST("api/users/me/verify")
    suspend fun verifyProfile(
        @Body criteria: VerificationCriteria
    ): Response<VerificationResult>

    @GET("api/stats")
    suspend fun getStats(): Response<AppStats>

    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("unread_only") unreadOnly: Boolean = false,
        @Query("limit") limit: Int = 50
    ): Response<List<NotificationItem>>

    @GET("api/search")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("type") type: String = "users",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<List<UserMiniProfile>>

    @GET("api/search")
    suspend fun searchVideos(
        @Query("q") query: String,
        @Query("type") type: String = "videos",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<List<VideoItem>>

    @PUT("api/users/me")
    @Multipart
    suspend fun updateProfile(
        @Part("bio") bio: okhttp3.RequestBody?,
        @Part("phone_number") phoneNumber: okhttp3.RequestBody?,
        @Part avatar: okhttp3.MultipartBody.Part?
    ): Response<Map<String, String>>

    @GET("api/videos/{video_id}/comments")
    suspend fun getVideoComments(@Path("video_id") videoId: String): Response<List<VideoComment>>

    @POST("api/videos/{video_id}/comments")
    suspend fun addVideoComment(
        @Path("video_id") videoId: String,
        @Body request: AddCommentRequest
    ): Response<VideoComment>

    @POST("api/videos/upload")
    @Multipart
    suspend fun uploadVideo(
        @Part video: okhttp3.MultipartBody.Part,
        @Part("description") description: okhttp3.RequestBody,
        @Part("is_public") isPublic: Boolean,
        @Part("has_original_sound") hasOriginalSound: Boolean
    ): Response<UploadVideoResponse>

    @GET("api/actfile")
    suspend fun getTextPosts(): Response<List<TextPost>>

    @POST("api/actfile")
    suspend fun createTextPost(
        @Body request: CreatePostRequest
    ): Response<TextPost>

    @POST("api/actfile/{post_id}/like")
    suspend fun likeTextPost(
        @Path("post_id") postId: String
    ): Response<LikeResponse>

    @GET("api/stories")
    suspend fun getStories(): Response<List<StoryItemResponse>>

    @POST("api/stories")
    @Multipart
    suspend fun uploadStory(
        @Part file: okhttp3.MultipartBody.Part,
        @Part("effect") effect: okhttp3.RequestBody?
    ): Response<Map<String, Any>>
}
