package com.solplus.app.data.repository

import com.solplus.app.data.local.SolPlusDatabase
import com.solplus.app.data.local.entity.AuditLogEntity
import com.solplus.app.data.local.entity.DoseOccurrenceEntity
import com.solplus.app.data.local.entity.DoseWithMedication
import com.solplus.app.data.local.entity.MedicationEntity
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.core.util.DefaultTimeProvider
import com.solplus.app.core.util.TimeProvider
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

class MedicationRepository(
    private val database: SolPlusDatabase,
    private val timeProvider: TimeProvider = DefaultTimeProvider
) {
    private val medDao = database.medicationDao()
    private val doseDao = database.doseOccurrenceDao()
    private val auditDao = database.auditLogDao()

    suspend fun ensureOccurrencesForDate(date: LocalDate) {
        val existingCount = doseDao.countByDate(date)
        if (existingCount == 0) {
            val activeMeds = medDao.getAllActiveList()
            if (activeMeds.isNotEmpty()) {
                val occurrences = activeMeds.map { med ->
                    val initialStatus = if (date.isBefore(timeProvider.nowLocalDate())) {
                        DoseStatus.NOT_CONFIRMED
                    } else {
                        DoseStatus.SCHEDULED
                    }
                    DoseOccurrenceEntity(
                        medicationId = med.id,
                        scheduledDate = date,
                        scheduledTime = med.scheduledTime,
                        status = initialStatus
                    )
                }
                doseDao.insertAll(occurrences)
            }
        }
    }

    fun getDosesForDate(date: LocalDate): Flow<List<DoseWithMedication>> {
        return doseDao.getByDate(date)
    }

    suspend fun getDosesForDateList(date: LocalDate): List<DoseWithMedication> {
        ensureOccurrencesForDate(date)
        return doseDao.getByDateList(date)
    }

    suspend fun getDosesForDateAndTime(date: LocalDate, time: LocalTime): List<DoseWithMedication> {
        ensureOccurrencesForDate(date)
        return doseDao.getByDateAndTime(date, time)
    }

    fun getDosesForMonth(yearMonth: YearMonth): Flow<List<DoseWithMedication>> {
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        return doseDao.getByDateRange(startDate, endDate)
    }

    fun getDosesForMedication(medicationId: Long): Flow<List<DoseWithMedication>> {
        return doseDao.getByMedication(medicationId)
    }

    fun getAllActiveMedications(): Flow<List<MedicationEntity>> {
        return medDao.getAllActive()
    }

    suspend fun confirmDose(
        occurrenceId: Long,
        confirmedAt: LocalDateTime = timeProvider.nowLocalDateTime(),
        notes: String? = null
    ) {
        val current = doseDao.getById(occurrenceId) ?: return
        if (current.occurrence.status == DoseStatus.CONFIRMED) return
        val prevStatus = current.occurrence.status
        val now = timeProvider.nowLocalDateTime()
        val updatedRows = doseDao.confirmIfNotConfirmed(
            id = occurrenceId,
            confirmedAt = confirmedAt,
            updatedAt = now,
            notes = notes
        )
        if (updatedRows == 0) return
        auditDao.insert(
            AuditLogEntity(
                doseOccurrenceId = occurrenceId,
                previousStatus = prevStatus,
                newStatus = DoseStatus.CONFIRMED,
                timestamp = now,
                reason = "Confirmação de dose pelo usuário"
            )
        )
    }

    suspend fun postponeDose(occurrenceId: Long, minutes: Long = 15) {
        val current = doseDao.getById(occurrenceId) ?: return
        val prevStatus = current.occurrence.status
        val now = timeProvider.nowLocalDateTime()
        val postponedUntil = now.plusMinutes(minutes)
        val updated = current.occurrence.copy(
            status = DoseStatus.POSTPONED,
            postponedUntil = postponedUntil,
            updatedAt = now
        )
        doseDao.update(updated)
        auditDao.insert(
            AuditLogEntity(
                doseOccurrenceId = occurrenceId,
                previousStatus = prevStatus,
                newStatus = DoseStatus.POSTPONED,
                timestamp = now,
                reason = "Adiado por $minutes minutos"
            )
        )
    }

    suspend fun markUncertain(occurrenceId: Long) {
        val current = doseDao.getById(occurrenceId) ?: return
        val prevStatus = current.occurrence.status
        val now = timeProvider.nowLocalDateTime()
        val updated = current.occurrence.copy(
            status = DoseStatus.UNCERTAIN,
            updatedAt = now
        )
        doseDao.update(updated)
        auditDao.insert(
            AuditLogEntity(
                doseOccurrenceId = occurrenceId,
                previousStatus = prevStatus,
                newStatus = DoseStatus.UNCERTAIN,
                timestamp = now,
                reason = "Usuário registrou incerteza (não sabe se tomou/aplicou)"
            )
        )
    }

    suspend fun markNotTaken(occurrenceId: Long, reason: String = "Não realizado") {
        val current = doseDao.getById(occurrenceId) ?: return
        val prevStatus = current.occurrence.status
        val now = timeProvider.nowLocalDateTime()
        val updated = current.occurrence.copy(
            status = DoseStatus.NOT_TAKEN,
            notes = reason,
            updatedAt = now
        )
        doseDao.update(updated)
        auditDao.insert(
            AuditLogEntity(
                doseOccurrenceId = occurrenceId,
                previousStatus = prevStatus,
                newStatus = DoseStatus.NOT_TAKEN,
                timestamp = now,
                reason = "Registrado como não realizado: $reason"
            )
        )
    }

    suspend fun correctDoseStatus(occurrenceId: Long, newStatus: DoseStatus, reason: String) {
        val current = doseDao.getById(occurrenceId) ?: return
        val prevStatus = current.occurrence.status
        val now = timeProvider.nowLocalDateTime()
        val updated = current.occurrence.copy(
            status = newStatus,
            confirmedAt = if (newStatus == DoseStatus.CONFIRMED) (current.occurrence.confirmedAt ?: now) else null,
            updatedAt = now
        )
        doseDao.update(updated)
        auditDao.insert(
            AuditLogEntity(
                doseOccurrenceId = occurrenceId,
                previousStatus = prevStatus,
                newStatus = newStatus,
                timestamp = now,
                reason = "Correção manual: $reason"
            )
        )
    }

    suspend fun updateMedication(medication: MedicationEntity) {
        medDao.update(medication)
    }

    fun getAllAuditLogs(): Flow<List<AuditLogEntity>> {
        return auditDao.getAll()
    }

    fun getAllHistory(): Flow<List<DoseWithMedication>> {
        return doseDao.getAllHistory()
    }
}
