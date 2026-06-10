package com.example.data

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.*
import java.util.concurrent.TimeUnit

class WebSocketManager(private val preferencesManager: PreferencesManager) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .writeTimeout(5000, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _messages = MutableSharedFlow<WSIncomingMessage>(extraBufferCapacity = 100)
    val messages: SharedFlow<WSIncomingMessage> = _messages.asSharedFlow()

    private val _connectionState = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val connectionState: SharedFlow<Boolean> = _connectionState.asSharedFlow()

    private var isConnecting = false
    private var isClosedIntentionally = false

    fun connect() {
        val userId = preferencesManager.userId ?: return
        if (webSocket != null || isConnecting) return
        isConnecting = true
        isClosedIntentionally = false

        val url = "wss://hoosthubs-g.onrender.com/ws/$userId"
        Log.d("WS", "Connecting to WebSocket: $url")
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnecting = false
                scope.launch {
                    _connectionState.emit(true)
                }
                Log.d("WS", "WebSocket Opened successfully")
                startHeartbeat()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WS", "Received text: $text")
                try {
                    val adapter = moshi.adapter(WSIncomingMessage::class.java)
                    val msg = adapter.fromJson(text)
                    if (msg != null) {
                        scope.launch {
                            _messages.emit(msg)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WS", "Error parsing WebSocket message", e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WS", "WebSocket closing: $code / $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                webSocketClosed()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WS", "WebSocket Failure", t)
                webSocketClosed()
                // Reconnect if it wasn't intentional
                if (!isClosedIntentionally) {
                    scope.launch {
                        delay(5000)
                        connect()
                    }
                }
            }
        })
    }

    private fun startHeartbeat() {
        scope.launch {
            while (webSocket != null && !isClosedIntentionally) {
                delay(30000) // 30 seconds
                try {
                    val pingJson = """{"type":"ping"}"""
                    webSocket?.send(pingJson)
                } catch (e: Exception) {
                    Log.e("WS", "Error sending WebSocket ping", e)
                }
            }
        }
    }

    private fun webSocketClosed() {
        webSocket = null
        isConnecting = false
        scope.launch {
            _connectionState.emit(false)
        }
    }

    fun sendMessage(receiverId: String, content: String, msgType: String = "text", senderUsername: String) {
        val wsSocket = webSocket
        if (wsSocket == null) {
            Log.e("WS", "Cannot send message, WebSocket is not connected")
            return
        }
        scope.launch {
            try {
                // Formatting payload as:
                // {"type":"message","receiver_id":"...","content":"...","msg_type":"...","sender_username":"..."}
                val payload = """
                    {
                        "type": "message",
                        "receiver_id": "$receiverId",
                        "content": "${escapeJson(content)}",
                        "msg_type": "$msgType",
                        "sender_username": "$senderUsername"
                    }
                """.trimIndent()
                wsSocket.send(payload)
                Log.d("WS", "Sent message payload: $payload")
            } catch (e: Exception) {
                Log.e("WS", "Error sending message payload", e)
            }
        }
    }

    fun sendLiveComment(liveId: String, comment: String) {
        val wsSocket = webSocket
        if (wsSocket == null) {
            Log.e("WS", "Cannot send live comment, WebSocket is not connected")
            return
        }
        scope.launch {
            try {
                val payload = """
                    {
                        "type": "live_comment",
                        "live_id": "$liveId",
                        "comment": "${escapeJson(comment)}"
                    }
                """.trimIndent()
                wsSocket.send(payload)
                Log.d("WS", "Sent Live Comment payload: $payload")
            } catch (e: Exception) {
                Log.e("WS", "Error sending live comment payload", e)
            }
        }
    }

    fun joinLiveStream(liveId: String) {
        val wsSocket = webSocket ?: return
        scope.launch {
            try {
                val payload = """
                    {
                        "type": "join_live",
                        "live_id": "$liveId"
                    }
                """.trimIndent()
                wsSocket.send(payload)
                Log.d("WS", "Joined Live Stream: $liveId")
            } catch (e: Exception) {
                Log.e("WS", "Error joining live stream", e)
            }
        }
    }

    fun leaveLiveStream(liveId: String) {
        val wsSocket = webSocket ?: return
        scope.launch {
            try {
                val payload = """
                    {
                        "type": "leave_live",
                        "live_id": "$liveId"
                    }
                """.trimIndent()
                wsSocket.send(payload)
                Log.d("WS", "Left Live Stream: $liveId")
            } catch (e: Exception) {
                Log.e("WS", "Error leaving live stream", e)
            }
        }
    }

    fun sendTyping(receiverId: String, isTyping: Boolean) {
        val wsSocket = webSocket ?: return
        scope.launch {
            try {
                val payload = """
                    {
                        "type": "typing",
                        "receiver_id": "$receiverId",
                        "is_typing": $isTyping
                    }
                """.trimIndent()
                wsSocket.send(payload)
            } catch (e: Exception) {
                Log.e("WS", "Error sending typing state", e)
            }
        }
    }

    fun disconnect() {
        isClosedIntentionally = true
        webSocket?.close(1000, "Clean Close")
        webSocket = null
        isConnecting = false
    }

    private fun escapeJson(text: String): String {
        return text.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
