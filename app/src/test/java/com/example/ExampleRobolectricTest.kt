package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.PersianUtils
import com.example.viewmodel.KasebanViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("کاسبان", appName)
    }

    @Test
    fun `persian utils converts numbers correctly`() {
        assertEquals("۱۲۳۴۵۶۷۸۹۰", PersianUtils.toPersianDigits("1234567890"))
        assertEquals("۵۰,۰۰۰ تومان", PersianUtils.formatPrice(50000L))
    }

    @Test
    fun `kaseban initial state has empty chats and valid auth flow`() {
        val application = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = KasebanViewModel(application)

        // Ensure initially there are no fake dummy chats
        assertTrue("Chats must be empty for a fresh install", viewModel.chatMessagesMap.value.isEmpty())

        // Test login with referral bonus
        val initialBalance = viewModel.walletBalance.value
        viewModel.completeAuth(
            name = "علی صادقی",
            phone = "۰۹۱۲۱۱۱۱۱۱۱",
            referralCode = "KASB-7492",
            role = "خریدار معتمد"
        )

        assertTrue(viewModel.isUserLoggedIn.value)
        assertEquals("علی صادقی", viewModel.userDisplayName.value)
        assertEquals("KASB-7492", viewModel.registeredReferralCode.value)
        assertEquals(initialBalance + 50000L, viewModel.walletBalance.value)

        // Test logout
        viewModel.logout()
        assertFalse(viewModel.isUserLoggedIn.value)
    }

    @Test
    fun `authViewModel initializes safely without crashing when Firebase is uninitialized`() {
        val authVm = com.example.viewmodel.AuthViewModel()
        assertFalse(authVm.isUserLoggedIn)
        assertEquals(com.example.viewmodel.AuthState.Idle, authVm.authState.value)
    }
}

