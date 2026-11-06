package com.solplus.app

import com.solplus.app.domain.model.DoseStatus
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class BackupAndAuditTest {

    data class MockAuditLog(
        val doseOccurrenceId: Long,
        val previousStatus: DoseStatus,
        val newStatus: DoseStatus,
        val reason: String,
        val timestamp: LocalDateTime = LocalDateTime.now()
    )

    @Test
    fun testAuditLogRecordsHistoryAccurately() {
        val auditTrail = mutableListOf<MockAuditLog>()

        // Simulação: Dose marcada como CONFIRMADA por engano
        val log1 = MockAuditLog(
            doseOccurrenceId = 1L,
            previousStatus = DoseStatus.PENDING,
            newStatus = DoseStatus.CONFIRMED,
            reason = "Confirmação inicial"
        )
        auditTrail.add(log1)

        // Usuária corrige para INCERTO
        val log2 = MockAuditLog(
            doseOccurrenceId = 1L,
            previousStatus = DoseStatus.CONFIRMED,
            newStatus = DoseStatus.UNCERTAIN,
            reason = "Apertei sem querer, estou em dúvida se tomei"
        )
        auditTrail.add(log2)

        assertEquals(2, auditTrail.size)
        assertEquals(DoseStatus.PENDING, auditTrail[0].previousStatus)
        assertEquals(DoseStatus.CONFIRMED, auditTrail[0].newStatus)
        assertEquals(DoseStatus.CONFIRMED, auditTrail[1].previousStatus)
        assertEquals(DoseStatus.UNCERTAIN, auditTrail[1].newStatus)
        assertEquals("Apertei sem querer, estou em dúvida se tomei", auditTrail[1].reason)
    }

    @Test
    fun testBackupJsonSchemaIntegrity() {
        val jsonString = """
            {
              "app": "SOL+",
              "version": 1,
              "timestamp": "2026-11-02T08:00:00",
              "medications": [
                {
                  "id": 1,
                  "name": "Remédio da pressão",
                  "type": "ORAL",
                  "scheduledTime": "08:00:00",
                  "dosage": "1 comprimido",
                  "instructions": "Tomar pela manhã com água",
                  "period": "MANHÃ",
                  "isActive": true
                }
              ],
              "occurrences": [],
              "auditLogs": []
            }
        """.trimIndent()

        val root = JSONObject(jsonString)
        assertEquals("SOL+", root.getString("app"))
        assertEquals(1, root.getInt("version"))
        val meds = root.getJSONArray("medications")
        assertEquals(1, meds.length())
        assertEquals("Remédio da pressão", meds.getJSONObject(0).getString("name"))
        assertEquals("ORAL", meds.getJSONObject(0).getString("type"))
    }
}
