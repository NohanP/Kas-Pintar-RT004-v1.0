package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.PaymentMethod
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.RtCashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopUpPettyCashDialog(
    selectedMonth: Int,
    selectedYear: Int,
    currentPettyCashBalance: Long,
    onConfirmTopUp: (
        amount: Long,
        sourceMethod: PaymentMethod,
        notes: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf("1000000") }
    var selectedSourceMethod by remember { mutableStateOf(PaymentMethod.TRANSFER_BCA) }
    var notes by remember { mutableStateOf("Pengisian dana kas kecil operasional RT") }
    var sourceDropdownExpanded by remember { mutableStateOf(false) }

    val quickAmounts = listOf(500_000L, 750_000L, 1_000_000L, 1_500_000L, 2_000_000L, 3_000_000L)
    val availableSources = listOf(
        PaymentMethod.TRANSFER_BCA,
        PaymentMethod.QRIS_BCA,
        PaymentMethod.TUNAI
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_top_up_petty_cash"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
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
                                .background(IncomeGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = null,
                                tint = IncomeGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Pengisian / Top Up Kas Kecil",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pencairan dari Kas Bank ke Kas Operasional RT",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBF7D0)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Saldo Kas Kecil Saat Ini:",
                                fontSize = 11.5.sp,
                                color = Color(0xFF166534)
                            )
                            Text(
                                text = RtCashViewModel.formatRupiah(currentPettyCashBalance),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pengisian dana kas kecil disesuaikan dengan estimasi kebutuhan kas operasional RT.",
                            fontSize = 11.sp,
                            color = Color(0xFF15803D),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nominal Top Up
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) amountText = it },
                    label = { Text("Jumlah Pengisian Dana (Rp) *", fontSize = 12.sp) },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold, color = IncomeGreen) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_topup_amount"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Quick Amount Chips
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickAmounts.forEach { amt ->
                        FilterChip(
                            selected = amountText == amt.toString(),
                            onClick = { amountText = amt.toString() },
                            label = { Text(RtCashViewModel.formatRupiah(amt), fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IncomeGreen.copy(alpha = 0.15f),
                                selectedLabelColor = IncomeGreen
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sumber Dana Dropdown
                ExposedDropdownMenuBox(
                    expanded = sourceDropdownExpanded,
                    onExpandedChange = { sourceDropdownExpanded = !sourceDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSourceMethod.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sumber Penarikan Dana", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = EmeraldPrimary)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = sourceDropdownExpanded,
                        onDismissRequest = { sourceDropdownExpanded = false }
                    ) {
                        availableSources.forEach { src ->
                            DropdownMenuItem(
                                text = { Text(src.label, fontSize = 13.sp) },
                                onClick = {
                                    selectedSourceMethod = src
                                    sourceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Keterangan Pengisian
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Keterangan Top Up", fontSize = 12.sp) },
                    placeholder = { Text("Misal: Pencairan kas bank untuk persiapan kegiatan") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            val parsedAmount = amountText.toLongOrNull() ?: 0L
                            if (parsedAmount > 0) {
                                onConfirmTopUp(parsedAmount, selectedSourceMethod, notes)
                            }
                        },
                        enabled = (amountText.toLongOrNull() ?: 0L) > 0,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("btn_confirm_topup"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Cairkan Top Up", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
