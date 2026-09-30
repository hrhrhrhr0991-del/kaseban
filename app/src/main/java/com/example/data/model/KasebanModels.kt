package com.example.data.model

data class MerchantProduct(
    val id: String,
    val title: String,
    val weight: String,
    val price: Long,
    val isAvailable: Boolean = true
)

data class BoothPost(
    val id: String,
    val merchantId: String,
    val title: String = "",
    val text: String,
    val imageUri: String? = null,
    val date: String = "هم‌اکنون",
    val likesCount: Int = 0
)

data class UserAccount(
    val phone: String,
    val pin: String,
    val name: String,
    val role: String,
    val referralCode: String = "",
    val shopTitle: String = "",
    val bio: String = "",
    val location: String = "ایران"
)

data class Merchant(
    val id: String,
    val name: String,
    val title: String,
    val phone: String = "",
    val avatarEmoji: String = "🏪",
    val avatarUri: String? = null,
    val specialty: String,
    val location: String,
    val isOnline: Boolean = true,
    val knownCount: Int = 0,
    val knownByMutual: String? = null,
    val storyTitle: String = "",
    val storyText: String = "",
    val reviewsCount: Int = 0,
    val reviewsSummary: String = "",
    val products: List<MerchantProduct> = emptyList(),
    val posts: List<BoothPost> = emptyList(),
    var isKnownByUser: Boolean = false
) {
    fun matchesCategory(category: String): Boolean {
        return when (category.trim()) {
            "نان و شیرینی" -> specialty.contains("نان") || specialty.contains("شیرینی") || specialty.contains("اگردک") || title.contains("نان") || title.contains("شیرینی")
            "عسل و مربا" -> specialty.contains("عسل") || specialty.contains("مربا") || specialty.contains("زنبور") || title.contains("عسل")
            "خشکبار و مغزها" -> specialty.contains("خشکبار") || specialty.contains("پسته") || specialty.contains("گردو") || specialty.contains("مغز") || title.contains("خشکبار") || title.contains("پسته")
            "گیاهی و عرقیات" -> specialty.contains("گلاب") || specialty.contains("عرقیات") || specialty.contains("گیاهی") || title.contains("گلاب") || title.contains("عرقیات")
            "لبنیات و پنیر" -> specialty.contains("لبنیات") || specialty.contains("پنیر") || specialty.contains("کره") || title.contains("لبنیات") || title.contains("پنیر")
            "روغن و چاشنی" -> specialty.contains("ارده") || specialty.contains("روغن") || specialty.contains("چاشنی") || title.contains("ارده") || title.contains("روغن")
            "میوه و ارگانیک" -> specialty.contains("میوه") || specialty.contains("انار") || specialty.contains("ارگانیک") || title.contains("میوه") || title.contains("انار")
            "صنایع دستی" -> specialty.contains("صنایع دستی") || specialty.contains("مس") || specialty.contains("فیروزه") || specialty.contains("قلم‌زنی") || title.contains("صنایع دستی")
            else -> specialty.contains(category) || title.contains(category)
        }
    }
}

data class DirectChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val time: String,
    val isFromUser: Boolean,
    val hasPaymentRequest: Boolean = false,
    val paymentAmount: Long = 0L,
    val isPaymentPaid: Boolean = false
)

data class WalletTransaction(
    val id: String,
    val title: String,
    val amount: Long,
    val isDeposit: Boolean,
    val date: String
)

data class UserAddress(
    val id: String,
    val title: String,
    val address: String,
    val phone: String,
    val isDefault: Boolean = true
)

data class MutualFamiliar(
    val id: String,
    val name: String,
    val avatarEmoji: String,
    val avatarUri: String? = null,
    val durationText: String,
    var isKnown: Boolean = true
)

data class MerchantReview(
    val id: String,
    val authorName: String,
    val avatarEmoji: String,
    val rating: Int = 5,
    val comment: String,
    val date: String
)

data class FeaturedBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val imageUrl: String,
    val merchantId: String
)

object SampleKasebanData {
    val merchants: List<Merchant> = emptyList()
    val sampleFeaturedBanners: List<FeaturedBanner> = emptyList()
}
