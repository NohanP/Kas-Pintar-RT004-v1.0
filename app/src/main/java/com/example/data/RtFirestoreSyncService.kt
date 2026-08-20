package com.example.data

import android.util.Log
import com.example.model.AnnouncementEntity
import com.example.model.CitizenEntity
import com.example.model.CitizenType
import com.example.model.DeviceSyncStatus
import com.example.model.PaymentMethod
import com.example.model.SyncDevice
import com.example.model.TransactionCategory
import com.example.model.TransactionEntity
import com.example.model.TransactionType
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RtFirestoreSyncService {
    private val tag = "RtFirestoreSyncService"
    private var firestore: FirebaseFirestore? = null

    private var txListenerRegistration: ListenerRegistration? = null
    private var citizenListenerRegistration: ListenerRegistration? = null
    private var announcementListenerRegistration: ListenerRegistration? = null
    private var deviceListenerRegistration: ListenerRegistration? = null

    var isFirestoreConnected: Boolean = false
        private set

    init {
        try {
            val db = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            db.firestoreSettings = settings
            firestore = db
            isFirestoreConnected = true
            Log.d(tag, "Firestore successfully initialized with offline persistence")
        } catch (e: Throwable) {
            Log.w(tag, "Firestore initialization notice (operating with local Room fallback): ${e.message}")
            firestore = null
            isFirestoreConnected = false
        }
    }

    private fun getRoomDocRef(roomCode: String) =
        firestore?.collection("rt_kas_rooms")?.document(sanitizeRoomCode(roomCode))

    private fun sanitizeRoomCode(code: String): String =
        code.trim().uppercase().replace(Regex("[^A-Z0-9_-]"), "_").ifEmpty { "RT004_RW08_JATI" }

    suspend fun saveTransactionToFirestore(roomCode: String, tx: TransactionEntity): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            val syncKey = if (tx.syncId.isNotBlank()) tx.syncId else "TX-${tx.id}-${tx.createdAtMillis}"
            val map = hashMapOf(
                "id" to tx.id,
                "syncId" to syncKey,
                "title" to tx.title,
                "amount" to tx.amount,
                "type" to tx.type.name,
                "category" to tx.category.name,
                "citizenId" to tx.citizenId,
                "citizenName" to tx.citizenName,
                "address" to tx.address,
                "month" to tx.month,
                "year" to tx.year,
                "dateMillis" to tx.dateMillis,
                "recordedBy" to tx.recordedBy,
                "paymentMethod" to tx.paymentMethod.name,
                "isApprovedByKetua" to tx.isApprovedByKetua,
                "receiptNumber" to tx.receiptNumber,
                "notes" to tx.notes,
                "createdAtMillis" to tx.createdAtMillis,
                "proofPhotoUri" to tx.proofPhotoUri,
                "proofPhotoCloudUrl" to tx.proofPhotoCloudUrl,
                "proofPhotoDescription" to tx.proofPhotoDescription,
                "isPettyCash" to tx.isPettyCash,
                "bpkkNumber" to tx.bpkkNumber,
                "recipientPerson" to tx.recipientPerson,
                "updatedAt" to System.currentTimeMillis()
            )

            getRoomDocRef(roomCode)
                ?.collection("transactions")
                ?.document(syncKey)
                ?.set(map, SetOptions.merge())
                ?.await()

            Log.d(tag, "Transaction synced to Firestore: $syncKey")
            true
        } catch (e: Throwable) {
            Log.w(tag, "Failed to save transaction to Firestore: ${e.message}")
            false
        }
    }

    suspend fun deleteTransactionFromFirestore(roomCode: String, syncKey: String): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            getRoomDocRef(roomCode)
                ?.collection("transactions")
                ?.document(syncKey)
                ?.delete()
                ?.await()
            Log.d(tag, "Transaction deleted from Firestore: $syncKey")
            true
        } catch (e: Throwable) {
            Log.w(tag, "Failed to delete transaction from Firestore: ${e.message}")
            false
        }
    }

    suspend fun saveCitizenToFirestore(roomCode: String, citizen: CitizenEntity): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            val docId = citizen.id.toString()
            val map = hashMapOf(
                "id" to citizen.id,
                "name" to citizen.name,
                "houseNumber" to citizen.houseNumber,
                "phone" to citizen.phone,
                "type" to citizen.type.name,
                "monthlyFee" to citizen.monthlyFee,
                "isActive" to citizen.isActive,
                "notes" to citizen.notes,
                "updatedAtMillis" to citizen.updatedAtMillis
            )

            getRoomDocRef(roomCode)
                ?.collection("citizens")
                ?.document(docId)
                ?.set(map, SetOptions.merge())
                ?.await()
            true
        } catch (e: Throwable) {
            Log.w(tag, "Failed to save citizen to Firestore: ${e.message}")
            false
        }
    }

    suspend fun deleteCitizenFromFirestore(roomCode: String, citizenId: Long): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            getRoomDocRef(roomCode)
                ?.collection("citizens")
                ?.document(citizenId.toString())
                ?.delete()
                ?.await()
            true
        } catch (e: Throwable) {
            Log.w(tag, "Failed to delete citizen from Firestore: ${e.message}")
            false
        }
    }

    suspend fun saveAnnouncementToFirestore(roomCode: String, announcement: AnnouncementEntity): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            val docId = announcement.id.toString()
            val map = hashMapOf(
                "id" to announcement.id,
                "title" to announcement.title,
                "content" to announcement.content,
                "authorRole" to announcement.authorRole,
                "dateMillis" to announcement.dateMillis,
                "priority" to announcement.priority
            )

            getRoomDocRef(roomCode)
                ?.collection("announcements")
                ?.document(docId)
                ?.set(map, SetOptions.merge())
                ?.await()
            true
        } catch (e: Throwable) {
            Log.w(tag, "Failed to save announcement to Firestore: ${e.message}")
            false
        }
    }

    suspend fun deleteAnnouncementFromFirestore(roomCode: String, announcementId: Long): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            getRoomDocRef(roomCode)
                ?.collection("announcements")
                ?.document(announcementId.toString())
                ?.delete()
                ?.await()
            true
        } catch (e: Throwable) {
            Log.w(tag, "Failed to delete announcement from Firestore: ${e.message}")
            false
        }
    }

    suspend fun saveDevicePresenceToFirestore(roomCode: String, device: SyncDevice): Boolean = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext false
        try {
            val map = hashMapOf(
                "deviceId" to device.deviceId,
                "deviceName" to device.deviceName,
                "roleName" to device.roleName,
                "status" to device.status.name,
                "lastSyncTime" to device.lastSyncTime,
                "ipAddress" to device.ipAddress,
                "updatedAt" to System.currentTimeMillis()
            )

            getRoomDocRef(roomCode)
                ?.collection("devices")
                ?.document(device.deviceId)
                ?.set(map, SetOptions.merge())
                ?.await()
            true
        } catch (e: Throwable) {
            Log.w(tag, "Failed to update device presence in Firestore: ${e.message}")
            false
        }
    }

    /**
     * Start Realtime Listeners for Firestore collections in the specified room.
     */
    fun startRealtimeRoomListeners(
        roomCode: String,
        onTransactionsUpdated: (List<TransactionEntity>) -> Unit,
        onCitizensUpdated: (List<CitizenEntity>) -> Unit,
        onAnnouncementsUpdated: (List<AnnouncementEntity>) -> Unit,
        onDevicesUpdated: (List<SyncDevice>) -> Unit
    ) {
        stopListeners()
        val roomRef = getRoomDocRef(roomCode) ?: return

        try {
            // 1. Transactions Listener
            txListenerRegistration = roomRef.collection("transactions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(tag, "Firestore tx listener notice: ${error.message}")
                        return@addSnapshotListener
                    }
                    try {
                        if (snapshot != null && !snapshot.isEmpty) {
                            val transactions = snapshot.documents.mapNotNull { doc ->
                                parseTransactionFromDoc(doc)
                            }
                            onTransactionsUpdated(transactions)
                        }
                    } catch (t: Throwable) {
                        Log.w(tag, "Error parsing transactions snapshot: ${t.message}")
                    }
                }

            // 2. Citizens Listener
            citizenListenerRegistration = roomRef.collection("citizens")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(tag, "Firestore citizen listener notice: ${error.message}")
                        return@addSnapshotListener
                    }
                    try {
                        if (snapshot != null && !snapshot.isEmpty) {
                            val citizens = snapshot.documents.mapNotNull { doc ->
                                parseCitizenFromDoc(doc)
                            }
                            onCitizensUpdated(citizens)
                        }
                    } catch (t: Throwable) {
                        Log.w(tag, "Error parsing citizens snapshot: ${t.message}")
                    }
                }

            // 3. Announcements Listener
            announcementListenerRegistration = roomRef.collection("announcements")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(tag, "Firestore announcement listener notice: ${error.message}")
                        return@addSnapshotListener
                    }
                    try {
                        if (snapshot != null && !snapshot.isEmpty) {
                            val announcements = snapshot.documents.mapNotNull { doc ->
                                parseAnnouncementFromDoc(doc)
                            }
                            onAnnouncementsUpdated(announcements)
                        }
                    } catch (t: Throwable) {
                        Log.w(tag, "Error parsing announcements snapshot: ${t.message}")
                    }
                }

            // 4. Devices Listener
            deviceListenerRegistration = roomRef.collection("devices")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(tag, "Firestore devices listener notice: ${error.message}")
                        return@addSnapshotListener
                    }
                    try {
                        if (snapshot != null && !snapshot.isEmpty) {
                            val devices = snapshot.documents.mapNotNull { doc ->
                                parseDeviceFromDoc(doc)
                            }
                            onDevicesUpdated(devices)
                        }
                    } catch (t: Throwable) {
                        Log.w(tag, "Error parsing devices snapshot: ${t.message}")
                    }
                }
            Log.d(tag, "Firestore real-time listeners registered for room: $roomCode")
        } catch (e: Throwable) {
            Log.w(tag, "Failed to attach Firestore listeners: ${e.message}")
        }
    }

    fun stopListeners() {
        txListenerRegistration?.remove()
        txListenerRegistration = null
        citizenListenerRegistration?.remove()
        citizenListenerRegistration = null
        announcementListenerRegistration?.remove()
        announcementListenerRegistration = null
        deviceListenerRegistration?.remove()
        deviceListenerRegistration = null
    }

    private fun parseTransactionFromDoc(doc: DocumentSnapshot): TransactionEntity? {
        return try {
            val id = doc.getLong("id") ?: 0L
            val title = doc.getString("title") ?: return null
            val amount = doc.getLong("amount") ?: 0L
            val typeStr = doc.getString("type") ?: TransactionType.PEMASUKAN.name
            val type = try { TransactionType.valueOf(typeStr) } catch (_: Exception) { TransactionType.PEMASUKAN }
            val catStr = doc.getString("category") ?: TransactionCategory.IURAN_WARGA.name
            val category = try { TransactionCategory.valueOf(catStr) } catch (_: Exception) {
                if (type == TransactionType.PEMASUKAN) TransactionCategory.IURAN_WARGA else TransactionCategory.GAJI_KEBERSIHAN
            }
            val citizenId = doc.getLong("citizenId")
            val citizenName = doc.getString("citizenName")
            val address = doc.getString("address")
            val month = (doc.getLong("month") ?: 8L).toInt()
            val year = (doc.getLong("year") ?: 2026L).toInt()
            val dateMillis = doc.getLong("dateMillis") ?: System.currentTimeMillis()
            val recordedBy = doc.getString("recordedBy") ?: "Bendahara RT"
            val payMethodStr = doc.getString("paymentMethod") ?: PaymentMethod.TUNAI.name
            val paymentMethod = try { PaymentMethod.valueOf(payMethodStr) } catch (_: Exception) { PaymentMethod.TUNAI }
            val isApproved = doc.getBoolean("isApprovedByKetua") ?: true
            val receiptNumber = doc.getString("receiptNumber").orEmpty()
            val notes = doc.getString("notes").orEmpty()
            val createdAtMillis = doc.getLong("createdAtMillis") ?: dateMillis
            val syncId = doc.getString("syncId") ?: doc.id
            val proofPhotoUri = doc.getString("proofPhotoUri")
            val proofPhotoCloudUrl = doc.getString("proofPhotoCloudUrl")
            val proofPhotoDescription = doc.getString("proofPhotoDescription")
            val isPettyCash = doc.getBoolean("isPettyCash") ?: false
            val bpkkNumber = doc.getString("bpkkNumber").orEmpty()
            val recipientPerson = doc.getString("recipientPerson")

            TransactionEntity(
                id = id,
                title = title,
                amount = amount,
                type = type,
                category = category,
                citizenId = citizenId,
                citizenName = citizenName,
                address = address,
                month = month,
                year = year,
                dateMillis = dateMillis,
                recordedBy = recordedBy,
                paymentMethod = paymentMethod,
                isApprovedByKetua = isApproved,
                receiptNumber = receiptNumber,
                notes = notes,
                createdAtMillis = createdAtMillis,
                syncId = syncId,
                proofPhotoUri = proofPhotoUri,
                proofPhotoCloudUrl = proofPhotoCloudUrl,
                proofPhotoDescription = proofPhotoDescription,
                isPettyCash = isPettyCash,
                bpkkNumber = bpkkNumber,
                recipientPerson = recipientPerson
            )
        } catch (e: Exception) {
            Log.w(tag, "Error parsing transaction doc: ${e.message}")
            null
        }
    }

    private fun parseCitizenFromDoc(doc: DocumentSnapshot): CitizenEntity? {
        return try {
            val id = doc.getLong("id") ?: (doc.id.toLongOrNull() ?: 0L)
            val name = doc.getString("name") ?: return null
            val houseNumber = doc.getString("houseNumber") ?: ""
            val phone = doc.getString("phone") ?: ""
            val typeStr = doc.getString("type") ?: CitizenType.WARGA_TETAP.name
            val type = try { CitizenType.valueOf(typeStr) } catch (_: Exception) { CitizenType.WARGA_TETAP }
            val monthlyFee = doc.getLong("monthlyFee") ?: 50_000L
            val isActive = doc.getBoolean("isActive") ?: true
            val notes = doc.getString("notes") ?: ""
            val updatedAtMillis = doc.getLong("updatedAtMillis") ?: System.currentTimeMillis()

            CitizenEntity(
                id = id,
                name = name,
                houseNumber = houseNumber,
                phone = phone,
                type = type,
                monthlyFee = monthlyFee,
                isActive = isActive,
                notes = notes,
                updatedAtMillis = updatedAtMillis
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseAnnouncementFromDoc(doc: DocumentSnapshot): AnnouncementEntity? {
        return try {
            val id = doc.getLong("id") ?: (doc.id.toLongOrNull() ?: 0L)
            val title = doc.getString("title") ?: return null
            val content = doc.getString("content") ?: ""
            val authorRole = doc.getString("authorRole") ?: "Pengurus RT"
            val dateMillis = doc.getLong("dateMillis") ?: System.currentTimeMillis()
            val priority = doc.getString("priority") ?: "INFO"

            AnnouncementEntity(
                id = id,
                title = title,
                content = content,
                authorRole = authorRole,
                dateMillis = dateMillis,
                priority = priority
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseDeviceFromDoc(doc: DocumentSnapshot): SyncDevice? {
        return try {
            val deviceId = doc.getString("deviceId") ?: doc.id
            val deviceName = doc.getString("deviceName") ?: "Perangkat Pengurus"
            val roleName = doc.getString("roleName") ?: "Pengurus RT"
            val statusStr = doc.getString("status") ?: DeviceSyncStatus.CONNECTED.name
            val status = try { DeviceSyncStatus.valueOf(statusStr) } catch (_: Exception) { DeviceSyncStatus.CONNECTED }
            val lastSyncTime = doc.getString("lastSyncTime") ?: "Baru saja"
            val ipAddress = doc.getString("ipAddress") ?: "192.168.1.100"

            SyncDevice(
                deviceId = deviceId,
                deviceName = deviceName,
                roleName = roleName,
                status = status,
                lastSyncTime = lastSyncTime,
                ipAddress = ipAddress,
                isCurrentDevice = false
            )
        } catch (e: Exception) {
            null
        }
    }
}
