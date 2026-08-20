package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CategoryBreakdown
import com.example.model.PettyCashRecap
import com.example.model.TransactionEntity
import com.example.model.TransactionType
import com.example.ui.components.PeriodHeaderCard
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.RtCashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PettyCashScreen(
    pettyCashRecap: PettyCashRecap?,
    transactions: List<TransactionEntity>,
    selectedMonth: Int,
    selectedYear: Int,
    searchQuery: String,
    selectedTypeFilter: TransactionType?,
    onSearchChange: (String) -> Unit,
    onTypeFilterChange: (TransactionType?) -> Unit,
    onPeriodClick: () -> Unit,
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    onAddExpenseClick: () -> Unit,
    onTopUpClick: () -> Unit,
    onShareReportClick: () -> Unit,
    onViewVoucherClick: (TransactionEntity) -> Unit,
    onEditTransactionClick: (TransactionEntity) -> Unit,
    onDeleteTransactionClick: (TransactionEntity) -> Unit,
    onViewPhotoClick: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_petty_cash"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Period Header Card
        item {
            PeriodHeaderCard(
                title = "Periode Kas Kecil",
                subtitle = "Buku Kas & Pengeluaran Operasional RT",
                icon = Icons.Default.AccountBalanceWallet,
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onOpenCalendarPicker = onPeriodClick,
                testTagPrefix = "petty_cash"
            )
        }

        // 2. Stat Cards: Financial Summary
        item {
            val recap = pettyCashRecap
            val starting = recap?.startingBalance ?: 0L
            val topUp = recap?.totalTopUp ?: 0L
            val disbursement = recap?.totalDisbursement ?: 0L
            val ending = recap?.endingBalance ?: 0L

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Row 1: Saldo Awal & Saldo Akhir
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PettyStatCard(
                        title = "Saldo Awal Kas",
                        amount = starting,
                        subtitle = "Per 1 ${RtCashViewModel.getMonthName(selectedMonth)}",
                        containerColor = Color(0xFFF8FAFC),
                        accentColor = Color(0xFF475569),
                        modifier = Modifier.weight(1f)
                    )
                    PettyStatCard(
                        title = "Saldo Akhir Kas Kecil",
                        amount = ending,
                        subtitle = "Sisa fisik di Bendahara",
                        containerColor = Color(0xFFECFDF5),
                        accentColor = EmeraldPrimary,
                        modifier = Modifier.weight(1f),
                        isHighlight = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: Top Up (Debet) & Pengeluaran (Kredit)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PettyStatCard(
                        title = "Pengisian / Top Up",
                        amount = topUp,
                        subtitle = "Pencairan dari Kas Bank",
                        containerColor = Color(0xFFF0FDF4),
                        accentColor = IncomeGreen,
                        modifier = Modifier.weight(1f),
                        prefix = "+"
                    )
                    PettyStatCard(
                        title = "Total Pengeluaran",
                        amount = disbursement,
                        subtitle = "${recap?.totalVouchers ?: 0} Transaksi",
                        containerColor = Color(0xFFFEF2F2),
                        accentColor = ExpenseRed,
                        modifier = Modifier.weight(1f),
                        prefix = "-"
                    )
                }
            }
        }

        // 3. Quick Action Buttons Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAddExpenseClick,
                    modifier = Modifier
                        .weight(1.35f)
                        .height(46.dp)
                        .testTag("btn_add_petty_cash_expense"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Catat Pengeluaran", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onTopUpClick,
                    modifier = Modifier
                        .weight(1.05f)
                        .height(46.dp)
                        .testTag("btn_top_up_petty_cash"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Top Up", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onShareReportClick,
                    modifier = Modifier
                        .weight(1.05f)
                        .height(46.dp)
                        .testTag("btn_petty_cash_report"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(18.dp), tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Laporan", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = EmeraldPrimary)
                }
            }
        }

        // 4. Category Breakdown Section (Pos Beban Kas Kecil)
        val categoryBreakdowns = pettyCashRecap?.expenseCategoryBreakdowns ?: emptyList()
        if (categoryBreakdowns.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Text(
                                text = "Distribusi Pemakaian Pos Beban",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${categoryBreakdowns.size} Pos Beban",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        categoryBreakdowns.forEachIndexed { index, cat ->
                            CategoryProgressRow(cat)
                            if (index < categoryBreakdowns.size - 1) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }

        // 5. Search Bar & Filter Chips
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Cari nomor bukti, penerima, atau uraian...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Hapus pencarian")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_petty_cash"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTypeFilter == null,
                        onClick = { onTypeFilterChange(null) },
                        label = { Text("Semua (${transactions.size})", fontSize = 11.5.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = EmeraldPrimary
                        )
                    )
                    FilterChip(
                        selected = selectedTypeFilter == TransactionType.PENGELUARAN,
                        onClick = { onTypeFilterChange(TransactionType.PENGELUARAN) },
                        label = { Text("Pengeluaran", fontSize = 11.5.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ExpenseRed.copy(alpha = 0.15f),
                            selectedLabelColor = ExpenseRed
                        )
                    )
                    FilterChip(
                        selected = selectedTypeFilter == TransactionType.PEMASUKAN,
                        onClick = { onTypeFilterChange(TransactionType.PEMASUKAN) },
                        label = { Text("Top Up Kas", fontSize = 11.5.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IncomeGreen.copy(alpha = 0.15f),
                            selectedLabelColor = IncomeGreen
                        )
                    )
                }
            }
        }

        // 6. Transaction List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Mutasi Kas Kecil",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${transactions.size} Mutasi",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // 7. Transactions or Empty State
        if (transactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Belum Ada Mutasi Kas Kecil",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gunakan tombol di atas untuk mencatat pengeluaran atau mengisi saldo kas kecil.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(transactions, key = { it.id }) { tx ->
                PettyCashTransactionItem(
                    transaction = tx,
                    onViewVoucher = { onViewVoucherClick(tx) },
                    onEdit = { onEditTransactionClick(tx) },
                    onDelete = { onDeleteTransactionClick(tx) },
                    onViewPhoto = onViewPhotoClick
                )
            }
        }
    }
}

@Composable
private fun PettyStatCard(
    title: String,
    amount: Long,
    subtitle: String,
    containerColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    prefix: String = "",
    isHighlight: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isHighlight) accentColor.copy(alpha = 0.4f) else Color(0xFFE2E8F0)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$prefix${RtCashViewModel.formatRupiah(amount)}",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CategoryProgressRow(cat: CategoryBreakdown) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = cat.category.title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${RtCashViewModel.formatRupiah(cat.totalAmount)} (${String.format("%.1f", cat.percentage)}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ExpenseRed
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        LinearProgressIndicator(
            progress = { (cat.percentage / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = ExpenseRed,
            trackColor = Color(0xFFFEE2E2)
        )
    }
}

@Composable
private fun PettyCashTransactionItem(
    transaction: TransactionEntity,
    onViewVoucher: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewPhoto: (String, String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isExpense = transaction.type == TransactionType.PENGELUARAN
    val bpkkLabel = if (transaction.bpkkNumber.isNotBlank()) transaction.bpkkNumber else transaction.receiptNumber

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewVoucher() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Indicator
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isExpense) ExpenseRed.copy(alpha = 0.12f) else IncomeGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isExpense) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (isExpense) ExpenseRed else IncomeGreen,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Main Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // BPKK tag
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isExpense) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (isExpense) Color(0xFFFECACA) else Color(0xFFBBF7D0)
                            )
                        )
                    ) {
                        Text(
                            text = bpkkLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isExpense) ExpenseRed else IncomeGreen,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = transaction.category.title,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val recipient = transaction.recipientPerson ?: transaction.citizenName
                    if (!recipient.isNullOrBlank()) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = recipient,
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(text = " • ", fontSize = 11.5.sp, color = Color(0xFF94A3B8))
                    }
                    Text(
                        text = RtCashViewModel.formatDate(transaction.dateMillis),
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount & Actions
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isExpense) "-" else "+"}${RtCashViewModel.formatRupiah(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    ),
                    color = if (isExpense) ExpenseRed else IncomeGreen
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val photo = transaction.proofPhotoUri ?: transaction.proofPhotoCloudUrl
                    if (!photo.isNullOrBlank()) {
                        IconButton(
                            onClick = {
                                onViewPhoto(photo, "Nota - $bpkkLabel")
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Lihat Nota",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Lihat Voucher BPKK", fontSize = 12.5.sp) },
                                leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                onClick = {
                                    menuExpanded = false
                                    onViewVoucher()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit Transaksi", fontSize = 12.5.sp) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                onClick = {
                                    menuExpanded = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Hapus Transaksi", fontSize = 12.5.sp, color = ExpenseRed) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(16.dp)) },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
