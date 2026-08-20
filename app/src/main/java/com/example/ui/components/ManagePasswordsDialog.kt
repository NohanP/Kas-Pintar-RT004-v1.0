package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.DeepSlateNavy
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.SoftMintBackground
import com.example.ui.theme.SoftMintContainer

@Composable
fun ManagePasswordsDialog(
    currentRole: UserRole,
    userProfiles: Map<UserRole, UserProfile>,
    rolePasswords: Map<UserRole, String>,
    onUpdatePassword: (role: UserRole, newPin: String) -> Boolean,
    onResetAllPasswords: () -> Boolean,
    onDismiss: () -> Unit
) {
    val isKetuaRt = currentRole == UserRole.KETUA_RT
    var editingRole by remember { mutableStateOf<UserRole?>(null) }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var pinErrorMessage by remember { mutableStateOf<String?>(null) }
    var successNotification by remember { mutableStateOf<String?>(null) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var visiblePasswords by remember { mutableStateOf(setOf<UserRole>()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("manage_passwords_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
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
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(DeepSlateNavy.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "Kelola Password",
                                tint = DeepSlateNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Kelola Password Pengurus",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "PIN 4 Digit Akses Aplikasi",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Authority Notice Banner
                if (isKetuaRt) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Wewenang Ketua RT",
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Wewenang Ketua RT Aktif: Anda dapat mengubah dan mereset password 4 digit untuk semua akun pengurus RT.",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp
                                ),
                                color = Color(0xFF166534)
                            )
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "Info Akses",
                                tint = Color(0xFF1D4ED8),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Kelola Password Akun: Anda dapat melihat dan mengubah password 4 digit untuk akun ${currentRole.title} Anda.",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp
                                ),
                                color = Color(0xFF1E40AF)
                            )
                        }
                    }
                }

                // Success notification banner
                successNotification?.let { successText ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6EE7B7))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Sukses",
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = successText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                ),
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // List of Roles and their PIN status
                val displayRoles = if (isKetuaRt) UserRole.values().toList() else listOf(currentRole)
                displayRoles.forEach { role ->
                    val profile = userProfiles[role] ?: UserProfile(role, role.defaultName)
                    val currentPin = rolePasswords[role] ?: "1234"
                    val isVisible = visiblePasswords.contains(role)
                    val isEditingThis = editingRole == role
                    val canEditThisRole = isKetuaRt || role == currentRole

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .testTag("role_password_card_${role.name.lowercase()}"),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isEditingThis) Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isEditingThis) 2.dp else 1.dp,
                            color = if (isEditingThis) Color(0xFF16A34A) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 1. Pengurus Identity Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(DeepSlateNavy.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = role.icon,
                                        contentDescription = role.title,
                                        tint = DeepSlateNavy,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = role.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            ),
                                            color = DeepSlateNavy
                                        )

                                        Surface(
                                            color = DeepSlateNavy.copy(alpha = 0.08f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = role.badgeLabel,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = DeepSlateNavy,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = profile.name,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.5.sp
                                        ),
                                        color = Color(0xFF334155)
                                    )
                                }
                            }

                            // 2. PIN Status Container (Vertical Item)
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "PIN Saat Ini:",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp
                                        ),
                                        color = Color(0xFF64748B)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isVisible) currentPin else "••••",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = if (isVisible) 3.sp else 4.sp,
                                                fontSize = 14.sp
                                            ),
                                            color = DeepSlateNavy
                                        )

                                        Spacer(modifier = Modifier.width(6.dp))

                                        IconButton(
                                            onClick = {
                                                visiblePasswords = if (isVisible) {
                                                    visiblePasswords - role
                                                } else {
                                                    visiblePasswords + role
                                                }
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle PIN",
                                                tint = Color(0xFF64748B),
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 3. Action Button (Vertical Item)
                            if (canEditThisRole) {
                                Button(
                                    onClick = {
                                        if (editingRole == role) {
                                            editingRole = null
                                            newPinInput = ""
                                            confirmPinInput = ""
                                            pinErrorMessage = null
                                        } else {
                                            editingRole = role
                                            newPinInput = ""
                                            confirmPinInput = ""
                                            pinErrorMessage = null
                                            successNotification = null
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isEditingThis) Color(0xFFE2E8F0) else DeepSlateNavy,
                                        contentColor = if (isEditingThis) Color(0xFF334155) else Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .testTag("btn_change_pin_${role.name.lowercase()}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isEditingThis) Icons.Default.Close else Icons.Default.Edit,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isEditingThis) "Batal Ubah PIN" else "Ubah PIN 4 Digit",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // 4. Inline Form to Edit PIN (Vertical Layout: Label -> Input -> Label -> Input -> Button)
                            if (isEditingThis) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Setel PIN Baru untuk ${role.title}:",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = DeepSlateNavy
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        // Label 1
                                        Text(
                                            text = "PIN Baru (4 Digit)",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.5.sp
                                            ),
                                            color = Color(0xFF334155)
                                        )

                                        // Input Text 1
                                        OutlinedTextField(
                                            value = newPinInput,
                                            onValueChange = {
                                                if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                                    newPinInput = it
                                                    pinErrorMessage = null
                                                }
                                            },
                                            placeholder = { Text("Masukkan 4 digit angka (misal: 5678)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                            visualTransformation = PasswordVisualTransformation(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("input_new_pin")
                                        )

                                        // Label 2
                                        Text(
                                            text = "Konfirmasi PIN Baru",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.5.sp
                                            ),
                                            color = Color(0xFF334155)
                                        )

                                        // Input Text 2
                                        OutlinedTextField(
                                            value = confirmPinInput,
                                            onValueChange = {
                                                if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                                    confirmPinInput = it
                                                    pinErrorMessage = null
                                                }
                                            },
                                            placeholder = { Text("Ketik ulang 4 digit PIN baru") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                            visualTransformation = PasswordVisualTransformation(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("input_confirm_pin")
                                        )

                                        pinErrorMessage?.let { err ->
                                            Text(
                                                text = err,
                                                color = ExpenseRed,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Button 1: Simpan PIN Baru (Aligned Vertically)
                                        Button(
                                            onClick = {
                                                if (newPinInput.length != 4) {
                                                    pinErrorMessage = "PIN harus tepat 4 digit angka."
                                                } else if (newPinInput != confirmPinInput) {
                                                    pinErrorMessage = "Konfirmasi PIN tidak cocok."
                                                } else {
                                                    val res = onUpdatePassword(role, newPinInput)
                                                    if (res) {
                                                        successNotification = "PIN ${role.title} berhasil diubah menjadi $newPinInput"
                                                        editingRole = null
                                                        newPinInput = ""
                                                        confirmPinInput = ""
                                                        pinErrorMessage = null
                                                    } else {
                                                        pinErrorMessage = "Gagal memperbarui PIN."
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp)
                                                .testTag("save_new_pin_btn")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Simpan PIN Baru",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }

                                        // Button 2: Batal (Aligned Vertically)
                                        OutlinedButton(
                                            onClick = {
                                                editingRole = null
                                                newPinInput = ""
                                                confirmPinInput = ""
                                                pinErrorMessage = null
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(40.dp)
                                                .testTag("cancel_edit_pin_btn"),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF64748B))
                                        ) {
                                            Text(
                                                text = "Batal",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Emergency Master Reset Button (Ketua RT only)
                if (isKetuaRt) {
                    OutlinedButton(
                        onClick = { showResetConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reset_all_passwords_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Reset",
                                tint = ExpenseRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Reset Semua Password ke Default (1234)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_manage_passwords_dialog"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepSlateNavy)
                ) {
                    Text("Selesai & Tutup", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }

    // Confirmation Alert Dialog for Reset All
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Semua Password?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Tindakan ini akan mengembalikan PIN 4 digit untuk Ketua RT, Sekretaris RT, dan Bendahara RT ke '1234'."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetAllPasswords()
                        showResetConfirmDialog = false
                        successNotification = "Semua password berhasil direset ke '1234'!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Ya, Reset ke 1234")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
