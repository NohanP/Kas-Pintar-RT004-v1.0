package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CitizenType(val label: String, val defaultFee: Long) {
    WARGA_TETAP("Warga Tetap", 50_000L),
    WARGA_KONTRAK("Warga Kontrak / Kos", 35_000L),
    PELAKU_USAHA("Pelaku Usaha / Toko / Ruko", 100_000L),
    WARUNG_PKL("Warung / PKL Lingkungan", 75_000L)
}

@Entity(tableName = "citizens")
data class CitizenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val houseNumber: String,
    val phone: String = "",
    val type: CitizenType = CitizenType.WARGA_TETAP,
    val monthlyFee: Long = 50_000L,
    val notes: String = "",
    val isActive: Boolean = true,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)
