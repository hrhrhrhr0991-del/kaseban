package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.GandomaDatabase
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import com.example.data.model.SampleProducts
import com.example.data.remote.GeminiApiClient
import com.example.ui.components.NavScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class GandomaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = GandomaDatabase.getDatabase(application)
    private val orderDao = db.orderDao()

    // Products State
    private val _products = MutableStateFlow<List<Product>>(SampleProducts.items)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _selectedCategory = MutableStateFlow("همه محصولات")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Cart State
    private val _cart = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cart: StateFlow<Map<String, CartItem>> = _cart.asStateFlow()

    // Navigation & Mode
    private val _currentScreen = MutableStateFlow(NavScreen.STORE)
    val currentScreen: StateFlow<NavScreen> = _currentScreen.asStateFlow()

    private val _isSellerMode = MutableStateFlow(false)
    val isSellerMode: StateFlow<Boolean> = _isSellerMode.asStateFlow()

    // Referral System
    val userReferralCode: String = generateOrGetReferralCode(application)
    private val _referralCount = MutableStateFlow(14)
    val referralCount: StateFlow<Int> = _referralCount.asStateFlow()

    // Direct Order Flow State
    private val _directStep = MutableStateFlow(1) // 1: Cart, 2: Delivery, 3: Address, 4: Payment, 5: Placed
    val directStep: StateFlow<Int> = _directStep.asStateFlow()

    private val _selectedDeliveryMethod = MutableStateFlow("ارسال با پیک اکسپرس گندما")
    val selectedDeliveryMethod: StateFlow<String> = _selectedDeliveryMethod.asStateFlow()

    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _customerPhone = MutableStateFlow("")
    val customerPhone: StateFlow<String> = _customerPhone.asStateFlow()

    private val _customerAddress = MutableStateFlow("")
    val customerAddress: StateFlow<String> = _customerAddress.asStateFlow()

    private val _payerName = MutableStateFlow("")
    val payerName: StateFlow<String> = _payerName.asStateFlow()

    private val _paymentSlipCode = MutableStateFlow("")
    val paymentSlipCode: StateFlow<String> = _paymentSlipCode.asStateFlow()

    private val _orderNotes = MutableStateFlow("")
    val orderNotes: StateFlow<String> = _orderNotes.asStateFlow()

    private val _lastCreatedOrder = MutableStateFlow<Order?>(null)
    val lastCreatedOrder: StateFlow<Order?> = _lastCreatedOrder.asStateFlow()

    // Orders from Local Database
    val allOrders: StateFlow<List<Order>> = orderDao.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Seller Filter
    private val _sellerFilter = MutableStateFlow<OrderStatus?>(null)
    val sellerFilter: StateFlow<OrderStatus?> = _sellerFilter.asStateFlow()

    // AI Assistant Messages
    private val _aiMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                senderName = "مشاور هوشمند گندما",
                text = "درود و شادباش! 🌾 من دستیار هوشمند و مشاور تغذیه سلامت‌محور سامانه جدید «گندما» (gandoma.ir) هستم. چطور می‌توانم در انتخاب نان‌های سبوس‌دار، آرد کامل و خوراکی‌های بدون شکر به شما کمک کنم؟",
                isFromUser = false
            )
        )
    )
    val aiMessages: StateFlow<List<ChatMessage>> = _aiMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        // Seed default sample order if DB is empty
        viewModelScope.launch {
            if (orderDao.getOrderById("GND-7821") == null) {
                orderDao.insertOrder(
                    Order(
                        orderId = "GND-7821",
                        customerName = "مریم حسینی",
                        phoneNumber = "۰۹۱۲۳۴۵۶۷۸۹",
                        deliveryMethod = "ارسال با پیک اکسپرس گندما",
                        address = "تهران، شهرک غرب، خیابان مهستان، پلاک ۱۲",
                        payerName = "مریم حسینی",
                        trackingNumber = "۹۴۸۲۷۱",
                        status = OrderStatus.PREPARING,
                        itemsSummary = "نان خمیرترش کامل (۲ عدد)، عسل سبلان (۱ عدد)",
                        totalPrice = 526000,
                        userReferralCode = userReferralCode,
                        createdAt = System.currentTimeMillis() - 7200000,
                        notes = "لطفاً بعدازظهر تحویل شود"
                    )
                )
            }
        }
    }

    // --- Product & Search Actions ---
    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addNewProduct(product: Product) {
        _products.value = listOf(product) + _products.value
    }

    // --- Cart Actions ---
    fun addToCart(product: Product) {
        val current = _cart.value.toMutableMap()
        val existing = current[product.id]
        if (existing != null) {
            current[product.id] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current[product.id] = CartItem(product, 1)
        }
        _cart.value = current
    }

    fun removeFromCart(product: Product) {
        val current = _cart.value.toMutableMap()
        val existing = current[product.id] ?: return
        if (existing.quantity > 1) {
            current[product.id] = existing.copy(quantity = existing.quantity - 1)
        } else {
            current.remove(product.id)
        }
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    val cartTotalCount: Int
        get() = _cart.value.values.sumOf { it.quantity }

    val cartTotalPrice: Long
        get() = _cart.value.values.sumOf { it.totalPrice }

    // --- Navigation ---
    fun navigateTo(screen: NavScreen) {
        _currentScreen.value = screen
    }

    fun toggleSellerMode() {
        _isSellerMode.value = !_isSellerMode.value
    }

    // --- Direct Order Flow Actions ---
    fun startDirectOrder() {
        if (_cart.value.isNotEmpty()) {
            _directStep.value = 1
            _currentScreen.value = NavScreen.DIRECT_ORDER
        }
    }

    fun setDirectStep(step: Int) {
        _directStep.value = step
    }

    fun setDeliveryMethod(method: String) {
        _selectedDeliveryMethod.value = method
    }

    fun updateCustomerInfo(name: String, phone: String, address: String) {
        _customerName.value = name
        _customerPhone.value = phone
        _customerAddress.value = address
    }

    fun updatePaymentInfo(payer: String, slipCode: String, notes: String) {
        _payerName.value = payer
        _paymentSlipCode.value = slipCode
        _orderNotes.value = notes
    }

    fun submitDirectOrder() {
        viewModelScope.launch {
            val orderId = "GND-${Random.nextInt(1000, 9999)}"
            val summary = _cart.value.values.joinToString("، ") { "${it.product.title} (${it.quantity})" }
            val newOrder = Order(
                orderId = orderId,
                customerName = _customerName.value.ifBlank { "کاربر گندما" },
                phoneNumber = _customerPhone.value.ifBlank { "۰۹۱۲۰۰۰۰۰۰۰" },
                deliveryMethod = _selectedDeliveryMethod.value,
                address = _customerAddress.value.ifBlank { "تحویل حضوری در فروشگاه گندما" },
                payerName = _payerName.value.ifBlank { "واریز کارت‌به‌کارت" },
                trackingNumber = _paymentSlipCode.value.ifBlank { "۴ رقمی-${Random.nextInt(1000, 9999)}" },
                status = OrderStatus.PENDING_REVIEW,
                itemsSummary = summary,
                totalPrice = cartTotalPrice,
                userReferralCode = userReferralCode,
                createdAt = System.currentTimeMillis(),
                notes = _orderNotes.value
            )

            orderDao.insertOrder(newOrder)
            _lastCreatedOrder.value = newOrder
            _directStep.value = 5 // Success Step
            clearCart()
        }
    }

    // --- Seller Admin Actions ---
    fun setSellerFilter(status: OrderStatus?) {
        _sellerFilter.value = status
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            orderDao.updateOrderStatus(orderId, newStatus)
        }
    }

    // --- Share & Referral Actions ---
    fun getShareableMessage(): String {
        return """
🌾 سلام! با برند جدید و مدرن «گندما» آشنا شدی؟

مرجع تخصصی نان‌های اصیل خمیرترش، آرد کامل سبوس‌دار و فرآورده‌های ارگانیک با نشان سلامت.

با کد معرف من هدیه خرید اول و ۱۰٪ تخفیف نقدی دریافت کن:
🌐 وب‌سایت رسمی و ثبت سفارش: https://gandoma.ir
🔖 کد تخفیف اختصاصی شما:
$userReferralCode

🌾 گندما (gandoma.ir) — عطر گندم، سلامت زندگی
        """.trimIndent()
    }

    fun shareReferral(context: Context) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getShareableMessage())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "ارسال کد اشتراک گندما")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    // --- AI Assistant Actions ---
    fun sendAiMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMessage = ChatMessage(
            senderName = "شما",
            text = prompt,
            isFromUser = true
        )

        _aiMessages.value = _aiMessages.value + userMessage
        _isAiLoading.value = true

        viewModelScope.launch {
            val history = _aiMessages.value.map { it.text to it.isFromUser }
            val answer = GeminiApiClient.askAssistant(prompt, history)

            val botMessage = ChatMessage(
                senderName = "مشاور هوشمند گندما",
                text = answer,
                isFromUser = false
            )

            _aiMessages.value = _aiMessages.value + botMessage
            _isAiLoading.value = false
        }
    }

    private fun generateOrGetReferralCode(context: Context): String {
        val prefs = context.getSharedPreferences("gandoma_prefs", Context.MODE_PRIVATE)
        var code = prefs.getString("ref_code", null)
        if (code == null) {
            code = "GND-${Random.nextInt(1000, 9999)}"
            prefs.edit().putString("ref_code", code).apply()
        }
        return code
    }
}
