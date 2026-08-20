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
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
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
import com.example.model.CitizenEntity
import com.example.model.PaymentMethod
import com.example.model.TransactionCategory
import com.example.model.TransactionType
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.RtCashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionDialog(
    citizens: List<CitizenEntity>,
    selectedMonth: Int,
    selectedYear: Int,
    onSave: (
        title: String,
        address: String?,
        amount: Long,
        type: TransactionType,
        category: TransactionCategory,
        citizen: CitizenEntity?,
        month: Int,
        year: Int,
        paymentMethod: PaymentMethod,
        notes: String,
        proofPhotoUri: Uri?,
        proofPhotoDescription: String?,
        context: android.content.Context
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var type by remember { mutableStateOf(TransactionType.PENGELUARAN) }
    var title by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("150000") }
    var selectedCategory by remember { mutableStateOf(TransactionCategory.PEMELIHARAAN_FASUM) }
    var selectedCitizen by remember { mutableStateOf<CitizenEntity?>(null) }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.TUNAI) }
    var notes by remember { mutableStateOf("") }
    var citizenDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // Photo Attachment State for Expense
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var proofPhotoDescription by remember { mutableStateOf("Foto Nota / Kuitansi Pembelian") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    val quickAmounts = listOf(20_000L, 50_000L, 75_000L, 100_000L, 250_000L, 500_000L, 1_000_000L, 1_500_000L)
    val availableCategories = TransactionCategory.values().filter { it.type == type }

    val photoDescriptions = listOf(
        "Foto Nota / Kuitansi Pembelian",
        "Foto Fisik Barang yang Dibeli",
        "Bukti Struk Toko / Material",
        "Dokumentasi Kegiatan / Pekerjaan"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("add_transaction_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (type == TransactionType.PENGELUARAN) ExpenseRed.copy(alpha = 0.15f) else IncomeGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (type == TransactionType.PENGELUARAN) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (type == TransactionType.PENGELUARAN) ExpenseRed else IncomeGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Catat Transaksi Kas RT",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Terhubung Firestore & Firebase Storage",
                                fontSize = 11.5.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Type Toggle: Pemasukan / Pengeluaran
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (type == TransactionType.PEMASUKAN) IncomeGreen else Color.Transparent)
                            .clickable {
                                type = TransactionType.PEMASUKAN
                                selectedCategory = TransactionCategory.IURAN_WARGA
                            }
                            .padding(vertical = 10.dp)
                            .testTag("toggle_type_income"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (type == TransactionType.PEMASUKAN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pemasukan",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (type == TransactionType.PEMASUKAN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (type == TransactionType.PENGELUARAN) ExpenseRed else Color.Transparent)
                            .clickable {
                                type = TransactionType.PENGELUARAN
                                selectedCategory = TransactionCategory.PEMELIHARAAN_FASUM
                            }
                            .padding(vertical = 10.dp)
                            .testTag("toggle_type_expense"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (type == TransactionType.PENGELUARAN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pengeluaran",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (type == TransactionType.PENGELUARAN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Nominal Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Nominal (Rp)") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tx_amount")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Amount Chips
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
                            label = { Text(RtCashViewModel.formatRupiah(amt), fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Selection
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory.title,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori Transaksi") },
                        leadingIcon = {
                            Icon(imageVector = selectedCategory.icon, contentDescription = null)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("select_tx_category"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        availableCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = cat.icon, contentDescription = null, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(cat.title)
                                    }
                                },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Citizen / Business Payer Dropdown (For Income)
                if (type == TransactionType.PEMASUKAN) {
                    ExposedDropdownMenuBox(
                        expanded = citizenDropdownExpanded,
                        onExpandedChange = { citizenDropdownExpanded = !citizenDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCitizen?.name ?: "Bukan dari Warga Tertentu (Umum/Donasi)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Warga / Pelaku Usaha Pembayar") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = citizenDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("select_tx_citizen"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = citizenDropdownExpanded,
                            onDismissRequest = { citizenDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Umum / Donasi Bebas / Kas Awal") },
                                onClick = {
                                    selectedCitizen = null
                                    citizenDropdownExpanded = false
                                }
                            )
                            citizens.forEach { c ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(c.name, fontWeight = FontWeight.Bold)
                                            Text("${c.houseNumber} • ${c.type.label}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    },
                                    onClick = {
                                        selectedCitizen = c
                                        amountText = c.monthlyFee.toString()
                                        address = c.houseNumber
                                        citizenDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Alamat (Address) Field
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Alamat") },
                    placeholder = {
                        Text(
                            if (selectedCitizen != null) selectedCitizen?.houseNumber ?: "Contoh: Blok A1 No. 04"
                            else "Contoh: Blok A1 No. 04 / Jl. Mawar No. 12"
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Alamat")
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tx_address")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Title / Description (Default: Nama Kategori jika kosong)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Keterangan (Default: ${selectedCategory.title})") },
                    placeholder = {
                        Text(
                            if (type == TransactionType.PEMASUKAN) "Contoh: Iuran Warga Agustus 2026 (Opsional)"
                            else "Contoh: Beli Pompa Air & Pipa Balai RT (Opsional)"
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tx_title")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ==========================================
                // FITUR INSERT FOTO KUITANSI / BARANG (OPSIONAL)
                // Disimpan ke Firebase Storage
                // ==========================================
                if (type == TransactionType.PENGELUARAN) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF8FAFC)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Foto Kuitansi / Barang (Opsional)",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF0F172A)
                                    )
                                }
                                Surface(
                                    color = Color(0xFFE0F2FE),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudUpload,
                                            contentDescription = null,
                                            tint = Color(0xFF0369A1),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Firebase Storage",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0369A1)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Lampirkan foto fisik nota belanja, kuitansi toko, atau foto barang yang dibeli untuk transparansi warga.",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Photo category selector chip
                            Text(
                                text = "Jenis Bukti Foto:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                photoDescriptions.forEach { desc ->
                                    FilterChip(
                                        selected = proofPhotoDescription == desc,
                                        onClick = { proofPhotoDescription = desc },
                                        label = { Text(desc, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                                            selectedLabelColor = EmeraldPrimary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (selectedImageUri != null) {
                                // Image Preview Card
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = IncomeGreen,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = proofPhotoDescription,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0F172A)
                                                )
                                            }
                                            IconButton(
                                                onClick = { selectedImageUri = null },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Hapus Foto",
                                                    tint = ExpenseRed,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(selectedImageUri)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Preview Bukti Foto",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedButton(
                                            onClick = { photoPickerLauncher.launch("image/*") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AddPhotoAlternate,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Ganti Foto Kuitansi / Barang", fontSize = 12.sp)
                                        }
                                    }
                                }
                            } else {
                                // Empty state pick button
                                OutlinedButton(
                                    onClick = { photoPickerLauncher.launch("image/*") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_pick_receipt_photo"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "+ Ambil / Pilih Foto Kuitansi / Barang",
                                        fontWeight = FontWeight.SemiBold,
                                        color = EmeraldPrimary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Payment Method
                Text(
                    text = "Metode Pembayaran",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethod.values().forEach { method ->
                        FilterChip(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            label = { Text(method.label, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Extra Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Tambahan / Nomor Nota (Opsional)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            val parsedAmount = amountText.toLongOrNull() ?: 0L
                            if (parsedAmount > 0) {
                                val effectiveTitle = if (title.isNotBlank()) title.trim() else selectedCategory.title
                                val effectiveAddress = if (address.isNotBlank()) address.trim() else selectedCitizen?.houseNumber

                                onSave(
                                    effectiveTitle,
                                    effectiveAddress,
                                    parsedAmount,
                                    type,
                                    selectedCategory,
                                    selectedCitizen,
                                    selectedMonth,
                                    selectedYear,
                                    paymentMethod,
                                    notes,
                                    selectedImageUri,
                                    if (selectedImageUri != null) proofPhotoDescription else null,
                                    context
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == TransactionType.PEMASUKAN) IncomeGreen else ExpenseRed
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_transaction_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan Kas", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
