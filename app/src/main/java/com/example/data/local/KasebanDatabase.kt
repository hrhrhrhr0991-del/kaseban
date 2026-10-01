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
 * Persistent local storage for Kaseban marketplace:
 * - Permanent User Accounts protected with 4-digit numeric PINs.
 * - Permanent Merchants, Booths, Products, and Posts that persist across app restarts and logouts.
 */
class KasebanDatabase(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("kaseban_persistent_db", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCOUNTS = "persistent_user_accounts"
        private const val KEY_MERCHANTS = "persistent_merchants_list"
    }

    // -------------------------------------------------------------
    // User Accounts & 4-Digit Numeric PIN Management
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
        return getAllAccounts().find { it.phone == cleanPhone }
    }

    @Synchronized
    fun registerAccount(account: UserAccount): String? {
        val cleanPhone = account.phone.trim()
        if (cleanPhone.length < 10) {
            return "شماره موبایل باید حداقل ۱۰ رقم باشد."
        }
        if (account.pin.length != 4 || !account.pin.all { it.isDigit() }) {
            return "رمز عبور باید دقیقاً یک پین عددی ۴ رقمی باشد."
        }
        val existing = findAccountByPhone(cleanPhone)
        if (existing != null) {
            return "این شماره تلفن قبلاً ثبت‌نام شده است. لطفاً وارد شوید."
        }
        val current = getAllAccounts().toMutableList()
        current.add(account)
        saveAccounts(current)
        return null // Success
    }

    @Synchronized
    fun verifyPinAndLogin(phone: String, pin: String): Pair<UserAccount?, String?> {
        val cleanPhone = phone.trim()
        val cleanPin = pin.trim()
        val account = findAccountByPhone(cleanPhone)
            ?: return null to "شماره موبایل یافت نشد. لطفاً ابتدا ثبت‌نام کنید."

        if (account.pin != cleanPin) {
            return null to "رمز عددی ۴ رقمی اشتباه است."
        }
        return account to null // Success
    }

    // -------------------------------------------------------------
    // Permanent Merchants, Booths, Products, and Posts
    // -------------------------------------------------------------

    @Synchronized
    fun getAllMerchants(): List<Merchant> {
        val jsonStr = prefs.getString(KEY_MERCHANTS, null)
        if (jsonStr.isNullOrBlank()) {
            val defaultList = getDefaultSeedMerchants()
            saveMerchants(defaultList)
            return defaultList
        }

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
                            description = p.optString("description", "")
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
                        name = obj.optString("name", ""),
                        title = obj.optString("title", ""),
                        phone = obj.optString("phone", ""),
                        avatarEmoji = obj.optString("avatarEmoji", "🏪"),
                        avatarUri = if (obj.isNull("avatarUri")) null else obj.optString("avatarUri"),
                        bannerUri = if (obj.isNull("bannerUri")) null else obj.optString("bannerUri"),
                        specialty = obj.optString("specialty", "عمومی"),
                        location = obj.optString("location", "ایران"),
                        address = obj.optString("address", ""),
                        isOnline = obj.optBoolean("isOnline", true),
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
                        products = products,
                        posts = posts,
                        isKnownByUser = false
                    )
                )
            }
            if (list.isEmpty()) {
                val defaultList = getDefaultSeedMerchants()
                saveMerchants(defaultList)
                defaultList
            } else {
                list
            }
        } catch (e: Exception) {
            val defaultList = getDefaultSeedMerchants()
            saveMerchants(defaultList)
            defaultList
        }
    }

    @Synchronized
    fun saveMerchants(merchants: List<Merchant>) {
        val arr = JSONArray()
        for (m in merchants) {
            val obj = JSONObject().apply {
                put("id", m.id)
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

    private fun getDefaultSeedMerchants(): List<Merchant> {
        return listOf(
            Merchant(
                id = "m_1",
                name = "حاج قاسم نانوایی",
                title = "نان و شیرینی سنتی کاک و اگردک",
                phone = "09121111111",
                avatarEmoji = "🥐",
                avatarUri = null,
                specialty = "نان‌های سنتی، اگردک زعفرانی و کاک کرمانشاه",
                location = "قزوین، محله دباغان",
                isOnline = true,
                knownCount = 42,
                knownByMutual = "معرفی شده توسط حاج رضا عطار",
                storyTitle = "پخت سنتی با تنور گلی و هیزم",
                storyText = "بیش از ۳۵ سال است که با آرد سبوس‌دار گندم دیم و روغن حیوانی کرمانشاهی، نان و شیرینی سنتی دست‌پخت مادربزرگ را زنده نگه داشته‌ایم.",
                reviewsCount = 28,
                reviewsSummary = "۴.۹ از ۵ (بسیار خوش‌طعم و تازه)",
                products = listOf(
                    MerchantProduct("p_101", "نان اگردک زعفرانی قزوین", "بسته ۶ عددی", 65000L),
                    MerchantProduct("p_102", "کاک سنتی با روغن حیوانی", "جعبه نیم کیلویی", 120000L),
                    MerchantProduct("p_103", "کلوچه فومن گردویی اصل", "بسته ۴ عددی", 55000L)
                ),
                posts = listOf(
                    BoothPost(
                        id = "post_101",
                        merchantId = "m_1",
                        title = "پخت داغ صبحگاهی اگردک",
                        text = "همین الان تنور گلی داغ شد و اولین سینی اگردک زعفرانی با شیر محلی و هل تازه بیرون آمد. نوش جان همسایگان و خریداران گرامی.",
                        imageUri = null,
                        date = "امروز صبح",
                        likesCount = 19
                    )
                )
            ),
            Merchant(
                id = "m_2",
                name = "حاج حسین عسل‌فروش",
                title = "عسل طبیعی سبلان و ژل رویال",
                phone = "09122222222",
                avatarEmoji = "🍯",
                avatarUri = null,
                specialty = "عسل گون کوهستان، عسل آویشن و ژل رویال اصل",
                location = "اردبیل، دامنه سرسبز سبلان",
                isOnline = true,
                knownCount = 68,
                knownByMutual = "آشنایی خانوادگی و ضمانت کیفیت",
                storyTitle = "زنبورداری طبیعی در ارتفاعات ۲۰۰۰ متری",
                storyText = "کندوهای ما بدون استفاده از هیچ‌گونه شکر یا اسانس در مراتع بکر گون و آویشن کوه سبلان نگهداری می‌شوند. با برگه آزمایش ساکارز زیر ۲ درصد.",
                reviewsCount = 45,
                reviewsSummary = "۵.۰ از ۵ (عطر و طعم فوق‌العاده)",
                products = listOf(
                    MerchantProduct("p_201", "عسل طبیعی گون سبلان", "شیشه ۱ کیلوگرم", 380000L),
                    MerchantProduct("p_202", "عسل وحشی کوهی صخره‌ای", "شیشه ۹۰۰ گرم", 490000L),
                    MerchantProduct("p_203", "ژل رویال خالص ایرانی", "پوکه ۲۰ گرمی", 240000L)
                ),
                posts = listOf(
                    BoothPost(
                        id = "post_201",
                        merchantId = "m_2",
                        title = "برداشت عسل آویشن بهاره",
                        text = "برداشت پربار عسل آویشن با عطر تند گیاهان کوهی به پایان رسید. بسته‌بندی‌ها آماده ارسال مستقیم به سراسر کشور با ضمانت مرجوعی است.",
                        imageUri = null,
                        date = "دیروز",
                        likesCount = 34
                    )
                )
            ),
            Merchant(
                id = "m_3",
                name = "مشهدی رضا قائناتی",
                title = "زعفران سوپرنگین و پسته قائنات",
                phone = "09123333333",
                avatarEmoji = "🌸",
                avatarUri = null,
                specialty = "زعفران صادراتی قائنات، پسته کله‌قوچی و اکبری",
                location = "خراسان جنوبی، شهر قائنات",
                isOnline = true,
                knownCount = 53,
                knownByMutual = "تأیید شده در شبکه اصناف سنتی",
                storyTitle = "دسترنج مستقیم کشاورز بدون واسطه",
                storyText = "گل‌های زعفران با طلوع آفتاب چیده شده و همان روز به روش سنتی خشک می‌گردند تا بالاترین رنگ‌دهی و عطر کروسین حفظ شود.",
                reviewsCount = 39,
                reviewsSummary = "۴.۸ از ۵ (رنگ‌دهی استثنایی)",
                products = listOf(
                    MerchantProduct("p_301", "زعفران سوپرنگین درجه یک", "یک مثقال (۴.۶ گرم)", 340000L),
                    MerchantProduct("p_302", "پسته خندان دست‌چین زعفرانی", "بسته ۵۰۰ گرمی", 380000L),
                    MerchantProduct("p_303", "مغز گردوی تویسرکان تازه", "بسته ۵۰۰ گرمی", 290000L)
                ),
                posts = listOf(
                    BoothPost(
                        id = "post_301",
                        merchantId = "m_3",
                        title = "آغاز چینش گل‌های زعفران",
                        text = "با عنایت حق، فصل برداشت زعفران امسال آغاز شد. کیفیت قلمه‌ها به دلیل سرمای مناسب امسال در بالاترین سطح کیفی است.",
                        imageUri = null,
                        date = "۳ روز پیش",
                        likesCount = 27
                    )
                )
            ),
            Merchant(
                id = "m_4",
                name = "بانو فاطمه کاشانی",
                title = "گلاب دوآتیشه و عرقیات سنتی قمصر",
                phone = "09124444444",
                avatarEmoji = "🌿",
                avatarUri = null,
                specialty = "گلاب ناب محمدی، عرق نعنا دوآتیشه، هل و گل‌گاوزبان",
                location = "کاشان، باغ‌های قمصر",
                isOnline = true,
                knownCount = 37,
                knownByMutual = "توصیه شده توسط مشتریان محلی",
                storyTitle = "تقطیر با دیگ‌های سنتی مسی",
                storyText = "ما گلاب و عرقیات را با گل محمدی تازه چیده شده صبحگاهی و در دیگ‌های سنتی مسی تقطیر می‌کنیم، بدون ذره‌ای مواد نگهدارنده.",
                reviewsCount = 22,
                reviewsSummary = "۴.۹ از ۵ (خالص و بدون اسانس)",
                products = listOf(
                    MerchantProduct("p_401", "گلاب دوآتیشه درجه یک قمصر", "بطری ۱ لیتری شیشه‌ای", 145000L),
                    MerchantProduct("p_402", "عرق نعنا دوآتیشه سنگین", "بطری ۱ لیتری", 85000L),
                    MerchantProduct("p_403", "عرق بهارنارنج شیراز", "بطری ۱ لیتری", 95000L)
                ),
                posts = listOf(
                    BoothPost(
                        id = "post_401",
                        merchantId = "m_4",
                        title = "دیگ‌های مسی گلاب‌گیری",
                        text = "بخار عطرآگین گل محمدی در فضای کارگاه سنتی پیچیده است. گلاب‌های تازه کشیده شده آماده سفارش هستند.",
                        imageUri = null,
                        date = "هفته پیش",
                        likesCount = 21
                    )
                )
            ),
            Merchant(
                id = "m_5",
                name = "میرزا یوسف آذربایجانی",
                title = "پنیر لیقوان اصیل و سرشیر محلی",
                phone = "09125555555",
                avatarEmoji = "🧀",
                avatarUri = null,
                specialty = "پنیر گوسفندی کهنه لیقوان، کره سنتی و سرشیر خالص",
                location = "تبریز، روستای ییلاقی لیقوان",
                isOnline = true,
                knownCount = 59,
                knownByMutual = "معتمد بازار سرپوشیده تبریز",
                storyTitle = "رسیده در غارهای خنک طبیعی لیقوان",
                storyText = "پنیر گوسفندی ما حداقل ۶ ماه در آب نمک طبیعی داخل غارهای کوه سهند استراحت می‌کند تا بافتی نرم، چرب و بی‌نظیر پیدا کند.",
                reviewsCount = 33,
                reviewsSummary = "۵.۰ از ۵ (طعم ماندگار پنیر اصیل)",
                products = listOf(
                    MerchantProduct("p_501", "پنیر گوسفندی سوپر لیقوان", "حلب ۱ کیلوگرمی", 270000L),
                    MerchantProduct("p_502", "کره محلی اعلا گوسفندی", "بسته ۵۰۰ گرمی", 195000L),
                    MerchantProduct("p_503", "روغن زرد حیوانی تبریز", "شیشه ۹۰۰ گرمی", 420000L)
                ),
                posts = listOf(
                    BoothPost(
                        id = "post_501",
                        merchantId = "m_5",
                        title = "بازگشایی غار نگهداری پنیرهای کهنه",
                        text = "پنیرهای رسیده شش‌ماهه با کیفیت استثنایی از غار خارج و آماده تحویل به مشتریان اهل ذوق شد.",
                        imageUri = null,
                        date = "۲ روز پیش",
                        likesCount = 41
                    )
                )
            )
        )
    }
}
