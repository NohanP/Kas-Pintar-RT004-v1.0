package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.MonthlyRecap
import com.example.model.TransactionEntity
import com.example.model.TransactionType
import com.example.ui.viewmodel.RtCashViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

object ReportExportHelper {

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID"))
    private val printDateTimeFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID"))

    /**
     * Extract only the first name (nama depan saja) from a citizen/recipient name string.
     * Handles honorifics like Pak, Bu, Bpk, Ibu gracefully.
     */
    fun extractFirstName(fullName: String?): String {
        if (fullName.isNullOrBlank()) return "-"
        val trimmed = fullName.trim().replace("\"", "").replace(";", ",")
        val parts = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (parts.isEmpty()) return "-"
        val first = parts[0]
        val honorifics = listOf("bpk", "pak", "ibu", "bu", "sdr", "sdri", "ustadz", "ust", "h.", "hj.", "dr.", "dr")
        if (honorifics.contains(first.lowercase()) && parts.size > 1) {
            return parts[1]
        }
        return first
    }

    /**
     * Export and share PDF Report in LANDSCAPE orientation (A4 Landscape: 842 x 595 pt)
     * equipped with Debit, Kredit, and Running Balance / Mutasi details.
     */
    fun exportAndSharePdf(context: Context, recap: MonthlyRecap) {
        try {
            val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val fileName = "Laporan_Kas_RT004_${recap.monthName}_${recap.year}_Landscape.pdf"
            val file = File(reportsDir, fileName)

            val pdfDocument = PdfDocument()
            val pageWidth = 842 // A4 Landscape width
            val pageHeight = 595 // A4 Landscape height

            val transactions = recap.transactions
            val itemsPerPage = 14
            val totalPages = max(1, ceil(transactions.size.toDouble() / itemsPerPage).toInt())

            val paint = Paint().apply { isAntiAlias = true }
            val printDate = printDateTimeFormatter.format(Date())

            for (pageIndex in 0 until totalPages) {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                val isFirstPage = pageIndex == 0
                val isLastPage = pageIndex == totalPages - 1

                // 1. Header Banner (Deep Slate Navy #134B70)
                val headerHeight = if (isFirstPage) 64f else 46f
                paint.color = AndroidColor.rgb(0x13, 0x4B, 0x70)
                canvas.drawRect(0f, 0f, pageWidth.toFloat(), headerHeight, paint)

                // Header Text
                paint.color = AndroidColor.WHITE
                if (isFirstPage) {
                    paint.textSize = 14.5f
                    paint.isFakeBoldText = true
                    canvas.drawText("BUKU KAS RT 004 / RW 08 KELURAHAN JATI, PULOGADUNG", 30f, 26f, paint)

                    paint.textSize = 10f
                    paint.isFakeBoldText = false
                    canvas.drawText(
                        "Laporan Rekapitulasi Arus Kas Bulanan (Debit & Kredit) • Periode: ${recap.monthName} ${recap.year}",
                        30f,
                        44f,
                        paint
                    )

                    paint.color = AndroidColor.rgb(0xBB, 0xEC, 0xE0)
                    paint.textSize = 8.5f
                    canvas.drawText("Dikeluarkan oleh Pengurus RT 004 / RW 08", 550f, 26f, paint)
                    canvas.drawText("Cetak: $printDate • Hal ${pageIndex + 1}/$totalPages", 550f, 44f, paint)
                } else {
                    paint.textSize = 11.5f
                    paint.isFakeBoldText = true
                    canvas.drawText("BUKU KAS RT 004 / RW 08 • Periode: ${recap.monthName} ${recap.year} (Lanjutan)", 30f, 28f, paint)

                    paint.color = AndroidColor.rgb(0xBB, 0xEC, 0xE0)
                    paint.textSize = 8.5f
                    paint.isFakeBoldText = false
                    canvas.drawText("Cetak: $printDate • Hal ${pageIndex + 1}/$totalPages", 630f, 28f, paint)
                }

                var currentY = headerHeight + 10f

                // 2. Executive Summary Block (Only on First Page)
                if (isFirstPage) {
                    val summaryBoxHeight = 52f
                    val summaryRect = RectF(30f, currentY, 812f, currentY + summaryBoxHeight)

                    // Summary Background Card
                    paint.color = AndroidColor.rgb(0xEE, 0xF8, 0xF6)
                    canvas.drawRoundRect(summaryRect, 8f, 8f, paint)

                    // Summary Border
                    paint.color = AndroidColor.rgb(0xC8, 0xEC, 0xE6)
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 1f
                    canvas.drawRoundRect(summaryRect, 8f, 8f, paint)
                    paint.style = Paint.Style.FILL

                    val col1X = 42f
                    val col2X = 230f
                    val col3X = 425f
                    val col4X = 620f
                    val labelY = currentY + 16f
                    val valY = currentY + 33f

                    // Col 1: Saldo Awal
                    paint.textSize = 8f
                    paint.color = AndroidColor.rgb(0x64, 0x74, 0x8B)
                    paint.isFakeBoldText = true
                    canvas.drawText("SALDO AWAL BULAN", col1X, labelY, paint)
                    paint.textSize = 10.5f
                    paint.color = AndroidColor.rgb(0x1E, 0x29, 0x3B)
                    canvas.drawText(RtCashViewModel.formatRupiah(recap.startingBalance), col1X, valY, paint)

                    // Col 2: Total Debit
                    paint.textSize = 8f
                    paint.color = AndroidColor.rgb(0x16, 0xA3, 0x4A)
                    canvas.drawText("TOTAL DEBIT (PENERIMAAN)", col2X, labelY, paint)
                    paint.textSize = 10.5f
                    canvas.drawText("+ ${RtCashViewModel.formatRupiah(recap.totalIncome)}", col2X, valY, paint)

                    // Col 3: Total Kredit
                    paint.textSize = 8f
                    paint.color = AndroidColor.rgb(0xDC, 0x26, 0x26)
                    canvas.drawText("TOTAL KREDIT (PENGELUARAN)", col3X, labelY, paint)
                    paint.textSize = 10.5f
                    canvas.drawText("- ${RtCashViewModel.formatRupiah(recap.totalExpense)}", col3X, valY, paint)

                    // Col 4: Saldo Akhir
                    paint.textSize = 8f
                    paint.color = AndroidColor.rgb(0x13, 0x4B, 0x70)
                    canvas.drawText("SALDO AKHIR KAS RT", col4X, labelY, paint)
                    paint.textSize = 11.5f
                    canvas.drawText(RtCashViewModel.formatRupiah(recap.endingBalance), col4X, valY, paint)

                    // Subtitle status line
                    paint.textSize = 7.5f
                    paint.isFakeBoldText = false
                    paint.color = AndroidColor.rgb(0x47, 0x55, 0x69)
                    canvas.drawText(
                        "Status Iuran Warga: ${recap.paidCitizensCount} dari ${recap.totalCitizens} Warga Lunas (${String.format(Locale("id", "ID"), "%.1f", recap.complianceRate)}%) • Belum Lunas: ${recap.unpaidCitizensCount} • Surplus/Defisit: ${RtCashViewModel.formatRupiah(recap.netBalance)}",
                        col1X,
                        currentY + 45f,
                        paint
                    )

                    currentY += summaryBoxHeight + 10f
                }

                // 3. Table Header (Landscape with Debit & Kredit Columns)
                val tableHeaderHeight = 20f
                paint.color = AndroidColor.rgb(0x13, 0x4B, 0x70)
                canvas.drawRect(30f, currentY, 812f, currentY + tableHeaderHeight, paint)

                paint.color = AndroidColor.WHITE
                paint.textSize = 8f
                paint.isFakeBoldText = true
                val textHeaderY = currentY + 13.5f

                canvas.drawText("NO", 35f, textHeaderY, paint)
                canvas.drawText("TANGGAL", 56f, textHeaderY, paint)
                canvas.drawText("NO KWITANSI", 116f, textHeaderY, paint)
                canvas.drawText("KATEGORI", 195f, textHeaderY, paint)
                canvas.drawText("URAIAN / PERIHAL TRANSAKSI", 295f, textHeaderY, paint)
                canvas.drawText("PENERIMA (NAMA DEPAN)", 480f, textHeaderY, paint)
                canvas.drawText("METODE", 600f, textHeaderY, paint)
                canvas.drawText("DEBIT / MASUK (RP)", 658f, textHeaderY, paint)
                canvas.drawText("KREDIT / KELUAR (RP)", 736f, textHeaderY, paint)

                currentY += tableHeaderHeight

                // 4. Table Rows
                val startIdx = pageIndex * itemsPerPage
                val endIdx = minOf(startIdx + itemsPerPage, transactions.size)
                val pageTransactions = if (startIdx < transactions.size) transactions.subList(startIdx, endIdx) else emptyList()

                val rowHeight = 17f
                paint.isFakeBoldText = false
                paint.textSize = 7.5f

                pageTransactions.forEachIndexed { idxOnPage, tx ->
                    val globalIdx = startIdx + idxOnPage
                    val rowY = currentY

                    // Row Zebra Background
                    if (globalIdx % 2 == 1) {
                        paint.color = AndroidColor.rgb(0xF8, 0xFA, 0xFC)
                        canvas.drawRect(30f, rowY, 812f, rowY + rowHeight, paint)
                    }

                    // Row divider line
                    paint.color = AndroidColor.rgb(0xEE, 0xF2, 0xF6)
                    paint.strokeWidth = 0.5f
                    canvas.drawLine(30f, rowY + rowHeight, 812f, rowY + rowHeight, paint)

                    val textY = rowY + 11.5f

                    // No
                    paint.color = AndroidColor.rgb(0x33, 0x41, 0x55)
                    canvas.drawText("${globalIdx + 1}", 35f, textY, paint)

                    // Tanggal
                    val dateFormatted = dateFormatter.format(Date(tx.dateMillis))
                    canvas.drawText(dateFormatted, 56f, textY, paint)

                    // No Kwitansi
                    canvas.drawText(tx.receiptNumber.take(16), 116f, textY, paint)

                    // Kategori
                    val shortCat = if (tx.category.title.length > 20) tx.category.title.take(18) + ".." else tx.category.title
                    canvas.drawText(shortCat, 195f, textY, paint)

                    // Uraian
                    val shortTitle = if (tx.title.length > 36) tx.title.take(34) + ".." else tx.title
                    canvas.drawText(shortTitle, 295f, textY, paint)

                    // Nama Penerima (Nama Depan Saja)
                    val firstName = extractFirstName(tx.citizenName ?: tx.recordedBy)
                    canvas.drawText(firstName.take(18), 480f, textY, paint)

                    // Metode
                    canvas.drawText(tx.paymentMethod.label.take(10), 600f, textY, paint)

                    // Debit (Masuk) Column
                    if (tx.type == TransactionType.PEMASUKAN) {
                        paint.color = AndroidColor.rgb(0x16, 0xA3, 0x4A)
                        paint.isFakeBoldText = true
                        canvas.drawText(RtCashViewModel.formatRupiah(tx.amount).replace("Rp ", ""), 658f, textY, paint)
                        paint.isFakeBoldText = false
                    } else {
                        paint.color = AndroidColor.rgb(0x94, 0xA3, 0xB8)
                        canvas.drawText("-", 690f, textY, paint)
                    }

                    // Kredit (Keluar) Column
                    if (tx.type == TransactionType.PENGELUARAN) {
                        paint.color = AndroidColor.rgb(0xDC, 0x26, 0x26)
                        paint.isFakeBoldText = true
                        canvas.drawText(RtCashViewModel.formatRupiah(tx.amount).replace("Rp ", ""), 736f, textY, paint)
                        paint.isFakeBoldText = false
                    } else {
                        paint.color = AndroidColor.rgb(0x94, 0xA3, 0xB8)
                        canvas.drawText("-", 768f, textY, paint)
                    }

                    currentY += rowHeight
                }

                // If last page: Draw Totals Row & Signatures
                if (isLastPage) {
                    // Total Summary Row
                    paint.color = AndroidColor.rgb(0xEE, 0xF8, 0xF6)
                    canvas.drawRect(30f, currentY, 812f, currentY + 19f, paint)

                    paint.color = AndroidColor.rgb(0x13, 0x4B, 0x70)
                    paint.textSize = 8f
                    paint.isFakeBoldText = true
                    canvas.drawText("TOTAL MUTASI PERIODE INI", 295f, currentY + 13f, paint)

                    paint.color = AndroidColor.rgb(0x16, 0xA3, 0x4A)
                    canvas.drawText(RtCashViewModel.formatRupiah(recap.totalIncome).replace("Rp ", ""), 658f, currentY + 13f, paint)

                    paint.color = AndroidColor.rgb(0xDC, 0x26, 0x26)
                    canvas.drawText(RtCashViewModel.formatRupiah(recap.totalExpense).replace("Rp ", ""), 736f, currentY + 13f, paint)

                    // 5. Signatures Block (Pengesah, Sekretaris, Bendahara)
                    val sigY = 520f
                    paint.isFakeBoldText = false
                    paint.color = AndroidColor.rgb(0x47, 0x55, 0x69)
                    paint.textSize = 8f

                    // Left: Ketua RT
                    canvas.drawText("Mengesahkan / Mengetahui,", 50f, sigY, paint)
                    paint.isFakeBoldText = true
                    paint.color = AndroidColor.rgb(0x1E, 0x29, 0x3B)
                    canvas.drawText("Ketua RT 004 / RW 08", 50f, sigY + 13f, paint)
                    canvas.drawText("( Nohan Pancono )", 50f, sigY + 50f, paint)

                    // Center: Sekretaris
                    paint.isFakeBoldText = false
                    paint.color = AndroidColor.rgb(0x47, 0x55, 0x69)
                    canvas.drawText("Mengetahui,", 340f, sigY, paint)
                    paint.isFakeBoldText = true
                    paint.color = AndroidColor.rgb(0x1E, 0x29, 0x3B)
                    canvas.drawText("Sekretaris RT 004", 340f, sigY + 13f, paint)
                    canvas.drawText("( Muhammad Rijaldi Imam M. )", 340f, sigY + 50f, paint)

                    // Right: Bendahara RT
                    paint.isFakeBoldText = false
                    paint.color = AndroidColor.rgb(0x47, 0x55, 0x69)
                    canvas.drawText("Jakarta Timur, ${recap.monthName} ${recap.year}", 610f, sigY, paint)
                    paint.isFakeBoldText = true
                    paint.color = AndroidColor.rgb(0x1E, 0x29, 0x3B)
                    canvas.drawText("Bendahara RT 004 (Penyusun)", 610f, sigY + 13f, paint)
                    canvas.drawText("( Prihatini Endah Yulia M. )", 610f, sigY + 50f, paint)
                }

                pdfDocument.finishPage(page)
            }

            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            shareFile(context, file, "application/pdf", "Bagikan Laporan PDF Landscape Arus Kas RT004")
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membuat PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Export and share CSV/Excel Spreadsheet file with:
     * - Debit (Pemasukan) column
     * - Kredit (Pengeluaran) column
     * - Nama Penerima / Warga (Nama Depan Saja)
     * Compatible with Microsoft Excel & Google Sheets.
     */
    fun exportAndShareExcelCsv(context: Context, recap: MonthlyRecap) {
        try {
            val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val fileName = "Laporan_Kas_RT004_${recap.monthName}_${recap.year}.csv"
            val file = File(reportsDir, fileName)

            val csvContent = StringBuilder().apply {
                append("LAPORAN ARUS KAS BULANAN RT004 / RW 08 JATI PULOGADUNG\n")
                append("Periode;,${recap.monthName} ${recap.year}\n")
                append("Saldo Awal;,${recap.startingBalance}\n")
                append("Total Penerimaan (Debit);,${recap.totalIncome}\n")
                append("Total Pengeluaran (Kredit);,${recap.totalExpense}\n")
                append("Surplus / Defisit;,${recap.netBalance}\n")
                append("Saldo Akhir Kas RT;,${recap.endingBalance}\n\n")
                append("Penyusun;,Prihatini Endah Yulia Maretiasari (Bendahara RT004)\n")
                append("Pengesah;,Nohan Pancono (Ketua RT 004)\n")
                append("Sekretaris;,Muhammad Rijaldi Imam Mustarih\n\n")
                append("No;Tanggal;No Kwitansi;Kategori;Perihal Transaksi;Jenis;Nama Penerima (Nama Depan);Metode Pembayaran;Debit / Masuk (Rp);Kredit / Keluar (Rp);Nominal Total (Rp);Keterangan Lengkap Warga\n")

                recap.transactions.forEachIndexed { index, tx ->
                    val cleanTitle = tx.title.replace(";", ",")
                    val citizenFull = tx.citizenName ?: "-"
                    val recipientFirstName = extractFirstName(tx.citizenName ?: tx.recordedBy)
                    val dateFormatted = dateFormatter.format(Date(tx.dateMillis))
                    val debitAmount = if (tx.type == TransactionType.PEMASUKAN) tx.amount else 0
                    val kreditAmount = if (tx.type == TransactionType.PENGELUARAN) tx.amount else 0

                    append("${index + 1};$dateFormatted;${tx.receiptNumber};${tx.category.title};\"$cleanTitle\";${tx.type.name};\"$recipientFirstName\";${tx.paymentMethod.label};$debitAmount;$kreditAmount;${tx.amount};\"$citizenFull\"\n")
                }
            }.toString()

            // Write with UTF-8 BOM for Microsoft Excel auto-encoding
            FileOutputStream(file).use { out ->
                out.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                out.write(csvContent.toByteArray(Charsets.UTF_8))
            }

            shareFile(context, file, "text/csv", "Buka di Microsoft Excel / Google Sheets")
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal ekspor Excel/CSV: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Export and open directly via Google Drive / Google Sheets Web or App
     */
    fun openInGoogleSheets(context: Context, recap: MonthlyRecap) {
        exportAndShareExcelCsv(context, recap)
    }

    /**
     * Export and share PDF Report for Petty Cash
     */
    fun exportAndSharePettyCashPdf(context: Context, recap: com.example.model.PettyCashRecap) {
        try {
            val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val fileName = "Laporan_Kas_Kecil_RT004_${recap.monthName}_${recap.year}.pdf"
            val file = File(reportsDir, fileName)

            val pdfDocument = PdfDocument()
            val pageWidth = 842 // A4 Landscape width
            val pageHeight = 595 // A4 Landscape height

            val transactions = recap.transactions
            val itemsPerPage = 14
            val totalPages = max(1, ceil(transactions.size.toDouble() / itemsPerPage).toInt())

            val paint = Paint().apply { isAntiAlias = true }
            val printDate = printDateTimeFormatter.format(Date())

            for (pageIndex in 0 until totalPages) {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                val isFirstPage = pageIndex == 0
                val isLastPage = pageIndex == totalPages - 1

                // 1. Header Banner (Forest Emerald #006A4E / Navy)
                val headerHeight = if (isFirstPage) 64f else 46f
                paint.color = AndroidColor.rgb(0x00, 0x5C, 0x4B)
                canvas.drawRect(0f, 0f, pageWidth.toFloat(), headerHeight, paint)

                // Header Text
                paint.color = AndroidColor.WHITE
                if (isFirstPage) {
                    paint.textSize = 14f
                    paint.isFakeBoldText = true
                    canvas.drawText("BUKU KAS KECIL RT 004 / RW 08 KELURAHAN JATI, PULOGADUNG", 30f, 26f, paint)

                    paint.textSize = 9.5f
                    paint.isFakeBoldText = false
                    canvas.drawText(
                        "Laporan Kas Kecil Operasional • Periode: ${recap.monthName} ${recap.year}",
                        30f,
                        44f,
                        paint
                    )

                    paint.color = AndroidColor.rgb(0xFF, 0xE0, 0x82)
                    paint.textSize = 8.5f
                    canvas.drawText("Kasir / Pemegang Kas: ${recap.custodianName}", 520f, 26f, paint)
                    canvas.drawText("Cetak: $printDate • Hal ${pageIndex + 1}/$totalPages", 520f, 44f, paint)
                } else {
                    paint.textSize = 11.5f
                    paint.isFakeBoldText = true
                    canvas.drawText("BUKU KAS KECIL RT 004 • Periode: ${recap.monthName} ${recap.year} (Lanjutan)", 30f, 28f, paint)

                    paint.color = AndroidColor.rgb(0xFF, 0xE0, 0x82)
                    paint.textSize = 8.5f
                    paint.isFakeBoldText = false
                    canvas.drawText("Cetak: $printDate • Hal ${pageIndex + 1}/$totalPages", 630f, 28f, paint)
                }

                var currentY = headerHeight + 10f

                // 2. Executive Petty Cash Summary Card (Only on First Page)
                if (isFirstPage) {
                    val summaryBoxHeight = 52f
                    val summaryRect = RectF(30f, currentY, 812f, currentY + summaryBoxHeight)

                    // Summary Background Card
                    paint.color = AndroidColor.rgb(0xF0, 0xFD, 0xF4)
                    canvas.drawRoundRect(summaryRect, 8f, 8f, paint)

                    // Summary Border
                    paint.color = AndroidColor.rgb(0xBB, 0xF7, 0xD0)
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 1f
                    canvas.drawRoundRect(summaryRect, 8f, 8f, paint)
                    paint.style = Paint.Style.FILL

                    val col1X = 42f
                    val col2X = 230f
                    val col3X = 425f
                    val col4X = 620f
                    val labelY = currentY + 16f
                    val valY = currentY + 33f

                    // Col 1: Saldo Awal Kas Kecil
                    paint.textSize = 8f
                    paint.color = AndroidColor.rgb(0x64, 0x74, 0x8B)
                    paint.isFakeBoldText = true
                    canvas.drawText("SALDO AWAL KAS KECIL", col1X, labelY, paint)
                    paint.textSize = 10.5f
                    paint.color = AndroidColor.rgb(0x1E, 0x29, 0x3B)
                    canvas.drawText(RtCashViewModel.formatRupiah(recap.startingBalance), col1X, valY, paint)

                    // Col 2: Total Top Up
                    paint.textSize = 8f
                    paint.color = AndroidColor.rgb(0x16, 0xA3, 0x4A)
                    canvas.drawText("PENGISIAN / TOP UP (DEBET)", col2X, labelY, paint)
                    paint.textSize = 10.5f
                    canvas.drawText("+ ${RtCashViewModel.formatRupiah(recap.totalTopUp)}", col2X, valY, paint)

                    // Col 3: Total Pemakaian
                    paint.textSize = 8f
                    paint.color = AndroidColor.rgb(0xDC, 0x26, 0x26)
                    canvas.drawText("TOTAL PENGELUARAN (KREDIT)", col3X, labelY, paint)
                    paint.textSize = 10.5f
                    canvas.drawText("- ${RtCashViewModel.formatRupiah(recap.totalDisbursement)}", col3X, valY, paint)

                    // Col 4: Saldo Akhir
                    paint.textSize = 8f
                    paint.color = AndroidColor.rgb(0x00, 0x5C, 0x4B)
                    canvas.drawText("SALDO AKHIR KAS KECIL", col4X, labelY, paint)
                    paint.textSize = 11.5f
                    canvas.drawText(RtCashViewModel.formatRupiah(recap.endingBalance), col4X, valY, paint)

                    // Subtitle status line
                    paint.textSize = 7.5f
                    paint.isFakeBoldText = false
                    paint.color = AndroidColor.rgb(0x47, 0x55, 0x69)
                    canvas.drawText(
                        "Jumlah Transaksi: ${recap.totalVouchers} Transaksi • Mutasi Bersih: ${RtCashViewModel.formatRupiah(recap.netFluctuation)}",
                        col1X,
                        currentY + 45f,
                        paint
                    )

                    currentY += summaryBoxHeight + 10f
                }

                // 3. Table Header
                val tableHeaderHeight = 20f
                paint.color = AndroidColor.rgb(0x00, 0x5C, 0x4B)
                canvas.drawRect(30f, currentY, 812f, currentY + tableHeaderHeight, paint)

                paint.color = AndroidColor.WHITE
                paint.textSize = 8f
                paint.isFakeBoldText = true
                val textHeaderY = currentY + 13.5f

                canvas.drawText("NO", 35f, textHeaderY, paint)
                canvas.drawText("TANGGAL", 56f, textHeaderY, paint)
                canvas.drawText("NO. BUKTI / VOUCHER", 116f, textHeaderY, paint)
                canvas.drawText("POS BEBAN / KATEGORI", 220f, textHeaderY, paint)
                canvas.drawText("URAIAN PEMAKAIAN KAS KECIL", 330f, textHeaderY, paint)
                canvas.drawText("PENERIMA / DIBAYARKAN KEPADA", 500f, textHeaderY, paint)
                canvas.drawText("TOP UP / DEBET (RP)", 645f, textHeaderY, paint)
                canvas.drawText("PEMAKAIAN / KREDIT (RP)", 730f, textHeaderY, paint)

                currentY += tableHeaderHeight

                // 4. Table Rows
                val startIdx = pageIndex * itemsPerPage
                val endIdx = minOf(startIdx + itemsPerPage, transactions.size)
                val pageTransactions = if (startIdx < transactions.size) transactions.subList(startIdx, endIdx) else emptyList()

                val rowHeight = 17f
                paint.isFakeBoldText = false
                paint.textSize = 7.5f

                pageTransactions.forEachIndexed { idxOnPage, tx ->
                    val globalIdx = startIdx + idxOnPage
                    val rowY = currentY

                    // Row Zebra Background
                    if (globalIdx % 2 == 1) {
                        paint.color = AndroidColor.rgb(0xF8, 0xFA, 0xFC)
                        canvas.drawRect(30f, rowY, 812f, rowY + rowHeight, paint)
                    }

                    // Row divider line
                    paint.color = AndroidColor.rgb(0xEE, 0xF2, 0xF6)
                    paint.strokeWidth = 0.5f
                    canvas.drawLine(30f, rowY + rowHeight, 812f, rowY + rowHeight, paint)

                    val textY = rowY + 11.5f

                    // No
                    paint.color = AndroidColor.rgb(0x33, 0x41, 0x55)
                    canvas.drawText("${globalIdx + 1}", 35f, textY, paint)

                    // Tanggal
                    val dateFormatted = dateFormatter.format(Date(tx.dateMillis))
                    canvas.drawText(dateFormatted, 56f, textY, paint)

                    // No Bukti
                    val bpkkDisplay = if (tx.bpkkNumber.isNotBlank()) tx.bpkkNumber else tx.receiptNumber
                    canvas.drawText(bpkkDisplay.take(16), 116f, textY, paint)

                    // Kategori / Pos Beban
                    val shortCat = if (tx.category.title.length > 20) tx.category.title.take(18) + ".." else tx.category.title
                    canvas.drawText(shortCat, 220f, textY, paint)

                    // Uraian
                    val shortTitle = if (tx.title.length > 34) tx.title.take(32) + ".." else tx.title
                    canvas.drawText(shortTitle, 330f, textY, paint)

                    // Penerima Person
                    val recipientDisplay = tx.recipientPerson ?: tx.citizenName ?: tx.recordedBy
                    val shortRecipient = extractFirstName(recipientDisplay)
                    canvas.drawText(shortRecipient.take(18), 500f, textY, paint)

                    // Debet (Top Up Kas Kecil)
                    if (tx.type == TransactionType.PEMASUKAN) {
                        paint.color = AndroidColor.rgb(0x16, 0xA3, 0x4A)
                        paint.isFakeBoldText = true
                        canvas.drawText(RtCashViewModel.formatRupiah(tx.amount).replace("Rp ", ""), 645f, textY, paint)
                        paint.isFakeBoldText = false
                    } else {
                        paint.color = AndroidColor.rgb(0x94, 0xA3, 0xB8)
                        canvas.drawText("-", 675f, textY, paint)
                    }

                    // Kredit (Pemakaian Kas Kecil)
                    if (tx.type == TransactionType.PENGELUARAN) {
                        paint.color = AndroidColor.rgb(0xDC, 0x26, 0x26)
                        paint.isFakeBoldText = true
                        canvas.drawText(RtCashViewModel.formatRupiah(tx.amount).replace("Rp ", ""), 730f, textY, paint)
                        paint.isFakeBoldText = false
                    } else {
                        paint.color = AndroidColor.rgb(0x94, 0xA3, 0xB8)
                        canvas.drawText("-", 760f, textY, paint)
                    }

                    currentY += rowHeight
                }

                // If last page: Totals and Signatures
                if (isLastPage) {
                    paint.color = AndroidColor.rgb(0xF0, 0xFD, 0xF4)
                    canvas.drawRect(30f, currentY, 812f, currentY + 19f, paint)

                    paint.color = AndroidColor.rgb(0x00, 0x5C, 0x4B)
                    paint.textSize = 8f
                    paint.isFakeBoldText = true
                    canvas.drawText("TOTAL MUTASI KAS KECIL PERIODE INI", 330f, currentY + 13f, paint)

                    paint.color = AndroidColor.rgb(0x16, 0xA3, 0x4A)
                    canvas.drawText(RtCashViewModel.formatRupiah(recap.totalTopUp).replace("Rp ", ""), 645f, currentY + 13f, paint)

                    paint.color = AndroidColor.rgb(0xDC, 0x26, 0x26)
                    canvas.drawText(RtCashViewModel.formatRupiah(recap.totalDisbursement).replace("Rp ", ""), 730f, currentY + 13f, paint)

                    // Signatures Block
                    val sigY = 520f
                    paint.isFakeBoldText = false
                    paint.color = AndroidColor.rgb(0x47, 0x55, 0x69)
                    paint.textSize = 8f

                    // Left: Ketua RT
                    canvas.drawText("Mengetahui & Menyetujui,", 60f, sigY, paint)
                    paint.isFakeBoldText = true
                    paint.color = AndroidColor.rgb(0x1E, 0x29, 0x3B)
                    canvas.drawText("Ketua RT 004 / RW 08", 60f, sigY + 13f, paint)
                    canvas.drawText("( Nohan Pancono )", 60f, sigY + 50f, paint)

                    // Right: Kasir / Pemegang Kas Kecil
                    paint.isFakeBoldText = false
                    paint.color = AndroidColor.rgb(0x47, 0x55, 0x69)
                    canvas.drawText("Jakarta Timur, ${recap.monthName} ${recap.year}", 580f, sigY, paint)
                    paint.isFakeBoldText = true
                    paint.color = AndroidColor.rgb(0x1E, 0x29, 0x3B)
                    canvas.drawText("Bendahara / Pemegang Kas Kecil", 580f, sigY + 13f, paint)
                    canvas.drawText("( Prihatini Endah Yulia M. )", 580f, sigY + 50f, paint)
                }

                pdfDocument.finishPage(page)
            }

            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            shareFile(context, file, "application/pdf", "Bagikan Laporan Kas Kecil PDF")
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membuat PDF Kas Kecil: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Export and share CSV/Excel Spreadsheet for Petty Cash
     */
    fun exportAndSharePettyCashExcelCsv(context: Context, recap: com.example.model.PettyCashRecap) {
        try {
            val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val fileName = "Laporan_Kas_Kecil_RT004_${recap.monthName}_${recap.year}.csv"
            val file = File(reportsDir, fileName)

            val csvContent = StringBuilder().apply {
                append("LAPORAN KAS KECIL RT004 / RW 08\n")
                append("Periode;,${recap.monthName} ${recap.year}\n")
                append("Saldo Awal Kas Kecil;,${recap.startingBalance}\n")
                append("Total Pengisian / Top Up;,${recap.totalTopUp}\n")
                append("Total Pengeluaran;,${recap.totalDisbursement}\n")
                append("Mutasi Bersih;,${recap.netFluctuation}\n")
                append("Saldo Akhir Kas Kecil;,${recap.endingBalance}\n")
                append("Jumlah Transaksi;,${recap.totalVouchers}\n\n")
                append("Pemegang Kas Kecil;,Prihatini Endah Yulia Maretiasari (Bendahara RT004)\n")
                append("Menyetujui;,Nohan Pancono (Ketua RT 004)\n\n")
                append("No;Tanggal;No Bukti;Pos Beban / Kategori;Uraian Pemakaian;Penerima Dana;Jenis;Debet / Top Up (Rp);Kredit / Keluar (Rp);Nominal Total (Rp);Catatan\n")

                recap.transactions.forEachIndexed { index, tx ->
                    val cleanTitle = tx.title.replace(";", ",")
                    val recipient = (tx.recipientPerson ?: tx.citizenName ?: tx.recordedBy).replace(";", ",")
                    val dateFormatted = dateFormatter.format(Date(tx.dateMillis))
                    val bpkk = if (tx.bpkkNumber.isNotBlank()) tx.bpkkNumber else tx.receiptNumber
                    val debitAmount = if (tx.type == TransactionType.PEMASUKAN) tx.amount else 0
                    val kreditAmount = if (tx.type == TransactionType.PENGELUARAN) tx.amount else 0

                    append("${index + 1};$dateFormatted;\"$bpkk\";${tx.category.title};\"$cleanTitle\";\"$recipient\";${tx.type.name};$debitAmount;$kreditAmount;${tx.amount};\"${tx.notes}\"\n")
                }
            }.toString()

            // Write with UTF-8 BOM for Microsoft Excel
            FileOutputStream(file).use { out ->
                out.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                out.write(csvContent.toByteArray(Charsets.UTF_8))
            }

            shareFile(context, file, "text/csv", "Buka Laporan Kas Kecil di Microsoft Excel / Google Sheets")
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal ekspor CSV Kas Kecil: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Laporan Arus Kas RT 04 ${file.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }
}
