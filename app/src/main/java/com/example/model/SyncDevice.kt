package com.example.model

enum class DeviceSyncStatus {
    CONNECTED,
    SYNCING,
    IDLE,
    OFFLINE
}

data class SyncDevice(
    val deviceId: String,
    val deviceName: String,
    val roleName: String,
    val status: DeviceSyncStatus,
    val lastSyncTime: String,
    val ipAddress: String,
    val isCurrentDevice: Boolean = false
)
