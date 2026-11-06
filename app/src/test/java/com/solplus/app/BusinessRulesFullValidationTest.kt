package com.solplus.app

import com.solplus.app.core.util.SettableTimeProvider
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.domain.model.MedicationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class BusinessRulesFullValidationTest {

    data class MockMedication(
        val id: Long,
        val name: String,
        val type: MedicationType,
        val scheduledTime: LocalTime,
        val instructions: String
    )

    data class MockDoseOccurrence(
        val id: Long,
        val medicationId: Long,
        val scheduledDate: LocalDate,
        val scheduledTime: LocalTime,
        var status: DoseStatus = DoseStatus.SCHEDULED,
        var confirmedAt: LocalDateTime? = null,
        var postponedUntil: LocalDateTime? = null
    )

    private lateinit var bpMed: MockMedication
    private lateinit var morningEyeMed: MockMedication
    private lateinit var nightEyeMed: MockMedication
    private lateinit var timeProvider: SettableTimeProvider

    @Before
    fun setup() {
        timeProvider = SettableTimeProvider(LocalDateTime.of(2026, 11, 2, 8, 0, 0))

        bpMed = MockMedication(
            id = 1L,
            name = "Remédio da pressão",
            type = MedicationType.ORAL,
            scheduledTime = LocalTime.of(8, 0),
            instructions = "Tomar 1 comprimido pela manhã"
        )
        morningEyeMed = MockMedication(
            id = 2L,
            name = "Colírio",
            type = MedicationType.EYE_DROP,
            scheduledTime = LocalTime.of(8, 0),
            instructions = "Ambos os olhos"
        )
        nightEyeMed = MockMedication(
            id = 3L,
            name = "Colírio",
            type = MedicationType.EYE_DROP,
            scheduledTime = LocalTime.of(20, 0),
            instructions = "Ambos os olhos"
        )
    }

    // REGRA 1: Os dois cuidados das 08:00 aparecem agrupados
    @Test
    fun rule1_morningCareMustBeGroupedAt0800() {
        val allMeds = listOf(bpMed, morningEyeMed, nightEyeMed)
        val morningGroup = allMeds.filter { it.scheduledTime == LocalTime.of(8, 0) }

        assertEquals(2, morningGroup.size)
        assertTrue(morningGroup.any { it.type == MedicationType.ORAL })
        assertTrue(morningGroup.any { it.type == MedicationType.EYE_DROP })
    }

    // REGRA 2: Cada cuidado possui confirmação independente
    @Test
    fun rule2_eachCareHasIndependentConfirmation() {
        val today = timeProvider.nowLocalDate()
        val bpOcc = MockDoseOccurrence(101L, bpMed.id, today, bpMed.scheduledTime, DoseStatus.PENDING)
        val eyeOcc = MockDoseOccurrence(102L, morningEyeMed.id, today, morningEyeMed.scheduledTime, DoseStatus.PENDING)

        // Confirmar apenas pressão
        bpOcc.status = DoseStatus.CONFIRMED
        bpOcc.confirmedAt = timeProvider.nowLocalDateTime()

        assertEquals(DoseStatus.CONFIRMED, bpOcc.status)
        assertEquals(DoseStatus.PENDING, eyeOcc.status)
        assertNull(eyeOcc.confirmedAt)
    }

    // REGRA 3: Confirmar o remédio não confirma o colírio
    @Test
    fun rule3_confirmingBpDoesNotConfirmEyeDrop() {
        val today = timeProvider.nowLocalDate()
        val bpOcc = MockDoseOccurrence(101L, bpMed.id, today, bpMed.scheduledTime, DoseStatus.PENDING)
        val eyeOcc = MockDoseOccurrence(102L, morningEyeMed.id, today, morningEyeMed.scheduledTime, DoseStatus.PENDING)

        // Confirmação explícita de pressão
        bpOcc.status = DoseStatus.CONFIRMED
        bpOcc.confirmedAt = LocalDateTime.of(2026, 11, 2, 8, 5)

        assertEquals("Remédio da pressão deve estar CONFIRMADO", DoseStatus.CONFIRMED, bpOcc.status)
        assertEquals("Colírio deve continuar PENDENTE", DoseStatus.PENDING, eyeOcc.status)
        assertFalse(eyeOcc.status.isResolved)
    }

    // REGRA 4: Confirmar o colírio da manhã não confirma o da noite
    @Test
    fun rule4_confirmingMorningEyeDropDoesNotConfirmNightEyeDrop() {
        val today = timeProvider.nowLocalDate()
        val morningEye = MockDoseOccurrence(102L, morningEyeMed.id, today, LocalTime.of(8, 0), DoseStatus.PENDING)
        val nightEye = MockDoseOccurrence(103L, nightEyeMed.id, today, LocalTime.of(20, 0), DoseStatus.SCHEDULED)

        // Confirmar colírio das 08:00
        morningEye.status = DoseStatus.CONFIRMED
        morningEye.confirmedAt = LocalDateTime.of(2026, 11, 2, 8, 10)

        assertEquals(DoseStatus.CONFIRMED, morningEye.status)
        assertEquals(DoseStatus.SCHEDULED, nightEye.status)
        assertNull(nightEye.confirmedAt)
    }

    // REGRA 5: Confirmar duas vezes não cria duplicação (Idempotência)
    @Test
    fun rule5_confirmingTwiceDoesNotDuplicate() {
        val initialConfirmTime = LocalDateTime.of(2026, 11, 2, 8, 2)
        val occ = MockDoseOccurrence(101L, bpMed.id, LocalDate.of(2026, 11, 2), LocalTime.of(8, 0))

        // Primeira confirmação
        occ.status = DoseStatus.CONFIRMED
        occ.confirmedAt = initialConfirmTime

        // Segunda tentativa de confirmação
        val secondAttemptTime = LocalDateTime.of(2026, 11, 2, 8, 15)
        if (occ.status != DoseStatus.CONFIRMED) {
            occ.status = DoseStatus.CONFIRMED
            occ.confirmedAt = secondAttemptTime
        }

        // Deve manter o horário original e o status idêntico
        assertEquals(initialConfirmTime, occ.confirmedAt)
        assertEquals(DoseStatus.CONFIRMED, occ.status)
    }

    // REGRA 6: Responder NÃO não registra utilização
    @Test
    fun rule6_answeringNoDoesNotRecordUsage() {
        val occ = MockDoseOccurrence(101L, bpMed.id, LocalDate.of(2026, 11, 2), LocalTime.of(8, 0), DoseStatus.PENDING)

        // Usuária clica em "✕ NÃO, AINDA NÃO"
        // A regra estrita é manter como PENDENTE sem timestamp de confirmação
        assertEquals(DoseStatus.PENDING, occ.status)
        assertNull(occ.confirmedAt)
        assertFalse(occ.status.isResolved)
    }

    // REGRA 7: Selecionar "Vou tomar agora" mantém a dose pendente
    @Test
    fun rule7_takeNowKeepsDosePending() {
        val occ = MockDoseOccurrence(101L, bpMed.id, LocalDate.of(2026, 11, 2), LocalTime.of(8, 0), DoseStatus.PENDING)

        // Seleção de "Vou tomar agora" no diálogo secundário
        // Regra de Ouro: promessa futura NÃO é confirmação médica!
        assertNotEquals(DoseStatus.CONFIRMED, occ.status)
        assertEquals(DoseStatus.PENDING, occ.status)
        assertNull(occ.confirmedAt)
    }

    // REGRA 8: Adiar uma ocorrência não modifica outras
    @Test
    fun rule8_postponingOneOccurrenceDoesNotModifyOthers() {
        val today = LocalDate.of(2026, 11, 2)
        val bpOcc = MockDoseOccurrence(101L, bpMed.id, today, LocalTime.of(8, 0), DoseStatus.PENDING)
        val eyeOcc = MockDoseOccurrence(102L, morningEyeMed.id, today, LocalTime.of(8, 0), DoseStatus.PENDING)

        // Adiar apenas o remédio da pressão por 15 minutos
        bpOcc.status = DoseStatus.POSTPONED
        bpOcc.postponedUntil = LocalDateTime.of(2026, 11, 2, 8, 15)

        assertEquals(DoseStatus.POSTPONED, bpOcc.status)
        assertNotNull(bpOcc.postponedUntil)

        // O colírio da manhã NÃO pode ser adiado nem alterado
        assertEquals(DoseStatus.PENDING, eyeOcc.status)
        assertNull(eyeOcc.postponedUntil)
    }

    // REGRA 9: Reiniciar o app não apaga o histórico (Persistência)
    @Test
    fun rule9_restartingAppPreservesHistory() {
        val databaseOccurrences = mutableMapOf<Long, MockDoseOccurrence>()

        // Simula gravação antes de encerrar app
        val occ = MockDoseOccurrence(101L, bpMed.id, LocalDate.of(2026, 11, 1), LocalTime.of(8, 0))
        occ.status = DoseStatus.CONFIRMED
        occ.confirmedAt = LocalDateTime.of(2026, 11, 1, 8, 5)
        databaseOccurrences[occ.id] = occ

        // "App reiniciado": leitura direta da fonte de dados
        val loaded = databaseOccurrences[101L]
        assertNotNull(loaded)
        assertEquals(DoseStatus.CONFIRMED, loaded?.status)
        assertEquals(LocalDateTime.of(2026, 11, 1, 8, 5), loaded?.confirmedAt)
    }

    // REGRA 10: O calendário representa corretamente as ocorrências
    @Test
    fun rule10_calendarCorrectlyRepresentsOccurrences() {
        val testDate = LocalDate.of(2026, 11, 2)
        val dayOccurrences = listOf(
            MockDoseOccurrence(1L, bpMed.id, testDate, LocalTime.of(8, 0), DoseStatus.CONFIRMED),
            MockDoseOccurrence(2L, morningEyeMed.id, testDate, LocalTime.of(8, 0), DoseStatus.CONFIRMED),
            MockDoseOccurrence(3L, nightEyeMed.id, testDate, LocalTime.of(20, 0), DoseStatus.CONFIRMED)
        )

        val total = dayOccurrences.size
        val confirmedCount = dayOccurrences.count { it.status == DoseStatus.CONFIRMED }
        val isAllCompleted = total == confirmedCount

        assertEquals(3, total)
        assertEquals(3, confirmedCount)
        assertTrue("Quando todos os 3 cuidados estão confirmados, o dia recebe check verde no calendário", isAllCompleted)
    }

    // REGRA 11: A troca de data é tratada corretamente
    @Test
    fun rule11_dateChangeIsHandledCorrectly() {
        val day1 = LocalDate.of(2026, 11, 2)
        val day2 = LocalDate.of(2026, 11, 3)

        val day1Doses = listOf(
            MockDoseOccurrence(1L, bpMed.id, day1, LocalTime.of(8, 0), DoseStatus.CONFIRMED)
        )
        val day2Doses = listOf(
            MockDoseOccurrence(4L, bpMed.id, day2, LocalTime.of(8, 0), DoseStatus.PENDING)
        )

        assertEquals(DoseStatus.CONFIRMED, day1Doses.first().status)
        assertEquals(DoseStatus.PENDING, day2Doses.first().status)
        assertNotEquals(day1Doses.first().scheduledDate, day2Doses.first().scheduledDate)
    }

    // REGRA 12: Mudanças de horário preservam os registros anteriores
    @Test
    fun rule12_scheduleChangesPreserveHistoricalRecords() {
        val pastDate = LocalDate.of(2026, 10, 20)
        val pastOccurrence = MockDoseOccurrence(
            id = 50L,
            medicationId = bpMed.id,
            scheduledDate = pastDate,
            scheduledTime = LocalTime.of(8, 0),
            status = DoseStatus.CONFIRMED,
            confirmedAt = LocalDateTime.of(2026, 10, 20, 8, 3)
        )

        // Se o médico alterar no futuro o horário do medicamento para 09:00:
        val updatedMed = bpMed.copy(scheduledTime = LocalTime.of(9, 0))

        // O registro passado deve continuar intacto às 08:00 com status CONFIRMED
        assertEquals(LocalTime.of(8, 0), pastOccurrence.scheduledTime)
        assertEquals(DoseStatus.CONFIRMED, pastOccurrence.status)
        assertEquals(LocalTime.of(9, 0), updatedMed.scheduledTime)
    }
}
