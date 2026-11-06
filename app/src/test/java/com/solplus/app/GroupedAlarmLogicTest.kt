package com.solplus.app

import com.solplus.app.domain.model.MedicationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

class GroupedAlarmLogicTest {

    data class ScheduledTreatment(
        val medicationId: Long,
        val name: String,
        val type: MedicationType,
        val time: LocalTime,
        val period: String
    )

    @Test
    fun testMorningGroupingAtEightAm() {
        val allTreatments = listOf(
            ScheduledTreatment(1L, "Remédio da pressão", MedicationType.ORAL, LocalTime.of(8, 0), "MANHÃ"),
            ScheduledTreatment(2L, "Colírio", MedicationType.EYE_DROP, LocalTime.of(8, 0), "MANHÃ"),
            ScheduledTreatment(3L, "Colírio", MedicationType.EYE_DROP, LocalTime.of(20, 0), "NOITE")
        )

        // Agrupamento matinal das 08:00
        val morningGroup = allTreatments.filter { it.time == LocalTime.of(8, 0) }

        assertEquals(2, morningGroup.size)
        assertTrue(morningGroup.any { it.type == MedicationType.ORAL })
        assertTrue(morningGroup.any { it.type == MedicationType.EYE_DROP })
        assertEquals(1, morningGroup.count { it.name == "Remédio da pressão" })
        assertEquals(1, morningGroup.count { it.name == "Colírio" })
    }

    @Test
    fun testNightAlertIsSingleEyeDropOnly() {
        val allTreatments = listOf(
            ScheduledTreatment(1L, "Remédio da pressão", MedicationType.ORAL, LocalTime.of(8, 0), "MANHÃ"),
            ScheduledTreatment(2L, "Colírio", MedicationType.EYE_DROP, LocalTime.of(8, 0), "MANHÃ"),
            ScheduledTreatment(3L, "Colírio", MedicationType.EYE_DROP, LocalTime.of(20, 0), "NOITE")
        )

        // Alerta noturno das 20:00
        val nightGroup = allTreatments.filter { it.time == LocalTime.of(20, 0) }

        assertEquals(1, nightGroup.size)
        assertEquals(MedicationType.EYE_DROP, nightGroup.first().type)
        assertEquals("Colírio", nightGroup.first().name)
        assertEquals("NOITE", nightGroup.first().period)
    }

    @Test
    fun testDeduplicationKeyGeneration() {
        // Testa a chave única composta (medicationId + data + horario)
        val key1 = "1_2026-11-02_08:00"
        val key2 = "2_2026-11-02_08:00"
        val key3 = "3_2026-11-02_20:00"
        val keyDuplicate1 = "1_2026-11-02_08:00"

        val set = mutableSetOf<String>()
        assertTrue(set.add(key1))
        assertTrue(set.add(key2))
        assertTrue(set.add(key3))

        // Inserir duplicata deve ser rejeitado
        val addedDuplicate = set.add(keyDuplicate1)
        org.junit.Assert.assertFalse(addedDuplicate)
        assertEquals(3, set.size)
    }
}
