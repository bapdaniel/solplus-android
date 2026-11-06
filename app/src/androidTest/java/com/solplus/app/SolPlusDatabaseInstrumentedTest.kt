package com.solplus.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.solplus.app.data.local.SolPlusDatabase
import com.solplus.app.data.local.entity.AuditLogEntity
import com.solplus.app.data.local.entity.DoseOccurrenceEntity
import com.solplus.app.data.local.entity.MedicationEntity
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.domain.model.MedicationType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class SolPlusDatabaseInstrumentedTest {

    private lateinit var db: SolPlusDatabase
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, SolPlusDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    // 1. Confirmar medicamento via DAO isolado
    @Test
    fun testConfirmMedication_updatesStatusToConfirmed() = runBlocking {
        val medId = db.medicationDao().insert(
            MedicationEntity(
                name = "Remédio da Pressão",
                type = MedicationType.ORAL,
                scheduledTime = LocalTime.of(8, 0),
                dosage = "1 comprimido",
                instructions = "Tomar com água",
                period = "Manhã"
            )
        )

        val occId = db.doseOccurrenceDao().insert(
            DoseOccurrenceEntity(
                medicationId = medId,
                scheduledDate = LocalDate.now(),
                scheduledTime = LocalTime.of(8, 0),
                status = DoseStatus.PENDING
            )
        )

        val now = LocalDateTime.now()
        val rows = db.doseOccurrenceDao().confirmIfNotConfirmed(
            id = occId,
            confirmedStatus = DoseStatus.CONFIRMED,
            confirmedAt = now,
            updatedAt = now,
            notes = "Confirmado no teste instrumentado"
        )

        assertEquals("Deve ter atualizado 1 linha com sucesso", 1, rows)
        val loaded = db.doseOccurrenceDao().getById(occId)
        assertEquals("Status deve ser CONFIRMED", DoseStatus.CONFIRMED, loaded?.occurrence?.status)
    }

    // 2. Impedir confirmações duplicadas (idempotência)
    @Test
    fun testPreventDuplicateConfirmations() = runBlocking {
        val medId = db.medicationDao().insert(
            MedicationEntity(
                name = "Colírio oftálmico",
                type = MedicationType.EYE_DROP,
                scheduledTime = LocalTime.of(8, 0),
                dosage = "1 gota em cada olho",
                instructions = "Ambos os olhos",
                period = "Manhã"
            )
        )

        val occId = db.doseOccurrenceDao().insert(
            DoseOccurrenceEntity(
                medicationId = medId,
                scheduledDate = LocalDate.now(),
                scheduledTime = LocalTime.of(8, 0),
                status = DoseStatus.PENDING
            )
        )

        val now = LocalDateTime.now()
        val rows1 = db.doseOccurrenceDao().confirmIfNotConfirmed(
            id = occId,
            confirmedStatus = DoseStatus.CONFIRMED,
            confirmedAt = now,
            updatedAt = now,
            notes = "1a vez"
        )
        assertEquals(1, rows1)

        val rows2 = db.doseOccurrenceDao().confirmIfNotConfirmed(
            id = occId,
            confirmedStatus = DoseStatus.CONFIRMED,
            confirmedAt = now.plusMinutes(1),
            updatedAt = now.plusMinutes(1),
            notes = "2a vez duplicada"
        )
        assertEquals("Tentativa duplicada deve afetar 0 linhas", 0, rows2)
    }

    // 3. Persistir os registros e histórico no Room
    @Test
    fun testPersistAuditAndDoseRecords() = runBlocking {
        val medId = db.medicationDao().insert(
            MedicationEntity(
                name = "Colírio Noturno",
                type = MedicationType.EYE_DROP,
                scheduledTime = LocalTime.of(20, 0),
                dosage = "1 gota em cada olho",
                instructions = "Noite",
                period = "Noite"
            )
        )

        val occId = db.doseOccurrenceDao().insert(
            DoseOccurrenceEntity(
                medicationId = medId,
                scheduledDate = LocalDate.now(),
                scheduledTime = LocalTime.of(20, 0),
                status = DoseStatus.CONFIRMED,
                confirmedAt = LocalDateTime.now()
            )
        )

        db.auditLogDao().insert(
            AuditLogEntity(
                doseOccurrenceId = occId,
                previousStatus = DoseStatus.PENDING,
                newStatus = DoseStatus.CONFIRMED,
                timestamp = LocalDateTime.now(),
                reason = "Auditoria de persistência"
            )
        )

        val logs = db.auditLogDao().getAllList()
        assertTrue("Deve haver pelo menos 1 log de auditoria persistido", logs.isNotEmpty())
        assertEquals("Auditoria de persistência", logs.firstOrNull { it.doseOccurrenceId == occId }?.reason)
    }

    // 4. Navegar no calendário e integridade de datas
    @Test
    fun testCalendarQueryIntegrity() = runBlocking {
        val targetDate = LocalDate.of(2026, 11, 2)
        val countBefore = db.doseOccurrenceDao().countByDate(targetDate)
        assertEquals(0, countBefore)

        val medId = db.medicationDao().insert(
            MedicationEntity(
                name = "Remédio",
                type = MedicationType.ORAL,
                scheduledTime = LocalTime.of(8, 0),
                dosage = "1 comp",
                instructions = "Com água",
                period = "Manhã"
            )
        )

        db.doseOccurrenceDao().insert(
            DoseOccurrenceEntity(
                medicationId = medId,
                scheduledDate = targetDate,
                scheduledTime = LocalTime.of(8, 0),
                status = DoseStatus.PENDING
            )
        )

        val countAfter = db.doseOccurrenceDao().countByDate(targetDate)
        assertEquals(1, countAfter)
    }

    // 5. Integridade na exportação de histórico completo
    @Test
    fun testHistoryListExportIntegrity() = runBlocking {
        val list = db.doseOccurrenceDao().getAllHistoryList()
        assertTrue("Lista deve iniciar vazia no banco isolado", list.isEmpty())
    }
}
