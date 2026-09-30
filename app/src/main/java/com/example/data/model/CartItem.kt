package com.example.data.model

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val totalPrice: Long
        get() = product.price * quantity
}
