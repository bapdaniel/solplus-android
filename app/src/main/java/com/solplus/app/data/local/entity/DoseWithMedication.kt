package com.solplus.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DoseWithMedication(
    @Embedded
    val occurrence: DoseOccurrenceEntity,

    @Relation(
        parentColumn = "medicationId",
        entityColumn = "id"
    )
    val medication: MedicationEntity
)
