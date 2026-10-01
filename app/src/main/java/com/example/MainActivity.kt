package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.KasebanBottomNav
import com.example.ui.components.KasebanHeader
import com.example.ui.components.KasebanScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.KasebanAiAssistantScreen
import com.example.ui.screens.MerchantsScreen
import com.example.ui.screens.ProfileWalletScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.KasebanViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: KasebanViewModel = viewModel()
            val themeMode by appViewModel.themeMode.collectAsState()
            val colorPalette by appViewModel.colorPalette.collectAsState()
            val isDark = themeMode == AppThemeMode.DARK

            MyApplicationTheme(
                darkTheme = isDark,
                palette = colorPalette
            ) {
                // Ensure natural Persian RTL layout direction throughout the Kaseban app
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    KasebanApp(viewModel = appViewModel)
                }
            }
        }
    }
}

@Composable
fun KasebanApp(
    viewModel: KasebanViewModel = viewModel()
) {
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedMerchant by viewModel.selectedMerchant.collectAsState()
    val activeChatMerchant by viewModel.activeChatMerchant.collectAsState()
    val userAvatarUri by viewModel.userAvatarUri.collectAsState()
    val userDisplayName by viewModel.userDisplayName.collectAsState()
    val colors = AppTheme.colors

    if (!isUserLoggedIn) {
        AuthScreen(viewModel = viewModel)
        return
    }

    // Handle back button behavior for deep sub-screens
    BackHandler(enabled = activeChatMerchant != null || selectedMerchant != null || currentScreen != KasebanScreen.MERCHANTS) {
        when {
            activeChatMerchant != null -> viewModel.closeChat()
            selectedMerchant != null -> viewModel.closeMerchantDetail()
            currentScreen != KasebanScreen.MERCHANTS -> viewModel.navigateTo(KasebanScreen.MERCHANTS)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.backgroundGradient)
    ) {
        // Ambient soft glowing orbs adapted to active color theme
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = (-40).dp, y = (-50).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.orb1, Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(380.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 100.dp, y = (-20).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.orb2, Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.orb3, Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 120.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.orb4, Color.Transparent)
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                KasebanHeader(
                    onProfileClick = { viewModel.navigateTo(KasebanScreen.PROFILE) },
                    onChatsClick = { viewModel.navigateTo(KasebanScreen.CHATS) },
                    onAiClick = { viewModel.navigateTo(KasebanScreen.AI_ASSISTANT) },
                    unreadChatsCount = 0,
                    userAvatarUri = userAvatarUri,
                    userName = userDisplayName,
                    onToggleDarkMode = { viewModel.toggleThemeMode() },
                    onCyclePalette = { viewModel.cycleColorPalette() }
                )
            },
            bottomBar = {
                KasebanBottomNav(
                    currentScreen = currentScreen,
                    onScreenSelected = { viewModel.navigateTo(it) },
                    unreadChatsCount = 0
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "kaseban_screen_transition"
                ) { targetScreen ->
                    when (targetScreen) {
                        KasebanScreen.MERCHANTS -> MerchantsScreen(viewModel = viewModel)
                        KasebanScreen.CHATS -> ChatScreen(viewModel = viewModel)
                        KasebanScreen.PROFILE -> ProfileWalletScreen(viewModel = viewModel)
                        KasebanScreen.AI_ASSISTANT -> KasebanAiAssistantScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
