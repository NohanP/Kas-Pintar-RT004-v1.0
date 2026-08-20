package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.UserRole
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.DeepSlateNavy
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MediumSlateBlue
import com.example.ui.theme.SoftMintBackground
import com.example.ui.theme.SoftMintContainer

@Composable
fun TopRoleHeader(
    currentRole: UserRole,
    userName: String,
    isOnline: Boolean,
    isSyncing: Boolean,
    onSwitchRoleClick: () -> Unit,
    onSyncStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SoftMintBackground,
        tonalElevation = 0.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() // Padding / Margin aman dari Status Bar sistem
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: RT 04 Logo Badge + User & Role Profile with Soft Mint Surface
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.85f))
                        .border(1.dp, Color(0xFFC8ECE6), RoundedCornerShape(14.dp))
                        .clickable { onSwitchRoleClick() }
                        .padding(vertical = 6.dp, horizontal = 8.dp)
                        .testTag("switch_role_header_btn")
                ) {
                    // RT 004 Logo WebP Asset
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.mipmap.logo_rt004_app),
                            contentDescription = "Logo RT 004",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = currentRole.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.5.sp
                            ),
                            color = DeepSlateNavy
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color(0xFF2C4A5E)
                        )
                    }
                }

                // Right: Multi-Device Sync Indicator
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        isSyncing -> AmberTertiary.copy(alpha = 0.15f)
                        isOnline -> IncomeGreen.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSyncStatusClick() }
                        .testTag("sync_status_header_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSyncing -> AmberTertiary
                                        isOnline -> IncomeGreen
                                        else -> MaterialTheme.colorScheme.error
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            imageVector = when {
                                isSyncing -> Icons.Default.Sync
                                isOnline -> Icons.Default.CloudDone
                                else -> Icons.Default.CloudOff
                            },
                            contentDescription = "Sync Status",
                            tint = when {
                                isSyncing -> AmberTertiary
                                isOnline -> IncomeGreen
                                else -> MaterialTheme.colorScheme.error
                            },
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when {
                                isSyncing -> "Sinkron..."
                                isOnline -> "Online"
                                else -> "Offline"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = when {
                                isSyncing -> AmberTertiary
                                isOnline -> IncomeGreen
                                else -> MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }
            }
        }
    }
}
