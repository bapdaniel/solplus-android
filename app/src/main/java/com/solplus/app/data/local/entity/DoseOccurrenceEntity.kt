package com.solplus.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.solplus.app.domain.model.DoseStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(
    tableName = "dose_occurrences",
    foreignKeys = [
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["medicationId", "scheduledDate", "scheduledTime"], unique = true),
        Index(value = ["scheduledDate"])
    ]
)
data class DoseOccurrenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicationId: Long,
    val scheduledDate: LocalDate,
    val scheduledTime: LocalTime,
    val status: DoseStatus = DoseStatus.SCHEDULED,
    val confirmedAt: LocalDateTime? = null,
    val postponedUntil: LocalDateTime? = null,
    val notes: String? = null,
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
