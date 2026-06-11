package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class ApiRepository(private val preferencesManager: PreferencesManager) {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            // Attach bearer token if stored
            preferencesManager.token?.let { token ->
                builder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val apiService = Retrofit.Builder()
        .baseUrl("https://hoosthubs-g.onrender.com/")
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(ApiService::class.java)

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(username, password)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Cache details instantly
                    preferencesManager.token = body.accessToken
                    preferencesManager.userId = body.userId
                    preferencesManager.username = body.username
                    preferencesManager.avatarUrl = body.avatarUrl
                    preferencesManager.isVerified = body.isVerified ?: false
                    Result.success(body)
                } else {
                    Result.failure(Exception("Login error: Empty body"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown server error"
                Result.failure(Exception("Error ${response.code()}: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, password: String, email: String?, phoneNumber: String?): Result<Map<String, Any>> {
        return try {
            val response = apiService.register(RegisterRequest(username, password, email, phoneNumber))
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyMap())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown registration error"
                Result.failure(Exception("Error ${response.code()}: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyProfile(): Result<UserProfile> {
        return try {
            val response = apiService.getMyProfile()
            if (response.isSuccessful && response.body() != null) {
                val profile = response.body()!!
                preferencesManager.username = profile.username
                preferencesManager.avatarUrl = profile.avatarUrl
                preferencesManager.isVerified = profile.isVerified
                preferencesManager.zodiacSign = profile.zodiacSign
                Result.success(profile)
            } else {
                Result.failure(Exception("Profile sync failed: code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val response = apiService.getUserProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("User fetch failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFeed(cursor: String? = null): Result<List<VideoItem>> {
        return try {
            // Let's first try standard feed. If it fails or is empty, we fallback to getFeedRandomApi
            val response = apiService.getFeed(cursor, 20)
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!
                if (list.isNotEmpty()) {
                    return Result.success(list)
                }
            }
            // Fallback to random feed
            val randResponse = apiService.getFeedRandomApi(20)
            if (randResponse.isSuccessful && randResponse.body() != null) {
                Result.success(randResponse.body()!!)
            } else {
                val oldFallback = apiService.getFeedRandom(10)
                if (oldFallback.isSuccessful && oldFallback.body() != null) {
                    Result.success(oldFallback.body()!!)
                } else {
                    Result.failure(Exception("Feed loading failed"))
                }
            }
        } catch (e: Exception) {
            // MOCK RESPONSE
            Result.success(
                listOf(
                    VideoItem("1", "https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4", null, "Super Video Live", 12400, 312, null, "2026-06-11T00:00:00Z", "user1", "John", "https://i.pravatar.cc/150?u=user1", false, true),
                    VideoItem("2", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", null, "Incroyable moment !!", 5210, 42, null, "2026-06-11T00:00:00Z", "user2", "Alice", "https://i.pravatar.cc/150?u=user2", false, false)
                )
            )
        }
    }

    suspend fun likeVideo(videoId: String): Result<LikeResponse> {
        return try {
            val response = apiService.likeVideo(videoId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Like action failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun viewVideo(videoId: String) {
        try {
            apiService.viewVideo(videoId)
        } catch (e: Exception) {
            // Ignore view increment errors
        }
    }

    suspend fun getConversations(): Result<List<ChatConversation>> {
        return try {
            val response = apiService.getConversations()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load conversations"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessages(userId: String): Result<List<ChatMessage>> {
        return try {
            val response = apiService.getMessages(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get messages for $userId"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(receiverId: String, content: String): Result<ChatMessage> {
        return try {
            val response = apiService.sendMessage(SendMessageRequest(content, receiverId))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to send message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun followUser(userId: String): Result<FollowResponse> {
        return try {
            val response = apiService.followUser(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Follow action failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveLives(): Result<List<LiveStreamItem>> {
        return try {
            val response = apiService.getActiveLives()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Active lives fetched failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startLive(title: String, description: String?, isPrivate: Boolean): Result<LiveStreamCreateResponse> {
        return try {
            val response = apiService.startLive(LiveStreamCreateRequest(title, description, isPrivate))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Start live request failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun stopLive(liveId: String): Result<Boolean> {
        return try {
            val response = apiService.stopLive(liveId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Stop live failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyProfile(birthDate: String): Result<VerificationResult> {
        return try {
            val response = apiService.verifyProfile(VerificationCriteria(birthDate))
            if (response.isSuccessful && response.body() != null) {
                val verification = response.body()!!
                if (verification.verified) {
                    preferencesManager.isVerified = true
                    preferencesManager.zodiacSign = verification.zodiacSign
                }
                Result.success(verification)
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                var parsedReason: String? = null
                try {
                    // Try to parse reason from errorBody if JSON
                    val adapter = moshi.adapter(Map::class.java)
                    val map = adapter.fromJson(errorBody)
                    parsedReason = map?.get("detail") as? String
                } catch (ignored: Exception) {}
                Result.failure(Exception(parsedReason ?: "La vérification a échoué. Veuillez vous assurer d'avoir le profil complet, vos 10k+ abonnés et 100k+ vues cumulées."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStats(): Result<AppStats> {
        return try {
            val response = apiService.getStats()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load statistics"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotifications(unreadOnly: Boolean = false): Result<List<NotificationItem>> {
        return try {
            val response = apiService.getNotifications(unreadOnly = unreadOnly)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load notifications"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchUsers(query: String): Result<List<UserMiniProfile>> {
        return try {
            val response = apiService.searchUsers(query = query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Search failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchVideos(query: String): Result<List<VideoItem>> {
        return try {
            val response = apiService.searchVideos(query = query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Search failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(bio: String?, phoneNumber: String?): Result<Map<String, String>> {
        return try {
            val bioPart = bio?.let { okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, it) }
            val phonePart = phoneNumber?.let { okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, it) }
            val response = apiService.updateProfile(bioPart, phonePart, null)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVideoComments(videoId: String): Result<List<VideoComment>> {
        return try {
            val response = apiService.getVideoComments(videoId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load comments"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addVideoComment(videoId: String, content: String): Result<VideoComment> {
        return try {
            val response = apiService.addVideoComment(videoId, AddCommentRequest(content))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to add comment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadVideo(description: String): Result<UploadVideoResponse> {
        return try {
            val emptyBody = okhttp3.RequestBody.create(null, ByteArray(0))
            val videoPart = okhttp3.MultipartBody.Part.createFormData("video", "video.mp4", emptyBody)
            val descBody = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, description)
            val response = apiService.uploadVideo(videoPart, descBody, true, true)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to upload video"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTextPosts(): Result<List<TextPost>> {
        return try {
            val response = apiService.getTextPosts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch posts from backend: code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTextPost(content: String): Result<TextPost> {
        return try {
            val response = apiService.createTextPost(CreatePostRequest(content))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create post: code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun likeTextPost(postId: String): Result<LikeResponse> {
        return try {
            val response = apiService.likeTextPost(postId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to like post: code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
