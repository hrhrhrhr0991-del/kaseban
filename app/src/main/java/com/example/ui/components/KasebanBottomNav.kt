package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppTheme
import com.example.ui.theme.VazirmatnFontFamily

import androidx.compose.material.icons.filled.AdminPanelSettings

enum class KasebanScreen(val title: String, val icon: ImageVector) {
    MERCHANTS("کاسبان", Icons.Default.Storefront),
    CHATS("گفتگوها", Icons.AutoMirrored.Filled.Chat),
    PROFILE("کیف پول و نمایه", Icons.Default.AccountBalanceWallet),
    AI_ASSISTANT("مشاور", Icons.Default.SupportAgent),
    ADMIN_PANEL("پنل مدیریت کل", Icons.Default.AdminPanelSettings)
}

@Composable
fun KasebanBottomNav(
    currentScreen: KasebanScreen,
    onScreenSelected: (KasebanScreen) -> Unit,
    unreadChatsCount: Int = 0,
    showAdminTab: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val visibleScreens = if (showAdminTab) KasebanScreen.values().toList() else KasebanScreen.values().filter { it != KasebanScreen.ADMIN_PANEL }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Floating Frosted Glass Dock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(26.dp),
                    spotColor = colors.primary.copy(alpha = 0.2f),
                    ambientColor = colors.primary.copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(colors.surfaceCard)
                .border(1.2.dp, colors.borderSubtle, RoundedCornerShape(26.dp))
                .padding(vertical = 5.dp, horizontal = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                visibleScreens.forEach { screen ->
                    val isSelected = currentScreen == screen

                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) colors.primary else colors.textMuted,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "navIconTint"
                    )

                    val capsuleBg by animateColorAsState(
                        targetValue = if (isSelected) colors.primaryLight else Color.Transparent,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "capsuleBg"
                    )

                    val capsuleBorderColor by animateColorAsState(
                        targetValue = if (isSelected) colors.borderSubtle else Color.Transparent,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "capsuleBorder"
                    )

                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(capsuleBg)
                            .border(1.dp, capsuleBorderColor, RoundedCornerShape(18.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onScreenSelected(screen) }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    tint = iconTint,
                                    modifier = Modifier.size(19.dp)
                                )

                                if (screen == KasebanScreen.CHATS && unreadChatsCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(colors.secondary)
                                            .border(1.dp, Color.White, CircleShape)
                                    )
                                }
                            }

                            Text(
                                text = screen.title,
                                color = if (isSelected) colors.primary else colors.textMuted,
                                fontSize = 10.5.sp,
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(top = 1.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
