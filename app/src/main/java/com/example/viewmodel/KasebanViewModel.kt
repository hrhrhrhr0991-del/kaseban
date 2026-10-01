package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.KasebanDatabase
import com.example.data.repository.FirestoreRepository
import com.example.data.model.BoothPost
import com.example.data.model.DirectChatMessage
import com.example.data.model.FeaturedBanner
import com.example.data.model.Merchant
import com.example.data.model.MerchantProduct
import com.example.data.model.MerchantReview
import com.example.data.model.MutualFamiliar
import com.example.data.model.UserAccount
import com.example.data.model.UserAddress
import com.example.data.model.WalletTransaction
import com.example.data.remote.GeminiApiClient
import com.example.ui.components.KasebanScreen
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KasebanViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("kaseban_app_session", Context.MODE_PRIVATE)
    val database = KasebanDatabase(application)
    val firestoreRepo = FirestoreRepository(application)

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

    // User Authentication State (Session)
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

    // Super Admin Master State
    private val _isSuperAdmin = MutableStateFlow(false)
    val isSuperAdmin: StateFlow<Boolean> = _isSuperAdmin.asStateFlow()

    private val _registeredAccounts = MutableStateFlow<List<UserAccount>>(emptyList())
    val registeredAccounts: StateFlow<List<UserAccount>> = _registeredAccounts.asStateFlow()

    // Active Screen
    private val _currentScreen = MutableStateFlow(KasebanScreen.MERCHANTS)
    val currentScreen: StateFlow<KasebanScreen> = _currentScreen.asStateFlow()

    // Persistent Merchants in Marketplace
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
        // 1. Load permanent merchants from local database
        val savedMerchants = database.getAllMerchants()
        _merchants.value = savedMerchants

        // 2. Restore user session if logged in
        val sessionActive = prefs.getBoolean("session_active", false)
        val sessionPhone = prefs.getString("session_phone", "") ?: ""

        if (sessionActive && sessionPhone.isNotBlank()) {
            val account = database.findAccountByPhone(sessionPhone)
            if (account != null) {
                _isUserLoggedIn.value = true
                _userPhone.value = account.phone
                _userDisplayName.value = account.name
                _userRole.value = account.role
                _userShopTitle.value = account.shopTitle
                _userBio.value = account.bio
                _userLocation.value = account.location
                _userAvatarUri.value = prefs.getString("user_avatar_uri", null)
                _registeredReferralCode.value = account.referralCode
            }
        }

        // 3. Observe real-time merchants & booths from Cloud Firestore
        viewModelScope.launch {
            try {
                firestoreRepo.observeMerchants().collect { cloudMerchants ->
                    if (cloudMerchants.isNotEmpty()) {
                        val currentLocal = _merchants.value
                        val merged = cloudMerchants + currentLocal.filter { local ->
                            cloudMerchants.none { it.id == local.id || (it.phone.isNotBlank() && it.phone == local.phone) }
                        }
                        _merchants.value = merged
                        database.saveMerchants(merged)
                    }
                }
            } catch (e: Exception) {
                // Retain local database
            }
        }
    }

    // -----------------------------------------------------------------
    // User Authentication with 4-Digit Numeric PIN (SECURE & PERSISTENT)
    // -----------------------------------------------------------------

    /**
     * Register a new user account with required 4-digit numeric PIN.
     * Returns null on success, or an error message string on failure.
     */
    fun registerWithPin(
        name: String,
        phone: String,
        pin: String,
        pinConfirm: String,
        role: String,
        referralCode: String = "",
        shopTitle: String = "",
        specialty: String = "محصولات و دسترنج محلی",
        location: String = "ایران",
        bio: String = ""
    ): String? {
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        val cleanPin = pin.trim()
        val cleanConfirm = pinConfirm.trim()

        if (cleanName.isBlank()) {
            return "لطفاً نام و نام‌خانوادگی خود را وارد نمایید."
        }
        if (cleanPhone.length < 10) {
            return "شماره تلفن همراه باید حداقل ۱۰ رقم باشد."
        }
        if (cleanPin.length != 4 || !cleanPin.all { it.isDigit() }) {
            return "رمز عبور باید دقیقاً ۴ رقم عدد باشد."
        }
        if (cleanPin != cleanConfirm) {
            return "رمز عبور با تکرار آن مطابقت ندارد."
        }

        val account = UserAccount(
            id = cleanPhone,
            phone = cleanPhone,
            pin = cleanPin,
            name = cleanName,
            role = role,
            referralCode = referralCode.trim(),
            shopTitle = shopTitle.trim(),
            bio = bio.trim(),
            location = location.trim().ifBlank { "ایران" }
        )

        val regError = database.registerAccount(account)
        if (regError != null) {
            return regError
        }

        // If registered as merchant, create their single official booth immediately
        var userBooth: Merchant? = null
        if (role == "کاسب و تولیدکننده") {
            val title = if (shopTitle.isNotBlank()) shopTitle.trim() else "غرفه $cleanName"
            userBooth = Merchant(
                id = "booth_$cleanPhone",
                ownerId = cleanPhone,
                name = cleanName,
                title = title,
                phone = cleanPhone,
                avatarEmoji = "🏪",
                avatarUri = null,
                specialty = specialty.ifBlank { "محصولات و دسترنج محلی" },
                location = location.ifBlank { "ایران" },
                isOnline = true,
                knownCount = 0,
                storyTitle = title,
                storyText = bio.ifBlank { "غرفه رسمی و معتبر در بازار کاسبان" },
                reviewsCount = 0,
                reviewsSummary = "",
                products = emptyList(),
                posts = emptyList(),
                isKnownByUser = false
            )
            val currentList = _merchants.value.filter { it.phone != cleanPhone }
            val updated = listOf(userBooth) + currentList
            _merchants.value = updated
            database.saveMerchants(updated)
        }

        // Persist to Cloud Firestore in background
        viewModelScope.launch {
            try {
                firestoreRepo.saveUserAccount(account)
                userBooth?.let { firestoreRepo.saveMerchantBooth(it) }
            } catch (e: Exception) {
                // Handled
            }
        }

        // Establish session
        setLoggedInSession(account)
        return null
    }

    /**
     * Log in an existing user with their phone and 4-digit PIN.
     * Returns null on success, or an error message string on failure.
     */
    /**
     * Log in an existing user with their phone and 4-digit PIN.
     * Special master credentials: Phone 128110 and PIN 128110 grants Super Admin access.
     * Returns null on success, or an error message string on failure.
     */
    fun loginWithPin(phone: String, pin: String): String? {
        val cleanPhone = phone.trim()
        val cleanPin = pin.trim()

        if (cleanPhone.isBlank()) {
            return "لطفاً شماره تلفن همراه خود را وارد کنید."
        }
        if (cleanPin.isBlank()) {
            return "لطفاً رمز عبور را وارد نمایید."
        }

        // 1. Super Admin Master Secret Login Bypass
        if ((cleanPhone == "128110" || cleanPhone == "09128110") && cleanPin == "128110") {
            val adminAccount = UserAccount(
                id = "128110",
                phone = "128110",
                pin = "128110",
                name = "مدیر کل سیستم",
                role = "مدیر کل",
                shopTitle = "مرکز فرماندهی بازار کاسبان",
                location = "تهران"
            )
            setLoggedInSession(adminAccount)
            _isSuperAdmin.value = true
            _currentScreen.value = KasebanScreen.ADMIN_PANEL
            loadAllRegisteredAccounts()
            return null
        }

        if (cleanPin.length != 4 || !cleanPin.all { it.isDigit() }) {
            return "لطفاً رمز عددی ۴ رقمی خود را به طور کامل وارد کنید."
        }

        val (account, error) = database.verifyPinAndLogin(cleanPhone, cleanPin)
        if (error != null || account == null) {
            return error ?: "اطلاعات ورود نادرست است."
        }

        setLoggedInSession(account)
        return null
    }

    fun handleGoogleSignInSuccess() {
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val currentUser = auth.currentUser ?: return
        val googleName = currentUser.displayName ?: "کاربر معتمد"
        val googleUid = currentUser.uid

        val existingAccount = database.getAllAccounts().find { it.id == googleUid || it.name == googleName }
        if (existingAccount != null) {
            setLoggedInSession(existingAccount)
        } else {
            val digits = googleUid.filter { ch: Char -> ch.isDigit() }
            val phone = currentUser.phoneNumber ?: ("09" + digits.takeLast(9).padStart(9, '1'))
            val newAccount = UserAccount(
                id = googleUid,
                phone = phone,
                pin = "1234",
                name = googleName,
                role = "خریدار معتمد",
                referralCode = "",
                shopTitle = ""
            )
            database.registerAccount(newAccount)
            setLoggedInSession(newAccount)
            viewModelScope.launch {
                try {
                    firestoreRepo.saveUserAccount(newAccount)
                } catch (e: Exception) {
                    // Handled
                }
            }
        }
    }

    private fun setLoggedInSession(account: UserAccount) {
        _isUserLoggedIn.value = true
        _userDisplayName.value = account.name
        _userPhone.value = account.phone
        _userRole.value = account.role
        _userShopTitle.value = account.shopTitle
        _userBio.value = account.bio
        _userLocation.value = account.location
        _registeredReferralCode.value = account.referralCode

        _isSuperAdmin.value = (account.phone == "128110" || account.role == "مدیر کل")
        if (_isSuperAdmin.value) {
            loadAllRegisteredAccounts()
        }

        prefs.edit()
            .putBoolean("session_active", true)
            .putString("session_phone", account.phone)
            .apply()
    }

    // -------------------------------------------------------------
    // Super Admin Master Operations
    // -------------------------------------------------------------

    fun loadAllRegisteredAccounts() {
        _registeredAccounts.value = database.getAllAccounts()
    }

    fun toggleMerchantVerifiedByAdmin(merchantId: String) {
        val newStatus = database.toggleMerchantVerified(merchantId)
        val updated = _merchants.value.map {
            if (it.id == merchantId) it.copy(isVerified = newStatus) else it
        }
        _merchants.value = updated
        val updatedMerchant = updated.find { it.id == merchantId }
        if (updatedMerchant != null) {
            viewModelScope.launch {
                try { firestoreRepo.saveMerchantBooth(updatedMerchant) } catch (e: Exception) {}
            }
        }
    }

    fun toggleMerchantPinnedByAdmin(merchantId: String) {
        val newStatus = database.toggleMerchantPinned(merchantId)
        val updated = _merchants.value.map {
            if (it.id == merchantId) it.copy(isPinned = newStatus) else it
        }
        _merchants.value = updated
        val updatedMerchant = updated.find { it.id == merchantId }
        if (updatedMerchant != null) {
            viewModelScope.launch {
                try { firestoreRepo.saveMerchantBooth(updatedMerchant) } catch (e: Exception) {}
            }
        }
    }

    fun deleteMerchantByAdmin(merchantId: String) {
        database.deleteMerchant(merchantId)
        _merchants.value = _merchants.value.filter { it.id != merchantId }
        viewModelScope.launch {
            try {
                firestoreRepo.db.collection("merchants").document(merchantId).delete()
            } catch (e: Exception) {}
        }
    }

    fun deleteUserByAdmin(phone: String) {
        database.deleteAccount(phone)
        loadAllRegisteredAccounts()
        val boothToDelete = _merchants.value.find { it.phone == phone || it.id == "booth_$phone" }
        if (boothToDelete != null) {
            deleteMerchantByAdmin(boothToDelete.id)
        }
    }

    fun updateUserRoleByAdmin(phone: String, newRole: String) {
        database.updateAccountRole(phone, newRole)
        loadAllRegisteredAccounts()
    }

    fun resetUserPinByAdmin(phone: String, newPin: String) {
        database.updateAccountPin(phone, newPin)
        loadAllRegisteredAccounts()
    }

    fun deleteProductByAdmin(merchantId: String, productId: String) {
        val booth = _merchants.value.find { it.id == merchantId } ?: return
        val updatedProducts = booth.products.filter { it.id != productId }
        val updatedBooth = booth.copy(products = updatedProducts)
        val updated = _merchants.value.map { if (it.id == merchantId) updatedBooth else it }
        _merchants.value = updated
        database.saveMerchants(updated)
        viewModelScope.launch {
            try { firestoreRepo.deleteProduct(merchantId, productId) } catch (e: Exception) {}
        }
    }

    fun deletePostByAdmin(merchantId: String, postId: String) {
        val booth = _merchants.value.find { it.id == merchantId } ?: return
        val updatedPosts = booth.posts.filter { it.id != postId }
        val updatedBooth = booth.copy(posts = updatedPosts)
        val updated = _merchants.value.map { if (it.id == merchantId) updatedBooth else it }
        _merchants.value = updated
        database.saveMerchants(updated)
        viewModelScope.launch {
            try { firestoreRepo.deletePost(merchantId, postId) } catch (e: Exception) {}
        }
    }

    fun logout() {
        // Clear ONLY active session - NEVER wipe the persistent database of accounts and merchants!
        prefs.edit()
            .putBoolean("session_active", false)
            .remove("session_phone")
            .apply()

        _isUserLoggedIn.value = false
        _userDisplayName.value = ""
        _userPhone.value = ""
        _userRole.value = "خریدار معتمد"
        _userShopTitle.value = ""
        _userBio.value = ""
        _userAvatarUri.value = null
        _walletBalance.value = 0L
        _walletTransactions.value = emptyList()
        _currentScreen.value = KasebanScreen.MERCHANTS
        _selectedMerchant.value = null
        _activeChatMerchant.value = null
    }

    // -----------------------------------------------------------------
    // Single Booth Management per Merchant (Rich Customization & Settings)
    // -----------------------------------------------------------------

    /**
     * Returns the current logged-in user's booth, if any.
     */
    fun getMyBooth(): Merchant? {
        val phone = _userPhone.value
        if (phone.isBlank()) return null
        return _merchants.value.find { it.phone == phone || it.id == "booth_$phone" }
    }

    /**
     * Creates or updates the single booth for the current user with complete settings.
     */
    fun createOrUpdateMyBooth(
        title: String,
        specialty: String,
        location: String,
        storyText: String,
        address: String = "",
        workHours: String = "همه‌روزه از ۸ صبح تا ۱۰ شب",
        deliveryMethods: String = "پست پیشتاز، تیپاکس، پیک شهری",
        freeShippingThreshold: Long = 0L,
        minOrderAmount: Long = 0L,
        guaranteePolicy: String = "ضمانت اصالت و بازگشت کامل وجه در صورت عدم رضایت",
        socialTelegram: String = "",
        socialWhatsapp: String = "",
        isOnline: Boolean = true,
        avatarUri: String? = null,
        bannerUri: String? = null
    ): Merchant {
        val phone = _userPhone.value
        val name = _userDisplayName.value
        val cleanTitle = title.trim().ifBlank { "غرفه $name" }
        val cleanSpecialty = specialty.trim().ifBlank { "محصولات و دسترنج محلی" }
        val cleanLoc = location.trim().ifBlank { _userLocation.value }
        val cleanStory = storyText.trim()

        val existing = getMyBooth()
        val booth = if (existing != null) {
            existing.copy(
                title = cleanTitle,
                specialty = cleanSpecialty,
                location = cleanLoc,
                storyTitle = cleanTitle,
                storyText = cleanStory.ifBlank { existing.storyText },
                address = address.trim(),
                workHours = workHours.trim().ifBlank { "همه‌روزه از ۸ صبح تا ۱۰ شب" },
                deliveryMethods = deliveryMethods.trim().ifBlank { "پست پیشتاز، تیپاکس، پیک شهری" },
                freeShippingThreshold = freeShippingThreshold,
                minOrderAmount = minOrderAmount,
                guaranteePolicy = guaranteePolicy.trim().ifBlank { "ضمانت اصالت و بازگشت کامل وجه در صورت عدم رضایت" },
                socialTelegram = socialTelegram.trim(),
                socialWhatsapp = socialWhatsapp.trim(),
                isOnline = isOnline,
                avatarUri = avatarUri ?: existing.avatarUri ?: _userAvatarUri.value,
                bannerUri = bannerUri ?: existing.bannerUri
            )
        } else {
            Merchant(
                id = "booth_$phone",
                name = name,
                title = cleanTitle,
                phone = phone,
                avatarEmoji = "🏪",
                avatarUri = avatarUri ?: _userAvatarUri.value,
                bannerUri = bannerUri,
                specialty = cleanSpecialty,
                location = cleanLoc,
                address = address.trim(),
                isOnline = isOnline,
                workHours = workHours.trim().ifBlank { "همه‌روزه از ۸ صبح تا ۱۰ شب" },
                deliveryMethods = deliveryMethods.trim().ifBlank { "پست پیشتاز، تیپاکس، پیک شهری" },
                freeShippingThreshold = freeShippingThreshold,
                minOrderAmount = minOrderAmount,
                guaranteePolicy = guaranteePolicy.trim().ifBlank { "ضمانت اصالت و بازگشت کامل وجه در صورت عدم رضایت" },
                socialTelegram = socialTelegram.trim(),
                socialWhatsapp = socialWhatsapp.trim(),
                knownCount = 0,
                storyTitle = cleanTitle,
                storyText = cleanStory.ifBlank { "غرفه معتبر و فعال در بازار کاسبان" },
                reviewsCount = 0,
                reviewsSummary = "",
                products = emptyList(),
                posts = emptyList(),
                isKnownByUser = false
            )
        }

        // Update role and shop title
        _userRole.value = "کاسب و تولیدکننده"
        _userShopTitle.value = cleanTitle

        val filtered = _merchants.value.filter { it.phone != phone && it.id != "booth_$phone" }
        val updatedList = listOf(booth) + filtered
        _merchants.value = updatedList
        database.saveMerchants(updatedList)

        // Also update account in persistent db
        val acc = database.findAccountByPhone(phone)
        if (acc != null) {
            val updatedAccounts = database.getAllAccounts().map {
                if (it.phone == phone) it.copy(role = "کاسب و تولیدکننده", shopTitle = cleanTitle) else it
            }
            database.saveAccounts(updatedAccounts)
        }

        // Persist booth changes to Cloud Firestore
        viewModelScope.launch {
            try {
                firestoreRepo.saveMerchantBooth(booth)
            } catch (e: Exception) {
                // Handled
            }
        }

        if (_selectedMerchant.value?.id == booth.id) {
            _selectedMerchant.value = booth
        }

        return booth
    }

    /**
     * Adds a product to the user's single booth and persists to database.
     */
    fun addProductToMyBooth(
        title: String,
        weight: String,
        price: Long,
        originalPrice: Long = 0L,
        category: String = "عمومی",
        description: String = ""
    ) {
        if (title.isBlank() || price <= 0) return
        val phone = _userPhone.value
        var booth = getMyBooth() ?: createOrUpdateMyBooth(_userShopTitle.value, "محصولات محلی", _userLocation.value, _userBio.value)

        val newProduct = MerchantProduct(
            id = "prod_${System.currentTimeMillis()}",
            merchantId = booth.id,
            title = title.trim(),
            weight = weight.trim().ifBlank { "۱ واحد" },
            price = price,
            originalPrice = originalPrice,
            isAvailable = true,
            category = category.trim().ifBlank { "عمومی" },
            description = description.trim()
        )

        val updatedProducts = listOf(newProduct) + booth.products
        booth = booth.copy(products = updatedProducts)

        val updatedList = _merchants.value.map { if (it.id == booth.id) booth else it }
        _merchants.value = updatedList
        database.saveMerchants(updatedList)

        // Save to Firestore
        viewModelScope.launch {
            try {
                firestoreRepo.saveProduct(booth.id, newProduct)
            } catch (e: Exception) {
                // Handled
            }
        }

        if (_selectedMerchant.value?.id == booth.id) {
            _selectedMerchant.value = booth
        }
    }

    /**
     * Toggles availability status of a product (موجود / ناموجود)
     */
    fun toggleProductAvailability(productId: String) {
        val booth = getMyBooth() ?: return
        val updatedProducts = booth.products.map {
            if (it.id == productId) it.copy(isAvailable = !it.isAvailable) else it
        }
        val updatedBooth = booth.copy(products = updatedProducts)

        val updatedList = _merchants.value.map { if (it.id == updatedBooth.id) updatedBooth else it }
        _merchants.value = updatedList
        database.saveMerchants(updatedList)

        val toggledProduct = updatedBooth.products.find { it.id == productId }
        if (toggledProduct != null) {
            viewModelScope.launch {
                try {
                    firestoreRepo.saveProduct(updatedBooth.id, toggledProduct)
                } catch (e: Exception) {
                    // Handled
                }
            }
        }

        if (_selectedMerchant.value?.id == updatedBooth.id) {
            _selectedMerchant.value = updatedBooth
        }
    }

    /**
     * Removes a product from the user's booth.
     */
    fun removeProductFromMyBooth(productId: String) {
        val booth = getMyBooth() ?: return
        val updatedProducts = booth.products.filter { it.id != productId }
        val updatedBooth = booth.copy(products = updatedProducts)

        val updatedList = _merchants.value.map { if (it.id == updatedBooth.id) updatedBooth else it }
        _merchants.value = updatedList
        database.saveMerchants(updatedList)

        viewModelScope.launch {
            try {
                firestoreRepo.deleteProduct(updatedBooth.id, productId)
            } catch (e: Exception) {
                // Handled
            }
        }

        if (_selectedMerchant.value?.id == updatedBooth.id) {
            _selectedMerchant.value = updatedBooth
        }
    }

    /**
     * Adds a post/story with text and photo to the user's booth and persists to database.
     */
    fun addPostToMyBooth(title: String, text: String, imageUri: String?) {
        if (text.isBlank()) return
        val phone = _userPhone.value
        var booth = getMyBooth() ?: createOrUpdateMyBooth(_userShopTitle.value, "محصولات محلی", _userLocation.value, _userBio.value)

        val newPost = BoothPost(
            id = "post_${System.currentTimeMillis()}",
            merchantId = booth.id,
            title = title.trim(),
            text = text.trim(),
            imageUri = imageUri,
            date = "هم‌اکنون",
            likesCount = 0
        )

        val updatedPosts = listOf(newPost) + booth.posts
        booth = booth.copy(posts = updatedPosts)

        val updatedList = _merchants.value.map { if (it.id == booth.id) booth else it }
        _merchants.value = updatedList
        database.saveMerchants(updatedList)

        viewModelScope.launch {
            try {
                firestoreRepo.savePost(booth.id, newPost)
            } catch (e: Exception) {
                // Handled
            }
        }

        if (_selectedMerchant.value?.id == booth.id) {
            _selectedMerchant.value = booth
        }
    }

    /**
     * Removes a post from the user's booth.
     */
    fun removePostFromMyBooth(postId: String) {
        val booth = getMyBooth() ?: return
        val updatedPosts = booth.posts.filter { it.id != postId }
        val updatedBooth = booth.copy(posts = updatedPosts)

        val updatedList = _merchants.value.map { if (it.id == updatedBooth.id) updatedBooth else it }
        _merchants.value = updatedList
        database.saveMerchants(updatedList)

        viewModelScope.launch {
            try {
                firestoreRepo.deletePost(updatedBooth.id, postId)
            } catch (e: Exception) {
                // Handled
            }
        }

        if (_selectedMerchant.value?.id == updatedBooth.id) {
            _selectedMerchant.value = updatedBooth
        }
    }

    // -----------------------------------------------------------------
    // Navigation & Screen Controls
    // -----------------------------------------------------------------

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

    fun selectCategory(cat: String?) {
        _selectedCategory.value = cat
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
        database.saveMerchants(_merchants.value)
        if (_selectedMerchant.value?.id == merchantId) {
            _selectedMerchant.value = _merchants.value.find { it.id == merchantId }
        }
    }

    // Chat actions
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

    fun toggleMutualFamiliarKnown(id: String) {
        _mutualFamiliars.value = _mutualFamiliars.value.map {
            if (it.id == id) it.copy(isKnown = !it.isKnown) else it
        }
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

        _merchants.value = _merchants.value.map { m ->
            if (m.id == targetMerchantId) {
                m.copy(reviewsCount = m.reviewsCount + 1)
            } else m
        }
        database.saveMerchants(_merchants.value)
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
🤝 دعوت به شبکه «کاسبان» (بازار معتمدین و تولیدکنندگان محلی)

خرید مستقیم و بدون واسطه از تولیدکنندگان و کسبه محلی.
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

        // Update database accounts
        val accounts = database.getAllAccounts().map {
            if (it.phone == _userPhone.value) {
                it.copy(name = finalName, shopTitle = finalShop, bio = finalBio, location = finalLoc)
            } else it
        }
        database.saveAccounts(accounts)

        // Update booth if existing
        val booth = getMyBooth()
        if (booth != null) {
            val updatedBooth = booth.copy(name = finalName, title = finalShop, location = finalLoc, storyText = finalBio)
            val updatedList = _merchants.value.map { if (it.id == updatedBooth.id) updatedBooth else it }
            _merchants.value = updatedList
            database.saveMerchants(updatedList)
        }
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
