package com.example.data.model

data class Product(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val price: Long,
    val originalPrice: Long? = null,
    val unit: String = "بسته",
    val tag: String? = null,
    val rating: Float = 4.9f,
    val calories: String? = null,
    val inStock: Boolean = true,
    val iconType: String = "bread"
)

object SampleProducts {
    val items = listOf(
        Product(
            id = "gnd_01",
            title = "نان خمیرترش روستایی گندم کامل",
            category = "نان سنتی و خمیرترش",
            description = "پخت سنتی با آرد سبوس‌دار سنگی و تخمیر آرام ۳۶ ساعته، ترد و خوش‌هضم بدون مواد افزودنی",
            price = 68000,
            originalPrice = 85000,
            unit = "قرص بزرگ",
            tag = "پرفروش",
            rating = 5.0f,
            calories = "۲۱۰ کیلوکالری در ۱۰۰ گرم",
            iconType = "bread"
        ),
        Product(
            id = "gnd_02",
            title = "عسل کوهستان دامنه‌های سبلان",
            category = "عسل طبیعی و ارگانیک",
            description = "عسل خام و حرارت ندیده با برگه آزمایش ساکاروز زیر ۲ درصد، سرشار از آنزیم‌های زنده و پرخاصیت",
            price = 390000,
            originalPrice = 450000,
            unit = "شیشه ۹۰۰ گرمی",
            tag = "۱۰۰٪ ارگانیک",
            rating = 4.9f,
            calories = "طبیعی و غنی از انرژی",
            iconType = "honey"
        ),
        Product(
            id = "gnd_03",
            title = "کوکی پروتئینی جو دوسر و بادام",
            category = "شیرینی و دسر سالم",
            description = "شیرین شده با شیره توت خالص، بدون شکر تصفیه شده و روغن‌های صنعتی، مناسب ورزشکاران",
            price = 95000,
            unit = "بسته ۶ عددی",
            tag = "رژیمی و مقوی",
            rating = 4.8f,
            calories = "۱۴۰ کیلوکالری هر عدد",
            iconType = "cookie"
        ),
        Product(
            id = "gnd_04",
            title = "نان چاودار رژیمی (روگن)",
            category = "نان سنتی و خمیرترش",
            description = "غنی از فیبر طبیعی و مناسب افراد دیابتی با شاخص گلایسمی پایین، ماندگاری بالا در یخچال",
            price = 75000,
            unit = "قرص اسلایس شده",
            tag = "ویژه دیابت",
            rating = 4.9f,
            calories = "۱۸۰ کیلوکالری در ۱۰۰ گرم",
            iconType = "bread"
        ),
        Product(
            id = "gnd_05",
            title = "دمنوش گل‌گاو‌زبان و به‌لیمو کوهی",
            category = "دمنوش و سلامت",
            description = "ترکیب آرام‌بخش گیاهان دارویی دست‌چین شده از مراتع زاگرس با بسته‌بندی زیپ‌کیپ معطر",
            price = 110000,
            originalPrice = 135000,
            unit = "بسته ۱۵۰ گرمی",
            tag = "دست‌چین طبیعی",
            rating = 4.7f,
            iconType = "tea"
        ),
        Product(
            id = "gnd_06",
            title = "شیرینی نخودچی سنتی با روغن کرمانشاهی",
            category = "شیرینی و دسر سالم",
            description = "تهیه شده از آرد نخودچی مرغوب دو بار تفت‌داده و زعفران قائنات با عطر مسحورکننده روغن اصیل",
            price = 185000,
            unit = "جعبه نیم‌کیلویی",
            tag = "اصیل و مجلسی",
            rating = 5.0f,
            iconType = "cake"
        ),
        Product(
            id = "gnd_07",
            title = "روغن زیتون فرابکر پرس سرد رودبار",
            category = "دمنوش و سلامت",
            description = "عصاره اول زیتون ارگانیک با اسیدیته زیر ۰.۵ درصد، طعم میوه‌ای اصیل مناسب سالاد و پخت ملایم",
            price = 320000,
            unit = "بطری شیشه‌ای ۷۵۰ میلی",
            tag = "پرس سرد اول",
            rating = 4.9f,
            iconType = "oil"
        ),
        Product(
            id = "gnd_08",
            title = "پک سوغات و پذیرایی اعلای گندما",
            category = "بسته‌های هدیه",
            description = "شامل نان سوخاری جو دوسر، شیشه کوچک عسل مینیاتوری، کوکی کنجدی و جعبه چوبی حکاکی شده لوکس",
            price = 540000,
            originalPrice = 620000,
            unit = "پک هدیه لوکس",
            tag = "جعبه اختصاصی",
            rating = 5.0f,
            iconType = "wheat"
        )
    )

    val categories = listOf(
        "همه محصولات",
        "نان سنتی و خمیرترش",
        "عسل طبیعی و ارگانیک",
        "شیرینی و دسر سالم",
        "دمنوش و سلامت",
        "بسته‌های هدیه"
    )
}
