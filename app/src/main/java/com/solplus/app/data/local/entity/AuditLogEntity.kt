package com.solplus.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.solplus.app.domain.model.DoseStatus
import java.time.LocalDateTime

@Entity(
    tableName = "audit_logs",
    foreignKeys = [
        ForeignKey(
            entity = DoseOccurrenceEntity::class,
            parentColumns = ["id"],
            childColumns = ["doseOccurrenceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["doseOccurrenceId"]),
        Index(value = ["timestamp"])
    ]
)
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val doseOccurrenceId: Long,
    val previousStatus: DoseStatus?,
    val newStatus: DoseStatus,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val reason: String
)
