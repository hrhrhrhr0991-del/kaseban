package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.SampleProducts
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.PersianUtils
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceDarkGlass
import com.example.ui.theme.SurfaceGlassLight
import com.example.ui.theme.SurfaceGlassMedium
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WheatGold
import com.example.ui.theme.WheatGoldDark
import com.example.ui.theme.WheatGoldLight
import com.example.viewmodel.GandomaViewModel

@Composable
fun StoreScreen(
    viewModel: GandomaViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cart by viewModel.cart.collectAsState()

    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }

    val filteredProducts = products.filter { product ->
        val matchesCategory = selectedCategory == "همه محصولات" || product.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                product.title.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Hero Glass Banner
            item {
                HeroBanner(onExploreClick = { viewModel.selectCategory("نان سنتی و خمیرترش") })
            }

            // Search Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    GlassTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        label = "جستجو در محصولات گندما...",
                        placeholder = "نان خمیرترش، عسل، کوکی، روغن زیتون...",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "جستجو",
                                tint = WheatGold
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "پاک کردن",
                                    tint = TextMuted,
                                    modifier = Modifier.clickable { viewModel.updateSearchQuery("") }
                                )
                            }
                        }
                    )
                }
            }

            // Categories Row
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SampleProducts.categories) { category ->
                        val isSelected = category == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) Color(0xFFFEF3C7) else Color(0xF5FFFFFF)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) WheatGold else GlassBorderSubtle,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.selectCategory(category) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) WheatGoldDark else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "محصولات دست‌چین ارگانیک (${PersianUtils.toPersianDigits(filteredProducts.size.toString())})",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "طبیعی و تازه پخت",
                        color = WheatGold,
                        fontSize = 12.sp
                    )
                }
            }

            // Products Grid / Cards
            items(filteredProducts) { product ->
                val quantityInCart = cart[product.id]?.quantity ?: 0
                ProductCardItem(
                    product = product,
                    quantity = quantityInCart,
                    onAdd = { viewModel.addToCart(product) },
                    onRemove = { viewModel.removeFromCart(product) },
                    onClick = { selectedProductForDetail = product },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🌾", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "محصولی با این مشخصات یافت نشد",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Floating Bottom Direct Checkout Bar
        AnimatedVisibility(
            visible = cart.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 76.dp, start = 16.dp, end = 16.dp)
        ) {
            FloatingDirectCheckoutBar(
                itemCount = viewModel.cartTotalCount,
                totalPrice = viewModel.cartTotalPrice,
                onProceedToDirect = { viewModel.startDirectOrder() }
            )
        }

        // Product Detail Dialog
        selectedProductForDetail?.let { product ->
            ProductDetailDialog(
                product = product,
                onDismiss = { selectedProductForDetail = null },
                onAddToCart = {
                    viewModel.addToCart(product)
                    selectedProductForDetail = null
                }
            )
        }
    }
}

@Composable
private fun HeroBanner(
    onExploreClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = Color(0xF8FFFFFF),
        borderColor = GlassBorder,
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x33FEF3C7),
                            Color(0x0DD97706),
                            Color(0x00FFFFFF)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassBadge(text = "🌾 برند جدید گندما | gandoma.ir", color = WheatGoldDark)
                    Text(text = "تخمیر سنتی ۳۶ ساعته ✨", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "نان‌های اصیل خمیرترش و فرآورده‌های طبیعی",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "پخت روزانه با آرد کامل سبوس‌دار بدون مواد نگهدارنده. ثبت آسان در دایرکت و تحویل فوری درب منزل.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassButton(
                        text = "مشاهده محصولات ویژه",
                        onClick = onExploreClick,
                        isPrimary = true
                    )
                    
                    Text(
                        text = "www.gandoma.ir",
                        color = WheatGoldDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductCardItem(
    product: Product,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        backgroundColor = SurfaceDarkGlass,
        borderColor = if (quantity > 0) GlassBorder else GlassBorderSubtle,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Icon Placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x33F59E0B))
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (product.iconType) {
                        "honey" -> "🍯"
                        "bread" -> "🥖"
                        "cookie" -> "🍪"
                        "tea" -> "🍵"
                        "cake" -> "🧁"
                        "oil" -> "🫒"
                        else -> "🌾"
                    },
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = product.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.description,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = PersianUtils.formatPrice(product.price),
                        color = WheatGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    product.originalPrice?.let { original ->
                        Text(
                            text = PersianUtils.formatPrice(original),
                            color = TextMuted,
                            fontSize = 11.sp,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    Text(
                        text = "• ${product.unit}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Add/Remove Counter
            if (quantity > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33F59E0B))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(WheatGold)
                            .clickable(onClick = onAdd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "افزایش",
                            tint = BackgroundDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = PersianUtils.toPersianDigits(quantity.toString()),
                        color = WheatGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0x40FFFFFF))
                            .clickable(onClick = onRemove),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "کاهش",
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(WheatGoldLight, WheatGold))
                        )
                        .clickable(onClick = onAdd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "افزودن به سبد",
                        tint = BackgroundDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingDirectCheckoutBar(
    itemCount: Int,
    totalPrice: Long,
    onProceedToDirect: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color(0xF8FFFFFF),
        borderColor = WheatGold,
        borderWidth = 1.5.dp,
        elevation = 6.dp,
        onClick = onProceedToDirect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
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
                        .background(WheatGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "سبد",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "${PersianUtils.toPersianDigits(itemCount.toString())} محصول در سبد خرید",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = PersianUtils.formatPrice(totalPrice),
                        color = WheatGoldDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "ثبت در دایرکت",
                    color = WheatGoldDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "💬", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ProductDetailDialog(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            GlassButton(
                text = "افزودن به سفارش (${PersianUtils.formatPrice(product.price)})",
                onClick = onAddToCart,
                isPrimary = true
            )
        },
        dismissButton = {
            GlassButton(
                text = "بستن",
                onClick = onDismiss,
                isPrimary = false
            )
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = when (product.iconType) {
                        "honey" -> "🍯"
                        "bread" -> "🥖"
                        "cookie" -> "🍪"
                        "tea" -> "🍵"
                        "cake" -> "🧁"
                        "oil" -> "🫒"
                        else -> "🌾"
                    },
                    fontSize = 24.sp
                )
                Text(
                    text = product.title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = product.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                product.calories?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "ارزش غذایی: ", color = WheatGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = it, color = TextPrimary, fontSize = 12.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "دسته‌بندی: ", color = WheatGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = product.category, color = TextPrimary, fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "واحد عرضه: ", color = WheatGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = product.unit, color = TextPrimary, fontSize = 12.sp)
                }
            }
        },
        containerColor = Color(0xF21F1A15),
        shape = RoundedCornerShape(20.dp)
    )
}
