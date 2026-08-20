package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.DeepSlateNavy
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.SoftMintBackground
import com.example.ui.theme.SoftMintContainer

@Composable
fun LoginScreen(
    userProfiles: Map<UserRole, UserProfile>,
    initialRole: UserRole = UserRole.KETUA_RT,
    onLoginSuccess: (selectedRole: UserRole, enteredPin: String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf(initialRole) }
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPinVisible by remember { mutableStateOf(false) }

    fun submitPin(pin: String) {
        if (pin.length != 4) {
            errorMessage = "Silakan masukkan 4 digit password / PIN."
            return
        }
        val success = onLoginSuccess(selectedRole, pin)
        if (!success) {
            errorMessage = "Password salah untuk ${selectedRole.title}. (Default: 1234)"
            pinInput = ""
        } else {
            errorMessage = null
        }
    }

    fun handleDigitPress(digit: String) {
        if (pinInput.length < 4) {
            val newPin = pinInput + digit
            pinInput = newPin
            errorMessage = null
            if (newPin.length == 4) {
                submitPin(newPin)
            }
        }
    }

    fun handleBackspace() {
        if (pinInput.isNotEmpty()) {
            pinInput = pinInput.dropLast(1)
            errorMessage = null
        }
    }

    fun handleClear() {
        pinInput = ""
        errorMessage = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SoftMintContainer,
                        SoftMintBackground,
                        Color.White
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("login_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // App Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.logo_rt004_app),
                    contentDescription = "Logo RT 004",
                    modifier = Modifier
                        .size(54.dp) // Menggunakan ukuran luar asli agar layout tidak bergeser
                        .clip(RoundedCornerShape(14.dp)), // Tetap mempertahankan sudut rounded pada file gambar jika diperlukan
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Kas Pintar RT004/08",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = DeepSlateNavy
                    )
                    Text(
                        text = "Jati, Pulogadung",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF2C5E55)
                    )
                }
            }

            // Role Selection Cards
            Text(
                text = "PILIH AKUN PENGURUS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = DeepSlateNavy.copy(alpha = 0.8f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 6.dp, bottom = 8.dp)
            )

            UserRole.values().forEach { role ->
                val isSelected = role == selectedRole
                val profile = userProfiles[role] ?: UserProfile(role, role.defaultName)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            selectedRole = role
                            pinInput = ""
                            errorMessage = null
                        }
                        .testTag("login_role_${role.name.lowercase()}"),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f),
                    border = if (isSelected) {
                        androidx.compose.foundation.BorderStroke(2.dp, DeepSlateNavy)
                    } else {
                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC4DDD8))
                    },
                    shadowElevation = if (isSelected) 4.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) DeepSlateNavy else Color(0xFFD4ECE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = role.icon,
                                contentDescription = role.title,
                                tint = if (isSelected) Color.White else DeepSlateNavy,
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
                                        fontSize = 13.5.sp
                                    ),
                                    color = DeepSlateNavy
                                )
                                if (role == UserRole.KETUA_RT) {
                                    Surface(
                                        color = Color(0xFF134B70).copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Akses Master PIN",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = DeepSlateNavy,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color(0xFF334155)
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(IncomeGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PIN Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pin_entry_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = DeepSlateNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Masukkan Password 4 Digit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            ),
                            color = DeepSlateNavy
                        )
                    }

                    Text(
                        text = "Login sebagai ${selectedRole.title}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // 4 Digit PIN Display Dots/Boxes
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        for (i in 0 until 4) {
                            val isFilled = i < pinInput.length
                            val char = if (isFilled) pinInput[i] else null

                            Surface(
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("pin_dot_$i"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isFilled) DeepSlateNavy.copy(alpha = 0.1f) else Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isFilled) 2.dp else 1.dp,
                                    color = if (isFilled) DeepSlateNavy else Color(0xFFCBD5E1)
                                )
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (isFilled) {
                                        if (isPinVisible) {
                                            Text(
                                                text = char.toString(),
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp
                                                ),
                                                color = DeepSlateNavy
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(DeepSlateNavy)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        IconButton(
                            onClick = { isPinVisible = !isPinVisible },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPinVisible) "Sembunyikan PIN" else "Tampilkan PIN",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Error Message Banner
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        errorMessage?.let { errorText ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                color = Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ExpenseRed.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = "Error",
                                        tint = ExpenseRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = errorText,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ExpenseRed,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Helper Default Password Info
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 12.dp),
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hanya Ketua RT yang dapat ubah semua password",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF1E40AF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    // Numeric Keypad
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val numpadRows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("C", "0", "DEL")
                        )

                        numpadRows.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { key ->
                                    when (key) {
                                        "C" -> {
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable { handleClear() }
                                                    .testTag("numpad_clear"),
                                                shape = RoundedCornerShape(12.dp),
                                                color = Color(0xFFF1F5F9)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "C",
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 16.sp
                                                        ),
                                                        color = Color(0xFF64748B)
                                                    )
                                                }
                                            }
                                        }
                                        "DEL" -> {
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable { handleBackspace() }
                                                    .testTag("numpad_del"),
                                                shape = RoundedCornerShape(12.dp),
                                                color = Color(0xFFF1F5F9)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Backspace,
                                                        contentDescription = "Hapus",
                                                        tint = Color(0xFF64748B),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                        else -> {
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable { handleDigitPress(key) }
                                                    .testTag("numpad_$key"),
                                                shape = RoundedCornerShape(12.dp),
                                                color = Color(0xFFF8FAFC),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = key,
                                                        style = MaterialTheme.typography.titleLarge.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 20.sp
                                                        ),
                                                        color = DeepSlateNavy
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Login Button & Quick Default PIN Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    pinInput = "1234"
                                    submitPin("1234")
                                }
                                .testTag("quick_default_pin_btn"),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE0F2FE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7DD3FC))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Isi Default Pass",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    ),
                                    color = Color(0xFF0369A1)
                                )
                            }
                        }

                        Button(
                            onClick = { submitPin(pinInput) },
                            enabled = pinInput.length == 4,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepSlateNavy,
                                disabledContainerColor = Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("login_submit_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Masuk Aplikasi",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer note
            Text(
                text = "Kas Pintar RT 04 / RW 08 Kelurahan Jati • Pulogadung",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                color = Color(0xFF4A7D75),
                textAlign = TextAlign.Center
            )
        }
    }
}
