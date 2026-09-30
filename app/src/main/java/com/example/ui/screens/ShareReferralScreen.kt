package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.PersianUtils
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceDarkGlass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WheatGold
import com.example.ui.theme.WheatGoldDark
import com.example.ui.theme.WheatGoldLight
import com.example.viewmodel.GandomaViewModel

@Composable
fun ShareReferralScreen(
    viewModel: GandomaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val referralCount by viewModel.referralCount.collectAsState()
    val userCode = viewModel.userReferralCode
    val fullShareMessage = viewModel.getShareableMessage()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp, top = 8.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "سیستم اشتراک و دعوت دوستان",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        GlassBadge(text = "پاداش نقدی 🎁", color = WheatGold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "با اشتراک‌گذاری پیام زیر در هر چت یا پیام‌رسان، کد اختصاصی شما خودکار در انتهای متن قرار گرفته و دوستانتان ۱۰٪ تخفیف دریافت می‌کنند.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // User Referral Code Hero Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    backgroundColor = Color(0xF8FFFFFF),
                    borderColor = GlassBorder,
                    borderWidth = 1.5.dp,
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "کد اشتراک اختصاصی شما در gandoma.ir",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Glowing Code Box with Copy Button
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFEF3C7))
                                .border(1.5.dp, WheatGold, RoundedCornerShape(16.dp))
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Gandoma Code", userCode))
                                    Toast.makeText(context, "کد $userCode کپی شد", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = userCode,
                                color = WheatGoldDark,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 3.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "کپی",
                                tint = WheatGoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = "برای کپی سریع روی کد ضربه بزنید",
                            color = TextMuted,
                            fontSize = 11.sp
                        )

                        HorizontalDivider(color = Color(0x1F000000), thickness = 1.dp)

                        // Action Buttons: Send to Apps & Copy Full Message
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GlassButton(
                                text = "ارسال به چت‌ها 🚀",
                                onClick = { viewModel.shareReferral(context) },
                                isPrimary = true,
                                modifier = Modifier.weight(1f),
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )

                            GlassButton(
                                text = "کپی متن کامل 📋",
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Gandoma Invite", fullShareMessage))
                                    Toast.makeText(context, "متن کامل همراه با کد اشتراک کپی شد!", Toast.LENGTH_SHORT).show()
                                },
                                isPrimary = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Message Preview Card
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "پیش‌نمایش متن ارسالی در پیام‌رسان‌ها:",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = SurfaceDarkGlass,
                        borderColor = GlassBorderSubtle
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = fullShareMessage,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // Referral Metrics & Rewards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "عملکرد و پاداش‌های شما",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "دعوت‌های موفق",
                            value = "${PersianUtils.toPersianDigits(referralCount.toString())} نفر",
                            icon = Icons.Default.Group,
                            color = StatusSuccess,
                            modifier = Modifier.weight(1f)
                        )

                        MetricCard(
                            title = "پاداش کیف پول",
                            value = PersianUtils.formatPrice(referralCount * 25000L),
                            icon = Icons.Default.CardGiftcard,
                            color = WheatGold,
                            modifier = Modifier.weight(1f)
                        )

                        MetricCard(
                            title = "تخفیف بعدی",
                            value = "۱۵٪ تخفیف",
                            icon = Icons.Default.Star,
                            color = WheatGoldLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = SurfaceDarkGlass,
        borderColor = color.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
