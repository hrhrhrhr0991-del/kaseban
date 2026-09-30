package com.example.viewmodel

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class CodeSent(val verificationId: String, val phoneNumber: String) : AuthState
    data class Authenticated(val user: FirebaseUser?) : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel : ViewModel() {

    private fun getFirebaseAuth(): FirebaseAuth? {
        return try {
            FirebaseAuth.getInstance()
        } catch (_: Throwable) {
            null
        }
    }

    private val auth: FirebaseAuth?
        get() = getFirebaseAuth()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        try {
            val user = getFirebaseAuth()?.currentUser
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
            }
        } catch (_: Throwable) {
            // Firebase not initialized in local test or dev environment
        }
    }

    val currentUser: FirebaseUser?
        get() = try { auth?.currentUser } catch (e: Throwable) { null }

    val isUserLoggedIn: Boolean
        get() = currentUser != null

    val userDisplayName: String
        get() = currentUser?.displayName ?: currentUser?.phoneNumber ?: ""

    // Sign in anonymously for instant guest preview without friction
    fun signInAnonymously() {
        val currentAuth = auth
        if (currentAuth == null) {
            _authState.value = AuthState.Authenticated(null)
            return
        }
        _authState.value = AuthState.Loading
        currentAuth.signInAnonymously()
            .addOnSuccessListener { result ->
                _authState.value = AuthState.Authenticated(result.user)
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.localizedMessage ?: "خطا در ورود مهمان")
            }
    }

    // Phone / OTP Verification Flow (Standard for Iranian users)
    fun sendPhoneOtp(phoneNumber: String, activity: Activity) {
        if (phoneNumber.isBlank()) {
            _authState.value = AuthState.Error("لطفاً شماره تلفن همراه را وارد کنید")
            return
        }

        val currentAuth = auth
        if (currentAuth == null) {
            // Emulate successful code sent in offline/un-configured environment
            _authState.value = AuthState.CodeSent("mock_verification_id", phoneNumber)
            return
        }

        _authState.value = AuthState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                currentAuth.signInWithCredential(credential)
                    .addOnSuccessListener { result ->
                        _authState.value = AuthState.Authenticated(result.user)
                    }
                    .addOnFailureListener { e ->
                        _authState.value = AuthState.Error(e.localizedMessage ?: "خطا در تایید شماره تلفن")
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "ارسال پیامک با خطا مواجه شد")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                _authState.value = AuthState.CodeSent(verificationId, phoneNumber)
            }
        }

        val formattedPhone = if (phoneNumber.startsWith("0")) "+98" + phoneNumber.substring(1) else phoneNumber

        val options = PhoneAuthOptions.newBuilder(currentAuth)
            .setPhoneNumber(formattedPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneOtp(verificationId: String, otpCode: String) {
        if (otpCode.length < 5) {
            _authState.value = AuthState.Error("کد تایید ۶ رقمی را به درستی وارد نمایید")
            return
        }

        val currentAuth = auth
        if (currentAuth == null) {
            _authState.value = AuthState.Authenticated(null)
            return
        }

        _authState.value = AuthState.Loading
        val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
        currentAuth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                _authState.value = AuthState.Authenticated(result.user)
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.localizedMessage ?: "کد وارد شده معتبر نمی‌باشد")
            }
    }

    // Google Sign-In via Android Credential Manager
    fun signInWithGoogle(context: Context, serverClientId: String = "") {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId.ifEmpty { "dummy-client-id" })
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential

                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    val currentAuth = auth
                    if (currentAuth != null) {
                        val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                        currentAuth.signInWithCredential(authCredential)
                            .addOnSuccessListener { authResult ->
                                _authState.value = AuthState.Authenticated(authResult.user)
                            }
                            .addOnFailureListener { e ->
                                _authState.value = AuthState.Error(e.localizedMessage ?: "خطا در تایید حساب گوگل")
                            }
                    } else {
                        _authState.value = AuthState.Authenticated(null)
                    }
                } else {
                    _authState.value = AuthState.Error("نوع اعتبار حساب پشتیبانی نمی‌شود")
                }
            } catch (e: GetCredentialException) {
                _authState.value = AuthState.Error("ورود با گوگل انجام نشد: ${e.localizedMessage}")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "خطای ناشناخته در ورود")
            }
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (_: Throwable) {}
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = try {
            val user = auth?.currentUser
            if (user != null) AuthState.Authenticated(user) else AuthState.Idle
        } catch (_: Throwable) {
            AuthState.Idle
        }
    }
}
