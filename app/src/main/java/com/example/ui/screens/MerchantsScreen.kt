package com.example.ui.screens

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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import com.example.ui.components.BoothManagementDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.FeaturedBanner
import com.example.data.model.Merchant
import com.example.data.model.MerchantReview
import com.example.data.model.MutualFamiliar
import com.example.ui.components.FluidAnimatedEntry
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.MerchantAvatar
import com.example.ui.components.PersianUtils
import com.example.ui.components.SkeletonLoadingFeed
import com.example.ui.components.TrustButton
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryDark
import com.example.ui.theme.GlassBorderBlue
import com.example.ui.theme.GlassBorderMint
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
import kotlinx.coroutines.delay

@Composable
fun MerchantsScreen(
    viewModel: KasebanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val merchantsTab by viewModel.merchantsTab.collectAsState()
    val merchants by viewModel.merchants.collectAsState()
    val selectedMerchant by viewModel.selectedMerchant.collectAsState()
    val userDisplayName by viewModel.userDisplayName.collectAsState()
    val featuredBanners by viewModel.featuredBanners.collectAsState()
    val mutualFamiliars by viewModel.mutualFamiliars.collectAsState()
    val reviews by viewModel.merchantReviews.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showMutualDialog by remember { mutableStateOf(false) }
    var showReviewsDialog by remember { mutableStateOf(false) }
    var showWriteReviewDialog by remember { mutableStateOf(false) }
    var showBoothManagementDialog by remember { mutableStateOf(false) }
    var isCategoriesExpanded by remember { mutableStateOf(false) }

    val categories = remember {
        listOf(
            MarketCategory("نان و شیرینی", "🥐", "نان‌های خمیرترش و شیرینی‌های سنتی محلی", Color(0x66FEF3C7)),
            MarketCategory("عسل و مربا", "🍯", "عسل طبیعی سبلان و مرباهای خانگی ارگانیک", Color(0x66FEF9C3)),
            MarketCategory("خشکبار و مغزها", "🥜", "خشکبار، بادام و پسته دستچین روستایی", Color(0x66FFEDD5)),
            MarketCategory("گیاهی و عرقیات", "🌿", "عرقیات سنتی کاشان و گیاهان دارویی کوهستان", Color(0x66DCFCE7)),
            MarketCategory("لبنیات و پنیر", "🧀", "لبنیات محلی و پنیر لیقوان و کره سنتی", Color(0x66E0F2FE)),
            MarketCategory("روغن و چاشنی", "🫒", "ارده دوآتیشه اردکان و روغن کنجد فرابکر", Color(0x66F0FDF4)),
            MarketCategory("میوه و ارگانیک", "🍎", "انار یاقوت سرخ ساوه و رب انار سنتی", Color(0x66FEE2E2)),
            MarketCategory("صنایع دستی", "🧵", "صنایع دستی، قلم‌زنی و فیروزه‌کوبی مس", Color(0x66F3E8FF))
        )
    }

    val filteredMerchants = remember(merchants, selectedCategory) {
        if (selectedCategory == null) merchants
        else merchants.filter { it.matchesCategory(selectedCategory!!) }
    }

    // UI presentation layer
    var isTabLoading by remember { mutableStateOf(false) }

    LaunchedEffect(merchantsTab) {
        isTabLoading = true
        delay(380)
        isTabLoading = false
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Circular Navigation Bar matching competitor app flow (00:00 - 00:05)
            FluidAnimatedEntry(delayMillis = 15) {
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
                        badgeText = "${PersianUtils.toPersianDigits(mutualFamiliars.size.toString())} معتمد",
                        icon = Icons.Default.Diversity3,
                        isSelected = merchantsTab == "آشنایان",
                        onClick = {
                            viewModel.setMerchantsTab("آشنایان")
                            isCategoriesExpanded = false
                        }
                    )

                    // 2. دسته‌بندی‌ها (Expandable 8 Categories Grid)
                    TopCircleShortcut(
                        title = "دسته‌بندی‌ها",
                        badgeText = if (isCategoriesExpanded) "بستن منو ▲" else if (selectedCategory != null) selectedCategory!! else "۸ رسته ▼",
                        icon = Icons.Default.Category,
                        isSelected = isCategoriesExpanded || selectedCategory != null,
                        onClick = { isCategoriesExpanded = !isCategoriesExpanded }
                    )

                    // 3. کاسبان (Market / Producers)
                    TopCircleShortcut(
                        title = "کاسبان",
                        badgeText = "غرفه‌های اصیل",
                        icon = Icons.Default.Storefront,
                        isSelected = merchantsTab == "کاسب‌ها" && selectedCategory == null && !isCategoriesExpanded,
                        onClick = {
                            viewModel.setMerchantsTab("کاسب‌ها")
                            viewModel.clearCategoryFilter()
                            isCategoriesExpanded = false
                        }
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
                        .padding(horizontal = 16.dp, vertical = 4.dp),
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
                                text = "دسته‌بندی رسته‌های تولیدی معتمد:",
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = TextPrimary
                            )

                            if (selectedCategory != null) {
                                Text(
                                    text = "نمایش همه غرفه‌ها ✕",
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7),
                                    modifier = Modifier.clickable {
                                        viewModel.clearCategoryFilter()
                                        isCategoriesExpanded = false
                                    }
                                )
                            }
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
                                val isCatSelected = selectedCategory == cat.title
                                CategoryItemTile(
                                    title = cat.title,
                                    emoji = cat.emoji,
                                    bgColor = cat.bgColor,
                                    isSelected = isCatSelected,
                                    onClick = {
                                        viewModel.selectCategory(cat.title)
                                        viewModel.setMerchantsTab("کاسب‌ها")
                                        isCategoriesExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 3. Active Category Filter Indicator Chip
            AnimatedVisibility(
                visible = selectedCategory != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                selectedCategory?.let { activeCat ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color(0xD9E0F2FE),
                        borderColor = Color(0x6638BDF8)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val catEmoji = categories.find { it.title == activeCat }?.emoji ?: "🏷️"
                                Text(text = catEmoji, fontSize = 18.sp)
                                Text(
                                    text = "رسته: $activeCat (${PersianUtils.toPersianDigits(filteredMerchants.size.toString())} غرفه معتمد)",
                                    fontFamily = VazirmatnFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF0369A1)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x330284C7))
                                    .clickable { viewModel.clearCategoryFilter() }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "حذف فیلتر ✕",
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7)
                                )
                            }
                        }
                    }
                }
            }

            if (isTabLoading) {
                // UI presentation layer
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    SkeletonLoadingFeed(count = 5)
                }
            } else if (merchantsTab == "کاسب‌ها") {
                // Merchants Feed
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp, top = 2.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Featured Banner Carousel (only when real announcements exist)
                    if (featuredBanners.isNotEmpty()) {
                        item {
                            FluidAnimatedEntry(delayMillis = 20) {
                                FeaturedBannersSection(
                                    banners = featuredBanners,
                                    onBannerClick = { banner ->
                                        val target = merchants.find { it.id == banner.merchantId }
                                        if (target != null) viewModel.selectMerchant(target)
                                    }
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 4.dp, end = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedCategory != null) "غرفه‌های رسته $selectedCategory" else "کاسبان و غرفه‌های معتمد",
                                color = TextPrimary,
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            // Manage / Add Shop Button
                            val myBooth = viewModel.getMyBooth()
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE0F2FE))
                                    .border(1.dp, Color(0x6638BDF8), RoundedCornerShape(12.dp))
                                    .clickable { showBoothManagementDialog = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (myBooth != null) Icons.Default.Storefront else Icons.Default.Add,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = if (myBooth != null) "مدیریت غرفه من" else "راه‌اندازی غرفه",
                                        fontFamily = VazirmatnFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = BluePrimary
                                    )
                                }
                            }
                        }
                    }

                    if (filteredMerchants.isEmpty()) {
                        item {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                shape = RoundedCornerShape(20.dp),
                                backgroundColor = Color(0xF2FFFFFF)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(text = "🏪", fontSize = 36.sp)
                                    Text(
                                        text = if (selectedCategory != null) "هیچ غرفه‌ای در رسته $selectedCategory ثبت نشده است" else "هنوز غرفه‌ای در بازار ثبت نشده است",
                                        fontFamily = VazirmatnFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "بازار کاسبان بستری برای معرفی تولیدکنندگان محلی و خرید بدون واسطه است. شما می‌توانید اولین غرفه را ثبت کنید یا با ارسال دعوت‌نامه از کسبه معتمد دعوت فرمایید.",
                                        fontFamily = VazirmatnFontFamily,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 20.sp
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        val myBooth = viewModel.getMyBooth()
                                        Button(
                                            onClick = { showBoothManagementDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = if (myBooth != null) "🏪 مدیریت غرفه من" else "➕ ایجاد و مدیریت غرفه",
                                                fontFamily = VazirmatnFontFamily,
                                                color = Color.White
                                            )
                                        }

                                        if (selectedCategory != null) {
                                            Button(
                                                onClick = { viewModel.clearCategoryFilter() },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(text = "مشاهده همه", fontFamily = VazirmatnFontFamily, color = TextPrimary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        items(filteredMerchants) { merchant ->
                            val index = filteredMerchants.indexOf(merchant)
                            FluidAnimatedEntry(delayMillis = (index * 45).coerceAtMost(300)) {
                                MerchantFeedCard(
                                    merchant = merchant,
                                    onKnowClick = { viewModel.toggleKnowMerchant(merchant.id) },
                                    onCardClick = { viewModel.selectMerchant(merchant) },
                                    onChatClick = { viewModel.openChatWithMerchant(merchant) }
                                )
                            }
                        }
                    }
                }
            } else {
                // Familiar Network Graph & Connection View (آشنایان)
                FamiliarsNetworkView(
                    familiars = mutualFamiliars,
                    inviteCode = viewModel.inviteCode,
                    onToggleKnown = { id -> viewModel.toggleMutualFamiliarKnown(id) },
                    onShareInvite = { viewModel.shareInvite(context) },
                    onAddFamiliar = { name, rel -> viewModel.addMutualFamiliar(name, rel) }
                )
            }
        }

        // Full Screen Merchant Detail Modal with smooth fluid spring entrance
        AnimatedVisibility(
            visible = selectedMerchant != null,
            enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                    slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) +
                    scaleIn(
                        initialScale = 0.94f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
            exit = fadeOut(animationSpec = tween(150)) +
                    slideOutVertically(targetOffsetY = { it / 4 }, animationSpec = tween(150)) +
                    scaleOut(targetScale = 0.96f, animationSpec = tween(150))
        ) {
            selectedMerchant?.let { merchant ->
                MerchantDetailSheet(
                    merchant = merchant,
                    onClose = { viewModel.closeMerchantDetail() },
                    onStartChatWithOrder = { prefilledMsg ->
                        viewModel.openChatWithMerchant(merchant)
                        if (prefilledMsg.isNotBlank()) {
                            viewModel.sendChatMessage(merchant.id, prefilledMsg)
                        }
                    },
                    onKnowClick = { viewModel.toggleKnowMerchant(merchant.id) },
                    onOpenMutuals = { showMutualDialog = true },
                    onOpenReviews = { showReviewsDialog = true }
                )
            }
        }


        // UI presentation layer
        if (showMutualDialog) {
            MutualFamiliarsDialog(
                familiars = mutualFamiliars,
                onDismiss = { showMutualDialog = false },
                onToggleKnown = { id -> viewModel.toggleMutualFamiliarKnown(id) }
            )
        }

        // UI presentation layer
        if (showReviewsDialog) {
            MerchantReviewsDialog(
                reviews = reviews,
                onDismiss = { showReviewsDialog = false },
                onWriteReviewClick = {
                    showWriteReviewDialog = true
                }
            )
        }

        // Write Review Dialog
        if (showWriteReviewDialog) {
            WriteReviewDialog(
                onDismiss = { showWriteReviewDialog = false },
                onSubmit = { comment, rating ->
                    viewModel.addMerchantReview(comment, rating)
                    showWriteReviewDialog = false
                    Toast.makeText(context, "دیدگاه شما با موفقیت ثبت شد", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Booth Management Dashboard Dialog
        if (showBoothManagementDialog) {
            BoothManagementDialog(
                onDismiss = { showBoothManagementDialog = false },
                viewModel = viewModel
            )
        }
    }
}

/**
 * Kaseban UI Component
 */
@Composable
private fun FeaturedBannersSection(
    banners: List<FeaturedBanner>,
    onBannerClick: (FeaturedBanner) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(banners) { banner ->
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(130.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .clickable { onBannerClick(banner) }
                        .shadow(4.dp, RoundedCornerShape(22.dp))
                ) {
                    AsyncImage(
                        model = banner.imageUrl,
                        contentDescription = banner.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dark gradient overlay for readable text
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0x33000000), Color(0xCC0F172A))
                                )
                            )
                    )

                    // Badge on top right
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xCC059669))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = banner.badge,
                            color = Color.White,
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    // Bottom title & subtitle
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = banner.title,
                            color = Color.White,
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp
                        )
                        Text(
                            text = banner.subtitle,
                            color = Color(0xFFE2E8F0),
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 10.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Indicator dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            banners.forEachIndexed { idx, _ ->
                val isSelected = listState.firstVisibleItemIndex == idx
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .width(if (isSelected) 14.dp else 5.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (isSelected) PastelMintPrimary else Color(0x4D64748B))
                )
            }
        }
    }
}

/**
 * Merchant Card in Feed
 */
@Composable
private fun MerchantFeedCard(
    merchant: Merchant,
    onKnowClick: () -> Unit,
    onCardClick: () -> Unit,
    onChatClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = SurfaceGlassCard,
        borderBrush = GlassBorderRefractionBrush,
        elevation = 3.dp,
        onClick = onCardClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MerchantAvatar(
                    name = merchant.name,
                    specialty = merchant.specialty,
                    size = 52.dp,
                    isOnline = merchant.isOnline,
                    avatarUri = merchant.avatarUri
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = merchant.name,
                            color = TextPrimary,
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xCCDBEAFE))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = merchant.location,
                                color = BluePrimary,
                                fontFamily = VazirmatnFontFamily,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = merchant.title,
                        color = TextSecondary,
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // UI presentation layer
            if (merchant.products.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(merchant.products.take(3)) { product ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x80FFFFFF))
                                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = product.title,
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = PersianUtils.formatPrice(product.price),
                                        fontFamily = VazirmatnFontFamily,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PastelMintPrimary
                                    )
                                    Text(
                                        text = "• ${product.weight}",
                                        fontFamily = VazirmatnFontFamily,
                                        fontSize = 9.5.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0x1F0D9488), thickness = 0.8.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xCCE0EDFC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diversity3,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = if (merchant.knownCount > 0) "${PersianUtils.toPersianDigits(merchant.knownCount.toString())} تأیید معتمدین" else "غرفه جدید",
                        color = TextMuted,
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xCCEAF2FE), Color(0xBFDBEAFE))
                                )
                            )
                            .border(1.dp, Color(0x6638BDF8), RoundedCornerShape(12.dp))
                            .clickable(onClick = onChatClick)
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = BluePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "گفتگو",
                                color = BluePrimaryDark,
                                fontFamily = VazirmatnFontFamily,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    TrustButton(
                        isKnown = merchant.isKnownByUser,
                        onClick = onKnowClick,
                        modifier = Modifier.widthIn(min = 74.dp)
                    )
                }
            }
        }
    }
}

/**
 * Merchant Detail Bottom Sheet with Checkbox Product Catalog, Dynamic Price, and Sticky Bottom Bar
 * Kaseban UI Component
 */
@Composable
private fun MerchantDetailSheet(
    merchant: Merchant,
    onClose: () -> Unit,
    onStartChatWithOrder: (String) -> Unit,
    onKnowClick: () -> Unit,
    onOpenMutuals: () -> Unit,
    onOpenReviews: () -> Unit
) {
    val selectedProducts = remember { mutableStateMapOf<String, Boolean>() }

    // UI presentation layer
    val checkedProducts = merchant.products.filter { selectedProducts[it.id] == true }
    val totalPrice = checkedProducts.sumOf { it.price }
    val selectedCount = checkedProducts.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xB30F172A))
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            backgroundColor = Color(0xD9EBF7F5),
            borderBrush = GlassBorderRefractionBrush
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top close & header bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xD9E2E8F0))
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = TextPrimary)
                    }

                    Text(
                        text = merchant.name,
                        color = TextPrimary,
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    GlassBadge(text = merchant.location, color = BluePrimary)
                }

                HorizontalDivider(color = Color(0x1F0D9488))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Merchant Profile Hero with Custom Avatar Monogram
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MerchantAvatar(
                                name = merchant.name,
                                specialty = merchant.specialty,
                                size = 64.dp,
                                isOnline = merchant.isOnline,
                                avatarUri = merchant.avatarUri
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = merchant.name,
                                    color = TextPrimary,
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = merchant.title,
                                    color = TextSecondary,
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    // Action Pills: [گفتگو] & [می‌شناسم]
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF0284C7)))
                                    )
                                    .clickable {
                                        onStartChatWithOrder("")
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "گفتگوی مستقیم",
                                        color = Color.White,
                                        fontFamily = VazirmatnFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (merchant.isKnownByUser) Color(0xFFDCFCE7) else Color(0x330284C7)
                                    )
                                    .border(
                                        1.dp,
                                        if (merchant.isKnownByUser) Color(0xFF059669) else Color(0x6638BDF8),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable(onClick = onKnowClick)
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (merchant.isKnownByUser) "✓ میشناسمش" else "میشناسم",
                                    color = if (merchant.isKnownByUser) Color(0xFF047857) else BluePrimary,
                                    fontFamily = VazirmatnFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    // Clickable Customer Reviews Card ("نظرات مردم")
                    // UI presentation layer
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Color(0xB8F1F8F5),
                            borderColor = TrustGreen.copy(alpha = 0.3f),
                            onClick = onOpenReviews
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFEAB308),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (merchant.reviewsCount > 0) "نظرات مردم (${PersianUtils.toPersianDigits(merchant.reviewsCount.toString())} دیدگاه)"
                                                   else "نظرات خریداران",
                                            color = TextPrimary,
                                            fontFamily = VazirmatnFontFamily,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (merchant.reviewsSummary.isNotBlank()) merchant.reviewsSummary
                                                   else if (merchant.reviewsCount > 0) "مشاهده نظرات ثبت‌شده"
                                                   else "هنوز نظری ثبت نشده است • ثبت دیدگاه",
                                            color = TextSecondary,
                                            fontFamily = VazirmatnFontFamily,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Text(
                                    text = "دیدگاه‌ها ›",
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PastelMintPrimary
                                )
                            }
                        }
                    }

                    // Product catalog title
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "شناسنامه و فهرست محصولات",
                                color = TextPrimary,
                                fontFamily = VazirmatnFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "جهت سفارش علامت بزنید",
                                color = TextMuted,
                                fontFamily = VazirmatnFontFamily,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // UI presentation layer
                    items(merchant.products) { product ->
                        val isChecked = selectedProducts[product.id] ?: false
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = if (isChecked) Color(0xD9EFF6FF) else SurfaceGlassCard,
                            borderColor = if (isChecked) BluePrimary else GlassBorderSubtle,
                            onClick = { selectedProducts[product.id] = !isChecked }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Checkbox circle
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(if (isChecked) BluePrimary else Color.Transparent)
                                            .border(1.5.dp, if (isChecked) BluePrimary else Color(0xFF94A3B8), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isChecked) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = product.title,
                                            color = TextPrimary,
                                            fontFamily = VazirmatnFontFamily,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = product.weight,
                                            color = TextMuted,
                                            fontFamily = VazirmatnFontFamily,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Text(
                                    text = PersianUtils.formatPrice(product.price),
                                    color = BluePrimary,
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Story & Workshop section
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = SurfaceGlassCard,
                            borderColor = GlassBorderSubtle
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = merchant.storyTitle.ifBlank { "درباره غرفه و دسترنج" },
                                        color = TextPrimary,
                                        fontFamily = VazirmatnFontFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = merchant.storyText.ifBlank { "غرفه معتبر و ثبت‌شده در سامانه معاملات مستقیم بازار کاسبان" },
                                    color = TextSecondary,
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 12.sp,
                                    lineHeight = 21.sp
                                )

                                if (merchant.address.isNotBlank()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "نشانی: ${merchant.address}",
                                            color = TextMuted,
                                            fontFamily = VazirmatnFontFamily,
                                            fontSize = 11.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Working Hours, Shipping & Guarantee Policies
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = SurfaceGlassCard,
                            borderColor = GlassBorderSubtle
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "شرایط ارسال، ساعت کاری و ضمانت",
                                        color = TextPrimary,
                                        fontFamily = VazirmatnFontFamily,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "ساعت پاسخگویی: ${merchant.workHours}",
                                        color = TextSecondary,
                                        fontFamily = VazirmatnFontFamily,
                                        fontSize = 11.5.sp
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "روش‌های ارسال: ${merchant.deliveryMethods}" + (if (merchant.freeShippingThreshold > 0) " (ارسال رایگان از ${PersianUtils.formatPrice(merchant.freeShippingThreshold)})" else ""),
                                        color = TextSecondary,
                                        fontFamily = VazirmatnFontFamily,
                                        fontSize = 11.5.sp
                                    )
                                }

                                if (merchant.guaranteePolicy.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFDCFCE7))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "🛡️ ${merchant.guaranteePolicy}",
                                            color = Color(0xFF15803D),
                                            fontFamily = VazirmatnFontFamily,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Posts & Stories Section
                    if (merchant.posts.isNotEmpty()) {
                        item {
                            Text(
                                text = "📸 پست‌ها و اخبار دسترنج غرفه",
                                color = TextPrimary,
                                fontFamily = VazirmatnFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }

                        items(merchant.posts, key = { it.id }) { post ->
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = SurfaceGlassCard,
                                borderColor = GlassBorderSubtle
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = post.title.ifBlank { "خبر دسترنج" },
                                            fontFamily = VazirmatnFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = BluePrimary
                                        )
                                        Text(
                                            text = post.date,
                                            fontFamily = VazirmatnFontFamily,
                                            fontSize = 10.5.sp,
                                            color = TextMuted
                                        )
                                    }

                                    if (post.imageUri != null) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(150.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        ) {
                                            AsyncImage(
                                                model = post.imageUri,
                                                contentDescription = post.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }

                                    Text(
                                        text = post.text,
                                        fontFamily = VazirmatnFontFamily,
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Sticky Bottom Action Bar with Dynamic Total Price Calculation
                // UI presentation layer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xB3FFFFFF))
                        .border(1.dp, Color(0x330D9488), androidx.compose.ui.graphics.RectangleShape)
                        .padding(14.dp)
                ) {
                    val orderText = if (selectedCount > 0) {
                        "ثبت سفارش ($selectedCount قلم به ارزش ${PersianUtils.formatPrice(totalPrice)}) و گفتگو"
                    } else {
                        "گفتگو و ثبت سفارش با ${merchant.name}"
                    }

                    val prefilledMessage = if (selectedCount > 0) {
                        val productNames = checkedProducts.joinToString("، ") { "${it.title} (${it.weight})" }
                        "سلام وقت بخیر جناب/سرکار ${merchant.name}؛ من مایل به خرید این اقلام هستم:\n$productNames\nمبلغ کل: ${PersianUtils.formatPrice(totalPrice)}"
                    } else {
                        ""
                    }

                    GlassButton(
                        text = orderText,
                        onClick = { onStartChatWithOrder(prefilledMessage) },
                        isPrimary = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Kaseban UI Component
 */
@Composable
private fun MutualFamiliarsDialog(
    familiars: List<MutualFamiliar>,
    onDismiss: () -> Unit,
    onToggleKnown: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, shape = RoundedCornerShape(26.dp), spotColor = Color(0x330D9488))
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xF2FFFFFF))
                    .border(1.2.dp, GlassBorderRefractionBrush, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "می‌شناسنش",
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "ارتباط با کاسب از طریق آشنایان مشترک شما",
                                fontFamily = VazirmatnFontFamily,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = TextMuted)
                        }
                    }

                    HorizontalDivider(color = Color(0x1F0D9488))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(familiars) { familiar ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x66F1F5F9))
                                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x330284C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = familiar.avatarEmoji, fontSize = 18.sp)
                                    }

                                    Column {
                                        Text(
                                            text = familiar.name,
                                            fontFamily = VazirmatnFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "آشنایی از: ${familiar.durationText}",
                                            fontFamily = VazirmatnFontFamily,
                                            fontSize = 10.5.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (familiar.isKnown) Color(0xFFDCFCE7) else Color(0x330284C7))
                                        .clickable { onToggleKnown(familiar.id) }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = if (familiar.isKnown) "✓ میشناسم" else "میشناسم",
                                        fontFamily = VazirmatnFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (familiar.isKnown) Color(0xFF047857) else BluePrimary,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Kaseban UI Component
 */
@Composable
private fun MerchantReviewsDialog(
    reviews: List<MerchantReview>,
    onDismiss: () -> Unit,
    onWriteReviewClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, shape = RoundedCornerShape(26.dp), spotColor = Color(0x330D9488))
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xF2FFFFFF))
                    .border(1.2.dp, GlassBorderRefractionBrush, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "نظرات خریداران",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFD1FAE5))
                                    .clickable(onClick = onWriteReviewClick)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(imageVector = Icons.Default.RateReview, contentDescription = null, tint = PastelMintPrimary, modifier = Modifier.size(14.dp))
                                    Text(text = "نوشتن نظر", fontFamily = VazirmatnFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PastelMintPrimary)
                                }
                            }

                            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = TextMuted)
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0x1F0D9488))

                    if (reviews.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "هنوز دیدگاهی برای این غرفه ثبت نشده است.\nبا زدن «نوشتن نظر» می‌توانید اولین دیدگاه خود را ثبت فرمایید.",
                                fontFamily = VazirmatnFontFamily,
                                fontSize = 12.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(reviews) { rv ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x66F8FAFC))
                                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(text = rv.avatarEmoji, fontSize = 16.sp)
                                            Text(text = rv.authorName, fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TextPrimary)
                                        }
                                        Row {
                                            repeat(rv.rating) {
                                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(13.dp))
                                            }
                                        }
                                    }

                                    Text(text = rv.comment, fontFamily = VazirmatnFontFamily, fontSize = 11.5.sp, color = TextSecondary, lineHeight = 18.sp)
                                    Text(text = rv.date, fontFamily = VazirmatnFontFamily, fontSize = 9.5.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Write Review Dialog
 */
@Composable
private fun WriteReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (comment: String, rating: Int) -> Unit
) {
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, shape = RoundedCornerShape(26.dp), spotColor = Color(0x330D9488))
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xF2FFFFFF))
                    .border(1.2.dp, GlassBorderRefractionBrush, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "ثبت دیدگاه درباره کاسب", fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { rating = star }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (star <= rating) Color(0xFFEAB308) else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("تجربه خرید و کیفیت محصولات", fontFamily = VazirmatnFontFamily) },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Button(
                        onClick = {
                            if (comment.isNotBlank()) {
                                onSubmit(comment, rating)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(text = "ارسال دیدگاه", fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Familiars Network View (آشنایان)
 */
@Composable
private fun FamiliarsNetworkView(
    familiars: List<MutualFamiliar>,
    inviteCode: String,
    onToggleKnown: (String) -> Unit,
    onShareInvite: () -> Unit,
    onAddFamiliar: (String, String) -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var newFamiliarName by remember { mutableStateOf("") }
    var newFamiliarRelation by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = SurfaceGlassCard,
                borderColor = GlassBorderBlue,
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .shadow(4.dp, shape = CircleShape, spotColor = Color(0x261D4ED8))
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFE0EEFD), Color(0xFFBAE6FD))
                                )
                            )
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Text(
                        text = "شبکه معتمدین و آشنایان",
                        color = TextPrimary,
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "با گسترش ارتباطات، کاسبان معتمد بیشتری از میان آشنایان مشترک در دسترس شما قرار می‌گیرند و خرید بدون واسطه را تجربه خواهید کرد.",
                        color = TextSecondary,
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 12.5.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    // Code Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xE6FFFFFF))
                            .border(1.dp, Color(0x6638BDF8), RoundedCornerShape(14.dp))
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("کد دعوت کاسبان", inviteCode))
                                Toast.makeText(context, "کد دعوت کپی شد: $inviteCode", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "کد دعوت اختصاصی شما: $inviteCode",
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BluePrimaryDark
                            )
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "کپی",
                                tint = BluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Action Buttons (Full standard touch targets, not small)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Share Invite Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF0284C7)))
                                )
                                .clickable(onClick = onShareInvite)
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "ارسال دعوت‌نامه",
                                    fontFamily = VazirmatnFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }

                        // Add Direct Familiar Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xCCFFFFFF))
                                .border(1.2.dp, Color(0x6638BDF8), RoundedCornerShape(16.dp))
                                .clickable { showAddDialog = true }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "افزودن آشنا",
                                    fontFamily = VazirmatnFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = BluePrimaryDark
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "افراد شبکه ارتباطی شما (${PersianUtils.toPersianDigits(familiars.size.toString())} نفر)",
                color = TextPrimary,
                fontFamily = VazirmatnFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp
            )
        }

        if (familiars.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = Color(0xF5FFFFFF),
                    borderBrush = GlassBorderRefractionBrush
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "هنوز عضوی در شبکه ارتباطی شما ثبت نشده است.",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "با دکمه «ارسال دعوت‌نامه» کد اختصاصی خود را برای دوستان بفرستید تا با عضویت آنها، کاسبان معتمد مشترک نمایش داده شوند.",
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        } else {
            items(familiars) { familiar ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SurfaceGlassCard,
                    borderBrush = GlassBorderRefractionBrush
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x330284C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = familiar.avatarEmoji, fontSize = 22.sp)
                            }

                            Column {
                                Text(
                                    text = familiar.name,
                                    fontFamily = VazirmatnFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "سابقه آشنایی: ${familiar.durationText}",
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 11.5.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .heightIn(min = 44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (familiar.isKnown) Color(0xFFDCFCE7) else Color(0x330284C7))
                                .border(1.dp, if (familiar.isKnown) Color(0xFF059669) else Color(0x4D38BDF8), RoundedCornerShape(14.dp))
                                .clickable { onToggleKnown(familiar.id) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (familiar.isKnown) "✓ تأیید شده" else "تأیید ارتباط",
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = if (familiar.isKnown) Color(0xFF047857) else BluePrimary
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Familiar Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "افزودن آشنا به شبکه معتمدین",
                    fontFamily = VazirmatnFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "نام و نسبت یا سابقه آشنایی فرد را وارد نمایید:",
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 12.5.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = newFamiliarName,
                        onValueChange = { newFamiliarName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("نام و نام خانوادگی", fontFamily = VazirmatnFontFamily, fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = newFamiliarRelation,
                        onValueChange = { newFamiliarRelation = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("نسبت (مثلاً همکار، دوست قدیمی)", fontFamily = VazirmatnFontFamily, fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFamiliarName.isNotBlank()) {
                            onAddFamiliar(newFamiliarName, newFamiliarRelation)
                            newFamiliarName = ""
                            newFamiliarRelation = ""
                            showAddDialog = false
                            Toast.makeText(context, "آشنا با موفقیت افزوده شد", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.heightIn(min = 44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ثبت آشنا", fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddDialog = false },
                    modifier = Modifier.heightIn(min = 44.dp)
                ) {
                    Text("انصراف", fontFamily = VazirmatnFontFamily)
                }
            }
        )
    }
}

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
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
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

@Composable
private fun CategoryItemTile(
    title: String,
    emoji: String,
    bgColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color(0xFFD1FAE5) else bgColor)
            .border(
                width = if (isSelected) 1.6.dp else 1.dp,
                brush = if (isSelected) Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF0284C7))) else Brush.linearGradient(listOf(GlassBorderSubtle, GlassBorderSubtle)),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Text(text = emoji, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontFamily = VazirmatnFontFamily,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF047857) else TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

/**
 * Dialog to register a genuine merchant shop in the Kaseban marketplace
 */
@Composable
private fun AddMerchantDialog(
    onDismiss: () -> Unit,
    onAddMerchant: (
        name: String,
        title: String,
        specialty: String,
        location: String,
        description: String,
        products: List<com.example.data.model.MerchantProduct>
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var shopTitle by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("نان و شیرینی") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var productTitle by remember { mutableStateOf("") }
    var productWeight by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    val productsList = remember { androidx.compose.runtime.mutableStateListOf<com.example.data.model.MerchantProduct>() }

    val categories = listOf(
        "نان و شیرینی", "عسل و مربا", "خشکبار و مغزها",
        "گیاهی و عرقیات", "لبنیات و پنیر", "روغن و چاشنی",
        "میوه و ارگانیک", "صنایع دستی"
    )

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, shape = RoundedCornerShape(26.dp), spotColor = Color(0x330D9488))
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xF2FFFFFF))
                    .border(1.2.dp, GlassBorderRefractionBrush, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ثبت غرفه و کسب‌وکار جدید",
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = TextMuted)
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("نام کاسب یا تولیدکننده", fontFamily = VazirmatnFontFamily) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = shopTitle,
                            onValueChange = { shopTitle = it },
                            label = { Text("عنوان غرفه / کسب‌وکار", fontFamily = VazirmatnFontFamily) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("شهر یا محله (مثال: همدان / لالجین)", fontFamily = VazirmatnFontFamily) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    item {
                        Text(
                            text = "رسته فعالیت:",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            items(categories) { cat ->
                                val isSelected = specialty == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PastelMintPrimary else Color(0xFFF1F5F9))
                                        .clickable { specialty = cat }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        fontFamily = VazirmatnFontFamily,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("درباره دسترنج و محصولات", fontFamily = VazirmatnFontFamily) },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    item {
                        HorizontalDivider(color = Color(0x1F0D9488), modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "افزودن محصولات غرفه (اختیاری):",
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = productTitle,
                                onValueChange = { productTitle = it },
                                label = { Text("نام محصول", fontFamily = VazirmatnFontFamily, fontSize = 10.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1.5f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = productWeight,
                                onValueChange = { productWeight = it },
                                label = { Text("وزن/تعداد", fontFamily = VazirmatnFontFamily, fontSize = 10.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = productPrice,
                                onValueChange = { if (it.all { ch -> ch.isDigit() }) productPrice = it },
                                label = { Text("قیمت (تومان)", fontFamily = VazirmatnFontFamily, fontSize = 10.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                if (productTitle.isNotBlank()) {
                                    val price = productPrice.toLongOrNull() ?: 0L
                                    productsList.add(
                                        com.example.data.model.MerchantProduct(
                                            id = "prod_${System.currentTimeMillis()}_${productsList.size}",
                                            title = productTitle.trim(),
                                            weight = productWeight.ifBlank { "۱ عدد" },
                                            price = price
                                        )
                                    )
                                    productTitle = ""
                                    productWeight = ""
                                    productPrice = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PastelBlueLight),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "➕ افزودن محصول به فهرست", fontFamily = VazirmatnFontFamily, fontSize = 11.sp, color = BluePrimaryDark)
                        }
                    }

                    if (productsList.isNotEmpty()) {
                        items(productsList) { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${p.title} (${p.weight})", fontFamily = VazirmatnFontFamily, fontSize = 11.sp, color = TextPrimary)
                                Text(text = "${p.price} ت", fontFamily = VazirmatnFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank() && shopTitle.isNotBlank()) {
                                    onAddMerchant(
                                        name.trim(),
                                        shopTitle.trim(),
                                        specialty,
                                        location.trim(),
                                        description.trim(),
                                        productsList.toList()
                                    )
                                }
                            },
                            enabled = name.isNotBlank() && shopTitle.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = "ثبت نهایی غرفه در بازار", fontFamily = VazirmatnFontFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

