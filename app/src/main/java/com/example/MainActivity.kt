package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.UserRole
import com.example.ui.components.EditUserProfileDialog
import com.example.ui.components.FullPhotoViewDialog
import com.example.ui.components.KwitansiDialog
import com.example.ui.components.ManagePasswordsDialog
import com.example.ui.components.MonthYearPickerDialog
import com.example.ui.components.NewAnnouncementDialog
import com.example.ui.components.PettyCashVoucherDialog
import com.example.ui.components.RoleSelectionDialog
import com.example.ui.components.SharePettyCashReportDialog
import com.example.ui.components.ShareReportDialog
import com.example.ui.components.TopRoleHeader
import com.example.ui.screens.AddEditCitizenDialog
import com.example.ui.screens.AddEditTransactionDialog
import com.example.ui.screens.AddPettyCashExpenseDialog
import com.example.ui.screens.CitizenListScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MonthlyRecapReportScreen
import com.example.ui.screens.MultiDeviceSyncScreen
import com.example.ui.screens.PettyCashScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TopUpPettyCashDialog
import com.example.ui.screens.TransactionListScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.RtCashViewModel

enum class AppScreen {
    SPLASH,
    LOGIN,
    MAIN_APP
}

class MainActivity : ComponentActivity() {
    private val viewModel: RtCashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppNavigationRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppNavigationRoot(viewModel: RtCashViewModel) {
    var appScreen by remember { mutableStateOf(AppScreen.SPLASH) }
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userProfiles by viewModel.userProfiles.collectAsStateWithLifecycle()

    Crossfade(
        targetState = appScreen,
        animationSpec = tween(durationMillis = 400),
        label = "AppScreenCrossfade"
    ) { currentScreen ->
        when (currentScreen) {
            AppScreen.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        appScreen = if (isLoggedIn) AppScreen.MAIN_APP else AppScreen.LOGIN
                    }
                )
            }
            AppScreen.LOGIN -> {
                LoginScreen(
                    userProfiles = userProfiles,
                    initialRole = viewModel.authManager.getLastRole(),
                    onLoginSuccess = { role, pin ->
                        val success = viewModel.loginWithPin(role, pin)
                        if (success) {
                            appScreen = AppScreen.MAIN_APP
                        }
                        success
                    }
                )
            }
            AppScreen.MAIN_APP -> {
                KasRtApp(
                    viewModel = viewModel,
                    onLogout = {
                        viewModel.logout()
                        appScreen = AppScreen.LOGIN
                    }
                )
            }
        }
    }
}

@Composable
fun KasRtApp(
    viewModel: RtCashViewModel,
    onLogout: () -> Unit = {}
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val currentUserName by viewModel.currentUserName.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isOnline by viewModel.repository.cloudSyncEngine.isOnline.collectAsStateWithLifecycle()
    val isSyncing by viewModel.repository.cloudSyncEngine.isSyncing.collectAsStateWithLifecycle()

    // Dialog state collectors
    val showRoleSelection by viewModel.showRoleSelectionDialog.collectAsStateWithLifecycle()
    val showManagePasswords by viewModel.showManagePasswordsDialog.collectAsStateWithLifecycle()
    val rolePasswords by viewModel.rolePasswords.collectAsStateWithLifecycle()
    val showPeriodPicker by viewModel.showPeriodPickerDialog.collectAsStateWithLifecycle()
    val showAddTransaction by viewModel.showAddTransactionDialog.collectAsStateWithLifecycle()
    val selectedTxForEdit by viewModel.selectedTransactionForEdit.collectAsStateWithLifecycle()
    val showAddCitizen by viewModel.showAddCitizenDialog.collectAsStateWithLifecycle()
    val selectedCitizenForEdit by viewModel.selectedCitizenForEdit.collectAsStateWithLifecycle()
    val selectedUserProfileForEdit by viewModel.selectedUserProfileForEdit.collectAsStateWithLifecycle()
    val userProfiles by viewModel.userProfiles.collectAsStateWithLifecycle()
    val selectedReceiptTx by viewModel.showReceiptDialog.collectAsStateWithLifecycle()
    val showShareReport by viewModel.showShareReportDialog.collectAsStateWithLifecycle()
    val showNewAnnouncement by viewModel.showNewAnnouncementDialog.collectAsStateWithLifecycle()
    val showFullPhoto by viewModel.showFullPhotoDialog.collectAsStateWithLifecycle()
    val allCitizens by viewModel.allCitizens.collectAsStateWithLifecycle()
    val monthlyRecap by viewModel.monthlyRecap.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()

    // Petty Cash state collectors
    val showAddPettyCashExpense by viewModel.showAddPettyCashExpenseDialog.collectAsStateWithLifecycle()
    val showTopUpPettyCash by viewModel.showTopUpPettyCashDialog.collectAsStateWithLifecycle()
    val showSharePettyCashReport by viewModel.showSharePettyCashReportDialog.collectAsStateWithLifecycle()
    val selectedPettyCashVoucherTx by viewModel.showPettyCashVoucherDialog.collectAsStateWithLifecycle()
    val pettyCashRecap by viewModel.pettyCashRecap.collectAsStateWithLifecycle()
    val pettyCashTransactions by viewModel.filteredPettyCashTransactions.collectAsStateWithLifecycle()
    val pettyCashSearchQuery by viewModel.pettyCashSearchQuery.collectAsStateWithLifecycle()
    val pettyCashTypeFilter by viewModel.pettyCashTypeFilter.collectAsStateWithLifecycle()
    val pettyCashBalance by viewModel.pettyCashBalance.collectAsStateWithLifecycle()

    val navTabs = listOf(
        NavigationItem(
            tab = NavigationTab.Dashboard,
            icon = Icons.Default.Dashboard,
            label = "Beranda",
            testTag = "nav_dashboard"
        ),
        NavigationItem(
            tab = NavigationTab.Transactions,
            icon = Icons.Default.ReceiptLong,
            label = "Arus Kas",
            testTag = "nav_transactions"
        ),
        NavigationItem(
            tab = NavigationTab.PettyCash,
            icon = Icons.Default.AccountBalanceWallet,
            label = "Kas Kecil",
            testTag = "nav_petty_cash"
        ),
        NavigationItem(
            tab = NavigationTab.Citizens,
            icon = Icons.Default.People,
            label = "Warga",
            testTag = "nav_citizens"
        ),
        NavigationItem(
            tab = NavigationTab.RecapReport,
            icon = Icons.Default.Assessment,
            label = "Rekap",
            testTag = "nav_recap"
        ),
        NavigationItem(
            tab = NavigationTab.MultiDeviceSync,
            icon = Icons.Default.Devices,
            label = "Sync Cloud",
            testTag = "nav_sync"
        )
    )

    Scaffold(
        topBar = {
            TopRoleHeader(
                currentRole = currentRole,
                userName = currentUserName,
                isOnline = isOnline,
                isSyncing = isSyncing,
                onSwitchRoleClick = { viewModel.showRoleSelectionDialog.value = true },
                onSyncStatusClick = { viewModel.setNavigationTab(NavigationTab.MultiDeviceSync) }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    navTabs.forEach { item ->
                        val isSelected = currentTab == item.tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setNavigationTab(item.tab) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = EmeraldPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    val direction = if (targetState.tabIndex() >= initialState.tabIndex()) 1 else -1
                    (slideInHorizontally(
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        initialOffsetX = { fullWidth -> direction * fullWidth }
                    ) + fadeIn(animationSpec = tween(300)))
                        .togetherWith(
                            slideOutHorizontally(
                                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                                targetOffsetX = { fullWidth -> -direction * fullWidth }
                            ) + fadeOut(animationSpec = tween(300))
                        )
                },
                label = "NavigationTabSlidingAnimation",
                modifier = Modifier.fillMaxSize()
            ) { targetTab ->
                when (targetTab) {
                    NavigationTab.Dashboard -> DashboardScreen(viewModel = viewModel)
                    NavigationTab.Transactions -> TransactionListScreen(viewModel = viewModel)
                    NavigationTab.PettyCash -> PettyCashScreen(
                        pettyCashRecap = pettyCashRecap,
                        transactions = pettyCashTransactions,
                        selectedMonth = selectedMonth,
                        selectedYear = selectedYear,
                        searchQuery = pettyCashSearchQuery,
                        selectedTypeFilter = pettyCashTypeFilter,
                        onSearchChange = { viewModel.setPettyCashSearchQuery(it) },
                        onTypeFilterChange = { viewModel.setPettyCashTypeFilter(it) },
                        onPeriodClick = { viewModel.showPeriodPickerDialog.value = true },
                        onPreviousMonth = {
                            if (selectedMonth == 1) {
                                viewModel.setPeriod(12, selectedYear - 1)
                            } else {
                                viewModel.setPeriod(selectedMonth - 1, selectedYear)
                            }
                        },
                        onNextMonth = {
                            if (selectedMonth == 12) {
                                viewModel.setPeriod(1, selectedYear + 1)
                            } else {
                                viewModel.setPeriod(selectedMonth + 1, selectedYear)
                            }
                        },
                        onAddExpenseClick = { viewModel.showAddPettyCashExpenseDialog.value = true },
                        onTopUpClick = { viewModel.showTopUpPettyCashDialog.value = true },
                        onShareReportClick = { viewModel.showSharePettyCashReportDialog.value = true },
                        onViewVoucherClick = { tx -> viewModel.showPettyCashVoucherDialog.value = tx },
                        onEditTransactionClick = { tx -> viewModel.selectedTransactionForEdit.value = tx },
                        onDeleteTransactionClick = { tx -> viewModel.deleteTransaction(tx) },
                        onViewPhotoClick = { photoUrl, title ->
                            viewModel.showFullPhotoDialog.value = Pair(photoUrl, title)
                        }
                    )
                    NavigationTab.Citizens -> CitizenListScreen(viewModel = viewModel)
                    NavigationTab.RecapReport -> MonthlyRecapReportScreen(viewModel = viewModel)
                    NavigationTab.MultiDeviceSync -> MultiDeviceSyncScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Role Selection Dialog
    if (showRoleSelection) {
        RoleSelectionDialog(
            currentRole = currentRole,
            userProfiles = userProfiles,
            onRoleSelected = { role ->
                viewModel.switchRole(role)
                viewModel.showRoleSelectionDialog.value = false
            },
            onEditUserProfile = { profile ->
                viewModel.selectedUserProfileForEdit.value = profile
            },
            onManagePasswordsClick = {
                viewModel.showManagePasswordsDialog.value = true
            },
            onLogoutClick = {
                onLogout()
            },
            onDismiss = { viewModel.showRoleSelectionDialog.value = false }
        )
    }

    // Manage Passwords Dialog (Hak Akses Master Ketua RT)
    if (showManagePasswords) {
        ManagePasswordsDialog(
            currentRole = currentRole,
            userProfiles = userProfiles,
            rolePasswords = rolePasswords,
            onUpdatePassword = { role, newPin ->
                viewModel.updateRolePassword(role, newPin)
            },
            onResetAllPasswords = {
                viewModel.resetAllPasswordsToDefault()
            },
            onDismiss = { viewModel.showManagePasswordsDialog.value = false }
        )
    }

    // Edit User Profile Dialog (Hak Akses Ketua RT)
    selectedUserProfileForEdit?.let { profile ->
        EditUserProfileDialog(
            userProfile = profile,
            onSave = { role, name, phone, email, notes ->
                viewModel.updateUserProfile(role, name, phone, email, notes)
                viewModel.selectedUserProfileForEdit.value = null
            },
            onDismiss = { viewModel.selectedUserProfileForEdit.value = null }
        )
    }

    // Month Year Picker Calendar Dialog
    if (showPeriodPicker) {
        MonthYearPickerDialog(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onPeriodSelected = { month, year ->
                viewModel.setPeriod(month, year)
            },
            onDismiss = { viewModel.showPeriodPickerDialog.value = false }
        )
    }

    // Add / Edit Transaction Dialog
    if (showAddTransaction || selectedTxForEdit != null) {
        AddEditTransactionDialog(
            citizens = allCitizens,
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            onSave = { title, address, amount, type, category, citizen, month, year, paymentMethod, notes, proofPhotoUri, proofPhotoDescription, ctx ->
                if (selectedTxForEdit != null) {
                    viewModel.updateExistingTransaction(
                        selectedTxForEdit!!.copy(
                            title = title,
                            amount = amount,
                            type = type,
                            category = category,
                            citizenId = citizen?.id,
                            citizenName = citizen?.name,
                            address = address,
                            month = month,
                            year = year,
                            paymentMethod = paymentMethod,
                            notes = notes,
                            proofPhotoDescription = proofPhotoDescription
                        ),
                        proofPhotoUri = proofPhotoUri,
                        context = ctx
                    )
                } else {
                    viewModel.recordNewTransaction(
                        title = title,
                        amount = amount,
                        type = type,
                        category = category,
                        citizen = citizen,
                        address = address,
                        month = month,
                        year = year,
                        paymentMethod = paymentMethod,
                        notes = notes,
                        proofPhotoUri = proofPhotoUri,
                        proofPhotoDescription = proofPhotoDescription,
                        context = ctx
                    )
                }
            },
            onDismiss = {
                viewModel.showAddTransactionDialog.value = false
                viewModel.selectedTransactionForEdit.value = null
            }
        )
    }

    // Add / Edit Citizen Dialog
    if (showAddCitizen || selectedCitizenForEdit != null) {
        AddEditCitizenDialog(
            initialCitizen = selectedCitizenForEdit,
            onSave = { id, name, houseNumber, phone, type, monthlyFee, notes ->
                viewModel.saveCitizen(
                    id = id,
                    name = name,
                    houseNumber = houseNumber,
                    phone = phone,
                    type = type,
                    monthlyFee = monthlyFee,
                    notes = notes
                )
            },
            onDelete = { citizen ->
                viewModel.deleteCitizen(citizen)
                viewModel.selectedCitizenForEdit.value = null
            },
            onDismiss = {
                viewModel.showAddCitizenDialog.value = false
                viewModel.selectedCitizenForEdit.value = null
            }
        )
    }

    // Digital Kwitansi Dialog
    selectedReceiptTx?.let { tx ->
        KwitansiDialog(
            transaction = tx,
            onViewFullPhoto = { photoUrl, title ->
                viewModel.showFullPhotoDialog.value = Pair(photoUrl, title)
            },
            onDismiss = { viewModel.showReceiptDialog.value = null }
        )
    }

    // Full Screen Photo View Dialog (Bukti Nota / Barang)
    showFullPhoto?.let { (photoUrl, title) ->
        FullPhotoViewDialog(
            photoUrlOrPath = photoUrl,
            title = title,
            onDismiss = { viewModel.showFullPhotoDialog.value = null }
        )
    }

    // WhatsApp Recap Report Dialog
    if (showShareReport) {
        monthlyRecap?.let { recap ->
            ShareReportDialog(
                recap = recap,
                onDismiss = { viewModel.showShareReportDialog.value = false }
            )
        }
    }

    // New Announcement Dialog
    if (showNewAnnouncement) {
        NewAnnouncementDialog(
            onPost = { title, content, priority ->
                viewModel.postAnnouncement(title, content, priority)
            },
            onDismiss = { viewModel.showNewAnnouncementDialog.value = false }
        )
    }

    // Add Petty Cash Expense Dialog (BPKK)
    if (showAddPettyCashExpense) {
        val suggestedBpkk = viewModel.generateSuggestedBpkkNumber()
        AddPettyCashExpenseDialog(
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            suggestedBpkkNumber = suggestedBpkk,
            onSave = { title, amount, category, recipientPerson, bpkkNumber, notes, proofPhotoUri, proofPhotoDescription, ctx ->
                viewModel.recordPettyCashDisbursement(
                    title = title,
                    amount = amount,
                    category = category,
                    recipientPerson = recipientPerson,
                    bpkkNumber = bpkkNumber,
                    notes = notes,
                    proofPhotoUri = proofPhotoUri,
                    proofPhotoDescription = proofPhotoDescription,
                    context = ctx
                )
            },
            onDismiss = { viewModel.showAddPettyCashExpenseDialog.value = false }
        )
    }

    // Top Up Petty Cash Dialog (Pencairan Fluktuasi)
    if (showTopUpPettyCash) {
        TopUpPettyCashDialog(
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            currentPettyCashBalance = pettyCashBalance,
            onConfirmTopUp = { amount, sourceMethod, notes ->
                viewModel.topUpPettyCash(
                    amount = amount,
                    sourceMethod = sourceMethod,
                    notes = notes
                )
            },
            onDismiss = { viewModel.showTopUpPettyCashDialog.value = false }
        )
    }

    // Petty Cash Voucher Dialog (Voucher BPKK)
    selectedPettyCashVoucherTx?.let { tx ->
        PettyCashVoucherDialog(
            transaction = tx,
            onViewPhoto = { photoPath, title ->
                viewModel.showFullPhotoDialog.value = Pair(photoPath, title)
            },
            onDismiss = { viewModel.showPettyCashVoucherDialog.value = null }
        )
    }

    // Share Petty Cash Report Dialog (Laporan Tersendiri Kas Kecil)
    if (showSharePettyCashReport) {
        pettyCashRecap?.let { recap ->
            SharePettyCashReportDialog(
                recap = recap,
                onDismiss = { viewModel.showSharePettyCashReportDialog.value = false }
            )
        }
    }
}

private data class NavigationItem(
    val tab: NavigationTab,
    val icon: ImageVector,
    val label: String,
    val testTag: String
)

private fun NavigationTab.tabIndex(): Int = when (this) {
    NavigationTab.Dashboard -> 0
    NavigationTab.Transactions -> 1
    NavigationTab.PettyCash -> 2
    NavigationTab.Citizens -> 3
    NavigationTab.RecapReport -> 4
    NavigationTab.MultiDeviceSync -> 5
}

