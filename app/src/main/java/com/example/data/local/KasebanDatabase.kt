package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.BoothPost
import com.example.data.model.Merchant
import com.example.data.model.MerchantProduct
import com.example.data.model.UserAccount
import org.json.JSONArray
import org.json.JSONObject

/**
 * Robust Local Persistence Database for Kaseban App.
 * Manages registered users, official merchant booths, products, and posts.
 */
class KasebanDatabase(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("kaseban_local_storage_db", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCOUNTS = "kaseban_saved_accounts_list"
        private const val KEY_MERCHANTS = "kaseban_saved_merchants_list"
        private const val KEY_DB_VERSION = "kaseban_clean_schema_version"
        const val SUPER_ADMIN_PHONE = "128110"
        const val SUPER_ADMIN_PIN = "128110"
    }

    init {
        val currentVersion = prefs.getInt(KEY_DB_VERSION, 1)
        if (currentVersion < 2) {
            // Automatically clear legacy mock data from device storage
            prefs.edit()
                .remove(KEY_MERCHANTS)
                .putInt(KEY_DB_VERSION, 2)
                .apply()
        }
    }

    // -------------------------------------------------------------
    // User Accounts & Authentication
    // -------------------------------------------------------------

    @Synchronized
    fun getAllAccounts(): List<UserAccount> {
        val jsonStr = prefs.getString(KEY_ACCOUNTS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<UserAccount>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    UserAccount(
                        id = obj.optString("id", obj.optString("phone", "")),
                        phone = obj.optString("phone", ""),
                        pin = obj.optString("pin", ""),
                        name = obj.optString("name", ""),
                        role = obj.optString("role", "خریدار معتمد"),
                        referralCode = obj.optString("referralCode", ""),
                        shopTitle = obj.optString("shopTitle", ""),
                        bio = obj.optString("bio", ""),
                        location = obj.optString("location", "ایران")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    @Synchronized
    fun saveAccounts(accounts: List<UserAccount>) {
        val arr = JSONArray()
        for (acc in accounts) {
            val obj = JSONObject().apply {
                put("id", acc.id.ifBlank { acc.phone })
                put("phone", acc.phone)
                put("pin", acc.pin)
                put("name", acc.name)
                put("role", acc.role)
                put("referralCode", acc.referralCode)
                put("shopTitle", acc.shopTitle)
                put("bio", acc.bio)
                put("location", acc.location)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_ACCOUNTS, arr.toString()).apply()
    }

    @Synchronized
    fun findAccountByPhone(phone: String): UserAccount? {
        val cleanPhone = phone.trim()
        if (cleanPhone == SUPER_ADMIN_PHONE || cleanPhone == "09128110") {
            return UserAccount(
                id = SUPER_ADMIN_PHONE,
                phone = SUPER_ADMIN_PHONE,
                pin = SUPER_ADMIN_PIN,
                name = "مدیر کل سیستم",
                role = "مدیر کل",
                shopTitle = "مرکز فرماندهی بازار کاسبان",
                location = "تهران"
            )
        }
        return getAllAccounts().find { it.phone == cleanPhone }
    }

    @Synchronized
    fun registerAccount(account: UserAccount): String? {
        val cleanPhone = account.phone.trim()
        val cleanPin = account.pin.trim()

        if (cleanPhone.length < 5) {
            return "شماره تماس معتبر نیست."
        }
        if (cleanPin.length != 4 && cleanPin != SUPER_ADMIN_PIN) {
            return "رمز عبور باید ۴ رقم باشد."
        }

        val existing = getAllAccounts()
        if (existing.any { it.phone == cleanPhone }) {
            return "این شماره قبلاً ثبت‌نام شده است. لطفاً از برگه ورود استفاده کنید."
        }

        val updated = existing + account
        saveAccounts(updated)
        return null // Success
    }

    @Synchronized
    fun verifyPinAndLogin(phone: String, pin: String): Pair<UserAccount?, String?> {
        val cleanPhone = phone.trim()
        val cleanPin = pin.trim()

        // Super Admin Secret Login Check
        if ((cleanPhone == SUPER_ADMIN_PHONE || cleanPhone == "09128110") && cleanPin == SUPER_ADMIN_PIN) {
            val adminAccount = UserAccount(
                id = SUPER_ADMIN_PHONE,
                phone = SUPER_ADMIN_PHONE,
                pin = SUPER_ADMIN_PIN,
                name = "مدیر کل سیستم",
                role = "مدیر کل",
                shopTitle = "مرکز فرماندهی بازار کاسبان",
                location = "تهران"
            )
            return adminAccount to null
        }

        val account = findAccountByPhone(cleanPhone)
            ?: return null to "شماره تلفن همراه یافت نشد. لطفاً ابتدا ثبت‌نام کنید."

        if (account.pin != cleanPin) {
            return null to "رمز عددی ۴ رقمی اشتباه است."
        }
        return account to null // Success
    }

    // -------------------------------------------------------------
    // Permanent Real Merchants, Products, and Posts
    // -------------------------------------------------------------

    @Synchronized
    fun getAllMerchants(): List<Merchant> {
        val jsonStr = prefs.getString(KEY_MERCHANTS, null) ?: return emptyList()

        return try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<Merchant>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)

                // Parse products
                val prodArr = obj.optJSONArray("products") ?: JSONArray()
                val products = mutableListOf<MerchantProduct>()
                for (j in 0 until prodArr.length()) {
                    val p = prodArr.getJSONObject(j)
                    products.add(
                        MerchantProduct(
                            id = p.optString("id", "p_$j"),
                            title = p.optString("title", ""),
                            weight = p.optString("weight", ""),
                            price = p.optLong("price", 0L),
                            originalPrice = p.optLong("originalPrice", 0L),
                            isAvailable = p.optBoolean("isAvailable", true),
                            category = p.optString("category", "عمومی"),
                            description = p.optString("description", ""),
                            imageUrl = p.optString("imageUrl", ""),
                            merchantId = obj.optString("id", "")
                        )
                    )
                }

                // Parse posts
                val postArr = obj.optJSONArray("posts") ?: JSONArray()
                val posts = mutableListOf<BoothPost>()
                for (k in 0 until postArr.length()) {
                    val postObj = postArr.getJSONObject(k)
                    posts.add(
                        BoothPost(
                            id = postObj.optString("id", "post_$k"),
                            merchantId = postObj.optString("merchantId", obj.optString("id", "")),
                            title = postObj.optString("title", ""),
                            text = postObj.optString("text", ""),
                            imageUri = if (postObj.isNull("imageUri")) null else postObj.optString("imageUri"),
                            date = postObj.optString("date", "هم‌اکنون"),
                            likesCount = postObj.optInt("likesCount", 0)
                        )
                    )
                }

                list.add(
                    Merchant(
                        id = obj.optString("id", "m_$i"),
                        ownerId = obj.optString("ownerId", obj.optString("id", "")),
                        name = obj.optString("name", ""),
                        title = obj.optString("title", ""),
                        phone = obj.optString("phone", ""),
                        avatarEmoji = obj.optString("avatarEmoji", "🏪"),
                        avatarUri = if (obj.isNull("avatarUri")) null else obj.optString("avatarUri"),
                        bannerUri = if (obj.isNull("bannerUri")) null else obj.optString("bannerUri"),
                        specialty = obj.optString("specialty", "محصولات و دسترنج محلی"),
                        location = obj.optString("location", "ایران"),
                        address = obj.optString("address", ""),
                        isOnline = obj.optBoolean("isOnline", true),
                        isVerified = obj.optBoolean("isVerified", false),
                        isPinned = obj.optBoolean("isPinned", false),
                        workHours = obj.optString("workHours", "همه‌روزه از ۸ صبح تا ۱۰ شب"),
                        deliveryMethods = obj.optString("deliveryMethods", "پست پیشتاز، تیپاکس، پیک شهری"),
                        freeShippingThreshold = obj.optLong("freeShippingThreshold", 0L),
                        minOrderAmount = obj.optLong("minOrderAmount", 0L),
                        guaranteePolicy = obj.optString("guaranteePolicy", "ضمانت اصالت و بازگشت کامل وجه در صورت عدم رضایت"),
                        socialTelegram = obj.optString("socialTelegram", ""),
                        socialWhatsapp = obj.optString("socialWhatsapp", ""),
                        knownCount = obj.optInt("knownCount", 0),
                        knownByMutual = if (obj.isNull("knownByMutual")) null else obj.optString("knownByMutual"),
                        storyTitle = obj.optString("storyTitle", ""),
                        storyText = obj.optString("storyText", ""),
                        reviewsCount = obj.optInt("reviewsCount", 0),
                        reviewsSummary = obj.optString("reviewsSummary", ""),
                        rating = obj.optDouble("rating", 5.0),
                        hasReturnGuarantee = obj.optBoolean("hasReturnGuarantee", true),
                        isEcoFriendly = obj.optBoolean("isEcoFriendly", true),
                        isOrganicCertified = obj.optBoolean("isOrganicCertified", true),
                        products = products,
                        posts = posts,
                        isKnownByUser = false
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    @Synchronized
    fun saveMerchants(merchants: List<Merchant>) {
        val arr = JSONArray()
        for (m in merchants) {
            val obj = JSONObject().apply {
                put("id", m.id)
                put("ownerId", m.ownerId)
                put("name", m.name)
                put("title", m.title)
                put("phone", m.phone)
                put("avatarEmoji", m.avatarEmoji)
                put("avatarUri", m.avatarUri)
                put("bannerUri", m.bannerUri)
                put("specialty", m.specialty)
                put("location", m.location)
                put("address", m.address)
                put("isOnline", m.isOnline)
                put("isVerified", m.isVerified)
                put("isPinned", m.isPinned)
                put("workHours", m.workHours)
                put("deliveryMethods", m.deliveryMethods)
                put("freeShippingThreshold", m.freeShippingThreshold)
                put("minOrderAmount", m.minOrderAmount)
                put("guaranteePolicy", m.guaranteePolicy)
                put("socialTelegram", m.socialTelegram)
                put("socialWhatsapp", m.socialWhatsapp)
                put("knownCount", m.knownCount)
                put("knownByMutual", m.knownByMutual)
                put("storyTitle", m.storyTitle)
                put("storyText", m.storyText)
                put("reviewsCount", m.reviewsCount)
                put("reviewsSummary", m.reviewsSummary)
                put("rating", m.rating)
                put("hasReturnGuarantee", m.hasReturnGuarantee)
                put("isEcoFriendly", m.isEcoFriendly)
                put("isOrganicCertified", m.isOrganicCertified)

                // Save products
                val prodArr = JSONArray()
                for (p in m.products) {
                    val pObj = JSONObject().apply {
                        put("id", p.id)
                        put("title", p.title)
                        put("weight", p.weight)
                        put("price", p.price)
                        put("originalPrice", p.originalPrice)
                        put("isAvailable", p.isAvailable)
                        put("category", p.category)
                        put("description", p.description)
                        put("imageUrl", p.imageUrl)
                    }
                    prodArr.put(pObj)
                }
                put("products", prodArr)

                // Save posts
                val postArr = JSONArray()
                for (post in m.posts) {
                    val postObj = JSONObject().apply {
                        put("id", post.id)
                        put("merchantId", post.merchantId)
                        put("title", post.title)
                        put("text", post.text)
                        put("imageUri", post.imageUri)
                        put("date", post.date)
                        put("likesCount", post.likesCount)
                    }
                    postArr.put(postObj)
                }
                put("posts", postArr)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_MERCHANTS, arr.toString()).apply()
    }

    // -------------------------------------------------------------
    // Super Admin Management Actions
    // -------------------------------------------------------------

    @Synchronized
    fun toggleMerchantVerified(merchantId: String): Boolean {
        val current = getAllMerchants()
        var newStatus = false
        val updated = current.map {
            if (it.id == merchantId) {
                newStatus = !it.isVerified
                it.copy(isVerified = newStatus)
            } else it
        }
        saveMerchants(updated)
        return newStatus
    }

    @Synchronized
    fun toggleMerchantPinned(merchantId: String): Boolean {
        val current = getAllMerchants()
        var newStatus = false
        val updated = current.map {
            if (it.id == merchantId) {
                newStatus = !it.isPinned
                it.copy(isPinned = newStatus)
            } else it
        }
        saveMerchants(updated)
        return newStatus
    }

    @Synchronized
    fun deleteMerchant(merchantId: String): Boolean {
        val current = getAllMerchants()
        val updated = current.filter { it.id != merchantId }
        saveMerchants(updated)
        return true
    }

    @Synchronized
    fun deleteAccount(phone: String): Boolean {
        val current = getAllAccounts()
        val updated = current.filter { it.phone != phone }
        saveAccounts(updated)
        return true
    }

    @Synchronized
    fun updateAccountRole(phone: String, newRole: String): Boolean {
        val current = getAllAccounts()
        val updated = current.map {
            if (it.phone == phone) it.copy(role = newRole) else it
        }
        saveAccounts(updated)
        return true
    }

    @Synchronized
    fun updateAccountPin(phone: String, newPin: String): Boolean {
        val current = getAllAccounts()
        val updated = current.map {
            if (it.phone == phone) it.copy(pin = newPin) else it
        }
        saveAccounts(updated)
        return true
    }

    @Synchronized
    fun clearAllMerchants() {
        prefs.edit().remove(KEY_MERCHANTS).apply()
    }
}
