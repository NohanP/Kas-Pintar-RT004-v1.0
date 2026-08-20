package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType(val label: String) {
    PEMASUKAN("Pemasukan / Iuran"),
    PENGELUARAN("Pengeluaran Operasional")
}

enum class PaymentMethod(val label: String) {
    TRANSFER_BCA("Transfer BCA"),
    TUNAI("Tunai"),
    QRIS_BCA("QRIS BCA")
}

enum class TransactionCategory(
    val title: String,
    val type: TransactionType,
    val icon: ImageVector
) {
    // Pemasukan
    IURAN_WARGA("Iuran warga", TransactionType.PEMASUKAN, Icons.Default.People),
    IURAN_USAHA("Iuran pelaku usaha", TransactionType.PEMASUKAN, Icons.Default.Business),
    PENGISIAN_KAS_KECIL("Pengisian Kas Kecil (Top Up)", TransactionType.PEMASUKAN, Icons.Default.MonetizationOn),
    PENGEMBALIAN_SISA_KAS_KECIL("Pengembalian Sisa Kas Kecil", TransactionType.PEMASUKAN, Icons.Default.CardGiftcard),
    DONASI_SUMBANGAN("Donasi / Sumbangan", TransactionType.PEMASUKAN, Icons.Default.Handshake),
    KAS_AWAL("Kas Awal / Saldo lalu", TransactionType.PEMASUKAN, Icons.Default.MonetizationOn),
    THR("THR", TransactionType.PEMASUKAN, Icons.Default.CardGiftcard),
    PEMASUKAN_LAINNYA("Pemasukkan lainnya", TransactionType.PEMASUKAN, Icons.Default.MoreHoriz),

    // Pengeluaran
    GAJI_KEBERSIHAN("Gaji Petugas Kebersihan", TransactionType.PENGELUARAN, Icons.Default.CleaningServices),
    TUNJANGAN_SECURITY("Tunjangan Security", TransactionType.PENGELUARAN, Icons.Default.Security),
    PEMELIHARAAN_FASUM("Pemeliharaan Fasum", TransactionType.PENGELUARAN, Icons.Default.Build),
    OPERASIONAL_CCTV("Operasional CCTV", TransactionType.PENGELUARAN, Icons.Default.Videocam),
    OPERASIONAL_ATK("Operasional ATK & Percetakaan", TransactionType.PENGELUARAN, Icons.Default.Description),
    KONSUMSI_RAPAT("Konsumsi Rapat & Acara RT", TransactionType.PENGELUARAN, Icons.Default.EventNote),
    PENGELUARAN_LAINNYA("Pengeluaran Lainnya", TransactionType.PENGELUARAN, Icons.Default.MoreHoriz)
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Long,
    val type: TransactionType,
    val category: TransactionCategory,
    val citizenId: Long? = null,
    val citizenName: String? = null,
    val address: String? = null,
    val month: Int, // 1 to 12
    val year: Int, // e.g. 2026
    val dateMillis: Long = System.currentTimeMillis(),
    val recordedBy: String = "Bendahara RT",
    val paymentMethod: PaymentMethod = PaymentMethod.TUNAI,
    val isApprovedByKetua: Boolean = true,
    val receiptNumber: String = "",
    val notes: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val syncId: String = "",
    val proofPhotoUri: String? = null,
    val proofPhotoCloudUrl: String? = null,
    val proofPhotoDescription: String? = null,
    val isPettyCash: Boolean = false,
    val bpkkNumber: String = "",
    val recipientPerson: String? = null
)
