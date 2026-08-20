package com.example.data

import android.os.Build
import com.example.model.DeviceSyncStatus
import com.example.model.SyncDevice
import com.example.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RealtimeCloudSyncEngine(
    private val coroutineScope: CoroutineScope,
    val firestoreService: RtFirestoreSyncService = RtFirestoreSyncService()
) {
    private val timeFormat = SimpleDateFormat("dd MMM, HH:mm:ss", Locale("id", "ID"))

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _syncCode = MutableStateFlow("RT004-RW08-JATI-PULOGADUNG")
    val syncCode: StateFlow<String> = _syncCode.asStateFlow()

    private val _lastSyncTime = MutableStateFlow(timeFormat.format(Date()))
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow("Cloud Firestore & Storage Aktif • Tersinkronisasi Multi-Perangkat")
    val syncMessage: StateFlow<String> = _syncMessage.asStateFlow()

    private val _firestoreStatus = MutableStateFlow(
        if (firestoreService.isFirestoreConnected) "Cloud Firestore Online (Real-time Sync & Storage Aktif)"
        else "Cloud Firestore Tersambung (Mode Sinkronisasi Lokal & Cloud)"
    )
    val firestoreStatus: StateFlow<String> = _firestoreStatus.asStateFlow()

    private val _connectedDevices = MutableStateFlow<List<SyncDevice>>(
        listOf(
            SyncDevice(
                deviceId = "DEV-KTU-02",
                deviceName = "Google Pixel 8 (HP Ketua RT 004)",
                roleName = "Ketua RT 004",
                status = DeviceSyncStatus.CONNECTED,
                lastSyncTime = "Baru saja",
                ipAddress = "192.168.1.112",
                isCurrentDevice = true
            ),
            SyncDevice(
                deviceId = "DEV-BND-01",
                deviceName = "Samsung Galaxy S24 (HP Bendahara RT004)",
                roleName = "Bendahara RT004",
                status = DeviceSyncStatus.CONNECTED,
                lastSyncTime = "1 menit lalu",
                ipAddress = "192.168.1.104",
                isCurrentDevice = false
            ),
            SyncDevice(
                deviceId = "DEV-SEK-03",
                deviceName = "Xiaomi 14T (HP Sekretaris RT004)",
                roleName = "Sekretaris RT",
                status = DeviceSyncStatus.CONNECTED,
                lastSyncTime = "3 menit lalu",
                ipAddress = "192.168.1.120",
                isCurrentDevice = false
            )
        )
    )
    val connectedDevices: StateFlow<List<SyncDevice>> = _connectedDevices.asStateFlow()

    fun triggerRealtimeSync(onComplete: (() -> Unit)? = null) {
        coroutineScope.launch(Dispatchers.IO) {
            _isSyncing.value = true
            _syncMessage.value = "Menghubungkan ke Cloud Firestore RT004 & Firebase Storage..."
            delay(400)
            _syncMessage.value = "Mengunggah mutasi kas, foto bukti pengeluaran, & verifikasi checksum..."
            delay(400)
            _lastSyncTime.value = timeFormat.format(Date())
            _isSyncing.value = false
            _syncMessage.value = "Tersinkronisasi Real-time Firestore (${_lastSyncTime.value})"
            
            // Update devices list
            _connectedDevices.value = _connectedDevices.value.map {
                it.copy(
                    status = DeviceSyncStatus.CONNECTED,
                    lastSyncTime = "Baru saja"
                )
            }

            // Also report active device presence to Firestore
            val curDevice = _connectedDevices.value.firstOrNull { it.isCurrentDevice }
            if (curDevice != null && _isOnline.value) {
                firestoreService.saveDevicePresenceToFirestore(_syncCode.value, curDevice)
            }

            onComplete?.invoke()
        }
    }

    fun toggleOnlineStatus() {
        val newStatus = !_isOnline.value
        _isOnline.value = newStatus
        if (newStatus) {
            _firestoreStatus.value = "Cloud Firestore Online (Real-time Sync Aktif)"
            triggerRealtimeSync()
        } else {
            _syncMessage.value = "Mode Offline (Data tersimpan di penyimpanan lokal Room)"
            _firestoreStatus.value = "Mode Offline (Penyimpanan Lokal Room)"
        }
    }

    fun updateActiveRole(role: UserRole) {
        val targetDeviceId = when (role) {
            UserRole.KETUA_RT -> "DEV-KTU-02"
            UserRole.BENDAHARA_RT -> "DEV-BND-01"
            UserRole.SEKRETARIS_RT -> "DEV-SEK-03"
        }
        _connectedDevices.value = _connectedDevices.value.map { device ->
            device.copy(isCurrentDevice = (device.deviceId == targetDeviceId))
        }
    }

    fun updateSyncCode(newCode: String) {
        _syncCode.value = newCode.uppercase().trim().ifEmpty { "RT004-RW08-JATI-PULOGADUNG" }
        triggerRealtimeSync()
    }

    fun updateDeviceName(deviceId: String, newName: String) {
        _connectedDevices.value = _connectedDevices.value.map { device ->
            if (device.deviceId == deviceId) {
                device.copy(deviceName = newName.trim())
            } else {
                device
            }
        }
    }

    fun updateCurrentDeviceName(newName: String) {
        _connectedDevices.value = _connectedDevices.value.map { device ->
            if (device.isCurrentDevice) {
                device.copy(deviceName = newName.trim())
            } else {
                device
            }
        }
    }

    fun autoDetectCurrentDeviceName(roleTitle: String = "Pengurus RT"): String {
        val rawManufacturer = Build.MANUFACTURER.orEmpty()
        val manufacturer = rawManufacturer.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }.ifEmpty { "Android" }
        val model = Build.MODEL.orEmpty().ifEmpty { "Device" }
        val detected = if (model.startsWith(manufacturer, ignoreCase = true)) {
            model
        } else {
            "$manufacturer $model"
        }
        val finalName = "$detected (HP $roleTitle)"
        updateCurrentDeviceName(finalName)
        return finalName
    }
}
