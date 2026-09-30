package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FluidAnimatedEntry
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.KasebanScreen
import com.example.ui.theme.BlueCyanGlow
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryDark
import com.example.ui.theme.GlassBorderBlue
import com.example.ui.theme.GlassBorderRefractionBrush
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.PastelBlueLight
import com.example.ui.theme.PastelBluePrimary
import com.example.ui.theme.PastelMintLight
import com.example.ui.theme.PastelMintPrimary
import com.example.ui.theme.SurfaceGlassCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrustGreen
import com.example.ui.theme.VazirmatnFontFamily
import com.example.viewmodel.KasebanViewModel

data class MarketCategory(
    val title: String,
    val emoji: String,
    val prompt: String,
    val bgColor: Color
)

@Composable
fun KasebanAiAssistantScreen(
    viewModel: KasebanViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.aiMessages.collectAsState()
    val isLoading by viewModel.isAiLoading.collectAsState()
    var promptInput by remember { mutableStateOf("") }
    var isCategoriesExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val categories = listOf(
        MarketCategory("نان و شیرینی", "🥐", "نان‌های خمیرترش و شیرینی‌های سنتی محلی", Color(0x66FEF3C7)),
        MarketCategory("عسل و مربا", "🍯", "عسل طبیعی سبلان و مرباهای خانگی ارگانیک", Color(0x66FEF9C3)),
        MarketCategory("خشکبار و مغزها", "🥜", "خشکبار، بادام و پسته دستچین روستایی", Color(0x66FFEDD5)),
        MarketCategory("گیاهی و عرقیات", "🌿", "عرقیات سنتی کاشان و گیاهان دارویی کوهستان", Color(0x66DCFCE7)),
        MarketCategory("لبنیات و پنیر", "🧀", "لبنیات محلی و کره بادام‌زمینی و کنجد", Color(0x66E0F2FE)),
        MarketCategory("روغن و چاشنی", "🫒", "ارده دوآتیشه اردکان و روغن کنجد فرابکر", Color(0x66F0FDF4)),
        MarketCategory("میوه و ارگانیک", "🍎", "سیب و میوه‌های فصلی باغات ارگانیک", Color(0x66FEE2E2)),
        MarketCategory("صنایع دستی", "🧵", "صنایع دستی، بافتنی و سفالگری معتمدین", Color(0x66F3E8FF))
    )

    val quickQuestions = listOf(
        "عسل گون کوهستان داری؟",
        "نان خمیرترش جو روستایی",
        "گلاب ناب عیار ۲۲ برزک کاشان",
        "ارده دوآتیشه سنگ‌مهار اردکان"
    )

    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
        // 1. Top Circular Navigation Bar matching competitor app flow (00:00 - 00:05)
        FluidAnimatedEntry(delayMillis = 20) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. آشنایان (Network / Familiars)
                TopCircleShortcut(
                    title = "آشنایان",
                    badgeText = "شبکه معتمدین",
                    icon = Icons.Default.Diversity3,
                    isSelected = false,
                    onClick = { viewModel.navigateTo(KasebanScreen.MERCHANTS) }
                )

                // 2. دسته‌بندی‌ها (Expandable 8 Categories Grid)
                TopCircleShortcut(
                    title = "دسته‌بندی‌ها",
                    badgeText = if (isCategoriesExpanded) "بستن منو ▲" else "۸ رسته ▼",
                    icon = Icons.Default.Category,
                    isSelected = isCategoriesExpanded,
                    onClick = { isCategoriesExpanded = !isCategoriesExpanded }
                )

                // 3. کاسبان (Market / Producers)
                TopCircleShortcut(
                    title = "کاسبان",
                    badgeText = "غرفه‌های اصیل",
                    icon = Icons.Default.Storefront,
                    isSelected = true,
                    onClick = { viewModel.navigateTo(KasebanScreen.MERCHANTS) }
                )
            }
        }

        // 2. Expandable Category Grid
        AnimatedVisibility(
            visible = isCategoriesExpanded,
            enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                    expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
            exit = fadeOut(animationSpec = tween(140)) +
                    shrinkVertically(animationSpec = tween(140))
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = Color(0xF2FFFFFF),
                borderBrush = GlassBorderRefractionBrush,
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "دسته‌بندی محصولات معتمد بازار:",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = TextPrimary
                        )

                        Text(
                            text = "انتخاب رسته جهت راهنمایی",
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categories) { cat ->
                            CategoryItemTile(
                                category = cat,
                                onClick = {
                                    viewModel.sendAiPrompt(cat.prompt)
                                    isCategoriesExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // 3. Welcome Assistant Introduction Banner
        FluidAnimatedEntry(delayMillis = 40) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = SurfaceGlassCard,
                borderBrush = GlassBorderRefractionBrush,
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PastelMintPrimary, PastelBluePrimary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "مشاور هوشمند",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "دستیار هوشمند کاسبان",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            GlassBadge(
                                text = "آماده پاسخگویی",
                                color = PastelMintPrimary,
                                backgroundColor = Color(0xFFDCFCE7)
                            )
                        }
                        Text(
                            text = "جستجوی محصولات، استعلام دسترنج و معرفی آشنایان معتمد",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = VazirmatnFontFamily
                        )
                    }
                }
            }
        }

        // 4. Quick Suggestion Chips for Market Guidance
        FluidAnimatedEntry(delayMillis = 60) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickQuestions) { question ->
                    Box(
                        modifier = Modifier
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1F2563EB))
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceGlassCard)
                            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.sendAiPrompt(question)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = question,
                            color = PastelBluePrimary,
                            fontSize = 11.5.sp,
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 5. Messages Feed
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { (text, isUser) ->
                KasebanAiBubble(text = text, isUser = isUser)
            }

            if (isLoading) {
                item {
                    FluidAnimatedEntry {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceGlassCard)
                                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = PastelMintPrimary,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "مشاور در حال آماده‌سازی پاسخ...",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontFamily = VazirmatnFontFamily
                            )
                        }
                    }
                }
            }
        }

        // 6. Bottom Input Bar with prompt placeholder ("چیزی نیاز داری؟")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                placeholder = "چیزی نیاز داری؟ (مثال: عسل خالص یا نان خمیرترش)",
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF059669), Color(0xFF0284C7))
                        )
                    )
                    .clickable {
                        if (promptInput.isNotBlank()) {
                            viewModel.sendAiPrompt(promptInput)
                            promptInput = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "ارسال",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Top Circular Shortcut Button
 */
@Composable
private fun TopCircleShortcut(
    title: String,
    badgeText: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = if (isSelected) 4.dp else 2.dp,
                    shape = CircleShape,
                    spotColor = Color(0x330D9488)
                )
                .clip(CircleShape)
                .background(
                    if (isSelected) {
                        Brush.linearGradient(listOf(Color(0xFFD1FAE5), Color(0xFFBAE6FD)))
                    } else {
                        Brush.verticalGradient(listOf(Color(0xCCFFFFFF), Color(0x99F1F5F9)))
                    }
                )
                .border(
                    width = if (isSelected) 1.8.dp else 1.dp,
                    brush = GlassBorderRefractionBrush,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) PastelMintPrimary else TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = title,
            fontFamily = VazirmatnFontFamily,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.5.sp,
            color = if (isSelected) PastelMintPrimary else TextPrimary
        )

        Text(
            text = badgeText,
            fontFamily = VazirmatnFontFamily,
            fontSize = 9.sp,
            color = TextMuted
        )
    }
}

/**
 * Category Item Tile in the 8-item grid
 */
@Composable
private fun CategoryItemTile(
    category: MarketCategory,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(category.bgColor)
            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Text(text = category.emoji, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = category.title,
            fontFamily = VazirmatnFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun KasebanAiBubble(
    text: String,
    isUser: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.Start else Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 4.dp else 18.dp,
                        bottomEnd = if (isUser) 18.dp else 4.dp
                    ),
                    spotColor = Color(0x1F0D9488)
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 4.dp else 18.dp,
                        bottomEnd = if (isUser) 18.dp else 4.dp
                    )
                )
                .background(
                    if (isUser) {
                        Brush.linearGradient(
                            listOf(Color(0xFF059669), Color(0xFF0D9488))
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(Color(0xE6FFFFFF), Color(0xE6FFFFFF))
                        )
                    }
                )
                .border(
                    width = 1.dp,
                    brush = GlassBorderRefractionBrush,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 4.dp else 18.dp,
                        bottomEnd = if (isUser) 18.dp else 4.dp
                    )
                )
                .padding(14.dp)
        ) {
            Text(
                text = text,
                color = if (isUser) Color.White else TextPrimary,
                fontFamily = VazirmatnFontFamily,
                fontSize = 12.5.sp,
                lineHeight = 21.sp
            )
        }
    }
}
