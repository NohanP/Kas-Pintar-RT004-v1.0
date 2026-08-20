package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.model.UserRole

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("rt_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        const val DEFAULT_PIN = "1234"
        private const val KEY_PIN_PREFIX = "pin_role_"
        private const val KEY_LAST_LOGGED_IN_ROLE = "last_logged_in_role"
    }

    /**
     * Mendapatkan PIN 4 digit untuk peran tertentu (default: 1234)
     */
    fun getPasswordForRole(role: UserRole): String {
        return prefs.getString(KEY_PIN_PREFIX + role.name, DEFAULT_PIN) ?: DEFAULT_PIN
    }

    /**
     * Mengubah password/PIN 4 digit untuk peran tertentu.
     * Mengembalikan true jika format valid (4 digit angka) dan berhasil disimpan.
     */
    fun setPasswordForRole(role: UserRole, newPin: String): Boolean {
        val trimmed = newPin.trim()
        if (trimmed.length == 4 && trimmed.all { it.isDigit() }) {
            prefs.edit().putString(KEY_PIN_PREFIX + role.name, trimmed).apply()
            return true
        }
        return false
    }

    /**
     * Memverifikasi input PIN dengan PIN yang tersimpan untuk peran yang dipilih.
     */
    fun verifyPassword(role: UserRole, inputPin: String): Boolean {
        val storedPin = getPasswordForRole(role)
        return storedPin == inputPin.trim()
    }

    /**
     * Mendapatkan role terakhir yang digunakan.
     */
    fun getLastRole(): UserRole {
        val roleName = prefs.getString(KEY_LAST_LOGGED_IN_ROLE, UserRole.KETUA_RT.name)
        return try {
            UserRole.valueOf(roleName ?: UserRole.KETUA_RT.name)
        } catch (_: Exception) {
            UserRole.KETUA_RT
        }
    }

    /**
     * Menyimpan role terakhir yang digunakan.
     */
    fun setLastRole(role: UserRole) {
        prefs.edit().putString(KEY_LAST_LOGGED_IN_ROLE, role.name).apply()
    }

    /**
     * Mengembalikan map seluruh password untuk role yang ada.
     */
    fun getAllPasswords(): Map<UserRole, String> {
        return UserRole.values().associateWith { role ->
            getPasswordForRole(role)
        }
    }

    /**
     * Reset semua password pengurus ke default "1234".
     */
    fun resetAllPasswordsToDefault() {
        val editor = prefs.edit()
        UserRole.values().forEach { role ->
            editor.putString(KEY_PIN_PREFIX + role.name, DEFAULT_PIN)
        }
        editor.apply()
    }
}
