package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.RtRepository
import com.example.model.AnnouncementEntity
import com.example.model.CitizenEntity
import com.example.model.CitizenType
import com.example.model.MonthlyRecap
import com.example.model.PaymentMethod
import com.example.model.TransactionCategory
import com.example.model.TransactionEntity
import com.example.model.TransactionType
import com.example.model.UserProfile
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class NavigationTab(val title: String, val route: String) {
    object Dashboard : NavigationTab("Dashboard", "dashboard")
    object Transactions : NavigationTab("Arus Kas", "transactions")
    object PettyCash : NavigationTab("Kas Kecil", "petty_cash")
    object Citizens : NavigationTab("Warga & Usaha", "citizens")
    object RecapReport : NavigationTab("Rekap Laporan", "recap_report")
    object MultiDeviceSync : NavigationTab("Sync Cloud", "cloud_sync")
}

private data class TxFilter(
    val month: Int,
    val year: Int,
    val type: TransactionType?,
    val category: TransactionCategory?,
    val query: String
)

private data class CitizenFilter(
    val month: Int,
    val year: Int,
    val type: CitizenType?,
    val isPaid: Boolean?,
    val query: String
)

data class CitizenWithDuesStatus(
    val citizen: CitizenEntity,
    val isPaid: Boolean,
    val transaction: TransactionEntity? = null
)

class RtCashViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = RtRepository(database, viewModelScope)
    val authManager = com.example.util.AuthManager(application)

    // Authentication State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Passwords for each role
    private val _rolePasswords = MutableStateFlow<Map<UserRole, String>>(authManager.getAllPasswords())
    val rolePasswords: StateFlow<Map<UserRole, String>> = _rolePasswords.asStateFlow()

    // Active User Profile & Role (Default: Ketua RT Nohan Pancono)
    private val _userProfiles = MutableStateFlow<Map<UserRole, UserProfile>>(
        mapOf(
            UserRole.KETUA_RT to UserProfile(
                role = UserRole.KETUA_RT,
                name = UserRole.KETUA_RT.defaultName,
                phone = "0812-3456-7890",
                email = "Nohan.P@gmail.com",
                notes = "Ketua RT 04 / RW 08 Jati Pulogadung - Hak Akses Penuh"
            ),
            UserRole.SEKRETARIS_RT to UserProfile(
                role = UserRole.SEKRETARIS_RT,
                name = UserRole.SEKRETARIS_RT.defaultName,
                phone = "0815-6677-8899",
                email = "sekretaris.rt04@gmail.com",
                notes = "Sekretaris RT 04 - Administrasi & Kepesertaan Warga"
            ),
            UserRole.BENDAHARA_RT to UserProfile(
                role = UserRole.BENDAHARA_RT,
                name = UserRole.BENDAHARA_RT.defaultName,
                phone = "0813-8899-1122",
                email = "bendahara.rt04@gmail.com",
                notes = "Bendahara RT 04 - Pengelolaan Kas & Rekapitulasi"
            )
        )
    )
    val userProfiles: StateFlow<Map<UserRole, UserProfile>> = _userProfiles.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.KETUA_RT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentUserName = MutableStateFlow(UserRole.KETUA_RT.defaultName)
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    // Navigation
    private val _currentTab = MutableStateFlow<NavigationTab>(NavigationTab.Dashboard)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    // Selected Period (Default August 2026)
    private val _selectedMonth = MutableStateFlow(8)
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    private val _selectedYear = MutableStateFlow(2026)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    // Filters
    private val _txTypeFilter = MutableStateFlow<TransactionType?>(null)
    val txTypeFilter: StateFlow<TransactionType?> = _txTypeFilter.asStateFlow()

    private val _txCategoryFilter = MutableStateFlow<TransactionCategory?>(null)
    val txCategoryFilter: StateFlow<TransactionCategory?> = _txCategoryFilter.asStateFlow()

    private val _txSearchQuery = MutableStateFlow("")
    val txSearchQuery: StateFlow<String> = _txSearchQuery.asStateFlow()

    private val _citizenTypeFilter = MutableStateFlow<CitizenType?>(null)
    val citizenTypeFilter: StateFlow<CitizenType?> = _citizenTypeFilter.asStateFlow()

    private val _citizenPaidStatusFilter = MutableStateFlow<Boolean?>(null) // null=All, true=Lunas, false=Belum
    val citizenPaidStatusFilter: StateFlow<Boolean?> = _citizenPaidStatusFilter.asStateFlow()

    private val _citizenSearchQuery = MutableStateFlow("")
    val citizenSearchQuery: StateFlow<String> = _citizenSearchQuery.asStateFlow()

    // Dialog & Action States
    val showAddTransactionDialog = MutableStateFlow(false)
    val showAddPettyCashExpenseDialog = MutableStateFlow(false)
    val showTopUpPettyCashDialog = MutableStateFlow(false)
    val showPettyCashVoucherDialog = MutableStateFlow<TransactionEntity?>(null)
    val showSharePettyCashReportDialog = MutableStateFlow(false)
    val showManagePasswordsDialog = MutableStateFlow(false)
    val showAddCitizenDialog = MutableStateFlow(false)
    val showRoleSelectionDialog = MutableStateFlow(false)
    val showPeriodPickerDialog = MutableStateFlow(false)
    val showReceiptDialog = MutableStateFlow<TransactionEntity?>(null)
    val showShareReportDialog = MutableStateFlow(false)
    val showNewAnnouncementDialog = MutableStateFlow(false)
    val showFullPhotoDialog = MutableStateFlow<Pair<String, String>?>(null) // Pair<PhotoUrlOrUri, Title>
    val selectedTransactionForEdit = MutableStateFlow<TransactionEntity?>(null)
    val selectedCitizenForEdit = MutableStateFlow<CitizenEntity?>(null)
    val selectedUserProfileForEdit = MutableStateFlow<UserProfile?>(null)

    // Data Flows from Room
    val allCitizens: StateFlow<List<CitizenEntity>> = repository.allCitizens
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Petty Cash Specific Filters & Search
    private val _pettyCashSearchQuery = MutableStateFlow("")
    val pettyCashSearchQuery: StateFlow<String> = _pettyCashSearchQuery.asStateFlow()

    private val _pettyCashTypeFilter = MutableStateFlow<TransactionType?>(null)
    val pettyCashTypeFilter: StateFlow<TransactionType?> = _pettyCashTypeFilter.asStateFlow()

    fun setPettyCashSearchQuery(query: String) {
        _pettyCashSearchQuery.value = query
    }

    fun setPettyCashTypeFilter(type: TransactionType?) {
        _pettyCashTypeFilter.value = type
    }

    fun generateSuggestedBpkkNumber(): String {
        val month = _selectedMonth.value
        val year = _selectedYear.value
        val count = filteredPettyCashTransactions.value.count { it.type == TransactionType.PENGELUARAN } + 1
        return "BPKK-$year${String.format("%02d", month)}-${String.format("%03d", count)}"
    }

    // Filtered Petty Cash Transactions for Selected Period
    val filteredPettyCashTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        selectedMonth,
        selectedYear,
        _pettyCashTypeFilter,
        _pettyCashSearchQuery
    ) { txList, month, year, typeFilter, query ->
        txList.filter { tx ->
            val isPetty = tx.isPettyCash || tx.paymentMethod == PaymentMethod.TUNAI ||
                    tx.category == TransactionCategory.PENGISIAN_KAS_KECIL ||
                    tx.category == TransactionCategory.PENGEMBALIAN_SISA_KAS_KECIL
            val matchPeriod = tx.month == month && tx.year == year
            val matchType = typeFilter == null || tx.type == typeFilter
            val matchQuery = query.isBlank() ||
                    tx.title.contains(query, ignoreCase = true) ||
                    (tx.recipientPerson?.contains(query, ignoreCase = true) == true) ||
                    (tx.citizenName?.contains(query, ignoreCase = true) == true) ||
                    tx.bpkkNumber.contains(query, ignoreCase = true) ||
                    tx.receiptNumber.contains(query, ignoreCase = true)
            isPetty && matchPeriod && matchType && matchQuery
        }.sortedByDescending { it.dateMillis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Intermediate Filters
    private val txFilterState = combine(
        selectedMonth,
        selectedYear,
        txTypeFilter,
        txCategoryFilter,
        txSearchQuery
    ) { month, year, type, cat, query ->
        TxFilter(month, year, type, cat, query)
    }

    private val citizenFilterState = combine(
        selectedMonth,
        selectedYear,
        citizenTypeFilter,
        citizenPaidStatusFilter,
        citizenSearchQuery
    ) { month, year, type, paid, query ->
        CitizenFilter(month, year, type, paid, query)
    }

    // Filtered Transactions for UI
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        txFilterState
    ) { txList, filter ->
        txList.filter { tx ->
            val matchPeriod = tx.month == filter.month && tx.year == filter.year
            val matchType = filter.type == null || tx.type == filter.type
            val matchCat = filter.category == null || tx.category == filter.category
            val matchQuery = filter.query.isBlank() ||
                    tx.title.contains(filter.query, ignoreCase = true) ||
                    (tx.citizenName?.contains(filter.query, ignoreCase = true) == true) ||
                    tx.receiptNumber.contains(filter.query, ignoreCase = true)
            matchPeriod && matchType && matchCat && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Citizens with Dues Status for Selected Period
    val citizensWithDuesStatus: StateFlow<List<CitizenWithDuesStatus>> = combine(
        allCitizens,
        allTransactions,
        citizenFilterState
    ) { citizens, txList, filter ->
        val monthDuesTx = txList.filter {
            it.month == filter.month && it.year == filter.year && it.type == TransactionType.PEMASUKAN && it.citizenId != null
        }
        val duesMap = monthDuesTx.associateBy { it.citizenId }

        citizens.filter { it.isActive }.map { citizen ->
            val tx = duesMap[citizen.id]
            CitizenWithDuesStatus(
                citizen = citizen,
                isPaid = tx != null,
                transaction = tx
            )
        }.filter { item ->
            val matchType = filter.type == null || item.citizen.type == filter.type
            val matchPaid = filter.isPaid == null || item.isPaid == filter.isPaid
            val matchQuery = filter.query.isBlank() ||
                    item.citizen.name.contains(filter.query, ignoreCase = true) ||
                    item.citizen.houseNumber.contains(filter.query, ignoreCase = true)
            matchType && matchPaid && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Automated Monthly Recap State
    private val _monthlyRecap = MutableStateFlow<MonthlyRecap?>(null)
    val monthlyRecap: StateFlow<MonthlyRecap?> = _monthlyRecap.asStateFlow()

    // Automated Petty Cash Recap State (Metode Dana Berubah / Fluctuating)
    private val _pettyCashRecap = MutableStateFlow<com.example.model.PettyCashRecap?>(null)
    val pettyCashRecap: StateFlow<com.example.model.PettyCashRecap?> = _pettyCashRecap.asStateFlow()

    // Overall Balance Summary
    val totalCashBalance: StateFlow<Long> = allTransactions.combine(selectedMonth) { txList, _ ->
        var balance = 0L
        for (tx in txList) {
            if (tx.type == TransactionType.PEMASUKAN) balance += tx.amount
            else balance -= tx.amount
        }
        balance
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Saldo Kas Kecil (Tunai / Kas Operasional di Bendahara)
    val pettyCashBalance: StateFlow<Long> = allTransactions.combine(selectedMonth) { txList, _ ->
        var petty = 0L
        for (tx in txList) {
            val isPetty = tx.isPettyCash || tx.paymentMethod == com.example.model.PaymentMethod.TUNAI ||
                    tx.category == TransactionCategory.PENGISIAN_KAS_KECIL ||
                    tx.category == TransactionCategory.PENGEMBALIAN_SISA_KAS_KECIL
            if (isPetty) {
                if (tx.type == TransactionType.PEMASUKAN) petty += tx.amount
                else petty -= tx.amount
            }
        }
        petty
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Saldo Kas Bank / QRIS
    val bankCashBalance: StateFlow<Long> = allTransactions.combine(selectedMonth) { txList, _ ->
        var bank = 0L
        for (tx in txList) {
            val isPetty = tx.isPettyCash || tx.paymentMethod == com.example.model.PaymentMethod.TUNAI ||
                    tx.category == TransactionCategory.PENGISIAN_KAS_KECIL ||
                    tx.category == TransactionCategory.PENGEMBALIAN_SISA_KAS_KECIL
            if (!isPetty) {
                if (tx.type == TransactionType.PEMASUKAN) bank += tx.amount
                else bank -= tx.amount
            }
        }
        bank
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    init {
        // Recalculate monthly recap & petty cash recap on transactions or period changes
        viewModelScope.launch {
            combine(allTransactions, allCitizens, selectedMonth, selectedYear) { _, _, month, year ->
                Pair(month, year)
            }.collect { (month, year) ->
                refreshMonthlyRecap(month, year)
                refreshPettyCashRecap(month, year)
            }
        }
    }

    fun setNavigationTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun setPeriod(month: Int, year: Int) {
        _selectedMonth.value = month
        _selectedYear.value = year
    }

    fun loginWithPin(role: UserRole, pin: String): Boolean {
        val isValid = authManager.verifyPassword(role, pin)
        if (isValid) {
            _currentRole.value = role
            val profile = _userProfiles.value[role]
            _currentUserName.value = profile?.name ?: role.defaultName
            _isLoggedIn.value = true
            authManager.setLastRole(role)
            repository.cloudSyncEngine.updateActiveRole(role)
            return true
        }
        return false
    }

    fun updateRolePassword(targetRole: UserRole, newPin: String): Boolean {
        // Ketua RT dapat mengubah semua password, sedangkan Sekretaris / Bendahara dapat mengubah password akun mereka sendiri
        if (_currentRole.value != UserRole.KETUA_RT && _currentRole.value != targetRole) {
            return false
        }
        val success = authManager.setPasswordForRole(targetRole, newPin)
        if (success) {
            _rolePasswords.value = authManager.getAllPasswords()
        }
        return success
    }

    fun resetAllPasswordsToDefault(): Boolean {
        if (_currentRole.value != UserRole.KETUA_RT) {
            return false
        }
        authManager.resetAllPasswordsToDefault()
        _rolePasswords.value = authManager.getAllPasswords()
        return true
    }

    fun login(role: UserRole, userName: String) {
        _currentRole.value = role
        val profile = _userProfiles.value[role]
        val effectiveName = if (userName.isNotBlank()) userName else (profile?.name ?: role.defaultName)
        _currentUserName.value = effectiveName
        _isLoggedIn.value = true
        repository.cloudSyncEngine.updateActiveRole(role)
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun switchRole(role: UserRole) {
        _currentRole.value = role
        val profile = _userProfiles.value[role]
        _currentUserName.value = profile?.name ?: role.defaultName
        repository.cloudSyncEngine.updateActiveRole(role)
    }

    fun updateUserProfile(role: UserRole, name: String, phone: String, email: String, notes: String) {
        val updated = _userProfiles.value.toMutableMap()
        updated[role] = UserProfile(role, name, phone, email, notes)
        _userProfiles.value = updated
        if (_currentRole.value == role) {
            _currentUserName.value = name
        }
    }

    fun setTxTypeFilter(type: TransactionType?) {
        _txTypeFilter.value = type
    }

    fun setTxCategoryFilter(cat: TransactionCategory?) {
        _txCategoryFilter.value = cat
    }

    fun setTxSearchQuery(query: String) {
        _txSearchQuery.value = query
    }

    fun setCitizenTypeFilter(type: CitizenType?) {
        _citizenTypeFilter.value = type
    }

    fun setCitizenPaidStatusFilter(status: Boolean?) {
        _citizenPaidStatusFilter.value = status
    }

    fun setCitizenSearchQuery(query: String) {
        _citizenSearchQuery.value = query
    }

    private suspend fun refreshMonthlyRecap(month: Int, year: Int) {
        val recap = repository.calculateMonthlyRecap(month, year)
        _monthlyRecap.value = recap
    }

    suspend fun refreshPettyCashRecap(month: Int, year: Int) {
        val recap = repository.calculatePettyCashRecap(month, year)
        _pettyCashRecap.value = recap
    }

    fun recordPettyCashDisbursement(
        title: String,
        amount: Long,
        category: TransactionCategory,
        recipientPerson: String,
        bpkkNumber: String = "",
        month: Int = _selectedMonth.value,
        year: Int = _selectedYear.value,
        notes: String = "",
        proofPhotoUri: android.net.Uri? = null,
        proofPhotoDescription: String? = null,
        context: android.content.Context? = null
    ) {
        viewModelScope.launch {
            val count = filteredPettyCashTransactions.value.count { it.type == TransactionType.PENGELUARAN } + 1
            val effectiveBpkk = if (bpkkNumber.isNotBlank()) bpkkNumber else "BPKK-${year}${String.format("%02d", month)}-${String.format("%03d", count)}"
            val receiptNum = "EXP-KK-${year}${String.format("%02d", month)}-${System.currentTimeMillis().toString().takeLast(4)}"

            val tx = TransactionEntity(
                title = title,
                amount = amount,
                type = TransactionType.PENGELUARAN,
                category = category,
                citizenId = null,
                citizenName = recipientPerson.ifBlank { "Pengeluaran Kas Kecil" },
                month = month,
                year = year,
                dateMillis = System.currentTimeMillis(),
                recordedBy = "${_currentRole.value.title} - ${_currentUserName.value}",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = receiptNum,
                notes = notes,
                syncId = "SYNC-KK-${System.currentTimeMillis()}",
                proofPhotoDescription = proofPhotoDescription,
                isPettyCash = true,
                bpkkNumber = effectiveBpkk,
                recipientPerson = recipientPerson
            )
            repository.insertTransaction(tx, selectedImageUri = proofPhotoUri, context = context)
            showAddPettyCashExpenseDialog.value = false
            refreshPettyCashRecap(month, year)
        }
    }

    fun topUpPettyCash(
        amount: Long,
        sourceMethod: PaymentMethod = PaymentMethod.TRANSFER_BCA,
        notes: String = "Pengisian dana kas kecil operasional",
        month: Int = _selectedMonth.value,
        year: Int = _selectedYear.value
    ) {
        viewModelScope.launch {
            val count = filteredPettyCashTransactions.value.count { it.type == TransactionType.PEMASUKAN } + 1
            val effectiveBpkk = "KK-IN-${year}${String.format("%02d", month)}-${String.format("%03d", count)}"
            val receiptNum = "KW-KK-${year}${String.format("%02d", month)}-${System.currentTimeMillis().toString().takeLast(4)}"

            val tx = TransactionEntity(
                title = "Pengisian / Top Up Kas Kecil (${sourceMethod.label})",
                amount = amount,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.PENGISIAN_KAS_KECIL,
                citizenId = null,
                citizenName = "Kasir Kas Kecil",
                month = month,
                year = year,
                dateMillis = System.currentTimeMillis(),
                recordedBy = "${_currentRole.value.title} - ${_currentUserName.value}",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = receiptNum,
                notes = notes,
                syncId = "SYNC-TOPUP-${System.currentTimeMillis()}",
                isPettyCash = true,
                bpkkNumber = effectiveBpkk,
                recipientPerson = "${_currentRole.value.title} (${_currentUserName.value})"
            )
            repository.insertTransaction(tx)
            showTopUpPettyCashDialog.value = false
            refreshPettyCashRecap(month, year)
        }
    }

    fun recordNewTransaction(
        title: String,
        amount: Long,
        type: TransactionType,
        category: TransactionCategory,
        citizen: CitizenEntity?,
        address: String? = null,
        month: Int = _selectedMonth.value,
        year: Int = _selectedYear.value,
        paymentMethod: PaymentMethod = PaymentMethod.TUNAI,
        notes: String = "",
        proofPhotoUri: android.net.Uri? = null,
        proofPhotoDescription: String? = null,
        context: android.content.Context? = null
    ) {
        viewModelScope.launch {
            val prefix = if (type == TransactionType.PEMASUKAN) "KW-IN" else "EXP-OUT"
            val receiptNum = "$prefix-${year}${String.format("%02d", month)}-${System.currentTimeMillis().toString().takeLast(5)}"

            val tx = TransactionEntity(
                title = title,
                amount = amount,
                type = type,
                category = category,
                citizenId = citizen?.id,
                citizenName = citizen?.name,
                address = address?.ifBlank { null } ?: citizen?.houseNumber?.ifBlank { null },
                month = month,
                year = year,
                dateMillis = System.currentTimeMillis(),
                recordedBy = "${_currentRole.value.title} - ${_currentUserName.value}",
                paymentMethod = paymentMethod,
                receiptNumber = receiptNum,
                notes = notes,
                syncId = "SYNC-${System.currentTimeMillis()}",
                proofPhotoDescription = proofPhotoDescription
            )
            repository.insertTransaction(tx, selectedImageUri = proofPhotoUri, context = context)
            showAddTransactionDialog.value = false
        }
    }

    fun updateExistingTransaction(
        transaction: TransactionEntity,
        proofPhotoUri: android.net.Uri? = null,
        context: android.content.Context? = null
    ) {
        viewModelScope.launch {
            repository.updateTransaction(transaction, selectedImageUri = proofPhotoUri, context = context)
            selectedTransactionForEdit.value = null
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun quickMarkCitizenPaid(citizen: CitizenEntity, paymentMethod: PaymentMethod = PaymentMethod.TUNAI) {
        viewModelScope.launch {
            repository.payCitizenDues(
                citizen = citizen,
                month = _selectedMonth.value,
                year = _selectedYear.value,
                amount = citizen.monthlyFee,
                paymentMethod = paymentMethod,
                recordedBy = "${_currentRole.value.title} - ${_currentUserName.value}"
            )
        }
    }

    fun saveCitizen(
        id: Long = 0,
        name: String,
        houseNumber: String,
        phone: String,
        type: CitizenType,
        monthlyFee: Long,
        notes: String
    ) {
        viewModelScope.launch {
            val citizen = CitizenEntity(
                id = id,
                name = name,
                houseNumber = houseNumber,
                phone = phone,
                type = type,
                monthlyFee = monthlyFee,
                notes = notes,
                updatedAtMillis = System.currentTimeMillis()
            )
            if (id == 0L) {
                repository.insertCitizen(citizen)
            } else {
                repository.updateCitizen(citizen)
            }
            showAddCitizenDialog.value = false
            selectedCitizenForEdit.value = null
        }
    }

    fun deleteCitizen(citizen: CitizenEntity) {
        viewModelScope.launch {
            repository.deleteCitizen(citizen)
        }
    }

    fun postAnnouncement(title: String, content: String, priority: String) {
        viewModelScope.launch {
            val announcement = AnnouncementEntity(
                title = title,
                content = content,
                authorRole = "${_currentRole.value.title} (${_currentUserName.value})",
                dateMillis = System.currentTimeMillis(),
                priority = priority
            )
            repository.insertAnnouncement(announcement)
            showNewAnnouncementDialog.value = false
        }
    }

    fun deleteAnnouncement(announcement: AnnouncementEntity) {
        viewModelScope.launch {
            repository.deleteAnnouncement(announcement)
        }
    }

    fun triggerCloudSync(onDone: (() -> Unit)? = null) {
        repository.cloudSyncEngine.triggerRealtimeSync(onDone)
    }

    fun toggleOnlineMode() {
        repository.cloudSyncEngine.toggleOnlineStatus()
    }

    fun updateSyncRoomCode(code: String) {
        repository.cloudSyncEngine.updateSyncCode(code)
    }

    fun updateDeviceName(deviceId: String, name: String) {
        repository.cloudSyncEngine.updateDeviceName(deviceId, name)
    }

    fun autoDetectCurrentDevice(): String {
        return repository.cloudSyncEngine.autoDetectCurrentDeviceName(_currentRole.value.title)
    }

    // Helper Formatters
    companion object {
        fun formatRupiah(amount: Long): String {
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            format.maximumFractionDigits = 0
            return format.format(amount).replace("Rp", "Rp ")
        }

        fun formatDate(millis: Long): String {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
            return sdf.format(Date(millis))
        }

        fun formatSimpleDate(millis: Long): String {
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            return sdf.format(Date(millis))
        }

        fun getMonthName(month: Int): String = when (month) {
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
}
