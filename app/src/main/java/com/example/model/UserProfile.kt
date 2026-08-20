package com.example.model

data class UserProfile(
    val role: UserRole,
    val name: String,
    val phone: String = "",
    val email: String = "",
    val notes: String = ""
)
