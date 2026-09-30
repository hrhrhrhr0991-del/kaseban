package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                spotColor = colors.primary.copy(alpha = 0.35f),
                ambientColor = colors.primaryDark.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(colors.primaryGradient)
            .border(
                width = if (isHero) 1.5.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFDE68A).copy(alpha = 0.75f),
                        Color.White.copy(alpha = 0.45f),
                        Color(0xFFF59E0B).copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.82f)) {
            val w = this.size.width
            val h = this.size.height

            val goldBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFEF08A),
                    Color(0xFFFBBF24),
                    Color(0xFFD97706)
                ),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )

            val strokeWidth = (w * 0.055f).coerceAtLeast(1.2f)
            val fineStroke = (w * 0.032f).coerceAtLeast(0.8f)

            // 1. Traditional Persian Bazaar Portal Arch
            val archPath = Path().apply {
                moveTo(w * 0.22f, h * 0.82f)
                lineTo(w * 0.22f, h * 0.50f)
                // Ogival arch curve up to central apex
                cubicTo(
                    w * 0.22f, h * 0.36f,
                    w * 0.38f, h * 0.24f,
                    w * 0.50f, h * 0.18f
                )
                cubicTo(
                    w * 0.62f, h * 0.24f,
                    w * 0.78f, h * 0.36f,
                    w * 0.78f, h * 0.50f
                )
                lineTo(w * 0.78f, h * 0.82f)
            }
            drawPath(
                path = archPath,
                brush = goldBrush,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 2. Threshold Base Line (سکو و آستانه بازار)
            drawLine(
                color = Color.White.copy(alpha = 0.9f),
                start = Offset(w * 0.15f, h * 0.82f),
                end = Offset(w * 0.85f, h * 0.82f),
                strokeWidth = strokeWidth * 1.1f,
                cap = StrokeCap.Round
            )

            // 3. Central Vertical Pillar (ستون میزان)
            drawLine(
                color = Color.White.copy(alpha = 0.85f),
                start = Offset(w * 0.50f, h * 0.36f),
                end = Offset(w * 0.50f, h * 0.78f),
                strokeWidth = fineStroke * 1.2f,
                cap = StrokeCap.Round
            )

            // 4. Scales Balance Beam (شاهین ترازوی کاسبان)
            val beamPath = Path().apply {
                moveTo(w * 0.30f, h * 0.49f)
                quadraticTo(w * 0.50f, h * 0.45f, w * 0.70f, h * 0.49f)
            }
            drawPath(
                path = beamPath,
                brush = goldBrush,
                style = Stroke(width = strokeWidth * 0.95f, cap = StrokeCap.Round)
            )

            // 5. Center Pivot Jewel
            drawCircle(
                color = Color(0xFFF59E0B),
                radius = fineStroke * 1.6f,
                center = Offset(w * 0.50f, h * 0.46f)
            )
            drawCircle(
                color = Color.White,
                radius = fineStroke * 0.8f,
                center = Offset(w * 0.50f, h * 0.46f)
            )

            // 6. Left and Right Scale Pans (کفه‌های ترازوی دادوستد)
            // Left cords & pan
            val leftPan = Path().apply {
                moveTo(w * 0.25f, h * 0.63f)
                cubicTo(
                    w * 0.25f, h * 0.69f,
                    w * 0.35f, h * 0.69f,
                    w * 0.35f, h * 0.63f
                )
                close()
            }
            drawPath(path = leftPan, color = Color.White.copy(alpha = 0.95f))
            drawLine(
                color = Color(0xFFBAE6FD),
                start = Offset(w * 0.30f, h * 0.49f),
                end = Offset(w * 0.25f, h * 0.63f),
                strokeWidth = fineStroke * 0.8f
            )
            drawLine(
                color = Color(0xFFBAE6FD),
                start = Offset(w * 0.30f, h * 0.49f),
                end = Offset(w * 0.35f, h * 0.63f),
                strokeWidth = fineStroke * 0.8f
            )

            // Right cords & pan
            val rightPan = Path().apply {
                moveTo(w * 0.65f, h * 0.63f)
                cubicTo(
                    w * 0.65f, h * 0.69f,
                    w * 0.75f, h * 0.69f,
                    w * 0.75f, h * 0.63f
                )
                close()
            }
            drawPath(path = rightPan, color = Color.White.copy(alpha = 0.95f))
            drawLine(
                color = Color(0xFFBAE6FD),
                start = Offset(w * 0.70f, h * 0.49f),
                end = Offset(w * 0.65f, h * 0.63f),
                strokeWidth = fineStroke * 0.8f
            )
            drawLine(
                color = Color(0xFFBAE6FD),
                start = Offset(w * 0.70f, h * 0.49f),
                end = Offset(w * 0.75f, h * 0.63f),
                strokeWidth = fineStroke * 0.8f
            )

            // 7. Apex Persian 8-Point Star Medallion (شمسه زرین کاسبان)
            val starCenter = Offset(w * 0.50f, h * 0.16f)
            val rOuter = w * 0.10f
            val rInner = w * 0.05f

            val starPath = Path()
            val points = 8
            for (i in 0 until points * 2) {
                val radius = if (i % 2 == 0) rOuter else rInner
                val angle = (i * Math.PI / points) - (Math.PI / 2)
                val x = (starCenter.x + radius * Math.cos(angle)).toFloat()
                val y = (starCenter.y + radius * Math.sin(angle)).toFloat()
                if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
            }
            starPath.close()

            drawPath(path = starPath, brush = goldBrush)
            drawCircle(
                color = Color.White,
                radius = fineStroke * 0.9f,
                center = starCenter
            )
        }
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
