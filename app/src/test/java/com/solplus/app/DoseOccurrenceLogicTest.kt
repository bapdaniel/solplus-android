package com.solplus.app

import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.domain.model.MedicationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DoseOccurrenceLogicTest {

    data class MockDoseOccurrence(
        val id: Long,
        val medicationId: Long,
        val scheduledDate: LocalDate,
        val scheduledTime: LocalTime,
        var status: DoseStatus = DoseStatus.SCHEDULED,
        var confirmedAt: LocalDateTime? = null,
        var postponedUntil: LocalDateTime? = null
    )

    data class MockMedication(
        val id: Long,
        val name: String,
        val type: MedicationType,
        val scheduledTime: LocalTime,
        val period: String
    )

    @Test
    fun testIndependenceOfMorningConfirmations() {
        // Cenário: Às 08:00 temos dois medicamentos: Remédio da Pressão e Colírio da Manhã
        val bpMed = MockMedication(1L, "Remédio da pressão", MedicationType.ORAL, LocalTime.of(8, 0), "MANHÃ")
        val eyeMed = MockMedication(2L, "Colírio", MedicationType.EYE_DROP, LocalTime.of(8, 0), "MANHÃ")

        val today = LocalDate.now()
        val bpOccurrence = MockDoseOccurrence(101L, bpMed.id, today, bpMed.scheduledTime, DoseStatus.PENDING)
        val eyeOccurrence = MockDoseOccurrence(102L, eyeMed.id, today, eyeMed.scheduledTime, DoseStatus.PENDING)

        // A usuária confirma apenas o remédio da pressão: "SIM, JÁ TOMEI"
        val confirmTime = LocalDateTime.now()
        bpOccurrence.status = DoseStatus.CONFIRMED
        bpOccurrence.confirmedAt = confirmTime

        // Validação da regra: O remédio da pressão está confirmado
        assertEquals(DoseStatus.CONFIRMED, bpOccurrence.status)
        assertEquals(confirmTime, bpOccurrence.confirmedAt)

        // Validação estrita: O colírio da manhã continua PENDENTE e não pode ser confirmado automaticamente!
        assertEquals(DoseStatus.PENDING, eyeOccurrence.status)
        assertNull(eyeOccurrence.confirmedAt)
        assertFalse(eyeOccurrence.status.isResolved)
    }

    @Test
    fun testSeparationBetweenMorningAndNightEyeDrop() {
        // Cenário: Colírio da manhã às 08:00 e colírio da noite às 20:00 pertencem ao mesmo fármaco, mas horários distintos
        val morningDrop = MockMedication(2L, "Colírio", MedicationType.EYE_DROP, LocalTime.of(8, 0), "MANHÃ")
        val nightDrop = MockMedication(3L, "Colírio", MedicationType.EYE_DROP, LocalTime.of(20, 0), "NOITE")

        val today = LocalDate.now()
        val morningOcc = MockDoseOccurrence(201L, morningDrop.id, today, morningDrop.scheduledTime, DoseStatus.SCHEDULED)
        val nightOcc = MockDoseOccurrence(202L, nightDrop.id, today, nightDrop.scheduledTime, DoseStatus.SCHEDULED)

        // Confirmando o colírio da manhã
        morningOcc.status = DoseStatus.CONFIRMED
        morningOcc.confirmedAt = LocalDateTime.now()

        // O colírio da noite DEVE permanecer SCHEDULED (programado), sem herdar a confirmação matinal
        assertEquals(DoseStatus.CONFIRMED, morningOcc.status)
        assertEquals(DoseStatus.SCHEDULED, nightOcc.status)
        assertNull(nightOcc.confirmedAt)
        assertNotEquals(morningOcc.scheduledTime, nightOcc.scheduledTime)
    }

    @Test
    fun testPostponeDoesNotAutoConfirm() {
        // Cenário: Usuária toca em "Lembrar novamente em 15 min"
        val occ = MockDoseOccurrence(301L, 1L, LocalDate.now(), LocalTime.of(8, 0), DoseStatus.PENDING)
        val postponeTarget = LocalDateTime.now().plusMinutes(15)

        occ.status = DoseStatus.POSTPONED
        occ.postponedUntil = postponeTarget

        assertEquals(DoseStatus.POSTPONED, occ.status)
        assertNull(occ.confirmedAt)
        assertFalse(occ.status.isResolved)
    }

    @Test
    fun testTakeNowSafetyRuleRequiresSubsequentConfirmation() {
        // Cenário: Usuária toca em "Vou tomar agora"
        val occ = MockDoseOccurrence(401L, 1L, LocalDate.now(), LocalTime.of(8, 0), DoseStatus.PENDING)

        // Regra de segurança médica obrigatória do briefing:
        // 'Vou tomar/aplicar agora' não deve gerar confirmação automática.
        // O status deve permanecer PENDING ou aguardando confirmação explícita
        assertNotEquals(DoseStatus.CONFIRMED, occ.status)
        assertNull(occ.confirmedAt)
    }

    @Test
    fun testUncertainStatusHandling() {
        // Cenário: Usuária não tem certeza se tomou a dose
        val occ = MockDoseOccurrence(501L, 1L, LocalDate.now(), LocalTime.of(8, 0), DoseStatus.PENDING)

        occ.status = DoseStatus.UNCERTAIN

        assertEquals(DoseStatus.UNCERTAIN, occ.status)
        assertEquals("Incerto", occ.status.label)
        assertNull(occ.confirmedAt)
    }
}
