package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
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

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DirectChatMessage
import com.example.data.model.Merchant
import com.example.ui.components.FluidAnimatedEntry
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconContainer
import com.example.ui.components.MerchantAvatar
import com.example.ui.components.PersianUtils
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GlassBorderRefractionBrush
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.SurfaceGlassCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrustGreen
import com.example.ui.theme.VazirmatnFontFamily
import com.example.viewmodel.KasebanViewModel

import androidx.compose.material.icons.filled.Forum
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.components.KasebanScreen

@Composable
fun ChatScreen(
    viewModel: KasebanViewModel,
    modifier: Modifier = Modifier
) {
    val activeMerchant by viewModel.activeChatMerchant.collectAsState()
    val merchants by viewModel.merchants.collectAsState()
    val chatMessagesMap by viewModel.chatMessagesMap.collectAsState()

    if (activeMerchant != null) {
        val merchant = activeMerchant!!
        val messages = chatMessagesMap[merchant.id] ?: emptyList()
        DirectChatDetailView(
            merchant = merchant,
            messages = messages,
            onBack = { viewModel.closeChat() },
            onSendMessage = { text -> viewModel.sendChatMessage(merchant.id, text) },
            onPayInvoice = { msgId, amount -> viewModel.payChatInvoice(merchant.id, msgId, amount) }
        )
    } else {
        val activeConversations = merchants.filter { chatMessagesMap.containsKey(it.id) }

        // Conversation List
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "گفتگوها",
                        color = TextPrimary,
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    GlassBadge(
                        text = if (activeConversations.isEmpty()) "بدون پیام جدید" else "${activeConversations.size} گفتگوی فعال",
                        color = BluePrimary
                    )
                }
            }

            if (activeConversations.isEmpty()) {
                item {
                    FluidAnimatedEntry(delayMillis = 50) {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            shape = RoundedCornerShape(24.dp),
                            backgroundColor = SurfaceGlassCard,
                            borderBrush = GlassBorderRefractionBrush,
                            elevation = 3.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .shadow(4.dp, shape = CircleShape, spotColor = BluePrimary.copy(alpha = 0.25f))
                                        .clip(CircleShape)
                                        .background(BlueLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Forum,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }

                                Text(
                                    text = "هنوز گفتگویی با کاسبان آغاز نشده است",
                                    fontFamily = VazirmatnFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "برای ثبت سفارش مستقیم، استعلام قیمت یا گفتگوی بدون واسطه با کاسبان و تولیدکنندگان، به بخش «کاسب‌ها» بروید و با کاسب مورد نظرتان گفتگو را شروع کنید.",
                                    fontFamily = VazirmatnFontFamily,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )

                                GlassButton(
                                    text = "مشاهده کاسبان معتمد بازار",
                                    onClick = { viewModel.navigateTo(KasebanScreen.MERCHANTS) },
                                    isPrimary = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            } else {
                items(activeConversations) { merchant ->
                    val index = activeConversations.indexOf(merchant)
                    val lastMessage = chatMessagesMap[merchant.id]?.lastOrNull()?.text ?: "شروع گفتگو"
                    FluidAnimatedEntry(delayMillis = (index * 40).coerceAtMost(250)) {
                        ConversationRowItem(
                            title = merchant.name,
                            subtitle = lastMessage,
                            isOnline = merchant.isOnline,
                            specialty = merchant.specialty,
                            onClick = { viewModel.openChatWithMerchant(merchant) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationRowItem(
    title: String,
    subtitle: String,
    isOnline: Boolean,
    specialty: String = "",
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = SurfaceGlassCard,
        borderBrush = GlassBorderRefractionBrush,
        elevation = 2.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MerchantAvatar(
                name = title,
                specialty = specialty,
                size = 48.dp,
                isOnline = isOnline
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontFamily = VazirmatnFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontFamily = VazirmatnFontFamily,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DirectChatDetailView(
    merchant: Merchant,
    messages: List<DirectChatMessage>,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onPayInvoice: (String, Long) -> Unit
) {
    val context = LocalContext.current
    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    BackHandler { onBack() }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Chat Top Bar with frosted glass
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, spotColor = Color(0x1F2563EB))
                .background(Color(0xD9FFFFFF))
                .border(1.dp, GlassBorderRefractionBrush, androidx.compose.ui.graphics.RectangleShape)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassIconContainer(
                    size = 36.dp,
                    shape = RoundedCornerShape(12.dp),
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                MerchantAvatar(
                    name = merchant.name,
                    specialty = merchant.specialty,
                    size = 40.dp,
                    isOnline = merchant.isOnline
                )

                Column {
                    Text(
                        text = merchant.name,
                        color = TextPrimary,
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (merchant.isOnline) "آنلاین در کاسبان" else "آخرین بازدید اخیراً",
                        color = if (merchant.isOnline) TrustGreen else TextMuted,
                        fontFamily = VazirmatnFontFamily,
                        fontSize = 10.5.sp
                    )
                }
            }

            GlassBadge(text = merchant.location, color = BluePrimary)
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Persian date banner
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33000000))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "امروز",
                            color = Color.White,
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            items(messages) { message ->
                ChatBubbleItem(
                    message = message,
                    onPay = {
                        onPayInvoice(message.id, message.paymentAmount)
                        Toast.makeText(context, "پرداخت با موفقیت انجام شد و به کیف پول کاسب منتقل گردید.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Quick Reply Suggestions with fluid AnimatedVisibility
        AnimatedVisibility(
            visible = inputMessage.isEmpty(),
            enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                    expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
            exit = fadeOut(animationSpec = tween(100)) +
                   shrinkVertically(animationSpec = tween(100))
        ) {
            val suggestions = listOf("سلام، وقت بخیر", "موجود دارید؟", "شرایط ارسال چطوره؟", "فاکتور خرید لطفاً")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { text ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xD9FFFFFF))
                            .border(1.dp, GlassBorderSubtle, RoundedCornerShape(12.dp))
                            .clickable {
                                inputMessage = text
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = text,
                            color = BluePrimary,
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Bottom Input Bar with Translucent Glass Sheen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, spotColor = Color(0x1F2563EB))
                .background(Color(0xD9FFFFFF))
                .border(1.dp, GlassBorderRefractionBrush, androidx.compose.ui.graphics.RectangleShape)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Attachment icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xCCEAF2FD))
                    .clickable { Toast.makeText(context, "ارسال فایل یا فاکتور پیوست", Toast.LENGTH_SHORT).show() },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, tint = TextMuted)
            }

            // Image gallery icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xCCEAF2FD))
                    .clickable { Toast.makeText(context, "ارسال عکس محصول", Toast.LENGTH_SHORT).show() },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = TextMuted)
            }

            // Text Field
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("پیام به ${merchant.name}...", color = TextMuted, fontSize = 12.sp, fontFamily = VazirmatnFontFamily) },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xD9FFFFFF),
                    unfocusedContainerColor = Color(0xB8FFFFFF),
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = GlassBorderSubtle
                ),
                maxLines = 3
            )

            // Send Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (inputMessage.isNotBlank()) BluePrimary else Color(0xFFE2E8F0))
                    .clickable(enabled = inputMessage.isNotBlank()) {
                        onSendMessage(inputMessage)
                        inputMessage = ""
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "ارسال",
                    tint = if (inputMessage.isNotBlank()) Color.White else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: DirectChatMessage,
    onPay: () -> Unit
) {
    val isUser = message.isFromUser
    FluidAnimatedEntry {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .clip(
                            RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomStart = if (isUser) 18.dp else 4.dp,
                                bottomEnd = if (isUser) 4.dp else 18.dp
                            )
                        )
                        .background(if (isUser) Color(0xCCDBEAFE) else SurfaceGlassCard)
                        .border(
                            1.dp,
                            if (isUser) Color(0x4D2563EB) else GlassBorderSubtle,
                            RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomStart = if (isUser) 18.dp else 4.dp,
                                bottomEnd = if (isUser) 4.dp else 18.dp
                            )
                        )
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = message.text,
                            color = TextPrimary,
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )

                        // Payment Invoice Card inside chat
                        if (message.hasPaymentRequest) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xD9FFFFFF))
                                    .border(1.dp, Color(0x332563EB), RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Payment,
                                                contentDescription = null,
                                                tint = BluePrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = "فاکتور خرید کاسبان",
                                                fontFamily = VazirmatnFontFamily,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                        Text(
                                            text = PersianUtils.formatPrice(message.paymentAmount),
                                            fontFamily = VazirmatnFontFamily,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BluePrimary
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = message.isPaymentPaid,
                                        enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                                                expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
                                        exit = fadeOut(animationSpec = tween(120))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFFDCFCE7))
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "پرداخت شده از کیف پول",
                                                color = TrustGreen,
                                                fontFamily = VazirmatnFontFamily,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    AnimatedVisibility(
                                        visible = !message.isPaymentPaid,
                                        enter = fadeIn(),
                                        exit = fadeOut(animationSpec = tween(120))
                                    ) {
                                        GlassButton(
                                            text = "پرداخت آنلاین امن",
                                            onClick = onPay,
                                            isPrimary = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = message.time,
                            color = TextMuted,
                            fontFamily = VazirmatnFontFamily,
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}
