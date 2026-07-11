package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cmo_strip_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SERVER_URL = "custom_server_url_v2"
        private const val KEY_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_AVATAR_URL = "avatar_url"
        private const val KEY_IS_VERIFIED = "is_verified"
        private const val KEY_ZODIAC_SIGN = "zodiac_sign"
        private const val KEY_BIO = "profile_bio"

        private const val KEY_WATCH_TIME = "watch_time_sec_v2"
        private const val KEY_LIKES = "likes_count_v2"
        private const val KEY_COMMENTS = "comments_count_v2"
        private const val KEY_POSTS = "posts_count_v2"
        private const val KEY_TREND_LINE = "trend_line_v2"
    }

    var watchTimeSeconds: Int
        get() = prefs.getInt(KEY_WATCH_TIME, 6 * 60) // starts with a realistic initial watch time of 6 mins
        set(value) = prefs.edit().putInt(KEY_WATCH_TIME, value).apply()

    var likesCount: Int
        get() = prefs.getInt(KEY_LIKES, 4) // starts with realistic values
        set(value) = prefs.edit().putInt(KEY_LIKES, value).apply()

    var commentsCount: Int
        get() = prefs.getInt(KEY_COMMENTS, 2)
        set(value) = prefs.edit().putInt(KEY_COMMENTS, value).apply()

    var postsCount: Int
        get() = prefs.getInt(KEY_POSTS, 1)
        set(value) = prefs.edit().putInt(KEY_POSTS, value).apply()

    var trendLineCsv: String
        get() = prefs.getString(KEY_TREND_LINE, "12,18,15,22,35,28,42") ?: "12,18,15,22,35,28,42"
        set(value) = prefs.edit().putString(KEY_TREND_LINE, value).apply()

    var serverUrl: String
        get() = prefs.getString(KEY_SERVER_URL, "https://hoosthubs-g.onrender.com/") ?: "https://hoosthubs-g.onrender.com/"
        set(value) {
            val formatted = if (value.endsWith("/")) value else "$value/"
            prefs.edit().putString(KEY_SERVER_URL, formatted).apply()
        }

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_TOKEN, value).apply()

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var username: String?
        get() = prefs.getString(KEY_USERNAME, null)
        set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()

    var avatarUrl: String?
        get() = prefs.getString(KEY_AVATAR_URL, null)
        set(value) = prefs.edit().putString(KEY_AVATAR_URL, value).apply()

    var isVerified: Boolean
        get() = prefs.getBoolean(KEY_IS_VERIFIED, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_VERIFIED, value).apply()

    var zodiacSign: String?
        get() = prefs.getString(KEY_ZODIAC_SIGN, null)
        set(value) = prefs.edit().putString(KEY_ZODIAC_SIGN, value).apply()

    var bio: String?
        get() = prefs.getString(KEY_BIO, "Mode local d'urgence (Serveur de secours autonome)")
        set(value) = prefs.edit().putString(KEY_BIO, value).apply()

    fun isLoggedIn(): Boolean {
        return !token.isNullOrBlank() && !userId.isNullOrBlank()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
