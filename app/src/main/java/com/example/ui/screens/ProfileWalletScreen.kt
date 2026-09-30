package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import com.example.ui.components.FluidAnimatedEntry

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.ui.components.EditProfileDialog
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.AppTheme
import com.example.ui.theme.PastelBluePrimary
import com.example.ui.theme.PastelMintPrimary
import com.example.ui.theme.PastelMintDark
import com.example.ui.theme.PastelMintLight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.MerchantProduct
import com.example.data.model.WalletTransaction
import com.example.ui.components.GlassAvatarBadge
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.MerchantAvatar
import com.example.ui.components.PersianUtils
import com.example.ui.theme.AppFontFamily
import com.example.ui.theme.VazirmatnFontFamily
import com.example.ui.theme.BlueCyanGlow
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryDark
import com.example.ui.theme.GlassBorderBlue
import com.example.ui.theme.GlassBorderRefractionBrush
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceGlassCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrustGreen
import com.example.ui.theme.TrustGreenLight
import com.example.viewmodel.KasebanViewModel

@Composable
fun ProfileWalletScreen(
    viewModel: KasebanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val walletBalance by viewModel.walletBalance.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()
    val isMerchantActive by viewModel.isMerchantPageActive.collectAsState()
    val myProducts by viewModel.myProducts.collectAsState()
    val userAddresses by viewModel.userAddresses.collectAsState()
    val userDisplayName by viewModel.userDisplayName.collectAsState()
    val userShopTitle by viewModel.userShopTitle.collectAsState()
    val userBio by viewModel.userBio.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()
    val userAvatarUri by viewModel.userAvatarUri.collectAsState()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val colorPalette by viewModel.colorPalette.collectAsState()

    var showDepositDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. User Info Header Glass Card with Auth Hook & Profile Editing
        item {
            FluidAnimatedEntry(delayMillis = 40) {
                UserProfileCard(
                    userDisplayName = userDisplayName,
                    userPhone = userPhone,
                    userRole = userRole,
                    userShopTitle = userShopTitle,
                    userLocation = userLocation,
                    userAvatarUri = userAvatarUri,
                    isLoggedIn = isUserLoggedIn,
                    onAuthClick = {
                        viewModel.logout()
                        Toast.makeText(context, "از حساب کاربری خارج شدید", Toast.LENGTH_SHORT).show()
                    },
                    onEditClick = { showEditProfileDialog = true }
                )
            }
        }

        // 2. High-End Pastel Glass Wallet Card
        item {
            FluidAnimatedEntry(delayMillis = 80) {
                WalletBalanceCard(
                    balance = walletBalance,
                    onRechargeClick = { showDepositDialog = true }
                )
            }
        }

        // 3. Theme & Appearance Settings Card (Light/Dark + Navy White + Emerald Green + Teal)
        item {
            FluidAnimatedEntry(delayMillis = 100) {
                AppThemeSettingsCard(
                    currentThemeMode = themeMode,
                    currentPalette = colorPalette,
                    onThemeModeChange = { viewModel.setThemeMode(it) },
                    onPaletteChange = { viewModel.setColorPalette(it) }
                )
            }
        }

        // 3. Invite & Commission-free Banner
        item {
            FluidAnimatedEntry(delayMillis = 120) {
                InviteCommissionCard(
                    inviteCode = viewModel.inviteCode,
                    onShareClick = { viewModel.shareInvite(context) }
                )
            }
        }

        // 4. Recent Wallet Transactions
        item {
            FluidAnimatedEntry(delayMillis = 160) {
                WalletTransactionsSection(
                    transactions = transactions
                )
            }
        }

        // 5. Merchant Business Mode (Only shown if user registered as merchant or activated store)
        if (userRole.contains("کاسب") || isMerchantActive || myProducts.isNotEmpty()) {
            item {
                FluidAnimatedEntry(delayMillis = 200) {
                    MyMerchantBusinessSection(
                        shopTitle = userShopTitle,
                        isActive = isMerchantActive,
                        onToggleActive = { viewModel.toggleMerchantPageActive() },
                        products = myProducts,
                        onAddProductClick = { showAddProductDialog = true }
                    )
                }
            }
        }

        // 6. Registered Delivery Addresses
        item {
            FluidAnimatedEntry(delayMillis = 230) {
                AddressesSection(addresses = userAddresses)
            }
        }
    }

    // Recharge Wallet Dialog
    if (showDepositDialog) {
        DepositWalletDialog(
            onDismiss = { showDepositDialog = false },
            onConfirmDeposit = { amount ->
                viewModel.depositToWallet(amount)
                showDepositDialog = false
                Toast.makeText(context, "کیف پول با موفقیت شارژ گردید", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Add Merchant Product Dialog
    if (showAddProductDialog) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onAddProduct = { title, weight, price ->
                viewModel.addMerchantProduct(title, weight, price)
                showAddProductDialog = false
                Toast.makeText(context, "محصول جدید به فروشگاه شما افزوده شد", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            initialName = userDisplayName,
            initialShopTitle = userShopTitle,
            initialBio = userBio,
            initialLocation = userLocation,
            initialPhone = userPhone,
            initialAvatarUri = userAvatarUri,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, shop, bio, loc, phone, avatarUri ->
                viewModel.updateUserProfile(name, shop, bio, loc, phone, avatarUri)
            },
            onAvatarSelected = { uri ->
                viewModel.updateUserAvatar(uri)
            }
        )
    }
}

@Composable
private fun UserProfileCard(
    userDisplayName: String,
    userPhone: String,
    userRole: String,
    userShopTitle: String,
    userLocation: String,
    userAvatarUri: String?,
    isLoggedIn: Boolean,
    onAuthClick: () -> Unit,
    onEditClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = SurfaceGlassCard,
        borderBrush = GlassBorderRefractionBrush,
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // High-End Tailored Persian Avatar with Camera Badge
                Box(
                    modifier = Modifier.clickable(onClick = onEditClick)
                ) {
                    MerchantAvatar(
                        name = userDisplayName.ifBlank { "کاربر" },
                        size = 64.dp,
                        isOnline = true,
                        avatarUri = userAvatarUri
                    )

                    // Camera Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF059669))
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "تغییر عکس",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userDisplayName.ifBlank { "کاربر گرامی" },
                        fontFamily = VazirmatnFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (userPhone.isNotBlank()) "${PersianUtils.toPersianDigits(userPhone)} • $userRole"
                               else if (userShopTitle.isNotBlank()) "$userShopTitle"
                               else userRole,
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 12.5.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "عضو شبکه کاسبان",
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 11.sp,
                        color = PastelMintPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prominent, Comfortable Action Buttons (Standard M3 44-48dp height)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Edit Profile Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFE0F2FE), Color(0xFFD1FAE5))
                            )
                        )
                        .border(1.dp, GlassBorderRefractionBrush, RoundedCornerShape(14.dp))
                        .clickable(onClick = onEditClick)
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = PastelBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "ویرایش مشخصات",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BluePrimaryDark
                        )
                    }
                }

                // Logout Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFEE2E2))
                        .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(14.dp))
                        .clickable(onClick = onAuthClick)
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "خروج از حساب",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletBalanceCard(
    balance: Long,
    onRechargeClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x1F0D9488))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x8CFFFFFF),
                        Color(0x66D1FAE5),
                        Color(0x66BAE6FD)
                    )
                )
            )
            .border(1.2.dp, GlassBorderRefractionBrush, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x33059669)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "کیف پول کاسبان",
                            tint = PastelMintPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "کیف پول رسمی کاسبان",
                        fontFamily = VazirmatnFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x66CCFBF1))
                        .border(1.dp, Color(0x4D34D399), RoundedCornerShape(12.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "پرداخت امن بدون کارمزد",
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 10.5.sp,
                        color = PastelMintPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "موجودی قابل پرداخت:",
                fontFamily = VazirmatnFontFamily,
                fontSize = 11.5.sp,
                color = TextSecondary
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = PersianUtils.formatPrice(balance),
                    fontFamily = VazirmatnFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = PastelMintDark
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Charge Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF0284C7)))
                        )
                        .clickable(onClick = onRechargeClick)
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "شارژ",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "افزایش موجودی",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = Color.White
                        )
                    }
                }

                // Security Tag
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x330D9488))
                        .border(1.dp, Color(0x4D34D399), RoundedCornerShape(16.dp))
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "تضمین",
                            tint = PastelMintPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تضمین بازگشت وجه",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InviteCommissionCard(
    inviteCode: String,
    onShareClick: () -> Unit
) {
    val context = LocalContext.current
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = Color(0xF6F0FDF4),
        borderColor = Color(0x6686EFAC),
        borderWidth = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TrustGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "اشتراک",
                        tint = TrustGreen,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "دعوت از دوستان و آشنایان",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Color(0xFF14532D)
                    )
                    Text(
                        text = "با اشتراک کد دعوت، آشنایان خود را به شبکه معتمدین اضافه کنید",
                        fontFamily = AppFontFamily,
                        fontSize = 11.sp,
                        color = Color(0xFF166534)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Code box with copy
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0x3316A34A), RoundedCornerShape(12.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("کد دعوت کاسبان", inviteCode))
                            Toast.makeText(context, "کد دعوت کپی شد: $inviteCode", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "کپی",
                            tint = TrustGreen,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = inviteCode,
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color(0xFF14532D),
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Share button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(TrustGreen)
                        .clickable(onClick = onShareClick)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "اشتراک‌گذاری",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "اشتراک با آشنایان",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletTransactionsSection(
    transactions: List<WalletTransaction>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F1FC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "سوابق",
                        tint = BluePrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "گردش حساب و تراکنش‌های کیف پول",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
            Text(
                text = "${PersianUtils.toPersianDigits(transactions.size.toString())} تراکنش",
                fontFamily = AppFontFamily,
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(0xF5FFFFFF),
            borderBrush = GlassBorderRefractionBrush
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (transactions.isEmpty()) {
                    Text(
                        text = "هنوز تراکنشی ثبت نشده است.",
                        fontFamily = AppFontFamily,
                        fontSize = 12.5.sp,
                        color = TextMuted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    transactions.forEachIndexed { index, tx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (tx.isDeposit) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (tx.isDeposit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = if (tx.isDeposit) StatusSuccess else Color(0xFFDC2626),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = tx.title,
                                        fontFamily = AppFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.5.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = tx.date,
                                        fontFamily = AppFontFamily,
                                        fontSize = 10.5.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Text(
                                text = (if (tx.isDeposit) "+ " else "- ") +
                                        PersianUtils.toPersianDigits(PersianUtils.formatPrice(tx.amount)) + " ت",
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = if (tx.isDeposit) StatusSuccess else Color(0xFFDC2626)
                            )
                        }

                        if (index < transactions.size - 1) {
                            HorizontalDivider(
                                color = Color(0x1A0F172A),
                                thickness = 0.8.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyMerchantBusinessSection(
    shopTitle: String,
    isActive: Boolean,
    onToggleActive: () -> Unit,
    products: List<MerchantProduct>,
    onAddProductClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F1FC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "کاسبی من",
                        tint = BluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مدیریت غرفه و دسترنج من",
                    fontFamily = VazirmatnFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .heightIn(min = 40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x330284C7))
                    .border(1.dp, Color(0x6638BDF8), RoundedCornerShape(12.dp))
                    .clickable(onClick = onAddProductClick)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "افزودن دسترنج",
                        tint = BluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "افزودن محصول",
                        fontFamily = VazirmatnFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = BluePrimaryDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(0xF5FFFFFF),
            borderBrush = GlassBorderRefractionBrush
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Active Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "وضعیت غرفه: ${shopTitle.ifBlank { "غرفه شخصی من" }}",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isActive) "غرفه فعال است و محصولات در بازار نمایش داده می‌شوند" else "غرفه موقتاً غیرفعال است",
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 11.sp,
                            color = if (isActive) TrustGreen else TextMuted
                        )
                    }

                    Switch(
                        checked = isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BluePrimary
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0x1F2563EB)
                )

                AnimatedVisibility(
                    visible = isActive,
                    enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                            expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
                    exit = fadeOut(animationSpec = tween(120)) +
                           shrinkVertically(animationSpec = tween(120))
                ) {
                    Column {
                        Text(
                            text = "محصولات غرفه من (${PersianUtils.toPersianDigits(products.size.toString())} قلم)",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (products.isEmpty()) {
                            Text(
                                text = "هنوز محصولی ثبت نشده است. با فشردن «افزودن محصول»، اولین دسترنج خود را وارد کنید.",
                                fontFamily = VazirmatnFontFamily,
                                fontSize = 11.5.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        } else {
                            products.forEach { prod ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = prod.title,
                                            fontFamily = VazirmatnFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.5.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "وزن/تعداد: ${prod.weight}",
                                            fontFamily = VazirmatnFontFamily,
                                            fontSize = 10.5.sp,
                                            color = TextMuted
                                        )
                                    }

                                    Text(
                                        text = PersianUtils.toPersianDigits(PersianUtils.formatPrice(prod.price)) + " ت",
                                        fontFamily = VazirmatnFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = BluePrimary
                                    )
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = !isActive,
                    enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                            expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
                    exit = fadeOut(animationSpec = tween(120)) +
                           shrinkVertically(animationSpec = tween(120))
                ) {
                    Text(
                        text = "غرفه شما در حال حاضر غیرفعال است. با فعال‌سازی آن، محصولات شما در فهرست آشنایان و جستجوی کاربران نمایش داده خواهد شد.",
                        color = TextMuted,
                        fontFamily = AppFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressesSection(addresses: List<com.example.data.model.UserAddress>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F1FC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "آدرس‌ها",
                        tint = BluePrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "نشانی‌های ارسال و تحویل",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        addresses.forEach { addr ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                backgroundColor = Color(0xF5FFFFFF),
                borderBrush = GlassBorderRefractionBrush
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(BlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = addr.title,
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            if (addr.isDefault) {
                                GlassBadge(
                                    text = "پیش‌فرض",
                                    color = BluePrimary,
                                    backgroundColor = BlueLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = addr.address,
                            fontFamily = AppFontFamily,
                            fontSize = 11.5.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = PersianUtils.toPersianDigits(addr.phone),
                                fontFamily = AppFontFamily,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DepositWalletDialog(
    onDismiss: () -> Unit,
    onConfirmDeposit: (Long) -> Unit
) {
    var amountText by remember { mutableStateOf("250000") }
    val quickAmounts = listOf(100000L, 250000L, 500000L, 1000000L)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = BluePrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "افزایش موجودی کیف پول",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "مبلغ مورد نظر برای شارژ حساب را انتخاب یا وارد فرمایید:",
                    fontFamily = AppFontFamily,
                    fontSize = 12.5.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickAmounts.forEach { amt ->
                        val isSelected = amountText == amt.toString()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) BluePrimary else BlueLight)
                                .clickable { amountText = amt.toString() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = PersianUtils.toPersianDigits((amt / 1000).toString()) + " ت",
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                color = if (isSelected) Color.White else BluePrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("مبلغ به تومان", fontFamily = AppFontFamily) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = GlassBorderBlue
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toLongOrNull() ?: 100000L
                    onConfirmDeposit(amt)
                },
                modifier = Modifier.heightIn(min = 44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "تایید و شارژ آنی",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 44.dp)
            ) {
                Text(text = "انصراف", fontFamily = AppFontFamily, color = TextMuted)
            }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = Color.White
    )
}

@Composable
private fun AddProductDialog(
    onDismiss: () -> Unit,
    onAddProduct: (String, String, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "افزودن محصول جدید به غرفه",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("نام محصول (مثال: نان شیرمال سنتی)", fontFamily = AppFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("وزن / بسته‌بندی (مثال: ۱ کیلوگرم)", fontFamily = AppFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("قیمت (تومان)", fontFamily = AppFontFamily) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val price = priceText.toLongOrNull() ?: 100000L
                        onAddProduct(title, if (weight.isBlank()) "۱ عدد" else weight, price)
                    }
                },
                modifier = Modifier.heightIn(min = 44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(text = "ثبت در غرفه", fontFamily = AppFontFamily, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 44.dp)
            ) {
                Text(text = "انصراف", fontFamily = AppFontFamily, color = TextMuted)
            }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = Color.White
    )
}

@Composable
fun AppThemeSettingsCard(
    currentThemeMode: AppThemeMode,
    currentPalette: AppColorPalette,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onPaletteChange: (AppColorPalette) -> Unit
) {
    val colors = AppTheme.colors

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = colors.surfaceCard,
        borderBrush = colors.borderBrush,
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.primaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "تنظیمات تم و رنگ‌بندی برنامه",
                        fontFamily = VazirmatnFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "انتخاب حالت شب/روز و پالت‌های رنگی اختصاصی",
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 11.5.sp,
                        color = colors.textMuted
                    )
                }
            }

            HorizontalDivider(color = colors.borderSubtle, thickness = 0.8.dp)

            // 1. Day / Night Mode Switcher
            Text(
                text = "حالت نمایش:",
                fontFamily = VazirmatnFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = colors.textPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Light Mode
                val isLight = currentThemeMode == AppThemeMode.LIGHT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isLight) colors.primaryLight else colors.surface)
                        .border(
                            1.2.dp,
                            if (isLight) colors.primary else colors.borderSubtle,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onThemeModeChange(AppThemeMode.LIGHT) }
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (isLight) colors.primary else colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "حالت روز (روشن)",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = if (isLight) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (isLight) colors.primaryDark else colors.textSecondary
                        )
                    }
                }

                // Dark Mode
                val isDark = currentThemeMode == AppThemeMode.DARK
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) colors.primaryLight else colors.surface)
                        .border(
                            1.2.dp,
                            if (isDark) colors.primary else colors.borderSubtle,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onThemeModeChange(AppThemeMode.DARK) }
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = if (isDark) colors.primary else colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "حالت شب (تاریک)",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = if (isDark) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (isDark) colors.primaryDark else colors.textSecondary
                        )
                    }
                }
            }

            // 2. Color Palette Selector (3 options requested)
            Text(
                text = "پالت رنگی بازار:",
                fontFamily = VazirmatnFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = colors.textPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple(AppColorPalette.PASTEL_TEAL, "فیروزه‌ای و نعنایی (روشن و ملایم)", Color(0xFF0F766E)),
                    Triple(AppColorPalette.NAVY_WHITE, "سرمه‌ای و سفید (کلاسیک و چشم‌نواز)", Color(0xFF1E3A8A)),
                    Triple(AppColorPalette.EMERALD_GREEN, "سبز و سفید (طبیعت و آرامش‌بخش)", Color(0xFF047857))
                ).forEach { (palette, desc, dotColor) ->
                    val isSelected = currentPalette == palette
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) colors.primaryLight else colors.surface)
                            .border(
                                1.2.dp,
                                if (isSelected) colors.primary else colors.borderSubtle,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onPaletteChange(palette) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Palette Color Dot
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                        .border(1.5.dp, Color.White, CircleShape)
                                )

                                Column {
                                    Text(
                                        text = palette.displayName,
                                        fontFamily = VazirmatnFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) colors.primaryDark else colors.textPrimary
                                    )
                                    Text(
                                        text = desc,
                                        fontFamily = VazirmatnFontFamily,
                                        fontSize = 10.5.sp,
                                        color = colors.textMuted
                                    )
                                }
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(colors.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "فعال",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Readability Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "✍️ قلم مورد استفاده در برنامه «وزیرمتن» است که خواناترین و ارگونومیک‌ترین فونت فارسی برای کاهش خستگی چشم محسوب می‌شود.",
                    fontFamily = VazirmatnFontFamily,
                    fontSize = 11.sp,
                    color = colors.textMuted,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
