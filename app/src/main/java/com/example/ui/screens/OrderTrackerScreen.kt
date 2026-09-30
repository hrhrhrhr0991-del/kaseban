package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.PersianUtils
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
fun OrderTrackerScreen(
    viewModel: GandomaViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.allOrders.collectAsState()
    var searchOrderId by remember { mutableStateOf("") }
    var selectedOrder by remember { mutableStateOf<Order?>(null) }

    // If no order is selected, default to the latest one
    val activeOrder = selectedOrder ?: orders.firstOrNull()

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
                            text = "پیگیری لحظه‌ای سفارشات | gandoma.ir",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        GlassBadge(text = "سامانه زنده 🟢", color = StatusSuccess)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "وضعیت پخت، آماده‌سازی و ارسال سفارشات ارگانیک را در سامانه آنلاین gandoma.ir دنبال کنید.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            // Search by Order ID
            item {
                GlassTextField(
                    value = searchOrderId,
                    onValueChange = { query ->
                        searchOrderId = query
                        val found = orders.find { it.orderId.equals(query.trim(), ignoreCase = true) }
                        if (found != null) {
                            selectedOrder = found
                        }
                    },
                    label = "جستجو با کد سفارش (مثال: GND-7821)",
                    placeholder = "GND-...",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "جستجو",
                            tint = WheatGold
                        )
                    }
                )
            }

            // Active Order Live Stepped Timeline Card
            if (activeOrder != null) {
                item {
                    ActiveOrderTimelineCard(order = activeOrder)
                }
            } else {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = SurfaceDarkGlass
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📦", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "هنوز سفارشی ثبت نکرده‌اید",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "پس از ثبت سفارش در دایرکت، وضعیت آن در اینجا نمایش داده می‌شود.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Order History List
            if (orders.isNotEmpty()) {
                item {
                    Text(
                        text = "تاریخچه سفارش‌های شما (${PersianUtils.toPersianDigits(orders.size.toString())})",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(orders) { order ->
                    val isSelected = activeOrder?.orderId == order.orderId
                    OrderHistoryItem(
                        order = order,
                        isSelected = isSelected,
                        onClick = { selectedOrder = order }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveOrderTimelineCard(order: Order) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = Color(0xF8FFFFFF),
        borderColor = GlassBorder,
        borderWidth = 1.2.dp,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: ID and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "سفارش شماره ${order.orderId}",
                        color = TextPrimary,
                        fontSize = 16.sp,
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

            Text(
                text = order.status.descriptionPersian,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            HorizontalDivider(color = Color(0x1F000000), thickness = 1.dp)

            // Step-by-Step Progress Timeline
            TimelineSteps(currentStep = order.status.stepIndex)

            HorizontalDivider(color = Color(0x1F000000), thickness = 1.dp)

            // Order summary details
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "اقلام سفارش:", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = order.itemsSummary,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "شیوه دریافت:", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = order.deliveryMethod,
                        color = WheatGold,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "مبلغ کل پرداختی:", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = PersianUtils.formatPrice(order.totalPrice),
                        color = WheatGoldDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "آدرس تحویل:", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = order.address,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineSteps(currentStep: Int) {
    val steps = listOf(
        1 to "ثبت سفارش",
        2 to "تایید پرداخت",
        3 to "آماده‌سازی",
        4 to "ارسال پیک/پست",
        5 to "تحویل شد"
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        steps.forEach { (stepNumber, stepTitle) ->
            val isPassed = currentStep >= stepNumber
            val isCurrent = currentStep == stepNumber

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPassed) WheatGold else Color(0xFFF3F4F6)
                        )
                        .border(
                            1.dp,
                            if (isCurrent) WheatGoldDark else Color.Transparent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPassed) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = PersianUtils.toPersianDigits(stepNumber.toString()),
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Text(
                    text = stepTitle,
                    color = if (isCurrent) WheatGoldDark else if (isPassed) TextPrimary else TextMuted,
                    fontSize = 12.sp,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Normal
                )

                if (isCurrent) {
                    GlassBadge(text = "در حال انجام", color = WheatGold)
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryItem(
    order: Order,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = if (isSelected) Color(0x33F59E0B) else SurfaceDarkGlass,
        borderColor = if (isSelected) WheatGold else GlassBorderSubtle,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "سفارش ${order.orderId}",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = PersianUtils.formatDate(order.createdAt),
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = PersianUtils.formatPrice(order.totalPrice),
                    color = WheatGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            GlassBadge(
                text = order.status.titlePersian,
                color = order.status.badgeColor
            )
        }
    }
}
