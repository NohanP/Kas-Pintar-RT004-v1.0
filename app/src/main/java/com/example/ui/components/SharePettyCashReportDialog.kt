package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.PettyCashRecap
import com.example.model.TransactionType
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.ReportExportHelper
import com.example.ui.viewmodel.RtCashViewModel

@Composable
fun SharePettyCashReportDialog(
    recap: PettyCashRecap,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_share_petty_cash_report"),
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
                                .background(EmeraldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Bagikan Laporan Kas Kecil",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Buku Kas Kecil ${recap.monthName} ${recap.year}",
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

                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBF7D0)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Saldo Awal Kas Kecil:", fontSize = 12.sp, color = Color(0xFF64748B))
                            Text(RtCashViewModel.formatRupiah(recap.startingBalance), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Top Up (Debet):", fontSize = 12.sp, color = IncomeGreen)
                            Text("+ ${RtCashViewModel.formatRupiah(recap.totalTopUp)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Pemakaian (Kredit):", fontSize = 12.sp, color = ExpenseRed)
                            Text("- ${RtCashViewModel.formatRupiah(recap.totalDisbursement)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Divider(color = Color(0xFFDCFCE7), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Saldo Akhir Kas Kecil:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                            Text(RtCashViewModel.formatRupiah(recap.endingBalance), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Pilih Format Pembagian:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 1. WhatsApp / Text Summary Button
                ExportOptionCard(
                    icon = Icons.Default.Description,
                    iconTint = EmeraldPrimary,
                    title = "Bagikan Ringkasan ke WhatsApp",
                    subtitle = "Salin & kirim ringkasan kas kecil ke grup pengurus RT",
                    onClick = {
                        sharePettyCashViaWhatsApp(context, recap)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Export PDF Document
                ExportOptionCard(
                    icon = Icons.Default.PictureAsPdf,
                    iconTint = Color(0xFFDC2626),
                    title = "Dokumen Resmi PDF (Buku Kas Kecil)",
                    subtitle = "Tabel pengeluaran dan lembar tanda tangan",
                    onClick = {
                        ReportExportHelper.exportAndSharePettyCashPdf(context, recap)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Export Excel / CSV
                ExportOptionCard(
                    icon = Icons.Default.TableChart,
                    iconTint = Color(0xFF16A34A),
                    title = "Spreadsheet Excel / CSV (.csv)",
                    subtitle = "Buka di Microsoft Excel atau Google Sheets untuk audit",
                    onClick = {
                        ReportExportHelper.exportAndSharePettyCashExcelCsv(context, recap)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("Tutup", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ExportOptionCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

private fun sharePettyCashViaWhatsApp(context: Context, recap: PettyCashRecap) {
    val b = StringBuilder()
    b.append("📊 *LAPORAN BUKU KAS KECIL RT 004 / RW 08*\n")
    b.append("🏛️ *Kel. Jati, Kec. Pulogadung - Jakarta Timur*\n")
    b.append("📅 *Periode:* ${recap.monthName} ${recap.year}\n")
    b.append("------------------------------------------\n")
    b.append("💰 *Saldo Awal Kas Kecil:* ${RtCashViewModel.formatRupiah(recap.startingBalance)}\n")
    b.append("🟢 *Total Pengisian / Top Up (Debet):* +${RtCashViewModel.formatRupiah(recap.totalTopUp)}\n")
    b.append("🔴 *Total Pengeluaran (Kredit):* -${RtCashViewModel.formatRupiah(recap.totalDisbursement)}\n")
    b.append("💵 *Saldo Akhir Kas Kecil:* *${RtCashViewModel.formatRupiah(recap.endingBalance)}*\n")
    b.append("📑 *Jumlah Bukti Pengeluaran:* ${recap.totalVouchers} Transaksi\n")
    b.append("------------------------------------------\n\n")

    if (recap.expenseCategoryBreakdowns.isNotEmpty()) {
        b.append("📋 *RINCIAN PENGELUARAN POS BEBAN:*\n")
        recap.expenseCategoryBreakdowns.forEachIndexed { i, cat ->
            b.append("${i + 1}. ${cat.category.title}: ${RtCashViewModel.formatRupiah(cat.totalAmount)} (${String.format("%.1f", cat.percentage)}%)\n")
        }
        b.append("------------------------------------------\n\n")
    }

    b.append("📝 *MUTASI BUKTI PENGELUARAN:*\n")
    recap.transactions.forEachIndexed { i, tx ->
        val sign = if (tx.type == TransactionType.PEMASUKAN) "(+)" else "(-)"
        val recipient = if (!tx.recipientPerson.isNullOrBlank()) " -> ${tx.recipientPerson}" else ""
        b.append("${i + 1}. ${tx.title}$recipient: $sign ${RtCashViewModel.formatRupiah(tx.amount)}\n")
    }

    b.append("\n------------------------------------------\n")
    b.append("👤 *Pemegang Kas Kecil:* Prihatini Endah Yulia M.\n")
    b.append("👔 *Mengetahui:* Nohan Pancono (Ketua RT 004)\n")
    b.append("_Dicetak otomatis via Aplikasi Kas RT 04 Cloud_")

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, b.toString())
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Bagikan Rekap Kas Kecil"))
}
