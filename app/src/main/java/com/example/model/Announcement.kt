package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val authorRole: String = "Pengurus RT",
    val dateMillis: Long = System.currentTimeMillis(),
    val priority: String = "INFO" // INFO, PENTING, KEUANGAN
)
