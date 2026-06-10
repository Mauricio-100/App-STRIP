package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cmo_strip_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_AVATAR_URL = "avatar_url"
        private const val KEY_IS_VERIFIED = "is_verified"
        private const val KEY_ZODIAC_SIGN = "zodiac_sign"
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

    fun isLoggedIn(): Boolean {
        return !token.isNullOrBlank() && !userId.isNullOrBlank()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
