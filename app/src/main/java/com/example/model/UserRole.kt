package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.ui.graphics.vector.ImageVector

enum class UserRole(
    val title: String,
    val defaultName: String,
    val description: String,
    val icon: ImageVector,
    val badgeLabel: String
) {
    KETUA_RT(
        title = "Ketua RT",
        defaultName = "Nohan Pancono",
        description = "Pengawasan menyeluruh arus kas, verifikasi & persetujuan laporan, evaluasi saldo bulanan",
        icon = Icons.Default.AdminPanelSettings,
        badgeLabel = "Pengawasan & Approval"
    ),
    SEKRETARIS_RT(
        title = "Sekretaris RT",
        defaultName = "Muhammad Rijaldi Imam Mustarih",
        description = "Manajemen data warga & pelaku usaha, administrasi kepesertaan iuran, monitoring keaktifan warga",
        icon = Icons.Default.Assignment,
        badgeLabel = "Data Warga & Administrasi"
    ),
    BENDAHARA_RT(
        title = "Bendahara RT",
        defaultName = "Prihatini Endah Yulia Maretiasari",
        description = "Pencatatan kas masuk & keluar, rekapitulasi laporan bulanan otomatis, cetak kuitansi iuran warga",
        icon = Icons.Default.AccountBalance,
        badgeLabel = "Keuangan & Kas"
    );

    val canRecordTransaction: Boolean
        get() = this == BENDAHARA_RT || this == KETUA_RT

    val canApproveReport: Boolean
        get() = this == KETUA_RT

    val canManageCitizens: Boolean
        get() = this == SEKRETARIS_RT || this == BENDAHARA_RT || this == KETUA_RT

    val canBroadcastAnnouncement: Boolean
        get() = this == KETUA_RT || this == SEKRETARIS_RT

    val canEditSyncCode: Boolean
        get() = this == KETUA_RT

    val canEditUserProfiles: Boolean
        get() = this == KETUA_RT

    val canManageAllPasswords: Boolean
        get() = this == KETUA_RT
}

