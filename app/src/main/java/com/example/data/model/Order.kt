package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    val orderId: String,
    val customerName: String,
    val phoneNumber: String,
    val deliveryMethod: String,
    val address: String,
    val payerName: String,
    val trackingNumber: String,
    val status: OrderStatus,
    val itemsSummary: String,
    val totalPrice: Long,
    val userReferralCode: String,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)
