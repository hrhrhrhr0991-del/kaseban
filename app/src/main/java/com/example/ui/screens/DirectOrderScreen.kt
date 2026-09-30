package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.NavScreen
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
fun DirectOrderScreen(
    viewModel: GandomaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cart by viewModel.cart.collectAsState()
    val directStep by viewModel.directStep.collectAsState()
    val deliveryMethod by viewModel.selectedDeliveryMethod.collectAsState()
    val customerName by viewModel.customerName.collectAsState()
    val customerPhone by viewModel.customerPhone.collectAsState()
    val customerAddress by viewModel.customerAddress.collectAsState()
    val payerName by viewModel.payerName.collectAsState()
    val paymentSlipCode by viewModel.paymentSlipCode.collectAsState()
    val orderNotes by viewModel.orderNotes.collectAsState()
    val lastOrder by viewModel.lastCreatedOrder.collectAsState()

    val storeCardNumber = "۶۰۳۷-۹۹۷۵-۸۳۱۴-۲۰۰۹"

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp, top = 8.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header message
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0x33F59E0B))
                            .border(1.dp, GlassBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌾", fontSize = 22.sp)
                    }

                    Column {
                        Text(
                            text = "دایرکت ثبت سفارش گندما | gandoma.ir",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(StatusSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "پشتیبانی آنلاین و هوشمند",
                                color = StatusSuccess,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Step 1: Products Summary Bubble
            item {
                DirectChatBubble(
                    isFromShop = true,
                    time = "هم‌اکنون",
                    title = "محصولات انتخابی شما در سبد خرید:"
                ) {
                    if (cart.isEmpty() && directStep != 5) {
                        Text(
                            text = "سبد خرید شما در حال حاضر خالی است. لطفاً از بخش فروشگاه محصولات مورد نظر را انتخاب نمایید.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        GlassButton(
                            text = "رفتن به فروشگاه گندما",
                            onClick = { viewModel.navigateTo(NavScreen.STORE) },
                            isPrimary = true
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            cart.values.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${item.product.title} (x${PersianUtils.toPersianDigits(item.quantity.toString())})",
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = PersianUtils.formatPrice(item.totalPrice),
                                        color = WheatGold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            HorizontalDivider(color = Color(0x24FFFFFF), thickness = 1.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "مجموع مبلغ سفارش:",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = PersianUtils.formatPrice(viewModel.cartTotalPrice),
                                    color = WheatGoldLight,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }

            // Step 2: Delivery Method Selection
            if (cart.isNotEmpty() || directStep >= 2) {
                item {
                    DirectChatBubble(
                        isFromShop = true,
                        time = "مرحله ۱ از ۳",
                        title = "روش تحویل سفارش را تعیین فرمایید:"
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            val deliveryOptions = listOf(
                                Triple(
                                    "ارسال با پیک اکسپرس گندما",
                                    "تحویل فوری در همان روز درب منزل (تهران)",
                                    Icons.Default.LocalShipping
                                ),
                                Triple(
                                    "تحویل حضوری در شعبه گندما",
                                    "دریافت رایگان و بدون معطلی از فروشگاه مرکزی",
                                    Icons.Default.Store
                                ),
                                Triple(
                                    "پست پیشتاز سراسری",
                                    "بسته‌بندی بهداشتی ضربه‌گیر ویژه سراسر کشور (۲ الی ۳ روز)",
                                    Icons.Default.LocalShipping
                                )
                            )

                            deliveryOptions.forEach { (title, subtitle, icon) ->
                                val isSelected = deliveryMethod == title
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) Color(0x33F59E0B) else Color(0x1FFFFFFF)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) WheatGold else GlassBorderSubtle,
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable { viewModel.setDeliveryMethod(title) }
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isSelected) WheatGold else TextSecondary,
                                            modifier = Modifier.size(22.dp)
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = title,
                                                color = if (isSelected) WheatGoldLight else TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = subtitle,
                                                color = TextMuted,
                                                fontSize = 11.sp
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "انتخاب شده",
                                                tint = WheatGold,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Step 3: Recipient Information
            if (cart.isNotEmpty() || directStep >= 3) {
                item {
                    DirectChatBubble(
                        isFromShop = true,
                        time = "مرحله ۲ از ۳",
                        title = "مشخصات تحویل‌گیرنده:"
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            GlassTextField(
                                value = customerName,
                                onValueChange = {
                                    viewModel.updateCustomerInfo(it, customerPhone, customerAddress)
                                },
                                label = "نام و نام خانوادگی خریدار",
                                placeholder = "مثال: علی احمدی",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = WheatGold
                                    )
                                }
                            )

                            GlassTextField(
                                value = customerPhone,
                                onValueChange = {
                                    viewModel.updateCustomerInfo(customerName, it, customerAddress)
                                },
                                label = "شماره تماس همراه",
                                placeholder = "۰۹۱۲۳۴۵۶۷۸۹",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = WheatGold
                                    )
                                }
                            )

                            GlassTextField(
                                value = customerAddress,
                                onValueChange = {
                                    viewModel.updateCustomerInfo(customerName, customerPhone, it)
                                },
                                label = "آدرس دقیق دریافت سفارش",
                                placeholder = "شهر، خیابان اصلی، کوچه، پلاک، زنگ یا واحد",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = WheatGold
                                    )
                                },
                                singleLine = false
                            )
                        }
                    }
                }
            }

            // Step 4: Payment Details (Card to Card & Slip confirmation)
            if (cart.isNotEmpty() || directStep >= 4) {
                item {
                    DirectChatBubble(
                        isFromShop = true,
                        time = "مرحله ۳ از ۳",
                        title = "اطلاعات واریز و تایید پرداخت:"
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Card Info Card with 1-tap Copy
                            GlassCard(
                                shape = RoundedCornerShape(14.dp),
                                backgroundColor = Color(0xFFFEF3C7),
                                borderColor = WheatGold,
                                elevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "شماره کارت رسمی فروشگاه گندما (gandoma.ir)",
                                            color = WheatGoldDark,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(Color(0x33FFFFFF))
                                                .clickable {
                                                    val clipboard =
                                                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(
                                                        ClipData.newPlainText("Card Number", "6037997583142009")
                                                    )
                                                    Toast.makeText(context, "شماره کارت کپی شد", Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "کپی شماره کارت",
                                                tint = WheatGold,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = storeCardNumber,
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 2.sp
                                    )
                                    Text(
                                        text = "به‌نام: فروشگاه محصولات ارگانیک گندما (بانک ملی)",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            GlassTextField(
                                value = payerName,
                                onValueChange = {
                                    viewModel.updatePaymentInfo(it, paymentSlipCode, orderNotes)
                                },
                                label = "نام صاحب حساب واریز کننده",
                                placeholder = "مثال: مریم محمدی",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = WheatGold
                                    )
                                }
                            )

                            GlassTextField(
                                value = paymentSlipCode,
                                onValueChange = {
                                    viewModel.updatePaymentInfo(payerName, it, orderNotes)
                                },
                                label = "شماره پیگیری فیش یا ۴ رقم آخر کارت",
                                placeholder = "مثال: پیگیری ۹۴۸۲ یا ۴ رقم ۵۲۱۸",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Payment,
                                        contentDescription = null,
                                        tint = WheatGold
                                    )
                                }
                            )

                            GlassTextField(
                                value = orderNotes,
                                onValueChange = {
                                    viewModel.updatePaymentInfo(payerName, paymentSlipCode, it)
                                },
                                label = "توضیحات و هماهنگی تحویل (اختیاری)",
                                placeholder = "مثلاً: لطفاً صبح ارسال شود یا زنگ زده شود"
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Submit Button
                            GlassButton(
                                text = "ثبت نهایی سفارش و ارسال به فروشنده 🚀",
                                onClick = {
                                    viewModel.submitDirectOrder()
                                    Toast.makeText(
                                        context,
                                        "سفارش با موفقیت ثبت شد و به پنل فروشنده ارسال گردید!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
                                isPrimary = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Step 5: Success Celebration Bubble
            if (directStep == 5 && lastOrder != null) {
                item {
                    DirectSuccessCard(
                        order = lastOrder!!,
                        onGoToTracker = { viewModel.navigateTo(NavScreen.TRACKER) },
                        onBackToStore = {
                            viewModel.setDirectStep(1)
                            viewModel.navigateTo(NavScreen.STORE)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DirectChatBubble(
    isFromShop: Boolean,
    time: String,
    title: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromShop) Arrangement.Start else Arrangement.End
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(0.96f),
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 20.dp
            ),
            backgroundColor = SurfaceDarkGlass,
            borderColor = GlassBorderSubtle
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = WheatGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = time,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                content()
            }
        }
    }
}

@Composable
private fun DirectSuccessCard(
    order: com.example.data.model.Order,
    onGoToTracker: () -> Unit,
    onBackToStore: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = Color(0xF8FFFFFF),
        borderColor = StatusSuccess,
        borderWidth = 1.5.dp,
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0x2610B981)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "ثبت شد",
                    tint = StatusSuccess,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                text = "سفارش شما با موفقیت ثبت شد!",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "کد رهگیری اختصاصی در سامانه gandoma.ir:",
                color = TextSecondary,
                fontSize = 13.sp
            )

            GlassBadge(
                text = order.orderId,
                color = WheatGoldDark,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "اطلاعات سفارش شما به پنل مدیریت فروشنده ارسال گردید و در سرورهای مرکزی gandoma.ir ذخیره شد.",
                color = TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            GlassButton(
                text = "پیگیری لحظه‌ای وضعیت سفارش 📦",
                onClick = onGoToTracker,
                isPrimary = true,
                modifier = Modifier.fillMaxWidth()
            )

            GlassButton(
                text = "بازگشت به فروشگاه",
                onClick = onBackToStore,
                isPrimary = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
