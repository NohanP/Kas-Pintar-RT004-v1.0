package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepSlateNavy
import com.example.ui.theme.MediumSlateBlue
import com.example.ui.theme.MintCyan
import com.example.ui.theme.SoftMintBackground
import com.example.ui.theme.SoftMintContainer
import com.example.ui.viewmodel.RtCashViewModel

@Composable
fun PeriodHeaderCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selectedMonth: Int,
    selectedYear: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onOpenCalendarPicker: () -> Unit,
    modifier: Modifier = Modifier,
    testTagPrefix: String = "period"
) {
    val monthName = RtCashViewModel.getMonthName(selectedMonth)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("${testTagPrefix}_period_selector"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SoftMintBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFC8ECE6))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Section Title & Subtitle (Stands out with Soft Mint theme & Deep Slate Navy)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DeepSlateNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        ),
                        color = DeepSlateNavy
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MediumSlateBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Month/Year Selector Row with Navigation Arrows and Calendar Trigger
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFC8ECE6)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPreviousMonth,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("${testTagPrefix}_prev_month_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Bulan Sebelumnya",
                            tint = DeepSlateNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onOpenCalendarPicker() }
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                            .testTag("${testTagPrefix}_open_period_picker_btn"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SoftMintContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Kalender",
                                tint = DeepSlateNavy,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$monthName $selectedYear",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.5.sp
                                ),
                                color = DeepSlateNavy
                            )
                            Text(
                                text = "Ketuk untuk pilih kalender",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MediumSlateBlue
                            )
                        }
                    }

                    IconButton(
                        onClick = onNextMonth,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("${testTagPrefix}_next_month_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Bulan Berikutnya",
                            tint = DeepSlateNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
