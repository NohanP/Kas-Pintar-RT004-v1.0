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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CitizenType
import com.example.ui.components.CitizenCard
import com.example.ui.components.PeriodHeaderCard
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.PendingYellow
import com.example.ui.viewmodel.RtCashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenListScreen(
    viewModel: RtCashViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val citizensWithStatus by viewModel.citizensWithDuesStatus.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val typeFilter by viewModel.citizenTypeFilter.collectAsStateWithLifecycle()
    val paidFilter by viewModel.citizenPaidStatusFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.citizenSearchQuery.collectAsStateWithLifecycle()

    val monthName = RtCashViewModel.getMonthName(selectedMonth)
    val totalCount = citizensWithStatus.size
    val paidCount = citizensWithStatus.count { it.isPaid }
    val unpaidCount = totalCount - paidCount

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("citizen_list_screen"),
        floatingActionButton = {
            if (currentRole.canManageCitizens) {
                FloatingActionButton(
                    onClick = { viewModel.showAddCitizenDialog.value = true },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_citizen")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Tambah Warga")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Tambah Warga/Usaha", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("citizen_list_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Unified Period Header Card with Title & Subtitle matching Rekap
            item {
                PeriodHeaderCard(
                    title = "Daftar Warga & Pelaku Usaha",
                    subtitle = "Status Iuran Kas Periode $monthName $selectedYear ($paidCount/$totalCount Lunas)",
                    icon = Icons.Default.Groups,
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
                    testTagPrefix = "citizen"
                )
            }

            // Search input and Filter area
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setCitizenSearchQuery(it) },
                        placeholder = { Text("Cari nama warga, warung, no rumah...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Cari", tint = EmeraldPrimary)
                        },
                        trailingIcon = if (searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { viewModel.setCitizenSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("citizen_search_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Type Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = typeFilter == null,
                            onClick = { viewModel.setCitizenTypeFilter(null) },
                            label = { Text("Semua Kategori") }
                        )

                        CitizenType.values().forEach { cType ->
                            FilterChip(
                                selected = typeFilter == cType,
                                onClick = { viewModel.setCitizenTypeFilter(cType) },
                                label = { Text(cType.label) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Payment Status Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = paidFilter == null,
                            onClick = { viewModel.setCitizenPaidStatusFilter(null) },
                            label = {
                                Text(
                                    text = "Semua Status ($totalCount)",
                                    fontSize = 12.sp,
                                    fontWeight = if (paidFilter == null) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = paidFilter == true,
                            onClick = { viewModel.setCitizenPaidStatusFilter(true) },
                            label = {
                                Text(
                                    text = "Lunas ($paidCount)",
                                    fontSize = 12.sp,
                                    fontWeight = if (paidFilter == true) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IncomeGreen,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = paidFilter == false,
                            onClick = { viewModel.setCitizenPaidStatusFilter(false) },
                            label = {
                                Text(
                                    text = "Belum Bayar ($unpaidCount)",
                                    fontSize = 12.sp,
                                    fontWeight = if (paidFilter == false) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PendingYellow,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Data Warga & Status Iuran",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$totalCount KK Terdaftar",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Citizens List
            if (citizensWithStatus.isEmpty()) {
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
                                imageVector = Icons.Default.Groups,
                                contentDescription = "Kosong",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tidak Ada Data Warga / Usaha",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            } else {
                items(citizensWithStatus, key = { it.citizen.id }) { item ->
                    CitizenCard(
                        item = item,
                        monthName = monthName,
                        canRecord = currentRole.canRecordTransaction,
                        onPayClick = { viewModel.quickMarkCitizenPaid(item.citizen) },
                        onReceiptClick = {
                            if (item.transaction != null) {
                                viewModel.showReceiptDialog.value = item.transaction
                            }
                        },
                        onCardClick = {
                            if (currentRole.canManageCitizens) {
                                viewModel.selectedCitizenForEdit.value = item.citizen
                            }
                        }
                    )
                }
            }
        }
    }
}
