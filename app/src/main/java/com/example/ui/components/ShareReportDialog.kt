package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Share
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.ReportExportHelper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.MonthlyRecap
import com.example.model.TransactionType
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.RtCashViewModel

@Composable
fun ShareReportDialog(
    recap: MonthlyRecap,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val reportText = buildWhatsAppRecapReport(recap)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("share_report_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bagikan Laporan Bulanan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Format siap kirim ke WhatsApp Group Warga RT004 / RW 08 Jati:",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                // Preview Box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = reportText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Export to PDF & Excel Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            ReportExportHelper.exportAndSharePdf(context, recap)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).testTag("dialog_export_pdf_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            tint = ExpenseRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF Landscape", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            ReportExportHelper.exportAndShareExcelCsv(context, recap)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).testTag("dialog_export_excel_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = "Excel",
                            tint = IncomeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Excel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Laporan Kas RT", reportText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Teks laporan disalin!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_report_text_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Salin",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Salin Teks")
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, reportText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Kirim Laporan Kas RT")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_whatsapp_report_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kirim WA")
                    }
                }
            }
        }
    }
}

private fun buildWhatsAppRecapReport(recap: MonthlyRecap): String {
    val incomeItems = recap.transactions.filter { it.type == TransactionType.PEMASUKAN }
    val expenseItems = recap.transactions.filter { it.type == TransactionType.PENGELUARAN }

    val incomeStr = incomeItems.joinToString("\n") {
        "• ${it.title}: ${RtCashViewModel.formatRupiah(it.amount)}"
    }

    val expenseStr = expenseItems.joinToString("\n") {
        "• ${it.title}: ${RtCashViewModel.formatRupiah(it.amount)}"
    }

    return """
📢 *LAPORAN REKAPITULASI ARUS KAS RT004 / RW 08 JATI*
Periode: *${recap.monthName} ${recap.year}*
Kelurahan Jati, Pulogadung • Kas Warga Transparan
=====================================
💰 *RINGKASAN KEUANGAN:*
• Saldo Awal     : ${RtCashViewModel.formatRupiah(recap.startingBalance)}
• Total Masuk    : ${RtCashViewModel.formatRupiah(recap.totalIncome)}
• Total Keluar   : ${RtCashViewModel.formatRupiah(recap.totalExpense)}
-------------------------------------
• *Surplus/Defisit*: ${RtCashViewModel.formatRupiah(recap.netBalance)}
• *SALDO AKHIR*   : *${RtCashViewModel.formatRupiah(recap.endingBalance)}*

📊 *PARTISIPASI IURAN:*
• Warga Bayar   : ${recap.paidCitizensCount} dari ${recap.totalCitizens} (${String.format("%.1f", recap.complianceRate)}%)
• Belum Lunas   : ${recap.unpaidCitizensCount} Warga/Pelaku Usaha

📥 *RINCIAN PEMASUKAN:*
$incomeStr

📤 *RINCIAN PENGELUARAN OPERASIONAL:*
$expenseStr

=====================================
Disusun oleh: *Bendahara RT004 (Prihatini Endah Yulia Maretiasari)*
Disahkan oleh: *Ketua RT 004 (Nohan Pancono)*
Sekretaris: *Muhammad Rijaldi Imam Mustarih*
_Laporan resmi disinkronkan secara real-time melalui Aplikasi Buku Kas RT004._
    """.trimIndent()
}
