package com.solplus.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.solplus.app.data.local.entity.DoseOccurrenceEntity
import com.solplus.app.data.local.entity.DoseWithMedication
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import com.solplus.app.domain.model.DoseStatus

@Dao
interface DoseOccurrenceDao {
    @Transaction
    @Query("SELECT * FROM dose_occurrences WHERE scheduledDate = :date ORDER BY scheduledTime ASC, id ASC")
    fun getByDate(date: LocalDate): Flow<List<DoseWithMedication>>

    @Transaction
    @Query("SELECT * FROM dose_occurrences WHERE scheduledDate = :date ORDER BY scheduledTime ASC, id ASC")
    suspend fun getByDateList(date: LocalDate): List<DoseWithMedication>

    @Transaction
    @Query("SELECT * FROM dose_occurrences WHERE scheduledDate = :date AND scheduledTime = :time ORDER BY id ASC")
    suspend fun getByDateAndTime(date: LocalDate, time: LocalTime): List<DoseWithMedication>

    @Transaction
    @Query("SELECT * FROM dose_occurrences WHERE scheduledDate BETWEEN :startDate AND :endDate ORDER BY scheduledDate ASC, scheduledTime ASC")
    fun getByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DoseWithMedication>>

    @Transaction
    @Query("SELECT * FROM dose_occurrences WHERE medicationId = :medicationId ORDER BY scheduledDate DESC, scheduledTime DESC")
    fun getByMedication(medicationId: Long): Flow<List<DoseWithMedication>>

    @Transaction
    @Query("SELECT * FROM dose_occurrences WHERE id = :id")
    suspend fun getById(id: Long): DoseWithMedication?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(occurrences: List<DoseOccurrenceEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(occurrence: DoseOccurrenceEntity): Long

    @Update
    suspend fun update(occurrence: DoseOccurrenceEntity)

    @Query(
        """
        UPDATE dose_occurrences
        SET status = :confirmedStatus,
            confirmedAt = :confirmedAt,
            updatedAt = :updatedAt,
            notes = COALESCE(:notes, notes)
        WHERE id = :id AND status != :confirmedStatus
        """
    )
    suspend fun confirmIfNotConfirmed(
        id: Long,
        confirmedStatus: DoseStatus = DoseStatus.CONFIRMED,
        confirmedAt: LocalDateTime,
        updatedAt: LocalDateTime,
        notes: String?
    ): Int

    @Query("SELECT COUNT(*) FROM dose_occurrences WHERE scheduledDate = :date")
    suspend fun countByDate(date: LocalDate): Int

    @Transaction
    @Query("SELECT * FROM dose_occurrences ORDER BY scheduledDate DESC, scheduledTime DESC")
    fun getAllHistory(): Flow<List<DoseWithMedication>>

    @Transaction
    @Query("SELECT * FROM dose_occurrences ORDER BY scheduledDate ASC, scheduledTime ASC")
    suspend fun getAllHistoryList(): List<DoseWithMedication>
}
