package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

enum class OrderStatus(
    val titlePersian: String,
    val descriptionPersian: String,
    val stepIndex: Int
) {
    PENDING_REVIEW(
        titlePersian = "در انتظار بررسی",
        descriptionPersian = "سفارش شما ثبت شده و در انتظار تایید فیش واریزی توسط فروشگاه است",
        stepIndex = 1
    ),
    PAYMENT_VERIFIED(
        titlePersian = "تایید پرداخت",
        descriptionPersian = "واریز شما با موفقیت تایید شد و به بخش آماده‌سازی ارسال گردید",
        stepIndex = 2
    ),
    PREPARING(
        titlePersian = "در حال آماده‌سازی",
        descriptionPersian = "محصولات تازه ارگانیک شما در حال بسته‌بندی بهداشتی هستند",
        stepIndex = 3
    ),
    SHIPPED(
        titlePersian = "ارسال شده",
        descriptionPersian = "سفارش تحویل پیک / پست شد و در مسیر تحویل است",
        stepIndex = 4
    ),
    DELIVERED(
        titlePersian = "تحویل داده شد",
        descriptionPersian = "سفارش با موفقیت تحویل خریدار گرامی شد",
        stepIndex = 5
    ),
    CANCELLED(
        titlePersian = "لغو شده",
        descriptionPersian = "سفارش لغو شد",
        stepIndex = 0
    );

    val badgeColor: Color
        get() = when (this) {
            PENDING_REVIEW -> StatusPending
            PAYMENT_VERIFIED -> StatusInfo
            PREPARING -> StatusWarning
            SHIPPED -> StatusInfo
            DELIVERED -> StatusSuccess
            CANCELLED -> StatusError
        }
}
