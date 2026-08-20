package com.example.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.model.AnnouncementEntity
import com.example.model.CategoryBreakdown
import com.example.model.CitizenEntity
import com.example.model.CitizenType
import com.example.model.MonthlyRecap
import com.example.model.PaymentMethod
import com.example.model.TransactionCategory
import com.example.model.TransactionEntity
import com.example.model.TransactionType
import com.example.util.ProofPhotoStorageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class RtRepository(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) {
    private val tag = "RtRepository"
    private val citizenDao = database.citizenDao()
    private val transactionDao = database.transactionDao()
    private val announcementDao = database.announcementDao()

    val cloudSyncEngine = RealtimeCloudSyncEngine(scope)

    val allCitizens: Flow<List<CitizenEntity>> = citizenDao.getAllCitizens()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allAnnouncements: Flow<List<AnnouncementEntity>> = announcementDao.getAllAnnouncements()

    init {
        scope.launch(Dispatchers.IO) {
            checkAndSeedInitialData()
            initFirestoreListeners()
        }
    }

    private suspend fun checkAndSeedInitialData() {
        val existingCitizens = citizenDao.getAllCitizens().first()
        if (existingCitizens.isEmpty()) {
            val initialCitizens = SampleDataProvider.getInitialCitizens()
            val initialTx = SampleDataProvider.getInitialTransactions()
            val initialAnnounce = SampleDataProvider.getInitialAnnouncements()

            citizenDao.insertAll(initialCitizens)
            transactionDao.insertAll(initialTx)
            announcementDao.insertAll(initialAnnounce)

            // Seed initial data to Firestore
            try {
                val room = cloudSyncEngine.syncCode.value
                initialTx.forEach { cloudSyncEngine.firestoreService.saveTransactionToFirestore(room, it) }
                initialCitizens.forEach { cloudSyncEngine.firestoreService.saveCitizenToFirestore(room, it) }
                initialAnnounce.forEach { cloudSyncEngine.firestoreService.saveAnnouncementToFirestore(room, it) }
            } catch (t: Throwable) {
                Log.w(tag, "Initial Firestore seed notice: ${t.message}")
            }
        }
    }

    private fun initFirestoreListeners() {
        val room = cloudSyncEngine.syncCode.value
        cloudSyncEngine.firestoreService.startRealtimeRoomListeners(
            roomCode = room,
            onTransactionsUpdated = { remoteTransactions ->
                scope.launch(Dispatchers.IO) {
                    try {
                        val localTx = transactionDao.getAllTransactions().first()
                        val localSyncIds = localTx.map { it.syncId }.toSet()
                        val newOrUpdated = remoteTransactions.filter { remote ->
                            val match = localTx.find { it.syncId == remote.syncId }
                            match == null || match.proofPhotoCloudUrl != remote.proofPhotoCloudUrl || match.amount != remote.amount
                        }
                        if (newOrUpdated.isNotEmpty()) {
                            transactionDao.insertAll(newOrUpdated)
                        }
                    } catch (e: Exception) {
                        Log.w(tag, "Error merging Firestore transactions: ${e.message}")
                    }
                }
            },
            onCitizensUpdated = { remoteCitizens ->
                scope.launch(Dispatchers.IO) {
                    try {
                        if (remoteCitizens.isNotEmpty()) {
                            citizenDao.insertAll(remoteCitizens)
                        }
                    } catch (e: Exception) {
                        Log.w(tag, "Error merging Firestore citizens: ${e.message}")
                    }
                }
            },
            onAnnouncementsUpdated = { remoteAnnouncements ->
                scope.launch(Dispatchers.IO) {
                    try {
                        if (remoteAnnouncements.isNotEmpty()) {
                            announcementDao.insertAll(remoteAnnouncements)
                        }
                    } catch (e: Exception) {
                        Log.w(tag, "Error merging Firestore announcements: ${e.message}")
                    }
                }
            },
            onDevicesUpdated = { remoteDevices ->
                // Devices updated via Firestore
            }
        )
    }

    fun getTransactionsByPeriod(month: Int, year: Int): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByMonthYear(month, year)
    }

    suspend fun insertTransaction(
        transaction: TransactionEntity,
        selectedImageUri: Uri? = null,
        context: Context? = null
    ): Long = withContext(Dispatchers.IO) {
        var txToSave = transaction
        if (txToSave.syncId.isBlank()) {
            txToSave = txToSave.copy(syncId = "TX-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(4)}")
        }

        // Save local photo cache if image URI provided
        if (selectedImageUri != null && context != null) {
            val localPath = ProofPhotoStorageManager.saveLocalReceiptPhoto(context, selectedImageUri)
            if (localPath != null) {
                txToSave = txToSave.copy(proofPhotoUri = localPath)
            }
        }

        val generatedId = transactionDao.insertTransaction(txToSave)
        val savedTx = txToSave.copy(id = generatedId)

        // Sync to Cloud Firestore
        val roomCode = cloudSyncEngine.syncCode.value
        cloudSyncEngine.firestoreService.saveTransactionToFirestore(roomCode, savedTx)
        cloudSyncEngine.triggerRealtimeSync()

        // Background Firebase Storage upload for expense proof photo
        if (context != null && savedTx.proofPhotoUri != null && savedTx.type == TransactionType.PENGELUARAN) {
            scope.launch(Dispatchers.IO) {
                try {
                    val downloadUrl = ProofPhotoStorageManager.uploadReceiptToFirebaseStorage(
                        context = context,
                        localPhotoPathOrUri = savedTx.proofPhotoUri,
                        roomCode = roomCode,
                        transactionSyncId = savedTx.syncId
                    )
                    if (downloadUrl != null) {
                        val updatedTxWithCloudUrl = savedTx.copy(proofPhotoCloudUrl = downloadUrl)
                        transactionDao.updateTransaction(updatedTxWithCloudUrl)
                        cloudSyncEngine.firestoreService.saveTransactionToFirestore(roomCode, updatedTxWithCloudUrl)
                    }
                } catch (e: Exception) {
                    Log.w(tag, "Photo storage upload notice: ${e.message}")
                }
            }
        }

        generatedId
    }

    suspend fun updateTransaction(
        transaction: TransactionEntity,
        selectedImageUri: Uri? = null,
        context: Context? = null
    ) = withContext(Dispatchers.IO) {
        var txToUpdate = transaction
        if (selectedImageUri != null && context != null) {
            val localPath = ProofPhotoStorageManager.saveLocalReceiptPhoto(context, selectedImageUri)
            if (localPath != null) {
                txToUpdate = txToUpdate.copy(proofPhotoUri = localPath)
            }
        }

        transactionDao.updateTransaction(txToUpdate)
        val roomCode = cloudSyncEngine.syncCode.value
        cloudSyncEngine.firestoreService.saveTransactionToFirestore(roomCode, txToUpdate)
        cloudSyncEngine.triggerRealtimeSync()

        // Background storage upload if local photo needs upload
        if (context != null && txToUpdate.proofPhotoUri != null && txToUpdate.proofPhotoCloudUrl == null && txToUpdate.type == TransactionType.PENGELUARAN) {
            scope.launch(Dispatchers.IO) {
                try {
                    val downloadUrl = ProofPhotoStorageManager.uploadReceiptToFirebaseStorage(
                        context = context,
                        localPhotoPathOrUri = txToUpdate.proofPhotoUri!!,
                        roomCode = roomCode,
                        transactionSyncId = txToUpdate.syncId
                    )
                    if (downloadUrl != null) {
                        val updatedWithCloud = txToUpdate.copy(proofPhotoCloudUrl = downloadUrl)
                        transactionDao.updateTransaction(updatedWithCloud)
                        cloudSyncEngine.firestoreService.saveTransactionToFirestore(roomCode, updatedWithCloud)
                    }
                } catch (e: Exception) {
                    Log.w(tag, "Photo upload error: ${e.message}")
                }
            }
        }
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        transactionDao.deleteTransaction(transaction)
        val roomCode = cloudSyncEngine.syncCode.value
        val syncKey = if (transaction.syncId.isNotBlank()) transaction.syncId else "TX-${transaction.id}-${transaction.createdAtMillis}"
        cloudSyncEngine.firestoreService.deleteTransactionFromFirestore(roomCode, syncKey)
        cloudSyncEngine.triggerRealtimeSync()
    }

    suspend fun insertCitizen(citizen: CitizenEntity): Long = withContext(Dispatchers.IO) {
        val id = citizenDao.insertCitizen(citizen)
        val savedCitizen = citizen.copy(id = id)
        val roomCode = cloudSyncEngine.syncCode.value
        cloudSyncEngine.firestoreService.saveCitizenToFirestore(roomCode, savedCitizen)
        cloudSyncEngine.triggerRealtimeSync()
        id
    }

    suspend fun updateCitizen(citizen: CitizenEntity) = withContext(Dispatchers.IO) {
        citizenDao.updateCitizen(citizen)
        val roomCode = cloudSyncEngine.syncCode.value
        cloudSyncEngine.firestoreService.saveCitizenToFirestore(roomCode, citizen)
        cloudSyncEngine.triggerRealtimeSync()
    }

    suspend fun deleteCitizen(citizen: CitizenEntity) = withContext(Dispatchers.IO) {
        citizenDao.deleteCitizen(citizen)
        val roomCode = cloudSyncEngine.syncCode.value
        cloudSyncEngine.firestoreService.deleteCitizenFromFirestore(roomCode, citizen.id)
        cloudSyncEngine.triggerRealtimeSync()
    }

    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long = withContext(Dispatchers.IO) {
        val id = announcementDao.insertAnnouncement(announcement)
        val savedAnnouncement = announcement.copy(id = id)
        val roomCode = cloudSyncEngine.syncCode.value
        cloudSyncEngine.firestoreService.saveAnnouncementToFirestore(roomCode, savedAnnouncement)
        cloudSyncEngine.triggerRealtimeSync()
        id
    }

    suspend fun deleteAnnouncement(announcement: AnnouncementEntity) = withContext(Dispatchers.IO) {
        announcementDao.deleteAnnouncement(announcement)
        val roomCode = cloudSyncEngine.syncCode.value
        cloudSyncEngine.firestoreService.deleteAnnouncementFromFirestore(roomCode, announcement.id)
        cloudSyncEngine.triggerRealtimeSync()
    }

    suspend fun payCitizenDues(
        citizen: CitizenEntity,
        month: Int,
        year: Int,
        amount: Long = citizen.monthlyFee,
        paymentMethod: PaymentMethod = PaymentMethod.TUNAI,
        recordedBy: String = "Bendahara RT"
    ): Long = withContext(Dispatchers.IO) {
        val isBusiness = citizen.type == CitizenType.PELAKU_USAHA || citizen.type == CitizenType.WARUNG_PKL
        val category = if (isBusiness) TransactionCategory.IURAN_USAHA else TransactionCategory.IURAN_WARGA
        val receiptPrefix = if (isBusiness) "KW-USH" else "KW-WRG"
        val receiptNumber = "$receiptPrefix-${year}${String.format("%02d", month)}-${UUID.randomUUID().toString().take(6).uppercase()}"

        val title = if (isBusiness) {
            "Iuran Usaha - ${citizen.name}"
        } else {
            "Iuran Warga - ${citizen.name}"
        }

        val transaction = TransactionEntity(
            title = title,
            amount = amount,
            type = TransactionType.PEMASUKAN,
            category = category,
            citizenId = citizen.id,
            citizenName = citizen.name,
            address = citizen.houseNumber.ifBlank { null },
            month = month,
            year = year,
            dateMillis = System.currentTimeMillis(),
            recordedBy = recordedBy,
            paymentMethod = paymentMethod,
            receiptNumber = receiptNumber,
            notes = "Iuran periode ${getMonthName(month)} $year (${citizen.houseNumber})",
            syncId = "SYNC-${System.currentTimeMillis()}"
        )

        val id = transactionDao.insertTransaction(transaction)
        val savedTx = transaction.copy(id = id)
        val roomCode = cloudSyncEngine.syncCode.value
        cloudSyncEngine.firestoreService.saveTransactionToFirestore(roomCode, savedTx)
        cloudSyncEngine.triggerRealtimeSync()
        id
    }

    suspend fun calculateMonthlyRecap(month: Int, year: Int): MonthlyRecap = withContext(Dispatchers.IO) {
        val allTx = transactionDao.getAllTransactions().first()
        val allCitizensList = citizenDao.getAllCitizens().first().filter { it.isActive }

        // Calculate starting balance before this month
        var startingBalance = 0L
        for (tx in allTx) {
            val isPrior = (tx.year < year) || (tx.year == year && tx.month < month)
            if (isPrior) {
                if (tx.type == TransactionType.PEMASUKAN) {
                    startingBalance += tx.amount
                } else {
                    startingBalance -= tx.amount
                }
            }
        }

        val monthTx = allTx.filter { it.month == month && it.year == year }
        val incomeTx = monthTx.filter { it.type == TransactionType.PEMASUKAN }
        val expenseTx = monthTx.filter { it.type == TransactionType.PENGELUARAN }

        val totalIncome = incomeTx.sumOf { it.amount }
        val totalExpense = expenseTx.sumOf { it.amount }
        val netBalance = totalIncome - totalExpense
        val endingBalance = startingBalance + netBalance

        // Citizen payment compliance
        val paidCitizenIds = monthTx
            .filter { it.type == TransactionType.PEMASUKAN && it.citizenId != null }
            .mapNotNull { it.citizenId }
            .toSet()

        val paidCount = allCitizensList.count { it.id in paidCitizenIds }
        val unpaidCount = allCitizensList.size - paidCount
        val complianceRate = if (allCitizensList.isNotEmpty()) {
            (paidCount.toFloat() / allCitizensList.size) * 100f
        } else 0f

        // Category breakdowns
        val incomeBreakdowns = incomeTx
            .groupBy { it.category }
            .map { (cat, list) ->
                val sum = list.sumOf { it.amount }
                val pct = if (totalIncome > 0) (sum.toFloat() / totalIncome) * 100f else 0f
                CategoryBreakdown(cat, sum, pct, list.size)
            }
            .sortedByDescending { it.totalAmount }

        val expenseBreakdowns = expenseTx
            .groupBy { it.category }
            .map { (cat, list) ->
                val sum = list.sumOf { it.amount }
                val pct = if (totalExpense > 0) (sum.toFloat() / totalExpense) * 100f else 0f
                CategoryBreakdown(cat, sum, pct, list.size)
            }
            .sortedByDescending { it.totalAmount }

        MonthlyRecap(
            month = month,
            year = year,
            startingBalance = startingBalance,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            netBalance = netBalance,
            endingBalance = endingBalance,
            totalCitizens = allCitizensList.size,
            paidCitizensCount = paidCount,
            unpaidCitizensCount = unpaidCount,
            complianceRate = complianceRate,
            incomeCategories = incomeBreakdowns,
            expenseCategories = expenseBreakdowns,
            transactions = monthTx,
            isApprovedByKetua = true,
            approvalNotes = "Laporan telah diverifikasi dan disahkan oleh Ketua RT"
        )
    }

    suspend fun calculatePettyCashRecap(month: Int, year: Int): com.example.model.PettyCashRecap = withContext(Dispatchers.IO) {
        val allTx = transactionDao.getAllTransactions().first()
        val pettyCashAll = allTx.filter {
            it.isPettyCash || it.paymentMethod == PaymentMethod.TUNAI ||
            it.category == TransactionCategory.PENGISIAN_KAS_KECIL ||
            it.category == TransactionCategory.PENGEMBALIAN_SISA_KAS_KECIL
        }

        // Calculate starting balance of petty cash prior to this month
        var startingBalance = 0L
        for (tx in pettyCashAll) {
            val isPrior = (tx.year < year) || (tx.year == year && tx.month < month)
            if (isPrior) {
                if (tx.type == TransactionType.PEMASUKAN) {
                    startingBalance += tx.amount
                } else {
                    startingBalance -= tx.amount
                }
            }
        }

        val monthPettyTx = pettyCashAll.filter { it.month == month && it.year == year }
        val topUpTx = monthPettyTx.filter { it.type == TransactionType.PEMASUKAN }
        val disbursementTx = monthPettyTx.filter { it.type == TransactionType.PENGELUARAN }

        val totalTopUp = topUpTx.sumOf { it.amount }
        val totalDisbursement = disbursementTx.sumOf { it.amount }
        val netFluctuation = totalTopUp - totalDisbursement
        val endingBalance = startingBalance + netFluctuation

        val expenseBreakdowns = disbursementTx
            .groupBy { it.category }
            .map { (cat, list) ->
                val sum = list.sumOf { it.amount }
                val pct = if (totalDisbursement > 0) (sum.toFloat() / totalDisbursement) * 100f else 0f
                CategoryBreakdown(cat, sum, pct, list.size)
            }
            .sortedByDescending { it.totalAmount }

        com.example.model.PettyCashRecap(
            month = month,
            year = year,
            startingBalance = startingBalance,
            totalTopUp = totalTopUp,
            totalDisbursement = totalDisbursement,
            netFluctuation = netFluctuation,
            endingBalance = endingBalance,
            totalVouchers = disbursementTx.size,
            expenseCategoryBreakdowns = expenseBreakdowns,
            transactions = monthPettyTx.sortedByDescending { it.dateMillis }
        )
    }

    private fun getMonthName(month: Int): String = when (month) {
        1 -> "Januari"
        2 -> "Februari"
        3 -> "Maret"
        4 -> "April"
        5 -> "Mei"
        6 -> "Juni"
        7 -> "Juli"
        8 -> "Agustus"
        9 -> "September"
        10 -> "Oktober"
        11 -> "November"
        12 -> "Desember"
        else -> "Bulan $month"
    }
}
