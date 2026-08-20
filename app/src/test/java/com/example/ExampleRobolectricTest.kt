package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.UserRole
import com.example.util.AuthManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Kas Pintar RT004/08", appName)
  }

  @Test
  fun `verify default PIN is 1234 for all roles`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val authManager = AuthManager(context)
    
    UserRole.values().forEach { role ->
      assertEquals("1234", authManager.getPasswordForRole(role))
      assertTrue(authManager.verifyPassword(role, "1234"))
      assertFalse(authManager.verifyPassword(role, "0000"))
    }
  }

  @Test
  fun `verify password change and reset`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val authManager = AuthManager(context)

    assertTrue(authManager.setPasswordForRole(UserRole.BENDAHARA_RT, "9876"))
    assertEquals("9876", authManager.getPasswordForRole(UserRole.BENDAHARA_RT))
    assertTrue(authManager.verifyPassword(UserRole.BENDAHARA_RT, "9876"))

    // Invalid PIN format rejected
    assertFalse(authManager.setPasswordForRole(UserRole.BENDAHARA_RT, "abc"))
    assertFalse(authManager.setPasswordForRole(UserRole.BENDAHARA_RT, "12345"))

    // Reset to default
    authManager.resetAllPasswordsToDefault()
    assertEquals("1234", authManager.getPasswordForRole(UserRole.BENDAHARA_RT))
  }
}

