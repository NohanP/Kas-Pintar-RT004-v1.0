package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DeepSlateNavy
import kotlinx.coroutines.delay

/**
 * Splash Screen Kas Pintar RT004/08 Jati, Pulogadung
 * Sesuai spesifikasi:
 * - Content logo di tengah
 * - Nama aplikasi "Kas Pintar RT004/08" [new line] "Jati, Pulogadung"
 * - Warna dasar: C8ECE6 (Soft Mint)
 * - Warna text: Menyesuaikan dengan kontras tinggi (Deep Slate Navy #134B70 & Dark Teal #1A4D45)
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseBackgroundColor = Color(0xFFC8ECE6) // Warna dasar C8ECE6 sesuai permintaan
    val brandTextColor = Color(0xFF134B70)       // Deep Slate Navy untuk kontras optimal
    val subTextColor = Color(0xFF1B4D45)         // Dark Mint Slate untuk baris kedua

    // Smooth pulse animation for the logo
    val infiniteTransition = rememberInfiniteTransition(label = "SplashPulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    // Timer transition to Login Screen
    LaunchedEffect(Unit) {
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseBackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onSplashFinished()
            }
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background soft radial glow
        Box(
            modifier = Modifier
                .size(360.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f),
                            Color(0xFFBCEAE1).copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Center Content: Logo + Nama Aplikasi
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 28.dp)
        ) {
            // Emblem Container with Logo
            Image(
                painter = painterResource(id = R.mipmap.logo_rt004_app),
                contentDescription = "Logo RT 004",
                modifier = Modifier
                    .size(130.dp) // Menggunakan ukuran container asal agar tata letak tetap konsisten
                    .scale(pulseScale) // Animasi pulse dipasang langsung di sini
                    .clip(RoundedCornerShape(22.dp))
                    .testTag("splash_logo_container"),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Nama Aplikasi Baris 1: "Kas Pintar RT004/08"
            Text(
                text = "Kas Pintar RT004/08",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.3.sp,
                    lineHeight = 32.sp
                ),
                color = brandTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("splash_app_name_line1")
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Nama Aplikasi Baris 2 (New Line): "Jati, Pulogadung"
            Text(
                text = "Jati, Pulogadung",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = subTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("splash_app_name_line2")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Badge / Subtitle: Wilayah Administrasi RT & RW
            Surface(
                color = Color.White.copy(alpha = 0.75f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF90D9CE)),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16A34A))
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "RW 08 Kelurahan Jati • Jakarta Timur",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF1E3A4C)
                    )
                }
            }
        }

        // Bottom Area: Loading indicator & Version tag
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = brandTextColor,
                strokeWidth = 2.5.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Memuat Sistem Keuangan...",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = Color(0xFF2D5E56)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Versi 2.0 • Keuangan & Kas Pintar",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color(0xFF4A7D75)
            )
        }
    }
}
