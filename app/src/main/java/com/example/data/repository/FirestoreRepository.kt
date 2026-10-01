package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.BoothPost
import com.example.data.model.Merchant
import com.example.data.model.MerchantProduct
import com.example.data.model.UserAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

class FirestoreRepository(
    context: Context,
    firestoreInstance: FirebaseFirestore? = null,
    authInstance: FirebaseAuth? = null
) {
    private val databaseId = try {
        context.getString(R.string.firestore_database_id)
    } catch (e: Exception) {
        "ai-studio-android-glassflo-4f64c7af-3fc5-48f1-ad43-de25a23afc82"
    }

    val db: FirebaseFirestore = firestoreInstance ?: FirebaseFirestore.getInstance(databaseId)
    val auth: FirebaseAuth = authInstance ?: Firebase.auth

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("کاربر وارد نشده است. لطفاً ابتدا وارد شوید.")
    }

    // -------------------------------------------------------------
    // Realtime Merchants Stream (with subcollections for products & posts)
    // -------------------------------------------------------------
    fun observeMerchants(): Flow<List<Merchant>> = callbackFlow {
        val merchantsCollection = db.collection("merchants")
        val listener = merchantsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, "merchants")
                close(error)
                return@addSnapshotListener
            }

            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val merchantDocs = snapshot.documents
            if (merchantDocs.isEmpty()) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            // For each merchant document, read main properties
            val merchantsList = mutableListOf<Merchant>()
            var pendingMerchants = merchantDocs.size

            for (doc in merchantDocs) {
                val merchantId = doc.id
                val name = doc.getString("name") ?: ""
                val title = doc.getString("title") ?: ""
                val phone = doc.getString("phone") ?: ""
                val ownerId = doc.getString("ownerId") ?: merchantId
                val avatarEmoji = doc.getString("avatarEmoji") ?: "🏪"
                val avatarUri = doc.getString("avatarUri")
                val bannerUri = doc.getString("bannerUri")
                val specialty = doc.getString("specialty") ?: "محصولات روستایی و سنتی"
                val location = doc.getString("location") ?: "ایران"
                val address = doc.getString("address") ?: ""
                val workHours = doc.getString("workHours") ?: "همه‌روزه از ۸ صبح تا ۱۰ شب"
                val deliveryMethods = doc.getString("deliveryMethods") ?: "پست پیشتاز، تیپاکس، پیک شهری"
                val guaranteePolicy = doc.getString("guaranteePolicy") ?: "ضمانت اصالت و بازگشت کامل وجه در صورت عدم رضایت"
                val socialTelegram = doc.getString("socialTelegram") ?: ""
                val socialWhatsapp = doc.getString("socialWhatsapp") ?: ""
                val storyTitle = doc.getString("storyTitle") ?: ""
                val storyText = doc.getString("storyText") ?: ""
                val reviewsSummary = doc.getString("reviewsSummary") ?: ""
                val rating = doc.getDouble("rating") ?: 5.0
                val reviewsCount = (doc.getLong("reviewsCount") ?: 0L).toInt()
                val isOnline = doc.getBoolean("isOnline") ?: true
                val isVerified = doc.getBoolean("isVerified") ?: true
                val hasReturnGuarantee = doc.getBoolean("hasReturnGuarantee") ?: true
                val isEcoFriendly = doc.getBoolean("isEcoFriendly") ?: true
                val isOrganicCertified = doc.getBoolean("isOrganicCertified") ?: true

                // Load subcollections for products & posts
                doc.reference.collection("products").get().addOnCompleteListener { prodTask ->
                    val products = mutableListOf<MerchantProduct>()
                    if (prodTask.isSuccessful && prodTask.result != null) {
                        for (pDoc in prodTask.result.documents) {
                            val pId = pDoc.id
                            val pTitle = pDoc.getString("title") ?: ""
                            val pWeight = pDoc.getString("weight") ?: ""
                            val pPrice = pDoc.getLong("price") ?: 0L
                            val pOriginalPrice = pDoc.getLong("originalPrice") ?: 0L
                            val pAvailable = pDoc.getBoolean("isAvailable") ?: true
                            val pCat = pDoc.getString("category") ?: "عمومی"
                            val pDesc = pDoc.getString("description") ?: ""
                            val pImg = pDoc.getString("imageUrl") ?: ""
                            products.add(
                                MerchantProduct(
                                    id = pId,
                                    merchantId = merchantId,
                                    title = pTitle,
                                    weight = pWeight,
                                    price = pPrice,
                                    originalPrice = pOriginalPrice,
                                    isAvailable = pAvailable,
                                    category = pCat,
                                    description = pDesc,
                                    imageUrl = pImg
                                )
                            )
                        }
                    }

                    doc.reference.collection("posts").get().addOnCompleteListener { postTask ->
                        val posts = mutableListOf<BoothPost>()
                        if (postTask.isSuccessful && postTask.result != null) {
                            for (postDoc in postTask.result.documents) {
                                val postId = postDoc.id
                                val postTitle = postDoc.getString("title") ?: ""
                                val postText = postDoc.getString("description") ?: ""
                                val postImageUri = postDoc.getString("imageUri")
                                val postLikes = (postDoc.getLong("likes") ?: 0L).toInt()
                                posts.add(
                                    BoothPost(
                                        id = postId,
                                        merchantId = merchantId,
                                        title = postTitle,
                                        text = postText,
                                        imageUri = postImageUri,
                                        likesCount = postLikes
                                    )
                                )
                            }
                        }

                        val fullMerchant = Merchant(
                            id = merchantId,
                            ownerId = ownerId,
                            name = name,
                            title = title,
                            phone = phone,
                            avatarEmoji = avatarEmoji,
                            avatarUri = avatarUri,
                            bannerUri = bannerUri,
                            specialty = specialty,
                            location = location,
                            address = address,
                            isOnline = isOnline,
                            workHours = workHours,
                            deliveryMethods = deliveryMethods,
                            guaranteePolicy = guaranteePolicy,
                            socialTelegram = socialTelegram,
                            socialWhatsapp = socialWhatsapp,
                            storyTitle = storyTitle,
                            storyText = storyText,
                            reviewsCount = reviewsCount,
                            reviewsSummary = reviewsSummary,
                            rating = rating,
                            isVerified = isVerified,
                            hasReturnGuarantee = hasReturnGuarantee,
                            isEcoFriendly = isEcoFriendly,
                            isOrganicCertified = isOrganicCertified,
                            products = products,
                            posts = posts
                        )

                        synchronized(merchantsList) {
                            merchantsList.add(fullMerchant)
                            pendingMerchants--
                            if (pendingMerchants == 0) {
                                trySend(merchantsList.toList())
                            }
                        }
                    }
                }
            }
        }

        awaitClose {
            listener.remove()
        }
    }.flowOn(Dispatchers.IO)

    // -------------------------------------------------------------
    // Save / Update Merchant Booth
    // -------------------------------------------------------------
    suspend fun saveMerchantBooth(merchant: Merchant): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uid = requireUserId()
            val merchantId = if (merchant.id.isNotBlank()) merchant.id else uid
            val merchantRef = db.collection("merchants").document(merchantId)

            val payload = mutableMapOf<String, Any>(
                "id" to merchantId,
                "ownerId" to uid,
                "name" to merchant.name,
                "title" to merchant.title,
                "phone" to merchant.phone,
                "specialty" to merchant.specialty,
                "location" to merchant.location,
                "avatarEmoji" to merchant.avatarEmoji,
                "address" to merchant.address,
                "workHours" to merchant.workHours,
                "deliveryMethods" to merchant.deliveryMethods,
                "guaranteePolicy" to merchant.guaranteePolicy,
                "socialTelegram" to merchant.socialTelegram,
                "socialWhatsapp" to merchant.socialWhatsapp,
                "storyTitle" to merchant.storyTitle,
                "storyText" to merchant.storyText,
                "reviewsSummary" to merchant.reviewsSummary,
                "rating" to merchant.rating,
                "reviewsCount" to merchant.reviewsCount,
                "isOnline" to merchant.isOnline,
                "isVerified" to merchant.isVerified,
                "hasReturnGuarantee" to merchant.hasReturnGuarantee,
                "isEcoFriendly" to merchant.isEcoFriendly,
                "isOrganicCertified" to merchant.isOrganicCertified,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            merchant.avatarUri?.let { if (it.isNotBlank()) payload["avatarUri"] = it }
            merchant.bannerUri?.let { if (it.isNotBlank()) payload["bannerUri"] = it }

            merchantRef.set(payload).await()

            // Save products
            for (prod in merchant.products) {
                val prodId = if (prod.id.isNotBlank()) prod.id else "prod_${System.currentTimeMillis()}_${(100..999).random()}"
                val prodRef = merchantRef.collection("products").document(prodId)
                val prodMap = mutableMapOf<String, Any>(
                    "id" to prodId,
                    "merchantId" to merchantId,
                    "title" to prod.title,
                    "weight" to prod.weight,
                    "price" to prod.price,
                    "originalPrice" to prod.originalPrice,
                    "isAvailable" to prod.isAvailable,
                    "category" to prod.category,
                    "description" to prod.description,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (prod.imageUrl.isNotBlank()) {
                    prodMap["imageUrl"] = prod.imageUrl
                }
                prodRef.set(prodMap).await()
            }

            // Save posts
            for (post in merchant.posts) {
                val postId = if (post.id.isNotBlank()) post.id else "post_${System.currentTimeMillis()}_${(100..999).random()}"
                val postRef = merchantRef.collection("posts").document(postId)
                val postMap = mutableMapOf<String, Any>(
                    "id" to postId,
                    "merchantId" to merchantId,
                    "title" to post.title,
                    "description" to post.text,
                    "likes" to post.likesCount,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                post.imageUri?.let { if (it.isNotBlank()) postMap["imageUri"] = it }
                postRef.set(postMap).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "merchants/${merchant.id}")
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // Add / Update Single Product
    // -------------------------------------------------------------
    suspend fun saveProduct(merchantId: String, product: MerchantProduct): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val prodId = if (product.id.isNotBlank()) product.id else "prod_${System.currentTimeMillis()}"
            val prodRef = db.collection("merchants").document(merchantId).collection("products").document(prodId)
            val prodMap = mutableMapOf<String, Any>(
                "id" to prodId,
                "merchantId" to merchantId,
                "title" to product.title,
                "weight" to product.weight,
                "price" to product.price,
                "originalPrice" to product.originalPrice,
                "isAvailable" to product.isAvailable,
                "category" to product.category,
                "description" to product.description,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (product.imageUrl.isNotBlank()) {
                prodMap["imageUrl"] = product.imageUrl
            }
            prodRef.set(prodMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "merchants/$merchantId/products/${product.id}")
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // Delete Product
    // -------------------------------------------------------------
    suspend fun deleteProduct(merchantId: String, productId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val prodRef = db.collection("merchants").document(merchantId).collection("products").document(productId)
            prodRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, "merchants/$merchantId/products/$productId")
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // Add Post / Story
    // -------------------------------------------------------------
    suspend fun savePost(merchantId: String, post: BoothPost): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val postId = if (post.id.isNotBlank()) post.id else "post_${System.currentTimeMillis()}"
            val postRef = db.collection("merchants").document(merchantId).collection("posts").document(postId)
            val postMap = mutableMapOf<String, Any>(
                "id" to postId,
                "merchantId" to merchantId,
                "title" to post.title,
                "description" to post.text,
                "likes" to post.likesCount,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            post.imageUri?.let { if (it.isNotBlank()) postMap["imageUri"] = it }
            postRef.set(postMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "merchants/$merchantId/posts/${post.id}")
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // Delete Post
    // -------------------------------------------------------------
    suspend fun deletePost(merchantId: String, postId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val postRef = db.collection("merchants").document(merchantId).collection("posts").document(postId)
            postRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, "merchants/$merchantId/posts/$postId")
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // User Profile persistence
    // -------------------------------------------------------------
    suspend fun saveUserAccount(user: UserAccount): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uid = requireUserId()
            val userRef = db.collection("users").document(uid)
            val userMap = mutableMapOf<String, Any>(
                "id" to uid,
                "phone" to user.phone,
                "pin" to user.pin,
                "name" to user.name,
                "role" to user.role,
                "referralCode" to user.referralCode,
                "shopTitle" to user.shopTitle,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            userRef.set(userMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "users/${user.phone}")
            Result.failure(e)
        }
    }

    suspend fun getUserAccount(userId: String): Result<UserAccount?> = withContext(Dispatchers.IO) {
        try {
            val doc = db.collection("users").document(userId).get().await()
            if (doc.exists()) {
                val account = UserAccount(
                    id = doc.id,
                    phone = doc.getString("phone") ?: "",
                    pin = doc.getString("pin") ?: "",
                    name = doc.getString("name") ?: "",
                    role = doc.getString("role") ?: "خریدار معتمد",
                    referralCode = doc.getString("referralCode") ?: "",
                    shopTitle = doc.getString("shopTitle") ?: ""
                )
                Result.success(account)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, "users/$userId")
            Result.failure(e)
        }
    }
}
