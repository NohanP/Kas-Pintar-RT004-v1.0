package com.example.ui.components

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.TransactionEntity
import com.example.model.TransactionType
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.RtCashViewModel

@Composable
fun PettyCashVoucherDialog(
    transaction: TransactionEntity,
    onViewPhoto: (photoPath: String, title: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isExpense = transaction.type == TransactionType.PENGELUARAN
    val voucherNumber = if (transaction.bpkkNumber.isNotBlank()) transaction.bpkkNumber else transaction.receiptNumber

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .testTag("dialog_petty_cash_voucher"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExpense) "VOUCHER PENGELUARAN KAS KECIL" else "BUKTI PENERIMAAN / TOP UP KAS KECIL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = if (isExpense) ExpenseRed else IncomeGreen
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Voucher Card Sheet Design
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFDFE)),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // RT Letterhead
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "RT",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "RUKUN TETANGGA 004 / RW 08",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Kel. Jati, Kec. Pulogadung, Jakarta Timur",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Voucher Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "NO. BUKTI PENGELUARAN",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = voucherNumber,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "TANGGAL TRANSAKSI",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = RtCashViewModel.formatDate(transaction.dateMillis),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Penerima / Dibayarkan Kepada
                        VoucherRow(
                            label = if (isExpense) "Dibayarkan Kepada" else "Diterima Dari",
                            value = transaction.recipientPerson ?: transaction.citizenName ?: "Kas RT"
                        )

                        // Pos Beban
                        VoucherRow(
                            label = "Pos Beban / Kategori",
                            value = transaction.category.title
                        )

                        // Uraian
                        VoucherRow(
                            label = "Uraian / Keperluan",
                            value = transaction.title
                        )

                        // Catatan jika ada
                        if (transaction.notes.isNotBlank()) {
                            VoucherRow(
                                label = "Keterangan",
                                value = transaction.notes
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Nominal Highlight Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isExpense) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isExpense) Color(0xFFFECACA) else Color(0xFFBBF7D0)
                                )
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "JUMLAH PEMBAYARAN KAS KECIL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isExpense) ExpenseRed else IncomeGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = RtCashViewModel.formatRupiah(transaction.amount),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isExpense) ExpenseRed else IncomeGreen
                                )
                            }
                        }

                        // Attached Photo Preview
                        val photo = transaction.proofPhotoUri ?: transaction.proofPhotoCloudUrl
                        if (!photo.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Lampiran Bukti Nota / Kwitansi:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .clickable {
                                        onViewPhoto(photo, "Bukti Nota - $voucherNumber")
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(photo)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Nota",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = transaction.proofPhotoDescription ?: "Foto Struk Belanja",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = "Ketuk untuk melihat ukuran penuh",
                                        fontSize = 10.sp,
                                        color = EmeraldPrimary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Signature Block in Voucher
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Penerima Dana,", fontSize = 10.sp, color = Color(0xFF64748B))
                                Spacer(modifier = Modifier.height(28.dp))
                                Text(
                                    text = (transaction.recipientPerson ?: transaction.citizenName ?: "Penerima").take(18),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Kasir / Pemegang Kas,", fontSize = 10.sp, color = Color(0xFF64748B))
                                Spacer(modifier = Modifier.height(28.dp))
                                Text(
                                    text = "Prihatini Endah Y. M.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons: Share WhatsApp & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Tutup", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            shareVoucherText(context, transaction, voucherNumber)
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp)
                            .testTag("btn_share_petty_cash_voucher"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bagikan Voucher", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun VoucherRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = value,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f)
        )
    }
}

private fun shareVoucherText(context: Context, tx: TransactionEntity, voucherNum: String) {
    val date = RtCashViewModel.formatDate(tx.dateMillis)
    val text = """
        *BUKTI PENGELUARAN KAS KECIL*
        *RT 004 / RW 08 JATI, PULOGADUNG*
        ------------------------------------------
        📋 *No. Bukti:* $voucherNum
        📅 *Tanggal:* $date
        🏢 *Pos Beban:* ${tx.category.title}
        📝 *Keperluan:* ${tx.title}
        👤 *Dibayarkan Kepada:* ${tx.recipientPerson ?: tx.citizenName ?: "-"}
        💰 *Jumlah:* ${RtCashViewModel.formatRupiah(tx.amount)}
        🏷️ *Metode:* Kas Kecil (Tunai di Bendahara)
        ${if (tx.notes.isNotBlank()) "📌 *Catatan:* ${tx.notes}\n" else ""}
        ------------------------------------------
        _Sistem Keuangan RT 004 / RW 08_
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Bagikan Bukti Pengeluaran via WhatsApp / Email"))
}
