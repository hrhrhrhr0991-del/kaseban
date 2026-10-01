package com.example.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.BoothPost
import com.example.data.model.Merchant
import com.example.data.model.MerchantProduct
import com.example.ui.theme.AppTheme
import com.example.ui.theme.PeydaFontFamily
import com.example.viewmodel.KasebanViewModel

/**
 * Comprehensive Merchant Booth Management & Configuration Studio
 * Offers full control over products, stories/posts, identity, policies, shipping & work hours.
 */
@Composable
fun BoothManagementDialog(
    onDismiss: () -> Unit,
    viewModel: KasebanViewModel
) {
    val colors = AppTheme.colors
    val context = LocalContext.current
    val merchants by viewModel.merchants.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()
    val userDisplayName by viewModel.userDisplayName.collectAsState()
    val userShopTitle by viewModel.userShopTitle.collectAsState()
    val userBio by viewModel.userBio.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    // Find or initialize user's single booth
    val myBooth = merchants.find { it.phone == userPhone || it.id == "booth_$userPhone" }

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("محصولات", "پست‌ها و دسترنج", "مشخصات غرفه", "تنظیمات و قوانین")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundGradient),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 26.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = colors.textPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "استودیو مدیریت و تنظیمات غرفه",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.5.sp,
                            color = colors.textPrimary
                        )
                        KasebanExclusiveLogoMark(size = 28.dp)
                    }
                }

                // Booth Overview Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(colors.primary, colors.border)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = myBooth?.title ?: (if (userShopTitle.isNotBlank()) userShopTitle else "غرفه $userDisplayName"),
                                        fontFamily = PeydaFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.5.sp,
                                        color = colors.primaryDark
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (myBooth?.isOnline != false) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (myBooth?.isOnline != false) "غرفه فعال" else "غرفه موقتاً بسته",
                                            fontFamily = PeydaFontFamily,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (myBooth?.isOnline != false) Color(0xFF15803D) else Color(0xFFB91C1C)
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = colors.textMuted,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = myBooth?.location ?: userLocation,
                                        fontFamily = PeydaFontFamily,
                                        fontSize = 11.5.sp,
                                        color = colors.textSecondary
                                    )
                                    Text(
                                        text = "• محصولات: ${(myBooth?.products?.size ?: 0)} عدد",
                                        fontFamily = PeydaFontFamily,
                                        fontSize = 11.5.sp,
                                        color = colors.textMuted
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.primaryGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Direct button to view the public booth page
                        Button(
                            onClick = {
                                val targetBooth = myBooth ?: viewModel.createOrUpdateMyBooth(
                                    title = userShopTitle,
                                    specialty = "محصولات و دسترنج محلی",
                                    location = userLocation,
                                    storyText = userBio
                                )
                                viewModel.selectMerchant(targetBooth)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primaryLight)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Preview,
                                    contentDescription = null,
                                    tint = colors.primaryDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "👁️ مشاهده زنده صفحه عمومی غرفه من در بازار",
                                    fontFamily = PeydaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = colors.primaryDark
                                )
                            }
                        }
                    }
                }

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = colors.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = colors.primary
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontFamily = PeydaFontFamily,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = if (selectedTab == index) colors.primary else colors.textSecondary,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Content of Selected Tab
                when (selectedTab) {
                    0 -> ProductsManagementTab(myBooth = myBooth, viewModel = viewModel)
                    1 -> PostsManagementTab(myBooth = myBooth, viewModel = viewModel)
                    2 -> BoothInfoTab(myBooth = myBooth, viewModel = viewModel, onSaved = {
                        Toast.makeText(context, "مشخصات غرفه با موفقیت به‌روزرسانی شد.", Toast.LENGTH_SHORT).show()
                    })
                    3 -> BoothPoliciesSettingsTab(myBooth = myBooth, viewModel = viewModel, onSaved = {
                        Toast.makeText(context, "تنظیمات فروش، ارسال و قوانین غرفه ذخیره شد.", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: Products Management (محصولات، قیمت، تخفیف و موجودی)
// -------------------------------------------------------------
@Composable
private fun ProductsManagementTab(
    myBooth: Merchant?,
    viewModel: KasebanViewModel
) {
    val colors = AppTheme.colors
    var showAddForm by remember { mutableStateOf(false) }

    var productTitle by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var originalPrice by remember { mutableStateOf("") }
    var productWeight by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("عمومی") }
    var productDescription by remember { mutableStateOf("") }

    val categories = listOf("عمومی", "نان و شیرینی", "عسل و مربا", "خشکبار و مغزها", "گیاهی و عرقیات", "لبنیات و پنیر", "روغن و چاشنی", "میوه و ارگانیک", "صنایع دستی")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Button(
                onClick = { showAddForm = !showAddForm },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Icon(
                    imageVector = if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showAddForm) "بستن فرم افزودن محصول" else "➕ افزودن محصول جدید با تمام مشخصات",
                    fontFamily = PeydaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }

        if (showAddForm) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(colors.primary, colors.borderSubtle)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "مشخصات و قیمت‌گذاری محصول جدید",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = colors.textPrimary
                        )

                        OutlinedTextField(
                            value = productTitle,
                            onValueChange = { productTitle = it },
                            label = { Text("نام محصول (مثال: عسل گون سبلان)", fontFamily = PeydaFontFamily, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border
                            ),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = productPrice,
                                onValueChange = { productPrice = it.filter { ch -> ch.isDigit() } },
                                label = { Text("قیمت فروش (تومان)", fontFamily = PeydaFontFamily, fontSize = 11.5.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.primary,
                                    unfocusedBorderColor = colors.border
                                ),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = originalPrice,
                                onValueChange = { originalPrice = it.filter { ch -> ch.isDigit() } },
                                label = { Text("قیمت قبل از تخفیف (اختیاری)", fontFamily = PeydaFontFamily, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.primary,
                                    unfocusedBorderColor = colors.border
                                ),
                                singleLine = true
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = productWeight,
                                onValueChange = { productWeight = it },
                                label = { Text("واحد و وزن (مثال: ۱ کیلوگرم / بسته ۶ تایی)", fontFamily = PeydaFontFamily, fontSize = 11.5.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.primary,
                                    unfocusedBorderColor = colors.border
                                ),
                                singleLine = true
                            )
                        }

                        // Category Chips
                        Text(
                            text = "دسته‌بندی محصول:",
                            fontFamily = PeydaFontFamily,
                            fontSize = 11.5.sp,
                            color = colors.textSecondary
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(categories) { cat ->
                                val isSelected = productCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) colors.primary else colors.surfaceCard)
                                        .border(1.dp, if (isSelected) colors.primary else colors.borderSubtle, RoundedCornerShape(8.dp))
                                        .clickable { productCategory = cat }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        fontFamily = PeydaFontFamily,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White else colors.textPrimary
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = productDescription,
                            onValueChange = { productDescription = it },
                            label = { Text("توضیحات، طعم و نکات کیفیت محصول...", fontFamily = PeydaFontFamily, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border
                            ),
                            maxLines = 3
                        )

                        Button(
                            onClick = {
                                val priceLong = productPrice.toLongOrNull() ?: 0L
                                val origPriceLong = originalPrice.toLongOrNull() ?: 0L
                                if (productTitle.isNotBlank() && priceLong > 0) {
                                    viewModel.addProductToMyBooth(
                                        title = productTitle,
                                        weight = productWeight.ifBlank { "۱ واحد" },
                                        price = priceLong,
                                        originalPrice = origPriceLong,
                                        category = productCategory,
                                        description = productDescription
                                    )
                                    productTitle = ""
                                    productPrice = ""
                                    originalPrice = ""
                                    productWeight = ""
                                    productDescription = ""
                                    showAddForm = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primaryDark)
                        ) {
                            Text(
                                text = "ثبت نهایی محصول در غرفه",
                                fontFamily = PeydaFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        val products = myBooth?.products ?: emptyList()
        if (products.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = colors.textMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "هنوز محصولی در غرفه خود ثبت نکرده‌اید.",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "با دکمه بالا محصولات خود را همراه با قیمت و واحد ثبت کنید تا در بازار نمایش داده شوند.",
                            fontFamily = PeydaFontFamily,
                            fontSize = 11.5.sp,
                            color = colors.textMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(products, key = { it.id }) { prod ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(colors.borderSubtle, colors.border)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (prod.isAvailable) colors.primaryLight else Color(0xFFF3F4F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = if (prod.isAvailable) colors.primaryDark else Color(0xFF9CA3AF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = prod.title,
                                        fontFamily = PeydaFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = colors.textPrimary
                                    )
                                    if (prod.category.isNotBlank() && prod.category != "عمومی") {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(colors.primaryLight)
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = prod.category,
                                                fontFamily = PeydaFontFamily,
                                                fontSize = 9.5.sp,
                                                color = colors.primaryDark
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${prod.weight} • ${prod.price} تومان",
                                        fontFamily = PeydaFontFamily,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.primaryDark
                                    )
                                    if (prod.originalPrice > prod.price) {
                                        Text(
                                            text = "${prod.originalPrice}",
                                            fontFamily = PeydaFontFamily,
                                            fontSize = 10.5.sp,
                                            color = colors.textMuted,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Quick Availability Toggle
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (prod.isAvailable) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                                    .clickable { viewModel.toggleProductAvailability(prod.id) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (prod.isAvailable) "موجود" else "ناموجود",
                                    fontFamily = PeydaFontFamily,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (prod.isAvailable) Color(0xFF15803D) else Color(0xFFB91C1C)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.removeProductFromMyBooth(prod.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف محصول",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: Posts & Stories Management (پست‌ها، عکس و داستان دسترنج)
// -------------------------------------------------------------
@Composable
private fun PostsManagementTab(
    myBooth: Merchant?,
    viewModel: KasebanViewModel
) {
    val colors = AppTheme.colors
    var showAddPostForm by remember { mutableStateOf(false) }

    var postTitle by remember { mutableStateOf("") }
    var postText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri?.toString()
    }

    // Curated artisan preset photos
    val presetImages = listOf(
        "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500" to "نان داغ و شیرینی سنتی",
        "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=500" to "عسل طبیعی کوهستان",
        "https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=500" to "سفال و سرامیک لالجین",
        "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=500" to "زعفران و هل قائنات",
        "https://images.unsplash.com/photo-1530595467537-0b5996c41f2d?w=500" to "خشکبار و گردوی تویسرکان",
        "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=500" to "صنایع دستی و بافت محلی"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Button(
                onClick = { showAddPostForm = !showAddPostForm },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Icon(
                    imageVector = if (showAddPostForm) Icons.Default.Close else Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showAddPostForm) "بستن فرم پست" else "📸 انتشار پست و خبر دسترنج (عکس و متن)",
                    fontFamily = PeydaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }

        if (showAddPostForm) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(colors.primary, colors.borderSubtle)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "ایجاد پست و داستان تازه غرفه",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = colors.textPrimary
                        )

                        OutlinedTextField(
                            value = postTitle,
                            onValueChange = { postTitle = it },
                            label = { Text("عنوان پست (مثال: نوبت پخت تازه نان محلی)", fontFamily = PeydaFontFamily, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = postText,
                            onValueChange = { postText = it },
                            label = { Text("متن ماجرای کیفیت، تاریخچه پخت یا برداشت...", fontFamily = PeydaFontFamily, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border
                            ),
                            maxLines = 4
                        )

                        // Photo Picker Section
                        Text(
                            text = "عکس دسترنج و غرفه:",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primaryLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = colors.primaryDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "گالری گوشی",
                                    fontFamily = PeydaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = colors.primaryDark
                                )
                            }

                            if (selectedImageUri != null) {
                                Text(
                                    text = "✓ تصویر انتخاب شد",
                                    fontFamily = PeydaFontFamily,
                                    fontSize = 11.sp,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = "(حذف)",
                                    fontFamily = PeydaFontFamily,
                                    fontSize = 11.sp,
                                    color = Color(0xFFEF4444),
                                    modifier = Modifier.clickable { selectedImageUri = null }
                                )
                            }
                        }

                        // Preset Photos Row
                        Text(
                            text = "یا انتخاب سریع عکس آماده باکیفیت:",
                            fontFamily = PeydaFontFamily,
                            fontSize = 11.sp,
                            color = colors.textMuted
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(presetImages) { (url, label) ->
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            width = if (selectedImageUri == url) 2.dp else 1.dp,
                                            color = if (selectedImageUri == url) colors.primary else colors.borderSubtle,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedImageUri = url }
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = label,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (postText.isNotBlank()) {
                                    viewModel.addPostToMyBooth(postTitle, postText, selectedImageUri)
                                    postTitle = ""
                                    postText = ""
                                    selectedImageUri = null
                                    showAddPostForm = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primaryDark)
                        ) {
                            Text(
                                text = "انتشار پست در صفحه غرفه",
                                fontFamily = PeydaFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        val posts = myBooth?.posts ?: emptyList()
        if (posts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DynamicFeed,
                            contentDescription = null,
                            tint = colors.textMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "هنوز پستی در غرفه خود منتشر نکرده‌اید.",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "با ارسال پست‌های تصویری از مراحل تولید، بسته‌بندی یا مواد اولیه، اعتماد خریداران را جلب کنید.",
                            fontFamily = PeydaFontFamily,
                            fontSize = 11.5.sp,
                            color = colors.textMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(posts, key = { it.id }) { post ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(colors.borderSubtle, colors.border)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = post.title.ifBlank { "خبر غرفه" },
                                fontFamily = PeydaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = colors.primaryDark
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = post.date,
                                    fontFamily = PeydaFontFamily,
                                    fontSize = 10.5.sp,
                                    color = colors.textMuted
                                )
                                IconButton(
                                    onClick = { viewModel.removePostFromMyBooth(post.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف پست",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        if (post.imageUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(10.dp))
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
                            fontFamily = PeydaFontFamily,
                            fontSize = 12.5.sp,
                            color = colors.textPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: Booth Information (مشخصات، هویت، شهر و داستان غرفه)
// -------------------------------------------------------------
@Composable
private fun BoothInfoTab(
    myBooth: Merchant?,
    viewModel: KasebanViewModel,
    onSaved: () -> Unit
) {
    val colors = AppTheme.colors
    val userShopTitle by viewModel.userShopTitle.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val userBio by viewModel.userBio.collectAsState()

    var title by remember { mutableStateOf(myBooth?.title ?: userShopTitle) }
    var specialty by remember { mutableStateOf(myBooth?.specialty ?: "محصولات و دسترنج محلی") }
    var location by remember { mutableStateOf(myBooth?.location ?: userLocation) }
    var address by remember { mutableStateOf(myBooth?.address ?: "") }
    var storyText by remember { mutableStateOf(myBooth?.storyText ?: userBio) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(colors.primary, colors.borderSubtle)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "مشخصات هویتی و معرفی غرفه",
                        fontFamily = PeydaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = colors.textPrimary
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان غرفه / نام تجاری کارگاه", fontFamily = PeydaFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = specialty,
                        onValueChange = { specialty = it },
                        label = { Text("رسته تخصصی (مثال: نان و شیرینی سنتی / عسل طبیعی)", fontFamily = PeydaFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("شهر و استان (مثال: قزوین / محله دباغان)", fontFamily = PeydaFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("آدرس دقیق کارگاه یا فروشگاه جهت تحویل حضوری (اختیاری)", fontFamily = PeydaFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = storyText,
                        onValueChange = { storyText = it },
                        label = { Text("داستان کیفیت، مواد اولیه و معرفی دسترنج غرفه", fontFamily = PeydaFontFamily) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        ),
                        maxLines = 5
                    )

                    Button(
                        onClick = {
                            viewModel.createOrUpdateMyBooth(
                                title = title,
                                specialty = specialty,
                                location = location,
                                storyText = storyText,
                                address = address,
                                workHours = myBooth?.workHours ?: "همه‌روزه از ۸ صبح تا ۱۰ شب",
                                deliveryMethods = myBooth?.deliveryMethods ?: "پست پیشتاز، تیپاکس، پیک شهری",
                                freeShippingThreshold = myBooth?.freeShippingThreshold ?: 0L,
                                minOrderAmount = myBooth?.minOrderAmount ?: 0L,
                                guaranteePolicy = myBooth?.guaranteePolicy ?: "ضمانت اصالت و بازگشت کامل وجه در صورت عدم رضایت",
                                socialTelegram = myBooth?.socialTelegram ?: "",
                                socialWhatsapp = myBooth?.socialWhatsapp ?: "",
                                isOnline = myBooth?.isOnline ?: true
                            )
                            onSaved()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text(
                            text = "ذخیره مشخصات غرفه",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: Booth Policies & Sales Settings (ساعات کاری، ارسال، ضمانت و شبکه‌ها)
// -------------------------------------------------------------
@Composable
private fun BoothPoliciesSettingsTab(
    myBooth: Merchant?,
    viewModel: KasebanViewModel,
    onSaved: () -> Unit
) {
    val colors = AppTheme.colors
    val userShopTitle by viewModel.userShopTitle.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val userBio by viewModel.userBio.collectAsState()

    var isOnline by remember { mutableStateOf(myBooth?.isOnline ?: true) }
    var workHours by remember { mutableStateOf(myBooth?.workHours ?: "همه‌روزه از ۸ صبح تا ۱۰ شب") }
    var deliveryMethods by remember { mutableStateOf(myBooth?.deliveryMethods ?: "پست پیشتاز، تیپاکس، پیک شهری") }
    var freeShippingThreshold by remember { mutableStateOf(if ((myBooth?.freeShippingThreshold ?: 0L) > 0) "${myBooth?.freeShippingThreshold}" else "") }
    var minOrderAmount by remember { mutableStateOf(if ((myBooth?.minOrderAmount ?: 0L) > 0) "${myBooth?.minOrderAmount}" else "") }
    var guaranteePolicy by remember { mutableStateOf(myBooth?.guaranteePolicy ?: "ضمانت اصالت و بازگشت کامل وجه در صورت عدم رضایت خریدار") }
    var socialTelegram by remember { mutableStateOf(myBooth?.socialTelegram ?: "") }
    var socialWhatsapp by remember { mutableStateOf(myBooth?.socialWhatsapp ?: "") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Online / Vacation Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(colors.primaryLight, colors.borderSubtle)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "وضعیت فعالیت غرفه (پذیرش سفارش)",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isOnline) "غرفه باز و آماده دریافت سفارشات است" else "غرفه موقتاً بسته و در حالت استراحت است",
                            fontFamily = PeydaFontFamily,
                            fontSize = 11.5.sp,
                            color = if (isOnline) Color(0xFF15803D) else Color(0xFFB91C1C)
                        )
                    }

                    Switch(
                        checked = isOnline,
                        onCheckedChange = { isOnline = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = colors.primary
                        )
                    )
                }
            }
        }

        // Work Hours & Support
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(colors.borderSubtle, colors.border)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "ساعات کاری و پاسخگویی",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = colors.textPrimary
                        )
                    }

                    OutlinedTextField(
                        value = workHours,
                        onValueChange = { workHours = it },
                        label = { Text("مثال: شنبه تا پنج‌شنبه از ساعت ۸ تا ۲۲", fontFamily = PeydaFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // Delivery & Shipping Methods
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(colors.borderSubtle, colors.border)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "روش‌های ارسال و شرایط حمل‌ونقل",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = colors.textPrimary
                        )
                    }

                    OutlinedTextField(
                        value = deliveryMethods,
                        onValueChange = { deliveryMethods = it },
                        label = { Text("روش‌های ارسال (مثال: پست پیشتاز، تیپاکس، پیک محلی)", fontFamily = PeydaFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        ),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = freeShippingThreshold,
                            onValueChange = { freeShippingThreshold = it.filter { ch -> ch.isDigit() } },
                            label = { Text("خرید برای ارسال رایگان (تومان)", fontFamily = PeydaFontFamily, fontSize = 10.5.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = minOrderAmount,
                            onValueChange = { minOrderAmount = it.filter { ch -> ch.isDigit() } },
                            label = { Text("حداقل مبلغ سفارش (تومان)", fontFamily = PeydaFontFamily, fontSize = 10.5.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border
                            ),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Guarantee & Trust Policy
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(colors.borderSubtle, colors.border)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "ضمانت کیفیت و مرجوعی کالا",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = colors.textPrimary
                        )
                    }

                    OutlinedTextField(
                        value = guaranteePolicy,
                        onValueChange = { guaranteePolicy = it },
                        label = { Text("متن ضمانت و تعهد کیفیت به خریدار...", fontFamily = PeydaFontFamily) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(85.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.border
                        ),
                        maxLines = 3
                    )
                }
            }
        }

        // Social & Support Channels
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(colors.borderSubtle, colors.border)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "راه‌های ارتباطی و پشتیبانی مشتریان",
                            fontFamily = PeydaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = colors.textPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = socialWhatsapp,
                            onValueChange = { socialWhatsapp = it },
                            label = { Text("شماره واتساپ پشتیبانی", fontFamily = PeydaFontFamily, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = socialTelegram,
                            onValueChange = { socialTelegram = it },
                            label = { Text("آیدی تلگرام / اینستاگرام", fontFamily = PeydaFontFamily, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border
                            ),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Save All Settings Button
        item {
            Button(
                onClick = {
                    viewModel.createOrUpdateMyBooth(
                        title = myBooth?.title ?: userShopTitle,
                        specialty = myBooth?.specialty ?: "محصولات محلی",
                        location = myBooth?.location ?: userLocation,
                        storyText = myBooth?.storyText ?: userBio,
                        address = myBooth?.address ?: "",
                        workHours = workHours,
                        deliveryMethods = deliveryMethods,
                        freeShippingThreshold = freeShippingThreshold.toLongOrNull() ?: 0L,
                        minOrderAmount = minOrderAmount.toLongOrNull() ?: 0L,
                        guaranteePolicy = guaranteePolicy,
                        socialTelegram = socialTelegram,
                        socialWhatsapp = socialWhatsapp,
                        isOnline = isOnline
                    )
                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "ذخیره تمام تنظیمات و قوانین غرفه",
                        fontFamily = PeydaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
