package com.solplus.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.solplus.app.domain.model.MedicationType
import java.time.LocalTime

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: MedicationType,
    val scheduledTime: LocalTime,
    val dosage: String,
    val instructions: String,
    val period: String, // "MANHÃ" ou "NOITE"
    val isActive: Boolean = true
)
