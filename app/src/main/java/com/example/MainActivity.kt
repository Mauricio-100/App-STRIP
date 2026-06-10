package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup full-bleed edge-to-edge drawing on modern Android
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigationRouter(viewModel)
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        viewModel.webSocketManager.disconnect()
    }
}

@Composable
fun AppNavigationRouter(viewModel: MainViewModel) {
    when (viewModel.currentScreen) {
        "login" -> LoginScreen(viewModel)
        "register" -> RegisterScreen(viewModel)
        "home_container" -> MainContainer(viewModel)
        "chat_detail" -> ChatDetailScreen(viewModel)
        "live_watch" -> LiveStreamWatchScreen(viewModel)
        "verify_screen" -> VerificationDialogScreen(viewModel)
        "other_profile" -> OtherProfileScreen(viewModel)
        "search_screen" -> SearchScreen(viewModel)
        "notifications_screen" -> NotificationsScreen(viewModel)
        else -> LoginScreen(viewModel)
    }
}
