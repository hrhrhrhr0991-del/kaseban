package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import com.example.ui.theme.AppFontFamily
import com.example.ui.theme.AppTheme

import com.example.ui.theme.BlueCyanGlow
import com.example.ui.theme.BlueGlassGradient
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryDark
import com.example.ui.theme.BluePrimaryGradient
import com.example.ui.theme.GlassBorderBlue
import com.example.ui.theme.GlassBorderRefractionBrush
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.SurfaceGlassCard
import com.example.ui.theme.SurfaceGlassWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrustGreen
import com.example.ui.theme.TrustGreenGradient
import com.example.ui.theme.VazirmatnFontFamily

object PersianUtils {
    fun toPersianDigits(input: String): String {
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val builder = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                builder.append(persianDigits[ch - '0'])
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    fun formatPrice(amount: Long): String {
        val formatted = String.format("%,d", amount)
        return "${toPersianDigits(formatted)} تومان"
    }

    fun formatDate(timestamp: Long): String {
        val date = java.text.SimpleDateFormat("yyyy/MM/dd - HH:mm", java.util.Locale.getDefault())
        return toPersianDigits(date.format(java.util.Date(timestamp)))
    }
}

/**
 * Authentic frosted glass card with soft refraction border, translucent surface, and subtle glow
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = AppTheme.colors.surfaceCard,
    borderBrush: Brush = AppTheme.colors.borderBrush,
    borderColor: Color = AppTheme.colors.borderSubtle,
    borderWidth: Dp = 1.2.dp,
    elevation: Dp = 4.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x1F1D4ED8),
                spotColor = Color(0x262563EB)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = (backgroundColor.alpha * 0.92f).coerceIn(0f, 1f))
                    )
                )
            )
            .border(borderWidth, borderBrush, shape)
            .then(clickableModifier),
        content = content
    )
}

/**
 * Subtle, spring-based fluid entry animation for glass-morphic surfaces using AnimatedVisibility
 */
@Composable
fun FluidAnimatedEntry(
    visible: Boolean = true,
    delayMillis: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(delayMillis == 0) }

    if (delayMillis > 0) {
        LaunchedEffect(Unit) {
            delay(delayMillis.toLong())
            isVisible = true
        }
    }

    AnimatedVisibility(
        visible = isVisible && visible,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + slideInVertically(
            initialOffsetY = { (it * 0.15f).toInt().coerceAtLeast(30) },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + scaleIn(
            initialScale = 0.95f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ),
        exit = fadeOut(animationSpec = tween(150)) +
                slideOutVertically(targetOffsetY = { 20 }, animationSpec = tween(150)) +
                scaleOut(targetScale = 0.96f, animationSpec = tween(150)),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Animated frosted glass card with subtle, organic fluid entrance
 */
@Composable
fun AnimatedGlassCard(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    delayMillis: Int = 0,
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = AppTheme.colors.surfaceCard,
    borderBrush: Brush = AppTheme.colors.borderBrush,
    borderColor: Color = AppTheme.colors.borderSubtle,
    borderWidth: Dp = 1.2.dp,
    elevation: Dp = 4.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    FluidAnimatedEntry(
        visible = visible,
        delayMillis = delayMillis,
        modifier = modifier
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            backgroundColor = backgroundColor,
            borderBrush = borderBrush,
            borderColor = borderColor,
            borderWidth = borderWidth,
            elevation = elevation,
            onClick = onClick,
            content = content
        )
    }
}


/**
 * Professional frosted glass container for icons - softens harsh default phone icons
 */
@Composable
fun GlassIconContainer(
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    shape: Shape = RoundedCornerShape(14.dp),
    glowColor: Color = BluePrimary,
    backgroundColor: Color = Color(0xBAEAF2FD),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 2.dp,
                shape = shape,
                ambientColor = glowColor.copy(alpha = 0.15f),
                spotColor = glowColor.copy(alpha = 0.2f)
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        Color(0xB3FFFFFF)
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xD9FFFFFF),
                        glowColor.copy(alpha = 0.35f),
                        Color(0x332563EB)
                    )
                ),
                shape
            )
            .then(clickModifier),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * High-end humanized merchant avatar with tailored color tones, Persian monogram,
 * luminous frosted glass ring, and verified status badge (only shown if isVerified == true)
 */
@Composable
fun MerchantAvatar(
    name: String,
    specialty: String = "",
    size: Dp = 54.dp,
    isOnline: Boolean = true,
    isVerified: Boolean = false,
    avatarUri: String? = null,
    modifier: Modifier = Modifier
) {
    // Generate tailored branding based on merchant personality
    val (monogram, gradientColors, accentColor) = when {
        name.contains("حامد") -> Triple(
            "حامد",
            listOf(Color(0xFF0F766E), Color(0xFF14B8A6), Color(0xFF2DD4BF)),
            Color(0xFF0D9488)
        )
        name.contains("ضیائی") -> Triple(
            "ضیائی",
            listOf(Color(0xFFB45309), Color(0xFFF59E0B), Color(0xFFFBBF24)),
            Color(0xFFD97706)
        )
        name.contains("محمودی") -> Triple(
            "فاطمه",
            listOf(Color(0xFFBE185D), Color(0xFFEC4899), Color(0xFFF472B6)),
            Color(0xFFDB2777)
        )
        name.contains("علی‌خانی") || name.contains("مصی") -> Triple(
            "معصومه",
            listOf(Color(0xFF6D28D9), Color(0xFF8B5CF6), Color(0xFFA78BFA)),
            Color(0xFF7C3AED)
        )
        else -> Triple(
            name.split(" ").firstOrNull() ?: name.take(2),
            listOf(Color(0xFF059669), Color(0xFF0D9488), Color(0xFF0284C7)),
            Color(0xFF0D9488)
        )
    }

    Box(modifier = modifier) {
        // Outer glowing frosted ring
        Box(
            modifier = Modifier
                .size(size)
                .shadow(elevation = 3.dp, shape = CircleShape, spotColor = accentColor.copy(alpha = 0.35f))
                .clip(CircleShape)
                .background(Brush.linearGradient(gradientColors))
                .border(
                    BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.3f), accentColor.copy(alpha = 0.6f))
                        )
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!avatarUri.isNullOrBlank()) {
                if (avatarUri.startsWith("emoji:")) {
                    Text(
                        text = avatarUri.removePrefix("emoji:"),
                        fontSize = (size.value * 0.48f).sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    AsyncImage(
                        model = avatarUri,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(size)
                    )
                }
            } else {
                Text(
                    text = monogram,
                    fontSize = if (monogram.length > 3) (size.value * 0.26f).sp else (size.value * 0.32f).sp,
                    fontFamily = VazirmatnFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.2.sp
                )
            }
        }

        // Verified micro badge on top corner - ONLY if merchant is verified by Admin
        if (isVerified) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0x330284C7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "کاسب تاییدشده و تیک آبی",
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // Online status indicator on bottom corner
        if (isOnline) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(13.dp)
                    .clip(CircleShape)
                    .background(TrustGreen)
                    .border(2.dp, Color.White, CircleShape)
            )
        }
    }
}

/**
 * Official verified translucent trust badge
 */
@Composable
fun KasebanWatermarkPill(
    text: String = "کاسبان • تأیید دسترنج معتمد",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(4.dp, shape = RoundedCornerShape(14.dp), spotColor = Color(0x33059669))
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xB3059669),
                        Color(0xB30284C7)
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.25f))
                ),
                RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 9.dp, vertical = 4.5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
            Text(
                text = text,
                fontFamily = VazirmatnFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = Color.White,
                letterSpacing = 0.2.sp
            )
        }
    }
}


/**
 * Frosted avatar badge for profile or user icons
 */
@Composable
fun GlassAvatarBadge(
    avatarText: String,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    badgeColor: Color = BluePrimary,
    isOnline: Boolean = true
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(size)
                .shadow(elevation = 3.dp, shape = CircleShape, spotColor = badgeColor.copy(alpha = 0.25f))
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFE2EEFC),
                            Color(0xFFCCE2F9)
                        )
                    )
                )
                .border(
                    BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(Color.White, badgeColor.copy(alpha = 0.5f), Color(0x3338BDF8))
                        )
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatarText,
                fontSize = (size.value * 0.42f).sp,
                fontFamily = VazirmatnFontFamily,
                fontWeight = FontWeight.Bold,
                color = BluePrimaryDark
            )
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(13.dp)
                    .clip(CircleShape)
                    .background(TrustGreen)
                    .border(2.dp, Color.White, CircleShape)
            )
        }
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null
) {
    val backgroundBrush = if (isPrimary) {
        BluePrimaryGradient
    } else {
        Brush.horizontalGradient(
            colors = listOf(Color(0xD9FFFFFF), Color(0xBFEBF3FD))
        )
    }

    val contentColor = if (isPrimary) Color.White else BluePrimary

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .shadow(
                elevation = if (isPrimary) 4.dp else 1.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x331D4ED8)
            ),
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.2.dp,
            if (isPrimary) Brush.linearGradient(listOf(Color(0x8093C5FD), Color.Transparent)) else GlassBorderRefractionBrush
        )
    ) {
        Box(
            modifier = Modifier
                .background(backgroundBrush)
                .padding(horizontal = 18.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
            ) {
                if (icon != null) {
                    icon()
                }
                Text(
                    text = text,
                    color = contentColor,
                    fontFamily = VazirmatnFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
        }
    }
}

/**
 * Trust button ("میشناسم") with frosted glass styling and single-line guaranteed typography
 */
@Composable
fun TrustButton(
    isKnown: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minWidth = 74.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (isKnown) Color(0xFF15803D) else Color(0x6616A34A)
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isKnown) TrustGreenGradient else Brush.horizontalGradient(listOf(Color(0xCCE8F5E9), Color(0xBFF1F8F2)))
                )
                .padding(horizontal = 9.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isKnown) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Text(
                    text = "میشناسم",
                    color = if (isKnown) Color.White else TrustGreen,
                    fontFamily = VazirmatnFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}


@Composable
fun GlassBadge(
    text: String,
    color: Color = BluePrimary,
    backgroundColor: Color = color.copy(alpha = 0.12f),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, color.copy(alpha = 0.35f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontFamily = VazirmatnFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val colors = AppTheme.colors
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = if (label.isNotBlank()) {
            { Text(label, color = colors.textSecondary, fontSize = 12.sp, fontFamily = VazirmatnFontFamily) }
        } else null,
        placeholder = { Text(placeholder, color = colors.textMuted, fontSize = 12.sp, fontFamily = VazirmatnFontFamily) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        shape = RoundedCornerShape(18.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = LocalTextStyle.current.copy(
            color = colors.textPrimary,
            fontSize = 13.sp,
            fontFamily = VazirmatnFontFamily,
            textAlign = TextAlign.Right
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceCard,
            unfocusedContainerColor = colors.surface,
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.borderSubtle,
            cursorColor = colors.primary,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary
        )
    )
}

/**
 * Animated Shimmer Brush for smooth skeleton loading states
 */
@Composable
fun shimmerBrush(
    targetValue: Float = 1000f,
    showShimmer: Boolean = true
): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color(0x33CBD5E1),
            Color(0x80CCFBF1),
            Color(0x80BAE6FD),
            Color(0x33CBD5E1)
        )
        val transition = rememberInfiniteTransition(label = "shimmerTransition")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(1100, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmerTranslate"
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

/**
 * Single Skeleton Merchant Card with Shimmer
 */
@Composable
fun SkeletonMerchantCard(
    modifier: Modifier = Modifier,
    brush: Brush = shimmerBrush()
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = Color(0x66FFFFFF),
        borderBrush = GlassBorderRefractionBrush,
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(brush)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .height(11.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )
            }
        }
    }
}

/**
 * Skeleton Loading List showing multiple shimmering cards
 */
@Composable
fun SkeletonLoadingFeed(
    modifier: Modifier = Modifier,
    count: Int = 5
) {
    val brush = shimmerBrush()
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        repeat(count) {
            SkeletonMerchantCard(brush = brush)
        }
    }
}
