package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun askAssistant(userPrompt: String, history: List<Pair<String, Boolean>> = emptyList()): String {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val systemPrompt = """
            شما «مشاور هوشمند کاسبان» هستید؛ مشاور شبکه اجتماعی و بازار آدم‌های معتمد «کاسبان».
            کاسبان پلتفرمی برای ارتباط مستقیم و بدون واسطه بین خریداران و تولیدکنندگان/کاسبان معتمد محلی است (مانند حامد آقاجانی از برزک کاشان تولیدکننده گلاب ۲۲ کیلوگرم و عرقیات سنتی، خانم ضیائی تولیدکننده نان‌های سبوس‌دار خمیرترش، فاطمه محمودی کیک و شیرینی خانگی و اگردک، سید مرتضی رضوی حلوا ارده سنتی).
            کاربران می‌توانند در چت با کاسب‌ها گفتگو کرده، فاکتور سفارش دریافت کنند و با کیف پول پرداخت کنند.
            پاسخ‌های شما باید به زبان فارسی گرم، محترمانه، راهگشا و متناسب با فرهنگ ایرانی و اعتماد محلی باشد.
        """.trimIndent()

        val contentsList = mutableListOf<GeminiContent>()

        // Add history turns if available
        history.takeLast(4).forEach { (msg, isUser) ->
            contentsList.add(
                GeminiContent(
                    role = if (isUser) "user" else "model",
                    parts = listOf(GeminiPart(text = msg))
                )
            )
        }

        // Add current user prompt
        contentsList.add(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = userPrompt))
            )
        )

        val request = GeminiRequest(
            contents = contentsList,
            systemInstruction = GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = systemPrompt))
            )
        )

        return try {
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                getSmartFallbackResponse(userPrompt)
            } else {
                val response = service.generateContent(apiKey, request)
                response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: getSmartFallbackResponse(userPrompt)
            }
        } catch (e: Exception) {
            getSmartFallbackResponse(userPrompt)
        }
    }

    private fun getSmartFallbackResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("کاسب") || lower.contains("آشنا") || lower.contains("اعتماد") -> {
                "🤝 در شبکه کاسبان، هر کاسب توسط افراد واقعی معرفی و تایید می‌شود. برای مثال حامد آقاجانی با +۱۴۳ تایید اعتماد، عرقیات سنتی برزک و برنج کامفیروز را مستقیم عرضه می‌کند. شما هم با زدن دکمه «می‌شناسم» می‌توانید اعتبار کاسب‌های آشنای خود را بالا ببرید!"
            }
            lower.contains("گلاب") || lower.contains("عرق") || lower.contains("حامد") -> {
                "🌸 گلاب عیار ۲۲ کیلو گل حامد آقاجانی در دیگ و پارچ سنتی برزک کاشان تقطیر شده و عطر ماندگار و غلظت خالص دارد. همچنین عرق نعناع و بهارنارنج او برای تقویت اعصاب و گوارش فوق‌العاده است."
            }
            lower.contains("نان") || lower.contains("خمیرترش") || lower.contains("ضیائی") -> {
                "🌾 نان‌های سبوس‌دار خانم ضیائی با تخمیر طولانی ۳۶ ساعته خمیرترش و آرد کامل بدون سبوس‌گیری پخته می‌شوند؛ بسیار سبک، پرخاصیت و مناسب برای کنترل قند خون و گوارش راحت!"
            }
            lower.contains("کیف پول") || lower.contains("پرداخت") || lower.contains("شارژ") -> {
                "💳 با کیف پول کاسبان، پس از توافق در چت اختصاصی و صدور فاکتور توسط کاسب، مبلغ با یک لمس امن پرداخت می‌شود. شما در بخش نمایه و کیف پول می‌توانید موجودی خود را آنی شارژ کرده و سوابق تراکنش‌ها را مشاهده کنید."
            }
            lower.contains("ارسال") || lower.contains("سفارش") || lower.contains("چت") -> {
                "💬 برای سفارش، وارد صفحه کاسب مورد نظر شوید یا از تب «گفتگوها» پیام دهید. کاسب‌ها فاکتور محصولات انتخابی را مستقیم در صفحه چت صادر می‌کنند و سفارش با هماهنگی به آدرس‌تان ارسال می‌گردد."
            }
            else -> {
                "درود و احترام! 🤝 من مشاور هوشمند شبکه «کاسبان» هستم. می‌توانید درباره شناخت کاسبان معتمد محلی، سفارش مستقیم عرقیات، نان‌های سالم، کار با کیف پول یا نحوه ثبت کسب‌وکار خود از من بپرسید."
            }
        }
    }
}
