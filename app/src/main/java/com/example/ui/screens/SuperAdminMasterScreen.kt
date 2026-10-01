package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Merchant
import com.example.data.model.UserAccount
import com.example.ui.components.KasebanScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.PeydaFontFamily
import com.example.viewmodel.KasebanViewModel

@Composable
fun SuperAdminMasterScreen(
    viewModel: KasebanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = AppTheme.colors

    val merchants by viewModel.merchants.collectAsState()
    val registeredAccounts by viewModel.registeredAccounts.collectAsState()

    var activeTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("مدیریت غرفه‌ها و تیک آبی", "مدیریت کاربران", "نظارت بر محصولات", "مرکز فرماندهی")

    var searchQuery by remember { mutableStateOf("") }
    var selectedMerchantForInspect by remember { mutableStateOf<Merchant?>(null) }
    var userToEditRole by remember { mutableStateOf<UserAccount?>(null) }
    var userToResetPin by remember { mutableStateOf<UserAccount?>(null) }
    var newPinInput by remember { mutableStateOf("") }
    var itemToDeleteConfirm by remember { mutableStateOf<Pair<String, () -> Unit>?>(null) }

    // Dialog for Delete Confirmation
    if (itemToDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { itemToDeleteConfirm = null },
            title = {
                Text(
                    text = "تأیید حذف توسط مدیر کل",
                    fontFamily = PeydaFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "آیا از حذف «${itemToDeleteConfirm?.first}» اطمینان دارید؟ این عملیات دائمی و غیرقابل بازگشت است.",
                    fontFamily = PeydaFontFamily,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDeleteConfirm?.second?.invoke()
                        itemToDeleteConfirm = null
                        Toast.makeText(context, "با موفقیت حذف شد.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("حذف قطعی", fontFamily = PeydaFontFamily, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDeleteConfirm = null }) {
                    Text("انصراف", fontFamily = PeydaFontFamily, color = colors.textMuted)
                }
            },
            containerColor = colors.surfaceCard
        )
    }

    // Dialog for Reset PIN
    if (userToResetPin != null) {
        AlertDialog(
            onDismissRequest = { userToResetPin = null; newPinInput = "" },
            title = {
                Text(
                    text = "تغییر رمز عددی کاربر",
                    fontFamily = PeydaFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "کاربر: ${userToResetPin?.name} (${userToResetPin?.phone})",
                        fontFamily = PeydaFontFamily,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4 && it.all { ch -> ch.isDigit() }) newPinInput = it },
                        placeholder = { Text("رمز ۴ رقمی جدید", fontFamily = PeydaFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.length == 4) {
                            viewModel.resetUserPinByAdmin(userToResetPin!!.phone, newPinInput)
                            Toast.makeText(context, "رمز عددی جدید تنظیم شد.", Toast.LENGTH_SHORT).show()
                            userToResetPin = null
                            newPinInput = ""
                        } else {
                            Toast.makeText(context, "رمز باید دقیقاً ۴ رقم باشد.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text("ثبت رمز جدید", fontFamily = PeydaFontFamily, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToResetPin = null; newPinInput = "" }) {
                    Text("انصراف", fontFamily = PeydaFontFamily, color = colors.textMuted)
                }
            },
            containerColor = colors.surfaceCard
        )
    }

    // Dialog for Role Change
    if (userToEditRole != null) {
        AlertDialog(
            onDismissRequest = { userToEditRole = null },
            title = {
                Text(
                    text = "تغییر سطح دسترسی کاربر",
                    fontFamily = PeydaFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "انتخاب نقش جدید برای ${userToEditRole?.name}:",
                        fontFamily = PeydaFontFamily,
                        color = colors.textSecondary
                    )
                    listOf("خریدار معتمد", "کاسب و تولیدکننده", "مدیر کل").forEach { role ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (userToEditRole?.role == role) colors.primaryLight else colors.background)
                                .clickable {
                                    viewModel.updateUserRoleByAdmin(userToEditRole!!.phone, role)
                                    Toast.makeText(context, "نقش کاربر به «$role» تغییر یافت.", Toast.LENGTH_SHORT).show()
                                    userToEditRole = null
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = role, fontFamily = PeydaFontFamily, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                            if (userToEditRole?.role == role) {
                                Icon(Icons.Default.CheckCircle, null, tint = colors.primary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { userToEditRole = null }) {
                    Text("بستن", fontFamily = PeydaFontFamily, color = colors.textMuted)
                }
            },
            containerColor = colors.surfaceCard
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Master Admin Top Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFFEAB308), Color(0xFFF59E0B)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "مرکز فرماندهی و مدیریت کل",
                                fontFamily = PeydaFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "دسترسی نامحدود ادمین • ورود رمز ۱۲۸۱۱۰",
                                fontFamily = PeydaFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFFEAB308),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Return to Marketplace or Refresh button
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = {
                                viewModel.loadAllRegisteredAccounts()
                                Toast.makeText(context, "اطلاعات به‌روزرسانی شد.", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "تازه سازی", tint = colors.primary)
                        }
                        Button(
                            onClick = { viewModel.navigateTo(KasebanScreen.MERCHANTS) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primaryLight),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "نمای بازار",
                                fontFamily = PeydaFontFamily,
                                fontSize = 11.5.sp,
                                color = colors.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Quick Analytics KPI Badges
                val verifiedCount = merchants.count { it.isVerified }
                val pinnedCount = merchants.count { it.isPinned }
                val totalProducts = merchants.sumOf { it.products.size }
                val totalPosts = merchants.sumOf { it.posts.size }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    item {
                        KpiChip(title = "کل غرفه‌ها", value = "${merchants.size}", color = colors.primary, icon = Icons.Default.Storefront)
                    }
                    item {
                        KpiChip(title = "تیک آبی رسمی", value = "$verifiedCount", color = Color(0xFF0284C7), icon = Icons.Default.Verified)
                    }
                    item {
                        KpiChip(title = "غرفه‌های پین‌شده", value = "$pinnedCount", color = Color(0xFFEAB308), icon = Icons.Default.PushPin)
                    }
                    item {
                        KpiChip(title = "کاربران ثبت‌نامی", value = "${registeredAccounts.size}", color = Color(0xFF8B5CF6), icon = Icons.Default.SupervisorAccount)
                    }
                    item {
                        KpiChip(title = "محصولات کل", value = "$totalProducts", color = Color(0xFF10B981), icon = Icons.Default.ShoppingBag)
                    }
                }
            }
        }

        // Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = colors.surfaceCard,
            contentColor = colors.primary,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = colors.primary,
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    text = {
                        Text(
                            text = title,
                            fontFamily = PeydaFontFamily,
                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.5.sp,
                            color = if (activeTab == index) colors.primary else colors.textMuted
                        )
                    }
                )
            }
        }

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = when (activeTab) {
                            0 -> "جستجو در نام غرفه، شماره همراه یا زمینه کاری..."
                            1 -> "جستجو در نام کاربر، شماره تلفن یا نقش..."
                            else -> "جستجوی آیتم..."
                        },
                        fontFamily = PeydaFontFamily,
                        fontSize = 12.sp,
                        color = colors.textMuted
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = colors.textMuted)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.borderSubtle
                )
            )
        }

        // Tab Content
        when (activeTab) {
            0 -> {
                // Tab 0: Booths Management & Blue Badge Toggles
                val filteredMerchants = merchants.filter {
                    searchQuery.isBlank() ||
                            it.title.contains(searchQuery, ignoreCase = true) ||
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            it.phone.contains(searchQuery, ignoreCase = true) ||
                            it.specialty.contains(searchQuery, ignoreCase = true)
                }

                if (filteredMerchants.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "🏪 هنوز هیچ غرفه‌ای ایجاد نشده است.",
                                fontFamily = PeydaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "اطلاعات کاملاً صفر است. هر کاربری که ثبت‌نام کند و غرفه بسازد، بلافاصله در این پنل ظاهر می‌شود و می‌توانید به آن تیک آبی بدهید یا آن را پین کنید.",
                                fontFamily = PeydaFontFamily,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = colors.textSecondary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredMerchants, key = { it.id }) { merchant ->
                            AdminMerchantCard(
                                merchant = merchant,
                                onToggleVerified = {
                                    viewModel.toggleMerchantVerifiedByAdmin(merchant.id)
                                    val nowVerified = !merchant.isVerified
                                    Toast.makeText(
                                        context,
                                        if (nowVerified) "تیک آبی رسمی به غرفه اعطا شد." else "تیک آبی رسمی از غرفه برداشته شد.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onTogglePinned = {
                                    viewModel.toggleMerchantPinnedByAdmin(merchant.id)
                                    val nowPinned = !merchant.isPinned
                                    Toast.makeText(
                                        context,
                                        if (nowPinned) "غرفه در بالای بازار پین شد." else "غرفه از حالت پین خارج شد.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onDelete = {
                                    itemToDeleteConfirm = merchant.title to {
                                        viewModel.deleteMerchantByAdmin(merchant.id)
                                    }
                                },
                                onInspect = {
                                    viewModel.selectMerchant(merchant)
                                    viewModel.navigateTo(KasebanScreen.MERCHANTS)
                                }
                            )
                        }
                    }
                }
            }
            1 -> {
                // Tab 1: User Accounts Management
                val filteredAccounts = registeredAccounts.filter {
                    searchQuery.isBlank() ||
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            it.phone.contains(searchQuery, ignoreCase = true) ||
                            it.role.contains(searchQuery, ignoreCase = true) ||
                            it.shopTitle.contains(searchQuery, ignoreCase = true)
                }

                if (filteredAccounts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "هیچ کاربری یافت نشد.",
                            fontFamily = PeydaFontFamily,
                            fontSize = 14.sp,
                            color = colors.textMuted
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredAccounts, key = { it.phone }) { acc ->
                            AdminUserCard(
                                account = acc,
                                onEditRole = { userToEditRole = acc },
                                onResetPin = { userToResetPin = acc },
                                onDelete = {
                                    itemToDeleteConfirm = "کاربر ${acc.name} (${acc.phone})" to {
                                        viewModel.deleteUserByAdmin(acc.phone)
                                    }
                                }
                            )
                        }
                    }
                }
            }
            2 -> {
                // Tab 2: Products & Posts Moderation
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(merchants, key = { it.id }) { merchant ->
                        if (merchant.products.isNotEmpty() || merchant.posts.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🏪 ${merchant.title} (${merchant.name})",
                                            fontFamily = PeydaFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = "${merchant.products.size} محصول • ${merchant.posts.size} پست",
                                            fontFamily = PeydaFontFamily,
                                            fontSize = 11.sp,
                                            color = colors.textMuted
                                        )
                                    }

                                    // Products
                                    if (merchant.products.isNotEmpty()) {
                                        Text(text = "محصولات:", fontFamily = PeydaFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp, color = colors.primary)
                                        merchant.products.forEach { prod ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(colors.background)
                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(text = prod.title, fontFamily = PeydaFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                                                    Text(text = "${prod.weight} • ${prod.price} تومان", fontFamily = PeydaFontFamily, fontSize = 10.5.sp, color = colors.textMuted)
                                                }
                                                IconButton(
                                                    onClick = {
                                                        itemToDeleteConfirm = prod.title to {
                                                            viewModel.deleteProductByAdmin(merchant.id, prod.id)
                                                        }
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "حذف محصول", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }

                                    // Posts
                                    if (merchant.posts.isNotEmpty()) {
                                        Text(text = "پست‌ها و دسترنج‌ها:", fontFamily = PeydaFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp, color = colors.primary)
                                        merchant.posts.forEach { post ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(colors.background)
                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = post.title.ifBlank { "پست بدون عنوان" }, fontFamily = PeydaFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                                                    Text(text = post.text, fontFamily = PeydaFontFamily, fontSize = 10.5.sp, color = colors.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                }
                                                IconButton(
                                                    onClick = {
                                                        itemToDeleteConfirm = post.title.ifBlank { "این پست" } to {
                                                            viewModel.deletePostByAdmin(merchant.id, post.id)
                                                        }
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "حذف پست", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                // Tab 3: System Overview & Tools
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "📊 وضعیت پایگاه‌داده و سرور ابری",
                                    fontFamily = PeydaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "پایگاه‌داده Cloud Firestore: متصل و پایدار\nهمگام‌سازی بلادرنگ: فعال\nدسترسی مدیر کل: سطح ۱ (Super Admin)",
                                    fontFamily = PeydaFontFamily,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                viewModel.navigateTo(KasebanScreen.MERCHANTS)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                        ) {
                            Text("بازگشت به بازار و نمای کاربران", fontFamily = PeydaFontFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = {
                                viewModel.logout()
                                Toast.makeText(context, "از حساب مدیر کل خارج شدید.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("خروج از حساب کاربری", fontFamily = PeydaFontFamily, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMerchantCard(
    merchant: Merchant,
    onToggleVerified: () -> Unit,
    onTogglePinned: () -> Unit,
    onDelete: () -> Unit,
    onInspect: () -> Unit
) {
    val colors = AppTheme.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = androidx.compose.foundation.BorderStroke(
            width = if (merchant.isPinned) 1.5.dp else 1.dp,
            color = if (merchant.isPinned) Color(0xFFEAB308) else colors.borderSubtle
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Top row: Info & Avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colors.primaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = merchant.avatarEmoji, fontSize = 22.sp)
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = merchant.title,
                                fontFamily = PeydaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = colors.textPrimary
                            )
                            if (merchant.isVerified) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "تیک آبی رسمی",
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            if (merchant.isPinned) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "پین شده",
                                    tint = Color(0xFFEAB308),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Text(
                            text = "${merchant.name} • ${merchant.phone}",
                            fontFamily = PeydaFontFamily,
                            fontSize = 11.5.sp,
                            color = colors.textMuted
                        )
                    }
                }

                // Delete Button
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف غرفه", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                }
            }

            // Tags row
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.background)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = merchant.specialty, fontFamily = PeydaFontFamily, fontSize = 10.5.sp, color = colors.textSecondary)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.background)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = merchant.location, fontFamily = PeydaFontFamily, fontSize = 10.5.sp, color = colors.textSecondary)
                }
            }

            // Super Admin Action Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Toggle Verified Button
                Button(
                    onClick = onToggleVerified,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (merchant.isVerified) Color(0xFF0284C7) else colors.background
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = if (merchant.isVerified) Color.White else Color(0xFF0284C7),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (merchant.isVerified) "تیک آبی فعال است" else "اعطای تیک آبی",
                            fontFamily = PeydaFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (merchant.isVerified) Color.White else colors.textPrimary
                        )
                    }
                }

                // Toggle Pinned Button
                Button(
                    onClick = onTogglePinned,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (merchant.isPinned) Color(0xFFEAB308) else colors.background
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null,
                            tint = if (merchant.isPinned) Color.White else Color(0xFFEAB308),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (merchant.isPinned) "پین شده در بالا" else "پین کردن در بالا",
                            fontFamily = PeydaFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (merchant.isPinned) Color.White else colors.textPrimary
                        )
                    }
                }

                // Preview Button
                OutlinedButton(
                    onClick = onInspect,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = "مشاهده", tint = colors.primary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun AdminUserCard(
    account: UserAccount,
    onEditRole: () -> Unit,
    onResetPin: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = AppTheme.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = account.name, fontFamily = PeydaFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = colors.textPrimary)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (account.role.contains("کاسب")) colors.primaryLight else Color(0xFFE0E7FF))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = account.role,
                            fontFamily = PeydaFontFamily,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (account.role.contains("کاسب")) colors.primary else Color(0xFF4338CA)
                        )
                    }
                }
                Text(
                    text = "شماره همراه: ${account.phone} • رمز: ${account.pin}",
                    fontFamily = PeydaFontFamily,
                    fontSize = 11.sp,
                    color = colors.textMuted
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEditRole, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "تغییر نقش", tint = colors.primary, modifier = Modifier.size(17.dp))
                }
                IconButton(onClick = onResetPin, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Key, contentDescription = "تغییر رمز", tint = Color(0xFFEAB308), modifier = Modifier.size(17.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف کاربر", tint = Color(0xFFEF4444), modifier = Modifier.size(17.dp))
                }
            }
        }
    }
}

@Composable
private fun KpiChip(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val colors = AppTheme.colors
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Column {
                Text(text = value, fontFamily = PeydaFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = color)
                Text(text = title, fontFamily = PeydaFontFamily, fontSize = 9.sp, color = colors.textMuted)
            }
        }
    }
}
