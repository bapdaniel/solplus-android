package com.solplus.app.core.util

import com.solplus.app.data.local.SolPlusDatabase
import com.solplus.app.data.local.entity.AuditLogEntity
import com.solplus.app.data.local.entity.DoseOccurrenceEntity
import com.solplus.app.data.local.entity.MedicationEntity
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.domain.model.MedicationType
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class BackupManager(private val database: SolPlusDatabase) {

    suspend fun createBackupJson(): String {
        val root = JSONObject()
        root.put("app", "SOL+")
        root.put("version", 1)
        root.put("timestamp", LocalDateTime.now().toString())

        // Medicamentos
        val meds = database.medicationDao().getAllActiveList()
        val medsArray = JSONArray()
        meds.forEach { med ->
            val obj = JSONObject()
            obj.put("id", med.id)
            obj.put("name", med.name)
            obj.put("type", med.type.name)
            obj.put("scheduledTime", med.scheduledTime.toString())
            obj.put("dosage", med.dosage)
            obj.put("instructions", med.instructions)
            obj.put("period", med.period)
            obj.put("isActive", med.isActive)
            medsArray.put(obj)
        }
        root.put("medications", medsArray)

        // Ocorrências (Histórico Completo)
        val occurrences = database.doseOccurrenceDao().getAllHistoryList()
        val occurrencesArray = JSONArray()
        occurrences.forEach { item ->
            val occ = item.occurrence
            val obj = JSONObject()
            obj.put("id", occ.id)
            obj.put("medicationId", occ.medicationId)
            obj.put("scheduledDate", occ.scheduledDate.toString())
            obj.put("scheduledTime", occ.scheduledTime.toString())
            obj.put("status", occ.status.name)
            obj.put("confirmedAt", occ.confirmedAt?.toString() ?: "")
            obj.put("notes", occ.notes ?: "")
            occurrencesArray.put(obj)
        }
        root.put("occurrences", occurrencesArray)

        // Logs de auditoria
        val audits = database.auditLogDao().getAllList()
        val auditsArray = JSONArray()
        audits.forEach { audit ->
            val obj = JSONObject()
            obj.put("id", audit.id)
            obj.put("doseOccurrenceId", audit.doseOccurrenceId)
            obj.put("previousStatus", audit.previousStatus?.name ?: "")
            obj.put("newStatus", audit.newStatus.name)
            obj.put("timestamp", audit.timestamp.toString())
            obj.put("reason", audit.reason)
            auditsArray.put(obj)
        }
        root.put("auditLogs", auditsArray)

        return root.toString(2)
    }

    suspend fun restoreBackupJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            if (root.optString("app") != "SOL+") return false

            val medsArray = root.getJSONArray("medications")
            val medsList = mutableListOf<MedicationEntity>()
            for (i in 0 until medsArray.length()) {
                val obj = medsArray.getJSONObject(i)
                medsList.add(
                    MedicationEntity(
                        id = obj.getLong("id"),
                        name = obj.getString("name"),
                        type = MedicationType.valueOf(obj.getString("type")),
                        scheduledTime = LocalTime.parse(obj.getString("scheduledTime")),
                        dosage = obj.getString("dosage"),
                        instructions = obj.getString("instructions"),
                        period = obj.getString("period"),
                        isActive = obj.getBoolean("isActive")
                    )
                )
            }
            if (medsList.isNotEmpty()) {
                database.medicationDao().insertAll(medsList)
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}
