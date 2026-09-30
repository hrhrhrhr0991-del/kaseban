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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.PersianUtils
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.SurfaceDarkGlass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WheatGold
import com.example.ui.theme.WheatGoldDark
import com.example.ui.theme.WheatGoldLight
import com.example.viewmodel.GandomaViewModel

@Composable
fun SellerAdminScreen(
    viewModel: GandomaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val orders by viewModel.allOrders.collectAsState()
    val sellerFilter by viewModel.sellerFilter.collectAsState()

    var showAddProductDialog by remember { mutableStateOf(false) }

    val filteredOrders = if (sellerFilter == null) {
        orders
    } else {
        orders.filter { it.status == sellerFilter }
    }

    val totalSales = orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.totalPrice }
    val pendingCount = orders.count { it.status == OrderStatus.PENDING_REVIEW }
    val activeCount = orders.count { it.status == OrderStatus.PAYMENT_VERIFIED || it.status == OrderStatus.PREPARING || it.status == OrderStatus.SHIPPED }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp, top = 8.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dashboard Header & Stats
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = WheatGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "پنل مدیریت فروشنده | gandoma.ir",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        GlassBadge(text = "مدیریت زنده", color = StatusWarning)
                    }

                    // Stat Cards Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SellerStatCard(
                            title = "مجموع فروش",
                            value = PersianUtils.formatPrice(totalSales),
                            icon = Icons.Default.AttachMoney,
                            color = WheatGold,
                            modifier = Modifier.weight(1.3f)
                        )
                        SellerStatCard(
                            title = "در انتظار واریز",
                            value = "${PersianUtils.toPersianDigits(pendingCount.toString())} سفارش",
                            icon = Icons.Default.PendingActions,
                            color = StatusPending,
                            modifier = Modifier.weight(1f)
                        )
                        SellerStatCard(
                            title = "در دست اقدام",
                            value = "${PersianUtils.toPersianDigits(activeCount.toString())} فعال",
                            icon = Icons.Default.LocalShipping,
                            color = StatusInfo,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Quick Add Product Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مدیریت سفارش‌ها (${PersianUtils.toPersianDigits(filteredOrders.size.toString())})",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    GlassButton(
                        text = "افزودن محصول جدید +",
                        onClick = { showAddProductDialog = true },
                        isPrimary = false
                    )
                }
            }

            // Status Filter Tabs
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        val isAllSelected = sellerFilter == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isAllSelected) WheatGold else Color(0x24FFFFFF))
                                .clickable { viewModel.setSellerFilter(null) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "همه (${PersianUtils.toPersianDigits(orders.size.toString())})",
                                color = if (isAllSelected) BackgroundDark else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    items(OrderStatus.values().filter { it != OrderStatus.CANCELLED }) { status ->
                        val isSelected = sellerFilter == status
                        val count = orders.count { it.status == status }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) status.badgeColor else Color(0x1AFFFFFF))
                                .border(1.dp, if (isSelected) status.badgeColor else GlassBorderSubtle, RoundedCornerShape(14.dp))
                                .clickable { viewModel.setSellerFilter(status) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "${status.titlePersian} (${PersianUtils.toPersianDigits(count.toString())})",
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Orders List
            items(filteredOrders) { order ->
                SellerOrderCard(
                    order = order,
                    onStatusChange = { newStatus ->
                        viewModel.updateOrderStatus(order.orderId, newStatus)
                        Toast.makeText(
                            context,
                            "وضعیت سفارش ${order.orderId} به «${newStatus.titlePersian}» تغییر یافت",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            if (filteredOrders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "سفارشی در این وضعیت وجود ندارد",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Add Product Dialog
        if (showAddProductDialog) {
            AddProductDialog(
                onDismiss = { showAddProductDialog = false },
                onAdd = { newProduct ->
                    viewModel.addNewProduct(newProduct)
                    showAddProductDialog = false
                    Toast.makeText(context, "محصول جدید به فروشگاه اضافه شد!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun SellerStatCard(
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
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                color = TextMuted,
                fontSize = 10.sp
            )
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SellerOrderCard(
    order: Order,
    onStatusChange: (OrderStatus) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color(0xF8FFFFFF),
        borderColor = order.status.badgeColor.copy(alpha = 0.4f),
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: ID and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "سفارش ${order.orderId}",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = PersianUtils.formatDate(order.createdAt),
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                GlassBadge(
                    text = order.status.titlePersian,
                    color = order.status.badgeColor
                )
            }

            HorizontalDivider(color = Color(0x1F000000), thickness = 1.dp)

            // Customer and Payment verification info
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "مشتری:", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = "${order.customerName} (${order.phoneNumber})",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "نام واریز کننده:", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = order.payerName,
                        color = WheatGoldDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "شماره پیگیری فیش:", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = order.trackingNumber,
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "روش تحویل:", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = order.deliveryMethod,
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "آدرس گیرنده:", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = order.address,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "مبلغ واریزی:", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = PersianUtils.formatPrice(order.totalPrice),
                        color = WheatGoldDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            HorizontalDivider(color = Color(0x1F000000), thickness = 1.dp)

            // Status Control Buttons (فروشنده سفارش را کنترل کنه)
            Text(
                text = "تغییر وضعیت مرحله‌ای سفارش:",
                color = TextMuted,
                fontSize = 11.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (order.status == OrderStatus.PENDING_REVIEW) {
                    GlassButton(
                        text = "تایید واریز ✓",
                        onClick = { onStatusChange(OrderStatus.PAYMENT_VERIFIED) },
                        isPrimary = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (order.status == OrderStatus.PAYMENT_VERIFIED) {
                    GlassButton(
                        text = "شروع آماده‌سازی 🌾",
                        onClick = { onStatusChange(OrderStatus.PREPARING) },
                        isPrimary = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (order.status == OrderStatus.PREPARING) {
                    GlassButton(
                        text = "تحویل به پیک 🛵",
                        onClick = { onStatusChange(OrderStatus.SHIPPED) },
                        isPrimary = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (order.status == OrderStatus.SHIPPED) {
                    GlassButton(
                        text = "تکمیل و تحویل شد ✅",
                        onClick = { onStatusChange(OrderStatus.DELIVERED) },
                        isPrimary = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddProductDialog(
    onDismiss: () -> Unit,
    onAdd: (Product) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("نان سنتی و خمیرترش") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("عدد") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            GlassButton(
                text = "افزودن محصول",
                onClick = {
                    val price = priceStr.toLongOrNull() ?: 50000
                    val newProduct = Product(
                        id = "custom_${System.currentTimeMillis()}",
                        title = title.ifBlank { "محصول دست‌ساز گندما" },
                        category = category,
                        description = description.ifBlank { "تهیه شده از مواد مرغوب و ارگانیک گندما" },
                        price = price,
                        unit = unit,
                        tag = "جدید",
                        iconType = "wheat"
                    )
                    onAdd(newProduct)
                },
                isPrimary = true
            )
        },
        dismissButton = {
            GlassButton(text = "انصراف", onClick = onDismiss, isPrimary = false)
        },
        title = {
            Text(
                text = "افزودن محصول جدید به گندما",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassTextField(value = title, onValueChange = { title = it }, label = "نام محصول")
                GlassTextField(value = priceStr, onValueChange = { priceStr = it }, label = "قیمت به تومان (مثال: ۸۵۰۰۰)")
                GlassTextField(value = unit, onValueChange = { unit = it }, label = "واحد (بسته، عدد، کیلو)")
                GlassTextField(value = description, onValueChange = { description = it }, label = "توضیحات کوتاه")
            }
        },
        containerColor = Color(0xF21C1813),
        shape = RoundedCornerShape(20.dp)
    )
}
