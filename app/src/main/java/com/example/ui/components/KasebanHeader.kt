package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.AppTheme
import com.example.ui.theme.VazirmatnFontFamily

@Composable
fun KasebanHeader(
    onProfileClick: () -> Unit,
    onChatsClick: () -> Unit,
    onAiClick: () -> Unit,
    unreadChatsCount: Int = 0,
    userAvatarUri: String? = null,
    userName: String = "من",
    onToggleDarkMode: (() -> Unit)? = null,
    onCyclePalette: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                spotColor = colors.primary.copy(alpha = 0.15f),
                ambientColor = colors.primary.copy(alpha = 0.08f)
            )
            .background(colors.surfaceCard)
            .border(1.dp, colors.borderSubtle, androidx.compose.ui.graphics.RectangleShape)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start (RTL Right): Brand Logo (Compact, responsive, never wrapping)
            KasebanTypographicLogo(
                style = LogoStyle.HEADER_COMPACT,
                showTagline = true,
                modifier = Modifier.weight(1f, fill = false)
            )

            // End (RTL Left): Action Controls (Theme, Advisor, Chat, Profile)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Color Palette Switcher (Navy/White, Emerald Green, Teal)
                if (onCyclePalette != null) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                            .clickable(onClick = onCyclePalette),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "تم رنگی: ${colors.palette.displayName}",
                            tint = colors.primary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Dark / Light Mode Toggle Button
                if (onToggleDarkMode != null) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                            .clickable(onClick = onToggleDarkMode),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (colors.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "حالت شب / روز",
                            tint = colors.primary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Advisor Capsule (Minimal & Sleek)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.chipBg)
                        .border(1.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                        .clickable(onClick = onAiClick)
                        .padding(horizontal = 7.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "مشاور کاسبان",
                            tint = colors.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "مشاور",
                            color = colors.chipText,
                            fontSize = 11.sp,
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                // Chat Icon Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                        .clickable(onClick = onChatsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "گفتگوها",
                        tint = colors.primary,
                        modifier = Modifier.size(17.dp)
                    )

                    if (unreadChatsCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(3.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(colors.secondary)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                }

                // User Avatar Monogram
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .shadow(elevation = 1.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(colors.primaryGradient)
                        .border(1.2.dp, colors.surface, CircleShape)
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (!userAvatarUri.isNullOrBlank()) {
                        AsyncImage(
                            model = userAvatarUri,
                            contentDescription = userName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = userName.trim().split(" ").firstOrNull()?.take(2) ?: "من",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
