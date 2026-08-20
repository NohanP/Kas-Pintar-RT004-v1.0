package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.CitizenEntity
import com.example.model.CitizenType
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCitizenDialog(
    initialCitizen: CitizenEntity? = null,
    onSave: (
        id: Long,
        name: String,
        houseNumber: String,
        phone: String,
        type: CitizenType,
        monthlyFee: Long,
        notes: String
    ) -> Unit,
    onDelete: ((CitizenEntity) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialCitizen?.name ?: "") }
    var houseNumber by remember { mutableStateOf(initialCitizen?.houseNumber ?: "") }
    var phone by remember { mutableStateOf(initialCitizen?.phone ?: "") }
    var type by remember { mutableStateOf(initialCitizen?.type ?: CitizenType.WARGA_TETAP) }
    var feeText by remember { mutableStateOf((initialCitizen?.monthlyFee ?: type.defaultFee).toString()) }
    var notes by remember { mutableStateOf(initialCitizen?.notes ?: "") }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("add_edit_citizen_dialog"),
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
                    Text(
                        text = if (initialCitizen == null) "Tambah Warga / Usaha" else "Edit Data Warga / Usaha",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap / Nama Toko / Usaha") },
                    placeholder = { Text("Contoh: Bpk. Rahmat Santoso / Warung Berkah") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_citizen_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // House / Unit Number
                OutlinedTextField(
                    value = houseNumber,
                    onValueChange = { houseNumber = it },
                    label = { Text("No. Rumah / Blok / Lokasi") },
                    placeholder = { Text("Contoh: Blok B2 No. 08 / Ruko Utama No. 03") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_citizen_house")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("No. WhatsApp / HP (Opsional)") },
                    placeholder = { Text("0812-xxxx-xxxx") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_citizen_phone")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = type.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori Kepesertaan") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("select_citizen_type"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        CitizenType.values().forEach { cType ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(cType.label, fontWeight = FontWeight.Bold)
                                        Text("Tarif standar: Rp ${cType.defaultFee}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = {
                                    type = cType
                                    if (initialCitizen == null) {
                                        feeText = cType.defaultFee.toString()
                                    }
                                    typeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Monthly Fee Amount
                OutlinedTextField(
                    value = feeText,
                    onValueChange = { feeText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Tarif Iuran Bulanan (Rp)") },
                    prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_citizen_fee")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Tambahan (Opsional)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (initialCitizen != null && onDelete != null) {
                        OutlinedButton(
                            onClick = {
                                onDelete(initialCitizen)
                                onDismiss()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus")
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank() && houseNumber.isNotBlank()) {
                                onSave(
                                    initialCitizen?.id ?: 0L,
                                    name.trim(),
                                    houseNumber.trim(),
                                    phone.trim(),
                                    type,
                                    feeText.toLongOrNull() ?: type.defaultFee,
                                    notes.trim()
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("save_citizen_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan Data", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
