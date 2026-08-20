package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.TransactionCategory
import com.example.model.TransactionType
import com.example.ui.components.TransactionCard
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.RtCashViewModel

import com.example.ui.components.PeriodHeaderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: RtCashViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val txTypeFilter by viewModel.txTypeFilter.collectAsStateWithLifecycle()
    val txCategoryFilter by viewModel.txCategoryFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.txSearchQuery.collectAsStateWithLifecycle()

    val monthName = RtCashViewModel.getMonthName(selectedMonth)

    val totalIncome = transactions.filter { it.type == TransactionType.PEMASUKAN }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == TransactionType.PENGELUARAN }.sumOf { it.amount }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("transaction_list_screen"),
        floatingActionButton = {
            if (currentRole.canRecordTransaction) {
                FloatingActionButton(
                    onClick = { viewModel.showAddTransactionDialog.value = true },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_transaction")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Catat Transaksi")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Catat Kas", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("tx_list_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Unified Period Selector Bar with Title
            item {
                PeriodHeaderCard(
                    title = "Buku Arus Kas & Mutasi",
                    subtitle = "Pencatatan Pemasukan & Pengeluaran Kas RT004",
                    icon = Icons.Default.ReceiptLong,
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
                    testTagPrefix = "tx_list"
                )
            }

            // Search Bar & Filter Tabs
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setTxSearchQuery(it) },
                        placeholder = { Text("Cari perihal, warga, no kwitansi...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Cari", tint = EmeraldPrimary)
                        },
                        trailingIcon = if (searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { viewModel.setTxSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tx_search_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Type Chips: Semua, Pemasukan, Pengeluaran (Bold & High Contrast)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = txTypeFilter == null,
                            onClick = { viewModel.setTxTypeFilter(null) },
                            label = {
                                Text(
                                    text = "Semua (${transactions.size})",
                                    fontSize = 12.5.sp,
                                    fontWeight = if (txTypeFilter == null) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("filter_tx_all")
                        )

                        FilterChip(
                            selected = txTypeFilter == TransactionType.PEMASUKAN,
                            onClick = { viewModel.setTxTypeFilter(TransactionType.PEMASUKAN) },
                            label = {
                                Text(
                                    text = "Masuk (${RtCashViewModel.formatRupiah(totalIncome)})",
                                    fontSize = 12.5.sp,
                                    fontWeight = if (txTypeFilter == TransactionType.PEMASUKAN) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IncomeGreen,
                                selectedLabelColor = Color.White,
                                containerColor = IncomeGreen.copy(alpha = 0.15f),
                                labelColor = IncomeGreen
                            ),
                            modifier = Modifier.testTag("filter_tx_income")
                        )

                        FilterChip(
                            selected = txTypeFilter == TransactionType.PENGELUARAN,
                            onClick = { viewModel.setTxTypeFilter(TransactionType.PENGELUARAN) },
                            label = {
                                Text(
                                    text = "Keluar (${RtCashViewModel.formatRupiah(totalExpense)})",
                                    fontSize = 12.5.sp,
                                    fontWeight = if (txTypeFilter == TransactionType.PENGELUARAN) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ExpenseRed,
                                selectedLabelColor = Color.White,
                                containerColor = ExpenseRed.copy(alpha = 0.15f),
                                labelColor = ExpenseRed
                            ),
                            modifier = Modifier.testTag("filter_tx_expense")
                        )
                    }
                }
            }

            // Transaction List Header / Count
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Transaksi Kas",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${transactions.size} Mutasi",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Transactions Items
            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = "Kosong",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tidak Ada Transaksi Ditemukan",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Gunakan tombol Catat Kas di bawah untuk mencatat pemasukan atau pengeluaran kas RT.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(transactions, key = { it.id }) { tx ->
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
            }
        }
    }
}
