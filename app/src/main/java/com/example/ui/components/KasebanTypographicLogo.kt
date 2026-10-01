package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AppTheme
import com.example.ui.theme.PeydaFontFamily

enum class LogoStyle {
    HEADER_COMPACT,
    HERO_PROMINENT,
    MINIMAL_BADGE
}

/**
 * Bespoke exclusive logo for "کاسبان" (Kaseban)
 * Features the signature traditional Persian market portal arch,
 * golden scale of honest trade, and 8-point Persian sun medallion.
 */
@Composable
fun KasebanTypographicLogo(
    modifier: Modifier = Modifier,
    style: LogoStyle = LogoStyle.HEADER_COMPACT,
    showTagline: Boolean = true
) {
    val colors = AppTheme.colors

    when (style) {
        LogoStyle.HEADER_COMPACT -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KasebanExclusiveLogoMark(size = 36.dp)

                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "کاسبان",
                        color = colors.textPrimary,
                        fontFamily = PeydaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.5.sp,
                        maxLines = 1,
                        softWrap = false
                    )

                    if (showTagline) {
                        Text(
                            text = "بازار معتمدان و تولیدکنندگان محلی",
                            color = colors.primary,
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 9.5.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        LogoStyle.HERO_PROMINENT -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Prominent exclusive emblem
                KasebanExclusiveLogoMark(
                    size = 68.dp,
                    isHero = true
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "کاسبان",
                    color = colors.textPrimary,
                    fontFamily = PeydaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    maxLines = 1,
                    softWrap = false
                )

                if (showTagline) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.primaryLight)
                            .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "بازار معتمدان محلی • معاملات مستقیم و بدون واسطه",
                            color = colors.primaryDark,
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        LogoStyle.MINIMAL_BADGE -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                KasebanExclusiveLogoMark(size = 26.dp)
                Text(
                    text = "کاسبان",
                    color = colors.primary,
                    fontFamily = PeydaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

/**
 * Handcrafted exclusive brand emblem for Kaseban.
 * Depicts the authentic Iranian bazaar portal archway (طاق قوسی سردر بازار),
 * the balance scale of justice and fair trade (ترازوی انصاف),
 * and the 8-pointed golden sun medallion (شمسه زرین).
 */
@Composable
fun KasebanExclusiveLogoMark(
    size: Dp = 36.dp,
    isHero: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val cornerRadius = size * 0.28f

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = if (isHero) 6.dp else 2.5.dp,
                shape = RoundedCornerShape(cornerRadius),
                spotColor = Color(0x330284C7),
                ambientColor = Color(0x1F0369A1)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White)
            .border(
                width = if (isHero) 1.5.dp else 1.dp,
                color = Color(0x260284C7),
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.kaseban_app_logo),
            contentDescription = "لوگوی رسمی کاسبان",
            modifier = Modifier.size(size * 0.95f)
        )
    }
}

/**
 * Standard emblem alias to retain backwards compatibility while using the bespoke mark.
 */
@Composable
fun KasebanStandardEmblem(
    size: Dp = 34.dp,
    modifier: Modifier = Modifier
) {
    KasebanExclusiveLogoMark(size = size, modifier = modifier)
}
