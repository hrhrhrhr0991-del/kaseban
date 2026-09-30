package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FluidAnimatedEntry
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.KasebanTypographicLogo
import com.example.ui.components.LogoStyle
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.AppTheme
import com.example.ui.theme.PeydaFontFamily
import com.example.viewmodel.KasebanViewModel

@Composable
fun AuthScreen(
    onAuthComplete: (name: String, phone: String, referralCode: String, role: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KasebanViewModel? = null,
    onGuestLogin: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val colors = AppTheme.colors

    // Active Tab: "عضویت و ثبت‌نام" | "ورود سریع"
    var activeTab by remember { mutableStateOf("عضویت و ثبت‌نام") }

    // Form fields
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("خریدار معتمد") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundGradient)
    ) {
        // Glowing ambient backdrop spheres matching current theme
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = (-40).dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.orb1, Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.orb2, Color.Transparent)
                    )
                )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Theme Selector Bar
            if (viewModel != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Palette Switcher Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surfaceCard)
                                .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
                                .clickable { viewModel.cycleColorPalette() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "تم رنگی",
                                    tint = colors.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = colors.palette.displayName,
                                    fontFamily = PeydaFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary,
                                    maxLines = 1
                                )
                            }
                        }

                        // Dark / Light Mode Toggle Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceCard)
                                .border(1.dp, colors.borderSubtle, CircleShape)
                                .clickable { viewModel.toggleThemeMode() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (colors.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "تغییر تم شب و روز",
                                tint = colors.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // High-End Typographic Logo Hero Header (Protected from wrapping)
            item {
                FluidAnimatedEntry(delayMillis = 20) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp)
                    ) {
                        KasebanTypographicLogo(
                            style = LogoStyle.HERO_PROMINENT,
                            showTagline = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Compact trust badges (No line wrapping)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassBadge(text = "خرید مستقیم", color = colors.primary)
                            GlassBadge(text = "ارتباط محلی", color = colors.secondary)
                        }
                    }
                }
            }

            // Clean Authentication Card (No fake video blocks, no fake claims)
            item {
                FluidAnimatedEntry(delayMillis = 50) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        backgroundColor = colors.surfaceCard,
                        borderColor = colors.border,
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(13.dp)
                        ) {
                            // Clean Tab Switcher: "عضویت و ثبت‌نام" | "ورود سریع"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(colors.surface)
                                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(16.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf("عضویت و ثبت‌نام", "ورود سریع").forEach { tab ->
                                    val isSelected = activeTab == tab
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(13.dp))
                                            .then(
                                                if (isSelected) Modifier.background(colors.primaryGradient)
                                                else Modifier
                                            )
                                            .clickable { activeTab = tab }
                                            .padding(vertical = 9.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = tab,
                                            fontFamily = PeydaFontFamily,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = if (isSelected) Color.White else colors.textSecondary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // Full Name (Only in Registration Mode)
                            AnimatedVisibility(
                                visible = activeTab == "عضویت و ثبت‌نام",
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "نام و نام خانوادگی",
                                        fontFamily = PeydaFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = colors.textPrimary
                                    )

                                    OutlinedTextField(
                                        value = fullName,
                                        onValueChange = { fullName = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = {
                                            Text(
                                                text = "مثال: علی رضایی",
                                                fontFamily = PeydaFontFamily,
                                                fontSize = 12.sp,
                                                color = colors.textMuted
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = colors.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = colors.surface,
                                            unfocusedContainerColor = colors.surface,
                                            focusedBorderColor = colors.primary,
                                            unfocusedBorderColor = colors.borderSubtle,
                                            focusedTextColor = colors.textPrimary,
                                            unfocusedTextColor = colors.textPrimary
                                        )
                                    )
                                }
                            }

                            // Mobile Number Field
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "شماره تلفن همراه",
                                    fontFamily = PeydaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = colors.textPrimary
                                )

                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { if (it.length <= 11) phoneNumber = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            text = "شماره همراه (۱۰ یا ۱۱ رقم)",
                                            fontFamily = PeydaFontFamily,
                                            fontSize = 12.sp,
                                            color = colors.textMuted
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = colors.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Phone,
                                        imeAction = if (activeTab == "عضویت و ثبت‌نام") ImeAction.Next else ImeAction.Done
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = colors.surface,
                                        unfocusedContainerColor = colors.surface,
                                        focusedBorderColor = colors.primary,
                                        unfocusedBorderColor = colors.borderSubtle,
                                        focusedTextColor = colors.textPrimary,
                                        unfocusedTextColor = colors.textPrimary
                                    )
                                )
                            }

                            // Role Selection (Only in Registration Mode)
                            AnimatedVisibility(
                                visible = activeTab == "عضویت و ثبت‌نام",
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "نوع فعالیت در بازار",
                                        fontFamily = PeydaFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = colors.textPrimary
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(
                                            "خریدار معتمد" to Icons.Default.ShoppingBag,
                                            "کاسب و تولیدکننده" to Icons.Default.Storefront
                                        ).forEach { (role, icon) ->
                                            val isRoleSelected = selectedRole == role
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (isRoleSelected) colors.primaryLight else colors.surface
                                                    )
                                                    .border(
                                                        1.2.dp,
                                                        if (isRoleSelected) colors.primary else colors.borderSubtle,
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable { selectedRole = role }
                                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = if (isRoleSelected) colors.primary else colors.textMuted,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text(
                                                        text = role,
                                                        fontFamily = PeydaFontFamily,
                                                        fontSize = 11.5.sp,
                                                        fontWeight = if (isRoleSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isRoleSelected) colors.primaryDark else colors.textSecondary,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Referral Code Section (Explicitly Optional)
                            AnimatedVisibility(
                                visible = activeTab == "عضویت و ثبت‌نام",
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "کد معرف (اختیاری)",
                                            fontFamily = PeydaFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = "کاملاً اختیاری",
                                            fontFamily = PeydaFontFamily,
                                            fontSize = 10.5.sp,
                                            color = colors.textMuted
                                        )
                                    }

                                    OutlinedTextField(
                                        value = referralCode,
                                        onValueChange = { referralCode = it.uppercase() },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = {
                                            Text(
                                                text = "اختیاری - در صورت نداشتن خالی بگذارید",
                                                fontFamily = PeydaFontFamily,
                                                fontSize = 11.5.sp,
                                                color = colors.textMuted
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Group,
                                                contentDescription = null,
                                                tint = colors.textMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = colors.surface,
                                            unfocusedContainerColor = colors.surface,
                                            focusedBorderColor = colors.primary,
                                            unfocusedBorderColor = colors.borderSubtle,
                                            focusedTextColor = colors.textPrimary,
                                            unfocusedTextColor = colors.textPrimary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            // Main Direct Auth Action Button
                            Surface(
                                onClick = {
                                    val cleanPhone = phoneNumber.trim()
                                    if (cleanPhone.length < 10) {
                                        Toast.makeText(context, "لطفاً شماره تلفن همراه معتبر وارد نمایید", Toast.LENGTH_SHORT).show()
                                        return@Surface
                                    }

                                    val finalName = if (fullName.isNotBlank()) fullName.trim() else "کاربر کاسبان"
                                    onAuthComplete(
                                        finalName,
                                        cleanPhone,
                                        referralCode,
                                        selectedRole
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp), spotColor = colors.primary),
                                color = Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colors.primaryGradient)
                                        .padding(vertical = 13.dp, horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = if (activeTab == "عضویت و ثبت‌نام") "تکمیل ثبت‌نام و ورود" else "ورود به بازار کاسبان",
                                            fontFamily = PeydaFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.5.sp,
                                            color = Color.White
                                        )
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // Direct Guest Entry Button (Zero barrier to enter marketplace)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(colors.surface)
                                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(14.dp))
                                    .clickable {
                                        if (onGuestLogin != null) {
                                            onGuestLogin()
                                        } else if (viewModel != null) {
                                            viewModel.loginAsGuest()
                                        } else {
                                            onAuthComplete("کاربر مهمان", "09120000000", "", "خریدار معتمد")
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "مشاهده بازار به عنوان مهمان (بدون ثبت‌نام)",
                                        fontFamily = PeydaFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Minimal Footer Notice
            item {
                FluidAnimatedEntry(delayMillis = 80) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "سامانه معاملات مستقیم و بدون واسطه بازار کاسبان",
                            fontFamily = PeydaFontFamily,
                            fontSize = 11.sp,
                            color = colors.textMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
