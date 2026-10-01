package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import com.example.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.KasebanExclusiveLogoMark
import com.example.ui.components.KasebanTypographicLogo
import com.example.ui.components.LogoStyle
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.AppTheme
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.PeydaFontFamily
import com.example.viewmodel.KasebanViewModel

@Composable
fun AuthScreen(
    onAuthComplete: ((name: String, phone: String, referralCode: String, role: String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: KasebanViewModel? = null
) {
    val context = LocalContext.current
    val colors = AppTheme.colors

    // Active Tab: "عضویت و ثبت‌نام" | "ورود با رمز عددی"
    var activeTab by remember { mutableStateOf("عضویت و ثبت‌نام") }

    // Form fields
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var pinNumber by remember { mutableStateOf("") }
    var pinConfirm by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("خریدار معتمد") }
    var shopTitle by remember { mutableStateOf("") }
    var shopCity by remember { mutableStateOf("ایران") }

    var isPinVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundGradient)
    ) {
        // Ambient glow
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = (-40).dp, y = (-60).dp)
                .background(Brush.radialGradient(listOf(colors.orb1, Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .background(Brush.radialGradient(listOf(colors.orb2, Color.Transparent)))
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Theme controls bar
            if (viewModel != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Palette Switcher
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
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        // Light / Dark Mode Toggle
                        val isDark = colors.isDark
                        IconButton(
                            onClick = { viewModel.toggleThemeMode() },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceCard)
                                .border(1.dp, colors.borderSubtle, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "تغییر حالت شب و روز",
                                tint = if (isDark) Color(0xFFFBBF24) else colors.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Hero Brand Logo
            item {
                Spacer(modifier = Modifier.height(6.dp))
                KasebanTypographicLogo(
                    style = LogoStyle.HERO_PROMINENT,
                    showTagline = true
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Main Auth Form Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Tabs: Register vs Login
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.surfaceCard)
                                .border(1.dp, colors.borderSubtle, RoundedCornerShape(14.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("عضویت و ثبت‌نام", "ورود با رمز عددی").forEach { tab ->
                                val isSelected = activeTab == tab
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) colors.primaryGradient else Brush.linearGradient(
                                                listOf(Color.Transparent, Color.Transparent)
                                            )
                                        )
                                        .clickable {
                                            activeTab = tab
                                            errorMessage = null
                                        }
                                        .padding(vertical = 9.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tab,
                                        fontFamily = PeydaFontFamily,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.5.sp,
                                        color = if (isSelected) Color.White else colors.textSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Error Banner if present
                        if (errorMessage != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFFEF2F2))
                                    .border(1.dp, Color(0xFFF87171), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = errorMessage!!,
                                    fontFamily = PeydaFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFFB91C1C),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Form Fields based on tab
                        if (activeTab == "عضویت و ثبت‌نام") {
                            // Name
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "نام و نام خانوادگی",
                                    fontFamily = PeydaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = colors.textPrimary
                                )
                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = {
                                        fullName = it
                                        errorMessage = null
                                    },
                                    placeholder = {
                                        Text(text = "مثال: علی رضایی", fontFamily = PeydaFontFamily, fontSize = 12.sp, color = colors.textMuted)
                                    },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = colors.primary)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.primary,
                                        unfocusedBorderColor = colors.border
                                    )
                                )
                            }

                            // Phone
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "شماره تلفن همراه",
                                    fontFamily = PeydaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = colors.textPrimary
                                )
                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = {
                                        phoneNumber = it.filter { ch -> ch.isDigit() }
                                        errorMessage = null
                                    },
                                    placeholder = {
                                        Text(text = "مثال: 09123456789 (حداقل ۱۰ رقم)", fontFamily = PeydaFontFamily, fontSize = 12.sp, color = colors.textMuted)
                                    },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = colors.primary)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.primary,
                                        unfocusedBorderColor = colors.border
                                    )
                                )
                            }

                            // Role Picker
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
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
                                                .background(if (isRoleSelected) colors.primaryLight else colors.surfaceCard)
                                                .border(
                                                    width = if (isRoleSelected) 1.5.dp else 1.dp,
                                                    color = if (isRoleSelected) colors.primary else colors.borderSubtle,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { selectedRole = role }
                                                .padding(vertical = 10.dp, horizontal = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = role,
                                                    tint = if (isRoleSelected) colors.primaryDark else colors.textSecondary,
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

                            // If Merchant: Booth Title & City
                            if (selectedRole == "کاسب و تولیدکننده") {
                                OutlinedTextField(
                                    value = shopTitle,
                                    onValueChange = { shopTitle = it },
                                    label = { Text("عنوان غرفه شما (مثال: نان سنتی کوثر)", fontFamily = PeydaFontFamily, fontSize = 11.5.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.primary,
                                        unfocusedBorderColor = colors.border
                                    )
                                )
                            }

                            // 4-Digit Numeric PIN (تعیین رمز عددی برای حفاظت از حساب و غرفه)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.primaryLight)
                                    .border(1.dp, colors.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = colors.primaryDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = "تنظیم رمز عددی ۴ رقمی برای حفاظت از غرفه و حساب",
                                            fontFamily = PeydaFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp,
                                            color = colors.primaryDark
                                        )
                                        Text(
                                            text = "این پین ۴ رقمی مختص شماست تا دسترسی به مدیریت غرفه کاملاً امن و اختصاصی باشد.",
                                            fontFamily = PeydaFontFamily,
                                            fontSize = 10.5.sp,
                                            color = colors.textSecondary
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "رمز عددی ۴ رقمی",
                                            fontFamily = PeydaFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = if (pinNumber.length == 4) "✓ ۴ رقم کامل" else "${pinNumber.length}/۴ رقم",
                                            fontFamily = PeydaFontFamily,
                                            fontSize = 10.sp,
                                            color = if (pinNumber.length == 4) Color(0xFF10B981) else colors.textMuted
                                        )
                                    }
                                    OutlinedTextField(
                                        value = pinNumber,
                                        onValueChange = {
                                            if (it.length <= 4) {
                                                pinNumber = it.filter { ch -> ch.isDigit() }
                                                errorMessage = null
                                            }
                                        },
                                        placeholder = { Text("۴ رقم (مثال: ۱۲۳۴)", fontFamily = PeydaFontFamily, fontSize = 11.sp, color = colors.textMuted) },
                                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = colors.primary, modifier = Modifier.size(16.dp)) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                        visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = colors.primary,
                                            unfocusedBorderColor = colors.border
                                        )
                                    )
                                }

                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "تکرار رمز عددی",
                                            fontFamily = PeydaFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp,
                                            color = colors.textPrimary
                                        )
                                        if (pinConfirm.isNotEmpty() && pinNumber.isNotEmpty()) {
                                            Text(
                                                text = if (pinConfirm == pinNumber) "✓ منطبق" else "✗ عدم تطابق",
                                                fontFamily = PeydaFontFamily,
                                                fontSize = 10.sp,
                                                color = if (pinConfirm == pinNumber) Color(0xFF10B981) else Color(0xFFEF4444)
                                            )
                                        }
                                    }
                                    OutlinedTextField(
                                        value = pinConfirm,
                                        onValueChange = {
                                            if (it.length <= 4) {
                                                pinConfirm = it.filter { ch -> ch.isDigit() }
                                                errorMessage = null
                                            }
                                        },
                                        placeholder = { Text("تکرار ۴ رقم", fontFamily = PeydaFontFamily, fontSize = 11.sp, color = colors.textMuted) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                        visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = colors.primary,
                                            unfocusedBorderColor = colors.border
                                        )
                                    )
                                }
                            }

                            // Referral Code (Optional)
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "کد معرف (اختیاری)", fontFamily = PeydaFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.textPrimary)
                                    Text(text = "اختیاری", fontFamily = PeydaFontFamily, fontSize = 10.5.sp, color = colors.textMuted)
                                }
                                OutlinedTextField(
                                    value = referralCode,
                                    onValueChange = { referralCode = it },
                                    placeholder = { Text(text = "اختیاری - در صورت نداشتن خالی بگذارید", fontFamily = PeydaFontFamily, fontSize = 11.sp, color = colors.textMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.primary,
                                        unfocusedBorderColor = colors.border
                                    )
                                )
                            }
                        } else {
                            // Login Form: Phone + 4-Digit PIN
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "شماره تلفن همراه ثبت‌شده",
                                    fontFamily = PeydaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = colors.textPrimary
                                )
                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = {
                                        phoneNumber = it.filter { ch -> ch.isDigit() }
                                        errorMessage = null
                                    },
                                    placeholder = {
                                        Text(text = "شماره همراه ثبت‌نام شده", fontFamily = PeydaFontFamily, fontSize = 12.sp, color = colors.textMuted)
                                    },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = colors.primary)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.primary,
                                        unfocusedBorderColor = colors.border
                                    )
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "رمز عددی ۴ رقمی",
                                        fontFamily = PeydaFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = if (isPinVisible) "مخفی‌سازی" else "نمایش رمز",
                                        fontFamily = PeydaFontFamily,
                                        fontSize = 10.5.sp,
                                        color = colors.primary,
                                        modifier = Modifier.clickable { isPinVisible = !isPinVisible }
                                    )
                                }
                                OutlinedTextField(
                                    value = pinNumber,
                                    onValueChange = {
                                        if (it.length <= 8) {
                                            pinNumber = it.filter { ch -> ch.isDigit() }
                                            errorMessage = null
                                        }
                                    },
                                    placeholder = {
                                        Text(text = "رمز ۴ رقمی خود را وارد کنید", fontFamily = PeydaFontFamily, fontSize = 12.sp, color = colors.textMuted)
                                    },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = colors.primary)
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                            Icon(
                                                imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = null,
                                                tint = colors.textMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.primary,
                                        unfocusedBorderColor = colors.border
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (viewModel != null) {
                                    if (activeTab == "عضویت و ثبت‌نام") {
                                        val err = viewModel.registerWithPin(
                                            name = fullName,
                                            phone = phoneNumber,
                                            pin = pinNumber,
                                            pinConfirm = pinConfirm,
                                            role = selectedRole,
                                            referralCode = referralCode,
                                            shopTitle = shopTitle,
                                            location = shopCity
                                        )
                                        if (err != null) {
                                            errorMessage = err
                                        } else {
                                            errorMessage = null
                                            Toast.makeText(context, "ثبت‌نام با موفقیت انجام شد و رمز عددی شما ذخیره گردید.", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        val err = viewModel.loginWithPin(
                                            phone = phoneNumber,
                                            pin = pinNumber
                                        )
                                        if (err != null) {
                                            errorMessage = err
                                        } else {
                                            errorMessage = null
                                            Toast.makeText(context, "ورود با موفقیت انجام شد.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    onAuthComplete?.invoke(fullName, phoneNumber, referralCode, selectedRole)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (activeTab == "عضویت و ثبت‌نام") "تکمیل ثبت‌نام و ورود با رمز عددی" else "ورود امن به بازار کاسبان",
                                    fontFamily = PeydaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
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

                        // Google Sign-In & Cloud Sync Button
                        OutlinedButton(
                            onClick = {
                                val clientId = try {
                                    context.getString(R.string.default_web_client_id)
                                } catch (e: Exception) {
                                    null
                                }
                                if (clientId.isNullOrBlank()) {
                                    Toast.makeText(context, "پیکربندی حساب گوگل در دسترس نیست.", Toast.LENGTH_SHORT).show()
                                    return@OutlinedButton
                                }
                                isGoogleLoading = true
                                val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
                                val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

                                coroutineScope.launch {
                                    try {
                                        val result = credentialManager.getCredential(context as Activity, request)
                                        val credential = result.credential
                                        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                            val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                                            val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                                            Firebase.auth.signInWithCredential(authCredential).await()
                                            viewModel?.handleGoogleSignInSuccess()
                                            isGoogleLoading = false
                                            Toast.makeText(context, "ورود با حساب گوگل با موفقیت انجام شد.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            isGoogleLoading = false
                                        }
                                    } catch (e: GetCredentialCancellationException) {
                                        Log.w("Auth", "Google Sign-In dismissed: ${e.message}", e)
                                        isGoogleLoading = false
                                    } catch (e: Exception) {
                                        Log.e("Auth", "Google Sign-In failed", e)
                                        isGoogleLoading = false
                                        Toast.makeText(context, "ورود با گوگل: ${e.localizedMessage ?: "خطا"}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isGoogleLoading
                        ) {
                            if (isGoogleLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = colors.primary
                                )
                            } else {
                                Text(
                                    text = "🌐 ورود و همگام‌سازی ابری با حساب گوگل",
                                    fontFamily = PeydaFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = colors.primary
                                )
                            }
                        }

                        // Tab Switching Quick Action (ثبت‌نام اول یا ورود بعدی)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surfaceCard)
                                .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
                                .clickable {
                                    activeTab = if (activeTab == "عضویت و ثبت‌نام") "ورود با رمز عددی" else "عضویت و ثبت‌نام"
                                    errorMessage = null
                                }
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (activeTab == "عضویت و ثبت‌نام")
                                    "قبلاً ثبت‌نام کرده‌اید؟ ورود با رمز عددی ۴ رقمی"
                                else
                                    "هنوز ثبت‌نام نکرده‌اید؟ ساخت حساب و راه‌اندازی غرفه",
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
}
