package com.example.data

import com.example.model.AnnouncementEntity
import com.example.model.CitizenEntity
import com.example.model.CitizenType
import com.example.model.PaymentMethod
import com.example.model.TransactionCategory
import com.example.model.TransactionEntity
import com.example.model.TransactionType

object SampleDataProvider {

    fun getInitialCitizens(): List<CitizenEntity> = listOf(
        CitizenEntity(
            id = 1,
            name = "Bpk. Nohan Pancono (Ketua RT)",
            houseNumber = "Blok A1 No. 01",
            phone = "0812-3456-7890",
            type = CitizenType.WARGA_TETAP,
            monthlyFee = 50_000L,
            notes = "Ketua RT"
        ),
        CitizenEntity(
            id = 2,
            name = "Ibu Prihatini Endah Yulia M. (Bendahara)",
            houseNumber = "Blok A1 No. 05",
            phone = "0813-8899-1122",
            type = CitizenType.WARGA_TETAP,
            monthlyFee = 50_000L,
            notes = "Bendahara Kas RT"
        ),
        CitizenEntity(
            id = 3,
            name = "Bpk. Muhammad Rijaldi Imam M. (Sekretaris)",
            houseNumber = "Blok A2 No. 03",
            phone = "0815-6677-8899",
            type = CitizenType.WARGA_TETAP,
            monthlyFee = 50_000L,
            notes = "Sekretaris RT"
        ),
        CitizenEntity(
            id = 4,
            name = "Bpk. Joko Santoso",
            houseNumber = "Blok A2 No. 07",
            phone = "0818-0909-1234",
            type = CitizenType.WARGA_TETAP,
            monthlyFee = 50_000L,
            notes = "Koordinator Keamanan"
        ),
        CitizenEntity(
            id = 5,
            name = "Bpk. Ahmad Wahyudi",
            houseNumber = "Blok B1 No. 02",
            phone = "0857-1122-3344",
            type = CitizenType.WARGA_TETAP,
            monthlyFee = 50_000L,
            notes = "Warga Tetap"
        ),
        CitizenEntity(
            id = 6,
            name = "Ibu Dewi Anggraeni",
            houseNumber = "Blok B1 No. 06",
            phone = "0878-5544-3322",
            type = CitizenType.WARGA_TETAP,
            monthlyFee = 50_000L,
            notes = "Kader Posyandu RT"
        ),
        CitizenEntity(
            id = 7,
            name = "Bpk. Rudi Hermawan",
            houseNumber = "Blok B2 No. 04",
            phone = "0812-9988-7766",
            type = CitizenType.WARGA_TETAP,
            monthlyFee = 50_000L,
            notes = "Warga Tetap"
        ),
        CitizenEntity(
            id = 8,
            name = "Sdr. Rian Pratama",
            houseNumber = "Blok C1 No. 08 (Kontrak)",
            phone = "0896-1234-5678",
            type = CitizenType.WARGA_KONTRAK,
            monthlyFee = 35_000L,
            notes = "Kontrak sejak Jan 2026"
        ),
        CitizenEntity(
            id = 9,
            name = "Sdri. Maya Putri",
            houseNumber = "Blok C1 No. 10 (Kontrak)",
            phone = "0821-4433-2211",
            type = CitizenType.WARGA_KONTRAK,
            monthlyFee = 35_000L,
            notes = "Kontrak 1 tahun"
        ),
        CitizenEntity(
            id = 10,
            name = "Warung Makan Bu Warsi",
            houseNumber = "Ruko Utama Blok D No. 01",
            phone = "0813-7766-5544",
            type = CitizenType.PELAKU_USAHA,
            monthlyFee = 100_000L,
            notes = "Usaha Kuliner / Warung Nasi"
        ),
        CitizenEntity(
            id = 11,
            name = "Toko Kelontong Berkah (Pak Tejo)",
            houseNumber = "Ruko Utama Blok D No. 02",
            phone = "0812-2233-4455",
            type = CitizenType.PELAKU_USAHA,
            monthlyFee = 100_000L,
            notes = "Toko Sembako Warga"
        ),
        CitizenEntity(
            id = 12,
            name = "Laundry Bersih Wangi",
            houseNumber = "Ruko Samping Blok D No. 05",
            phone = "0877-3344-5566",
            type = CitizenType.PELAKU_USAHA,
            monthlyFee = 100_000L,
            notes = "Jasa Laundry Kiloan"
        ),
        CitizenEntity(
            id = 13,
            name = "Gerobak Martabak Mas Agus (PKL)",
            houseNumber = "Pos Depan Gapura RT",
            phone = "0856-7788-9900",
            type = CitizenType.WARUNG_PKL,
            monthlyFee = 75_000L,
            notes = "PKL Binaan RT"
        )
    )

    fun getInitialTransactions(): List<TransactionEntity> {
        val now = System.currentTimeMillis()
        val dayMillis = 86_400_000L

        return listOf(
            // Kas Awal Saldo Bawaan Agustus 2026 (Kas Bank)
            TransactionEntity(
                id = 1,
                title = "Kas Awal / Saldo lalu (Bawaan Juli)",
                amount = 4_850_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.KAS_AWAL,
                citizenId = null,
                citizenName = "Kas RT",
                month = 8,
                year = 2026,
                dateMillis = now - (14 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TRANSFER_BCA,
                receiptNumber = "KW-202608-001",
                notes = "Saldo kas per 1 Agustus 2026",
                syncId = "TX-INIT-001",
                isPettyCash = false
            ),
            // Pembentukan & Pengisian Awal Kas Kecil
            TransactionEntity(
                id = 18,
                title = "Pengisian Kas Kecil - Top Up Awal Bulan",
                amount = 1_500_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.PENGISIAN_KAS_KECIL,
                citizenId = null,
                citizenName = "Bendahara Kasir RT",
                month = 8,
                year = 2026,
                dateMillis = now - (13 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "KW-KK-202608-001",
                notes = "Pencairan dana kas kecil dari Rekening BCA RT",
                syncId = "TX-202608-KK01",
                isPettyCash = true,
                bpkkNumber = "KK-IN-202608-001",
                recipientPerson = "Prihatini Endah Yulia M. (Bendahara Kasir)"
            ),
            // Iuran Warga Agustus 2026
            TransactionEntity(
                id = 2,
                title = "Iuran warga - Bpk. Nohan Pancono",
                amount = 50_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.IURAN_WARGA,
                citizenId = 1,
                citizenName = "Bpk. Nohan Pancono",
                month = 8,
                year = 2026,
                dateMillis = now - (12 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TRANSFER_BCA,
                receiptNumber = "KW-202608-002",
                notes = "Iuran Warga Agustus 2026",
                syncId = "TX-202608-002",
                isPettyCash = false
            ),
            TransactionEntity(
                id = 3,
                title = "Iuran warga - Ibu Prihatini Endah Yulia M.",
                amount = 50_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.IURAN_WARGA,
                citizenId = 2,
                citizenName = "Ibu Prihatini Endah Yulia M.",
                month = 8,
                year = 2026,
                dateMillis = now - (11 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "KW-202608-003",
                notes = "Iuran Warga Agustus 2026",
                syncId = "TX-202608-003",
                isPettyCash = false
            ),
            TransactionEntity(
                id = 4,
                title = "Iuran warga - Bpk. Muhammad Rijaldi Imam M.",
                amount = 50_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.IURAN_WARGA,
                citizenId = 3,
                citizenName = "Bpk. Muhammad Rijaldi Imam M.",
                month = 8,
                year = 2026,
                dateMillis = now - (10 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.QRIS_BCA,
                receiptNumber = "KW-202608-004",
                notes = "Iuran Warga Agustus 2026 (QRIS BCA)",
                syncId = "TX-202608-004",
                isPettyCash = false
            ),
            TransactionEntity(
                id = 5,
                title = "Iuran pelaku usaha - Warung Makan Bu Warsi",
                amount = 100_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.IURAN_USAHA,
                citizenId = 10,
                citizenName = "Warung Makan Bu Warsi",
                month = 8,
                year = 2026,
                dateMillis = now - (9 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "KW-202608-005",
                notes = "Iuran Kebersihan & Keamanan Usaha Agt 2026",
                syncId = "TX-202608-005",
                isPettyCash = false
            ),
            TransactionEntity(
                id = 6,
                title = "Iuran pelaku usaha - Toko Kelontong Berkah",
                amount = 100_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.IURAN_USAHA,
                citizenId = 11,
                citizenName = "Toko Kelontong Berkah",
                month = 8,
                year = 2026,
                dateMillis = now - (8 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TRANSFER_BCA,
                receiptNumber = "KW-202608-006",
                notes = "Iuran Tempat Usaha Agt 2026",
                syncId = "TX-202608-006",
                isPettyCash = false
            ),
            TransactionEntity(
                id = 7,
                title = "Iuran warga - Bpk. Joko Santoso",
                amount = 50_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.IURAN_WARGA,
                citizenId = 4,
                citizenName = "Bpk. Joko Santoso",
                month = 8,
                year = 2026,
                dateMillis = now - (7 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "KW-202608-007",
                notes = "Iuran Warga Agustus 2026",
                syncId = "TX-202608-007",
                isPettyCash = false
            ),
            TransactionEntity(
                id = 8,
                title = "Donasi / Sumbangan HUT Kemerdekaan RI - H. Sulaiman",
                amount = 500_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.DONASI_SUMBANGAN,
                citizenId = null,
                citizenName = "H. Sulaiman (Donatur)",
                month = 8,
                year = 2026,
                dateMillis = now - (6 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TRANSFER_BCA,
                receiptNumber = "KW-202608-008",
                notes = "Donasi Peringatan 17 Agustus RT",
                syncId = "TX-202608-008",
                isPettyCash = false
            ),
            TransactionEntity(
                id = 9,
                title = "THR Petugas Kebersihan & Keamanan",
                amount = 500_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.THR,
                citizenId = null,
                citizenName = "Titipan Partisipasi Warga",
                month = 8,
                year = 2026,
                dateMillis = now - (5 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "KW-202608-009",
                notes = "Penerimaan THR untuk petugas lingkungan",
                syncId = "TX-202608-009",
                isPettyCash = false
            ),
            TransactionEntity(
                id = 10,
                title = "Pemasukkan lainnya - Sewa Tenda & Kursi RT",
                amount = 150_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.PEMASUKAN_LAINNYA,
                citizenId = null,
                citizenName = "Peminjaman Fasum Warga",
                month = 8,
                year = 2026,
                dateMillis = now - (4 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "KW-202608-010",
                notes = "Infaq sewa tenda & kursi kas RT",
                syncId = "TX-202608-010",
                isPettyCash = false
            ),
            // Top Up Tambahan Kas Kecil (Dana Berubah)
            TransactionEntity(
                id = 19,
                title = "Pengisian Tambahan Kas Kecil (Metode Fluktuasi)",
                amount = 500_000L,
                type = TransactionType.PEMASUKAN,
                category = TransactionCategory.PENGISIAN_KAS_KECIL,
                citizenId = null,
                citizenName = "Bendahara Kasir RT",
                month = 8,
                year = 2026,
                dateMillis = now - (3 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "KW-KK-202608-002",
                notes = "Top up tambahan kas kecil untuk persiapan HUT RI",
                syncId = "TX-202608-KK02",
                isPettyCash = true,
                bpkkNumber = "BPKK-IN-202608-002",
                recipientPerson = "Prihatini Endah Yulia M. (Bendahara Kasir)"
            ),

            // Pengeluaran Operasional Bulanan Agustus 2026
            TransactionEntity(
                id = 11,
                title = "Gaji Petugas Kebersihan / Angkut Sampah (Pak Udin)",
                amount = 1_200_000L,
                type = TransactionType.PENGELUARAN,
                category = TransactionCategory.GAJI_KEBERSIHAN,
                citizenId = null,
                citizenName = "Pak Udin (Petugas)",
                month = 8,
                year = 2026,
                dateMillis = now - (10 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "EXP-202608-001",
                notes = "Gaji operasional kebersihan sampah bulanan",
                syncId = "TX-202608-EXP01",
                isPettyCash = false
            ),
            TransactionEntity(
                id = 12,
                title = "Tunjangan Security Jaga Malam & Pos Ronda (Pak Slamet)",
                amount = 1_500_000L,
                type = TransactionType.PENGELUARAN,
                category = TransactionCategory.TUNJANGAN_SECURITY,
                citizenId = null,
                citizenName = "Pak Slamet (Satpam)",
                month = 8,
                year = 2026,
                dateMillis = now - (9 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "EXP-202608-002",
                notes = "Tunjangan keamanan & patroli shift malam",
                syncId = "TX-202608-EXP02",
                isPettyCash = false
            ),
            // Pengeluaran Kas Kecil (BPKK 001, 002, 003, 004)
            TransactionEntity(
                id = 13,
                title = "Pemeliharaan Fasum - Pompa Air & Kran Balai RT",
                amount = 180_000L,
                type = TransactionType.PENGELUARAN,
                category = TransactionCategory.PEMELIHARAAN_FASUM,
                citizenId = null,
                citizenName = "Toko Bangunan Berkat",
                month = 8,
                year = 2026,
                dateMillis = now - (6 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "EXP-202608-003",
                notes = "Ganti kran 2 unit dan lem pipa fasilitas umum",
                syncId = "TX-202608-EXP03",
                isPettyCash = true,
                bpkkNumber = "BPKK-202608-001",
                recipientPerson = "Toko Bangunan Berkat (Bpk. Yanto)"
            ),
            TransactionEntity(
                id = 14,
                title = "Operasional CCTV - Maintenance & Server Cloud",
                amount = 250_000L,
                type = TransactionType.PENGELUARAN,
                category = TransactionCategory.OPERASIONAL_CCTV,
                citizenId = null,
                citizenName = "Vendor CCTV Lingkungan",
                month = 8,
                year = 2026,
                dateMillis = now - (5 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TRANSFER_BCA,
                receiptNumber = "EXP-202608-004",
                notes = "Biaya cloud recording & servis kamera gang",
                syncId = "TX-202608-EXP04",
                isPettyCash = false
            ),
            TransactionEntity(
                id = 15,
                title = "Operasional ATK & Percetakaan - Kertas Form & Kwitansi",
                amount = 120_000L,
                type = TransactionType.PENGELUARAN,
                category = TransactionCategory.OPERASIONAL_ATK,
                citizenId = null,
                citizenName = "Fotokopi & Percetakan Kita",
                month = 8,
                year = 2026,
                dateMillis = now - (4 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "EXP-202608-005",
                notes = "Cetak buku tanda terima dan formulir warga",
                syncId = "TX-202608-EXP05",
                isPettyCash = true,
                bpkkNumber = "BPKK-202608-002",
                recipientPerson = "Percetakan Kita (Sdr. Hendra)"
            ),
            TransactionEntity(
                id = 16,
                title = "Konsumsi Rapat & Acara RT Persiapan 17-an",
                amount = 275_000L,
                type = TransactionType.PENGELUARAN,
                category = TransactionCategory.KONSUMSI_RAPAT,
                citizenId = null,
                citizenName = "Ibu-ibu Dawis RT",
                month = 8,
                year = 2026,
                dateMillis = now - (2 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "EXP-202608-006",
                notes = "Snack, kopi, dan air mineral rapat pengurus",
                syncId = "TX-202608-EXP06",
                isPettyCash = true,
                bpkkNumber = "BPKK-202608-003",
                recipientPerson = "Ibu-ibu Dawis RT 04"
            ),
            TransactionEntity(
                id = 17,
                title = "Pengeluaran Lainnya - Lampu Penerangan & Cat Pos",
                amount = 150_000L,
                type = TransactionType.PENGELUARAN,
                category = TransactionCategory.PENGELUARAN_LAINNYA,
                citizenId = null,
                citizenName = "Toko Listrik Terang",
                month = 8,
                year = 2026,
                dateMillis = now - (1 * dayMillis),
                recordedBy = "Bendahara RT - Prihatini Endah Y. M.",
                paymentMethod = PaymentMethod.TUNAI,
                receiptNumber = "EXP-202608-007",
                notes = "Beli lampu LED PJU dan perlengkapan pos",
                syncId = "TX-202608-EXP07",
                isPettyCash = true,
                bpkkNumber = "BPKK-202608-004",
                recipientPerson = "Toko Listrik Terang (Bpk. Mamat)"
            )
        )
    }

    fun getInitialAnnouncements(): List<AnnouncementEntity> = listOf(
        AnnouncementEntity(
            id = 1,
            title = "Transparansi Arus Kas RT Periode Agustus 2026",
            content = "Laporan kas per tanggal berjalan telah diperbarui oleh Bendahara. Warga dipersilakan memeriksa status iuran dan rincian pengeluaran operasional di aplikasi.",
            authorRole = "Ketua RT & Bendahara",
            dateMillis = System.currentTimeMillis() - 86_400_000L,
            priority = "KEUANGAN"
        ),
        AnnouncementEntity(
            id = 2,
            title = "Kerja Bakti & Perapian Fasum Lingkungan",
            content = "Dihimbau kepada seluruh warga dan pemilik usaha untuk berpartisipasi pada kerja bakti hari Minggu pukul 07.00 WIB di Balai RT.",
            authorRole = "Sekretaris RT",
            dateMillis = System.currentTimeMillis() - 2 * 86_400_000L,
            priority = "INFO"
        )
    )
}
