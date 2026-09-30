package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.GeminiApiClient
import com.example.data.model.DirectChatMessage
import com.example.data.model.FeaturedBanner
import com.example.data.model.Merchant
import com.example.data.model.MerchantProduct
import com.example.data.model.MerchantReview
import com.example.data.model.MutualFamiliar
import com.example.data.model.UserAddress
import com.example.data.model.WalletTransaction
import com.example.ui.components.KasebanScreen
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KasebanViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("kaseban_app_prefs", Context.MODE_PRIVATE)

    // Theme Mode (Light / Dark) and Color Palette (Pastel Teal / Navy White / Emerald Green)
    private val _themeMode = MutableStateFlow(
        if (prefs.getBoolean("is_dark_mode", false)) AppThemeMode.DARK else AppThemeMode.LIGHT
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _colorPalette = MutableStateFlow(
        when (prefs.getString("color_palette", "PASTEL_TEAL")) {
            "NAVY_WHITE" -> AppColorPalette.NAVY_WHITE
            "EMERALD_GREEN" -> AppColorPalette.EMERALD_GREEN
            else -> AppColorPalette.PASTEL_TEAL
        }
    )
    val colorPalette: StateFlow<AppColorPalette> = _colorPalette.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putBoolean("is_dark_mode", mode == AppThemeMode.DARK).apply()
    }

    fun toggleThemeMode() {
        val newMode = if (_themeMode.value == AppThemeMode.LIGHT) AppThemeMode.DARK else AppThemeMode.LIGHT
        setThemeMode(newMode)
    }

    fun setColorPalette(palette: AppColorPalette) {
        _colorPalette.value = palette
        prefs.edit().putString("color_palette", palette.name).apply()
    }

    fun cycleColorPalette() {
        val next = when (_colorPalette.value) {
            AppColorPalette.PASTEL_TEAL -> AppColorPalette.NAVY_WHITE
            AppColorPalette.NAVY_WHITE -> AppColorPalette.EMERALD_GREEN
            AppColorPalette.EMERALD_GREEN -> AppColorPalette.PASTEL_TEAL
        }
        setColorPalette(next)
    }

    fun loginAsGuest() {
        completeAuth(name = "کاربر مهمان", phone = "09120000000", referralCode = "", role = "خریدار معتمد")
    }

    // User Authentication State (Login & Registration)
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _userDisplayName = MutableStateFlow("")
    val userDisplayName: StateFlow<String> = _userDisplayName.asStateFlow()

    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userRole = MutableStateFlow("خریدار معتمد")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _userAvatarUri = MutableStateFlow<String?>(null)
    val userAvatarUri: StateFlow<String?> = _userAvatarUri.asStateFlow()

    private val _userBio = MutableStateFlow("")
    val userBio: StateFlow<String> = _userBio.asStateFlow()

    private val _userShopTitle = MutableStateFlow("")
    val userShopTitle: StateFlow<String> = _userShopTitle.asStateFlow()

    private val _userLocation = MutableStateFlow("ایران")
    val userLocation: StateFlow<String> = _userLocation.asStateFlow()

    private val _registeredReferralCode = MutableStateFlow("")
    val registeredReferralCode: StateFlow<String> = _registeredReferralCode.asStateFlow()

    // Active Screen
    private val _currentScreen = MutableStateFlow(KasebanScreen.MERCHANTS)
    val currentScreen: StateFlow<KasebanScreen> = _currentScreen.asStateFlow()

    // Merchants & Familiar Network - 100% Genuine, No Dummy Pre-population
    private val _merchantsTab = MutableStateFlow("کاسب‌ها") // "کاسب‌ها" or "آشنایان"
    val merchantsTab: StateFlow<String> = _merchantsTab.asStateFlow()

    private val _merchants = MutableStateFlow<List<Merchant>>(emptyList())
    val merchants: StateFlow<List<Merchant>> = _merchants.asStateFlow()

    private val _featuredBanners = MutableStateFlow<List<FeaturedBanner>>(emptyList())
    val featuredBanners: StateFlow<List<FeaturedBanner>> = _featuredBanners.asStateFlow()

    private val _mutualFamiliars = MutableStateFlow<List<MutualFamiliar>>(emptyList())
    val mutualFamiliars: StateFlow<List<MutualFamiliar>> = _mutualFamiliars.asStateFlow()

    private val _merchantReviews = MutableStateFlow<List<MerchantReview>>(emptyList())
    val merchantReviews: StateFlow<List<MerchantReview>> = _merchantReviews.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedMerchant = MutableStateFlow<Merchant?>(null)
    val selectedMerchant: StateFlow<Merchant?> = _selectedMerchant.asStateFlow()

    // Chat Messenger
    private val _activeChatMerchant = MutableStateFlow<Merchant?>(null)
    val activeChatMerchant: StateFlow<Merchant?> = _activeChatMerchant.asStateFlow()

    private val _chatMessagesMap = MutableStateFlow<Map<String, List<DirectChatMessage>>>(
        emptyMap()
    )
    val chatMessagesMap: StateFlow<Map<String, List<DirectChatMessage>>> = _chatMessagesMap.asStateFlow()

    // Profile & Wallet State
    private val _walletBalance = MutableStateFlow(prefs.getLong("wallet_balance", 0L))
    val walletBalance: StateFlow<Long> = _walletBalance.asStateFlow()

    private val _walletTransactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val walletTransactions: StateFlow<List<WalletTransaction>> = _walletTransactions.asStateFlow()

    // Merchant Management Mode (مدیریت کاسبی من)
    private val _isMerchantPageActive = MutableStateFlow(prefs.getBoolean("merchant_page_active", false))
    val isMerchantPageActive: StateFlow<Boolean> = _isMerchantPageActive.asStateFlow()

    private val _myProducts = MutableStateFlow<List<MerchantProduct>>(emptyList())
    val myProducts: StateFlow<List<MerchantProduct>> = _myProducts.asStateFlow()

    // Addresses
    private val _userAddresses = MutableStateFlow<List<UserAddress>>(emptyList())
    val userAddresses: StateFlow<List<UserAddress>> = _userAddresses.asStateFlow()

    // Dynamic Personal Invite Code
    val inviteCode: String
        get() = if (_userPhone.value.length >= 4) "KB-" + _userPhone.value.takeLast(4) else "KASB-MEMBER"

    // AI Assistant
    private val _aiMessages = MutableStateFlow(
        listOf(
            "درود و احترام! 🤝 من دستیار شبکه «کاسبان» هستم. می‌توانید درباره ثبت غرفه، دسته‌بندی محصولات و راهنمایی‌های بازار از من بپرسید." to false
        )
    )
    val aiMessages: StateFlow<List<Pair<String, Boolean>>> = _aiMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        val savedLoggedIn = prefs.getBoolean("user_logged_in", false)
        val savedPhone = prefs.getString("user_phone", "") ?: ""
        val savedName = prefs.getString("user_display_name", "") ?: ""

        val isDummyOrGuest = !savedLoggedIn ||
                savedPhone.isBlank() ||
                savedPhone == "حساب مهمان" ||
                savedPhone == "guest" ||
                savedName.contains("مهمان") ||
                savedName == "بانو مهسا رئیسیان"

        if (isDummyOrGuest) {
            prefs.edit().clear().apply()
            _isUserLoggedIn.value = false
            _userDisplayName.value = ""
            _userPhone.value = ""
            _userRole.value = "خریدار معتمد"
            _userShopTitle.value = ""
            _userBio.value = ""
            _userLocation.value = "ایران"
            _userAvatarUri.value = null
            _registeredReferralCode.value = ""
            _merchants.value = emptyList()
        } else {
            _isUserLoggedIn.value = true
            _userDisplayName.value = savedName
            _userPhone.value = savedPhone
            val role = prefs.getString("user_role", "خریدار معتمد") ?: "خریدار معتمد"
            _userRole.value = role
            val shop = prefs.getString("user_shop_title", "") ?: ""
            _userShopTitle.value = shop
            _userBio.value = prefs.getString("user_bio", "") ?: ""
            _userLocation.value = prefs.getString("user_location", "ایران") ?: "ایران"
            _userAvatarUri.value = prefs.getString("user_avatar_uri", null)
            _registeredReferralCode.value = prefs.getString("registered_referral_code", "") ?: ""

            // If user logged in as merchant, ensure their shop is present in marketplace
            if (role == "کاسب و تولیدکننده" && shop.isNotBlank()) {
                val myShop = Merchant(
                    id = "my_shop",
                    name = savedName,
                    title = shop,
                    avatarEmoji = "🏪",
                    specialty = "محصولات و دسترنج محلی",
                    location = _userLocation.value,
                    isOnline = true,
                    knownCount = 0,
                    storyTitle = shop,
                    storyText = "غرفه ثبت‌شده در بازار کاسبان",
                    reviewsCount = 0,
                    reviewsSummary = "",
                    products = emptyList(),
                    isKnownByUser = false
                )
                _merchants.value = listOf(myShop)
            }
        }
    }

    // Navigation
    fun navigateTo(screen: KasebanScreen) {
        _currentScreen.value = screen
        _selectedMerchant.value = null
        _activeChatMerchant.value = null
    }

    fun setMerchantsTab(tab: String) {
        _merchantsTab.value = tab
    }

    fun clearCategoryFilter() {
        _selectedCategory.value = null
    }

    fun selectMerchant(merchant: Merchant) {
        _selectedMerchant.value = merchant
    }

    fun closeMerchantDetail() {
        _selectedMerchant.value = null
    }

    fun toggleKnowMerchant(merchantId: String) {
        _merchants.value = _merchants.value.map {
            if (it.id == merchantId) {
                val newKnown = !it.isKnownByUser
                val newCount = if (newKnown) it.knownCount + 1 else (it.knownCount - 1).coerceAtLeast(0)
                it.copy(isKnownByUser = newKnown, knownCount = newCount)
            } else it
        }
        if (_selectedMerchant.value?.id == merchantId) {
            _selectedMerchant.value = _merchants.value.find { it.id == merchantId }
        }
    }

    // Register a new real merchant in the marketplace
    fun registerNewMerchant(
        name: String,
        shopTitle: String,
        specialty: String,
        location: String,
        description: String,
        products: List<MerchantProduct> = emptyList()
    ) {
        if (name.isBlank() || shopTitle.isBlank()) return
        val newMerchant = Merchant(
            id = "m_${System.currentTimeMillis()}",
            name = name.trim(),
            title = shopTitle.trim(),
            avatarEmoji = "🏪",
            avatarUri = null,
            specialty = specialty.ifBlank { "عمومی" },
            location = location.ifBlank { "ایران" },
            isOnline = true,
            knownCount = 0,
            storyTitle = shopTitle.trim(),
            storyText = description.trim(),
            reviewsCount = 0,
            reviewsSummary = "",
            products = products,
            isKnownByUser = false
        )
        _merchants.value = listOf(newMerchant) + _merchants.value
    }

    // Chat actions - Honest direct messaging with real user inputs
    fun openChatWithMerchant(merchant: Merchant) {
        _activeChatMerchant.value = merchant
        if (!_chatMessagesMap.value.containsKey(merchant.id)) {
            val updated = _chatMessagesMap.value.toMutableMap()
            updated[merchant.id] = emptyList()
            _chatMessagesMap.value = updated
        }
        _currentScreen.value = KasebanScreen.CHATS
        _selectedMerchant.value = null
    }

    fun closeChat() {
        _activeChatMerchant.value = null
    }

    fun sendChatMessage(merchantId: String, text: String) {
        if (text.isBlank()) return
        val currentList = _chatMessagesMap.value[merchantId] ?: emptyList()
        val newMessage = DirectChatMessage(
            id = System.currentTimeMillis().toString(),
            senderName = "شما",
            text = text,
            time = "هم‌اکنون",
            isFromUser = true
        )
        val updatedMap = _chatMessagesMap.value.toMutableMap()
        updatedMap[merchantId] = currentList + newMessage
        _chatMessagesMap.value = updatedMap
        // No fake auto-reply bots simulating real merchants!
    }

    fun payChatInvoice(merchantId: String, messageId: String, amount: Long) {
        val currentList = _chatMessagesMap.value[merchantId] ?: return
        val updated = currentList.map {
            if (it.id == messageId) it.copy(isPaymentPaid = true) else it
        }
        val updatedMap = _chatMessagesMap.value.toMutableMap()
        updatedMap[merchantId] = updated
        _chatMessagesMap.value = updatedMap

        // Update wallet
        if (_walletBalance.value >= amount) {
            _walletBalance.value -= amount
        }
        _walletTransactions.value = listOf(
            WalletTransaction(
                id = "tx-${System.currentTimeMillis()}",
                title = "پرداخت سفارش به ${_activeChatMerchant.value?.name ?: "کاسب"}",
                amount = amount,
                isDeposit = false,
                date = "امروز"
            )
        ) + _walletTransactions.value
    }

    // Wallet actions
    fun depositToWallet(amount: Long) {
        _walletBalance.value += amount
        _walletTransactions.value = listOf(
            WalletTransaction(
                id = "tx-${System.currentTimeMillis()}",
                title = "شارژ آنلاین کیف پول",
                amount = amount,
                isDeposit = true,
                date = "امروز"
            )
        ) + _walletTransactions.value
    }

    fun toggleMerchantPageActive() {
        _isMerchantPageActive.value = !_isMerchantPageActive.value
    }

    fun addMerchantProduct(title: String, weight: String, price: Long) {
        val newProduct = MerchantProduct(
            id = "p-${System.currentTimeMillis()}",
            title = title,
            weight = weight,
            price = price
        )
        _myProducts.value = listOf(newProduct) + _myProducts.value

        // Also update the user's merchant listing in marketplace
        _merchants.value = _merchants.value.map { m ->
            if (m.name == _userDisplayName.value || m.title == _userShopTitle.value) {
                m.copy(products = listOf(newProduct) + m.products)
            } else m
        }
    }

    fun selectCategory(cat: String?) {
        _selectedCategory.value = cat
    }

    fun toggleMutualFamiliarKnown(id: String) {
        _mutualFamiliars.value = _mutualFamiliars.value.map {
            if (it.id == id) it.copy(isKnown = !it.isKnown) else it
        }
    }

    fun addMutualFamiliar(name: String, relation: String = "آشنای معتمد") {
        if (name.isBlank()) return
        val newFamiliar = MutualFamiliar(
            id = "mf_${System.currentTimeMillis()}",
            name = name.trim(),
            avatarEmoji = "🤝",
            avatarUri = null,
            durationText = relation.ifBlank { "به‌تازگی" },
            isKnown = true
        )
        _mutualFamiliars.value = listOf(newFamiliar) + _mutualFamiliars.value
    }

    fun addMerchantReview(comment: String, rating: Int = 5) {
        if (comment.isBlank()) return
        val targetMerchantId = _selectedMerchant.value?.id ?: return
        val newReview = MerchantReview(
            id = "rv_${System.currentTimeMillis()}",
            authorName = _userDisplayName.value.ifBlank { "کاربر کاسبان" },
            avatarEmoji = "👤",
            rating = rating,
            comment = comment.trim(),
            date = "هم‌اکنون"
        )
        _merchantReviews.value = listOf(newReview) + _merchantReviews.value

        // Increment reviews count on the actual merchant
        _merchants.value = _merchants.value.map { m ->
            if (m.id == targetMerchantId) {
                m.copy(reviewsCount = m.reviewsCount + 1)
            } else m
        }
        _selectedMerchant.value = _merchants.value.find { it.id == targetMerchantId }
    }

    fun addUserAddress(title: String, address: String, phone: String) {
        if (title.isBlank() || address.isBlank()) return
        val newAddress = UserAddress(
            id = "addr_${System.currentTimeMillis()}",
            title = title.trim(),
            address = address.trim(),
            phone = phone.ifBlank { _userPhone.value },
            isDefault = _userAddresses.value.isEmpty()
        )
        _userAddresses.value = _userAddresses.value + listOf(newAddress)
    }

    fun removeUserAddress(id: String) {
        _userAddresses.value = _userAddresses.value.filter { it.id != id }
    }

    fun transferWalletBalance(amount: Long, destinationTitle: String): Boolean {
        if (amount <= 0 || _walletBalance.value < amount) return false
        _walletBalance.value -= amount
        val tx = WalletTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = "انتقال به $destinationTitle",
            amount = amount,
            isDeposit = false,
            date = "امروز"
        )
        _walletTransactions.value = listOf(tx) + _walletTransactions.value
        return true
    }

    fun shareInvite(context: Context) {
        val message = """
🤝 دعوت به شبکه «کاسبان» (بازار آدم‌های معتمد)

خرید مستقیم و بدون واسطه از تولیدکنندگان و کسبه محلی.
سامانه کاسبان: https://kaseban.ir
کد معرف من: $inviteCode
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(sendIntent, "دعوت به کاسبان")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    // AI advisor
    fun sendAiPrompt(prompt: String) {
        if (prompt.isBlank()) return
        _aiMessages.value = _aiMessages.value + listOf(Pair(prompt, true))
        _isAiLoading.value = true

        viewModelScope.launch {
            val response = GeminiApiClient.askAssistant(
                prompt,
                _aiMessages.value.map { it.first to it.second }
            )
            _aiMessages.value = _aiMessages.value + listOf(Pair(response, false))
            _isAiLoading.value = false
        }
    }

    // User Authentication Methods
    fun completeAuth(name: String, phone: String, referralCode: String, role: String) {
        val finalName = if (name.isNotBlank()) name.trim() else "کاربر کاسبان"
        val finalPhone = phone.trim()
        val finalRole = if (role.isNotBlank()) role else "خریدار معتمد"
        val finalRef = referralCode.trim()
        val defaultShop = if (finalRole == "کاسب و تولیدکننده") "غرفه $finalName" else ""

        _userDisplayName.value = finalName
        _userPhone.value = finalPhone
        _userRole.value = finalRole
        _userShopTitle.value = defaultShop
        _userBio.value = ""
        _userLocation.value = "ایران"
        _userAvatarUri.value = null
        _registeredReferralCode.value = finalRef
        _isUserLoggedIn.value = true
        _myProducts.value = emptyList()
        _userAddresses.value = emptyList()
        _mutualFamiliars.value = emptyList()
        _isMerchantPageActive.value = (finalRole == "کاسب و تولیدکننده")

        // If the user registered as a merchant, create their genuine shop in the marketplace
        if (finalRole == "کاسب و تولیدکننده") {
            val userMerchant = Merchant(
                id = "m_${System.currentTimeMillis()}",
                name = finalName,
                title = defaultShop,
                avatarEmoji = "🏪",
                avatarUri = null,
                specialty = "محصولات و دسترنج محلی",
                location = "ایران",
                isOnline = true,
                knownCount = 0,
                storyTitle = defaultShop,
                storyText = "غرفه رسمی و ثبت‌شده در بازار معتمدین کاسبان",
                reviewsCount = 0,
                reviewsSummary = "",
                products = emptyList(),
                isKnownByUser = false
            )
            _merchants.value = listOf(userMerchant)
        } else {
            _merchants.value = emptyList()
        }

        // Clean zero balance for newly registered account
        _walletBalance.value = 0L
        _walletTransactions.value = emptyList()

        prefs.edit()
            .putBoolean("user_logged_in", true)
            .putString("user_display_name", finalName)
            .putString("user_phone", finalPhone)
            .putString("user_role", finalRole)
            .putString("user_shop_title", defaultShop)
            .putString("user_bio", "")
            .putString("user_location", "ایران")
            .remove("user_avatar_uri")
            .putString("registered_referral_code", finalRef)
            .putLong("wallet_balance", 0L)
            .putBoolean("merchant_page_active", (finalRole == "کاسب و تولیدکننده"))
            .apply()
    }

    fun logout() {
        prefs.edit().clear().apply()
        _isUserLoggedIn.value = false
        _userDisplayName.value = ""
        _userPhone.value = ""
        _userRole.value = "خریدار معتمد"
        _userShopTitle.value = ""
        _userBio.value = ""
        _userAvatarUri.value = null
        _walletBalance.value = 0L
        _walletTransactions.value = emptyList()
        _myProducts.value = emptyList()
        _userAddresses.value = emptyList()
        _mutualFamiliars.value = emptyList()
        _merchants.value = emptyList()
        _currentScreen.value = KasebanScreen.MERCHANTS
    }

    // Profile & Avatar Editing
    fun updateUserProfile(
        name: String,
        shopTitle: String,
        bio: String,
        location: String,
        phone: String,
        avatarUri: String? = _userAvatarUri.value
    ) {
        val finalName = if (name.isNotBlank()) name.trim() else _userDisplayName.value
        val finalShop = if (shopTitle.isNotBlank()) shopTitle.trim() else _userShopTitle.value
        val finalBio = if (bio.isNotBlank()) bio.trim() else _userBio.value
        val finalLoc = if (location.isNotBlank()) location.trim() else _userLocation.value
        val finalPhone = if (phone.isNotBlank()) phone.trim() else _userPhone.value

        _userDisplayName.value = finalName
        _userShopTitle.value = finalShop
        _userBio.value = finalBio
        _userLocation.value = finalLoc
        _userPhone.value = finalPhone
        _userAvatarUri.value = avatarUri

        prefs.edit()
            .putString("user_display_name", finalName)
            .putString("user_shop_title", finalShop)
            .putString("user_bio", finalBio)
            .putString("user_location", finalLoc)
            .putString("user_phone", finalPhone)
            .putString("user_avatar_uri", avatarUri)
            .apply()
    }

    fun updateUserAvatar(uri: String?) {
        _userAvatarUri.value = uri
        if (uri != null) {
            prefs.edit().putString("user_avatar_uri", uri).apply()
        } else {
            prefs.edit().remove("user_avatar_uri").apply()
        }
    }
}
