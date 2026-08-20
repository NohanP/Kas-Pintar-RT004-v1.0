package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AnnouncementEntity
import com.example.model.TransactionEntity
import com.example.model.TransactionType
import com.example.model.UserRole
import com.example.ui.components.MainBalanceHeroCard
import com.example.ui.components.SummaryMiniCard
import com.example.ui.components.TransactionCard
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.AmberTertiaryContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.PendingYellow
import com.example.ui.theme.PendingYellowLight
import com.example.ui.theme.TealSecondary
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.RtCashViewModel

import com.example.ui.components.PeriodHeaderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: RtCashViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val totalBalance by viewModel.totalCashBalance.collectAsStateWithLifecycle()
    val pettyCashBalance by viewModel.pettyCashBalance.collectAsStateWithLifecycle()
    val bankCashBalance by viewModel.bankCashBalance.collectAsStateWithLifecycle()
    val monthlyRecap by viewModel.monthlyRecap.collectAsStateWithLifecycle()
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()

    val monthName = RtCashViewModel.getMonthName(selectedMonth)
    val monthIncome = monthlyRecap?.totalIncome ?: 0L
    val monthExpense = monthlyRecap?.totalExpense ?: 0L
    val complianceRate = monthlyRecap?.complianceRate ?: 0f
    val paidCount = monthlyRecap?.paidCitizensCount ?: 0
    val totalCitizens = monthlyRecap?.totalCitizens ?: 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Period Selector Bar with Title
        item {
            PeriodHeaderCard(
                title = "Ringkasan Kas RT004",
                subtitle = "Posisi Saldo & Performa Iuran Warga",
                icon = Icons.Default.AccountBalance,
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
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
                onOpenCalendarPicker = { viewModel.showPeriodPickerDialog.value = true },
                testTagPrefix = "dashboard"
            )
        }

        // Main Balance Hero Card with Saldo Kas Kecil
        item {
            MainBalanceHeroCard(
                totalBalance = totalBalance,
                pettyCashBalance = pettyCashBalance,
                bankCashBalance = bankCashBalance,
                monthIncome = monthIncome,
                monthExpense = monthExpense,
                monthName = monthName,
                year = selectedYear
            )
        }

        // Monthly Dues Participation Tracker Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dues_compliance_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "Partisipasi Iuran",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Partisipasi Iuran $monthName",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$paidCount dari $totalCitizens Warga & Usaha Sudah Bayar",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "${complianceRate.toInt()}%",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                color = if (complianceRate >= 80f) IncomeGreen else PendingYellow
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { (complianceRate / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (complianceRate >= 80f) IncomeGreen else AmberTertiary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Belum Bayar: ${totalCitizens - paidCount} Warga/Pelaku Usaha",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Buka Daftar Warga →",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.clickable {
                                viewModel.setNavigationTab(NavigationTab.Citizens)
                            }
                        )
                    }
                }
            }
        }

        // Recent Transactions Header & List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaksi Terkini ($monthName)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Lihat Semua (${transactions.size}) →",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .clickable { viewModel.setNavigationTab(NavigationTab.Transactions) }
                        .testTag("see_all_transactions_btn")
                )
            }
        }

        items(transactions.take(6)) { tx ->
            TransactionCard(
                transaction = tx,
                canDelete = currentRole.canRecordTransaction,
                onReceiptClick = { viewModel.showReceiptDialog.value = tx },
                onDeleteClick = { viewModel.deleteTransaction(tx) },
                onPhotoClick = { photoUrl, title ->
                    viewModel.showFullPhotoDialog.value = Pair(photoUrl, title)
                }
            )
        }

        if (transactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Belum Ada",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada transaksi di bulan $monthName $selectedYear",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
