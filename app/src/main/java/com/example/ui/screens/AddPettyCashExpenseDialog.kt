package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.TransactionCategory
import com.example.model.TransactionType
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.viewmodel.RtCashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPettyCashExpenseDialog(
    selectedMonth: Int,
    selectedYear: Int,
    suggestedBpkkNumber: String = "",
    onSave: (
        title: String,
        amount: Long,
        category: TransactionCategory,
        recipientPerson: String,
        bpkkNumber: String,
        notes: String,
        proofPhotoUri: Uri?,
        proofPhotoDescription: String?,
        context: android.content.Context
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("120000") }
    var selectedCategory by remember { mutableStateOf(TransactionCategory.OPERASIONAL_ATK) }
    var recipientPerson by remember { mutableStateOf("") }
    var bpkkNumber by remember { mutableStateOf(suggestedBpkkNumber) }
    var notes by remember { mutableStateOf("") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // Photo Attachment State
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var proofPhotoDescription by remember { mutableStateOf("Foto Nota / Bon / Struk Toko") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    val quickAmounts = listOf(25_000L, 50_000L, 75_000L, 100_000L, 150_000L, 250_000L, 350_000L, 500_000L)
    val pettyCashExpenseCategories = listOf(
        TransactionCategory.OPERASIONAL_ATK,
        TransactionCategory.KONSUMSI_RAPAT,
        TransactionCategory.PEMELIHARAAN_FASUM,
        TransactionCategory.PENGELUARAN_LAINNYA,
        TransactionCategory.OPERASIONAL_CCTV,
        TransactionCategory.PENGEMBALIAN_SISA_KAS_KECIL
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_add_petty_cash_expense"),
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
                                .background(ExpenseRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = ExpenseRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Bukti Pengeluaran Kas Kecil",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Periode ${RtCashViewModel.getMonthName(selectedMonth)} $selectedYear",
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

                // Info Tag Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Pengeluaran kas kecil akan langsung mengurangi saldo fisik kas kecil (Tunai di Bendahara) dan tercatat dalam Buku Kas Kecil.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // No. Bukti / Voucher & Pos Beban
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bpkkNumber,
                        onValueChange = { bpkkNumber = it },
                        label = { Text("No. Bukti Pengeluaran", fontSize = 12.sp) },
                        placeholder = { Text("Otomatis jika kosong") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_bpkk_number"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pos Beban / Kategori Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.title,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pos Beban / Kategori", fontSize = 12.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("dropdown_petty_cash_category"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        pettyCashExpenseCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.title, fontSize = 13.sp) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Uraian Pemakaian
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Uraian / Keperluan Pengeluaran *", fontSize = 12.sp) },
                    placeholder = { Text("Misal: Beli Kertas HVS, Konsumsi Kerja Bakti, dsb.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_petty_cash_title"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = false,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dibayarkan Kepada / Penerima Uang
                OutlinedTextField(
                    value = recipientPerson,
                    onValueChange = { recipientPerson = it },
                    label = { Text("Dibayarkan Kepada (Penerima Dana / Toko) *", fontSize = 12.sp) },
                    placeholder = { Text("Misal: Toko Alat Tulis Kita / Bpk. Yanto") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_petty_cash_recipient"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Jumlah Nominal
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) amountText = it },
                    label = { Text("Nominal Pengeluaran (Rp) *", fontSize = 12.sp) },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold, color = ExpenseRed) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_petty_cash_amount"),
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
                                selectedContainerColor = ExpenseRed.copy(alpha = 0.15f),
                                selectedLabelColor = ExpenseRed
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Lampiran Foto Struk / Nota
                Text(
                    text = "Lampiran Bukti Fisik Nota / Bon (Opsional)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (selectedImageUri == null) {
                    OutlinedButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_attach_petty_cash_receipt"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pilih Foto Nota / Struk Toko", fontSize = 12.5.sp)
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(selectedImageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto Struk",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Nota Terlampir",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = proofPhotoDescription,
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { selectedImageUri = null }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus Foto", tint = ExpenseRed)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Catatan Tambahan
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Tambahan (Opsional)", fontSize = 12.sp) },
                    placeholder = { Text("Keterangan persetujuan atau nomor faktur toko") },
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
                            val finalTitle = if (title.isBlank()) selectedCategory.title else title
                            if (parsedAmount > 0) {
                                onSave(
                                    finalTitle,
                                    parsedAmount,
                                    selectedCategory,
                                    recipientPerson,
                                    bpkkNumber,
                                    notes,
                                    selectedImageUri,
                                    proofPhotoDescription,
                                    context
                                )
                            }
                        },
                        enabled = (amountText.toLongOrNull() ?: 0L) > 0,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("btn_save_petty_cash_expense"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                    ) {
                        Text("Simpan Pengeluaran", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
