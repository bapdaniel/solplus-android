package com.solplus.app.core.testing

import android.app.AlarmManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationManagerCompat
import com.solplus.app.core.alarm.AlarmScheduler
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.domain.model.MedicationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Evento registrado na sessão do Laboratório de Testes SOL+
 */
data class TestLabEvent(
    val timestamp: String,
    val title: String,
    val description: String,
    val isSuccess: Boolean = true
)

/**
 * Dose simulada em memória, totalmente ISOLADA do Room Database real.
 */
data class SimulatedDose(
    val id: Long,
    val medicationName: String,
    val type: MedicationType,
    val scheduledTime: LocalTime,
    var status: DoseStatus = DoseStatus.SCHEDULED,
    var confirmedAt: LocalDateTime? = null,
    var postponedUntil: LocalDateTime? = null
)

/**
 * Gerenciador de testes do Laboratório SOL+
 */
class TestLabManager(
    private val context: Context,
    private val alarmScheduler: AlarmScheduler
) {
    private val _events = MutableStateFlow<List<TestLabEvent>>(emptyList())
    val events: StateFlow<List<TestLabEvent>> = _events.asStateFlow()

    private val _simulatedDoses = MutableStateFlow<List<SimulatedDose>>(createInitialSimulatedDoses())
    val simulatedDoses: StateFlow<List<SimulatedDose>> = _simulatedDoses.asStateFlow()

    private fun logEvent(title: String, description: String, isSuccess: Boolean = true) {
        val now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val event = TestLabEvent(now, title, description, isSuccess)
        _events.value = listOf(event) + _events.value
    }

    private fun createInitialSimulatedDoses(): List<SimulatedDose> {
        return listOf(
            SimulatedDose(
                id = 1L,
                medicationName = "Remédio da pressão",
                type = MedicationType.ORAL,
                scheduledTime = LocalTime.of(8, 0),
                status = DoseStatus.PENDING
            ),
            SimulatedDose(
                id = 2L,
                medicationName = "Colírio (ambos os olhos)",
                type = MedicationType.EYE_DROP,
                scheduledTime = LocalTime.of(8, 0),
                status = DoseStatus.PENDING
            ),
            SimulatedDose(
                id = 3L,
                medicationName = "Colírio (ambos os olhos)",
                type = MedicationType.EYE_DROP,
                scheduledTime = LocalTime.of(20, 0),
                status = DoseStatus.SCHEDULED
            )
        )
    }

    fun resetSimulatedDoses() {
        _simulatedDoses.value = createInitialSimulatedDoses()
        logEvent("Ambiente Resetado", "Doses simuladas reiniciadas para o estado matinal padrão.")
    }

    // 1. Simular confirmação "SIM, JÁ TOMEI/APLIQUEI" para uma dose específica
    fun simulateConfirm(doseId: Long) {
        val current = _simulatedDoses.value.toMutableList()
        val index = current.indexOfFirst { it.id == doseId }
        if (index != -1) {
            val dose = current[index]
            if (dose.status == DoseStatus.CONFIRMED) {
                // Tentativa de confirmação duplicada (idempotência)
                logEvent(
                    "Confirmação Duplicada Ignorada",
                    "A dose '${dose.medicationName}' já estava confirmada em ${dose.confirmedAt?.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}. Nenhuma duplicação gerada.",
                    isSuccess = true
                )
                return
            }
            val confirmTime = LocalDateTime.now()
            current[index] = dose.copy(
                status = DoseStatus.CONFIRMED,
                confirmedAt = confirmTime
            )
            _simulatedDoses.value = current
            logEvent(
                "Dose Confirmada (SIM)",
                "'${dose.medicationName}' às ${dose.scheduledTime} marcada como CONFIRMADA. Doses irmãs NÃO foram afetadas."
            )
        }
    }

    // 2. Simular resposta "NÃO, AINDA NÃO"
    fun simulateNotYet(doseId: Long) {
        val dose = _simulatedDoses.value.find { it.id == doseId }
        if (dose != null) {
            logEvent(
                "Resposta 'NÃO' Registrada",
                "'${dose.medicationName}': Mantida como PENDENTE. Nenhuma utilização indevida foi registrada."
            )
        }
    }

    // 3. Simular "Vou tomar agora"
    fun simulateTakeNow(doseId: Long) {
        val dose = _simulatedDoses.value.find { it.id == doseId }
        if (dose != null) {
            logEvent(
                "Ação 'Vou tomar agora'",
                "Conforme a Regra de Ouro Médica, 'Vou tomar agora' NÃO marca a dose como confirmada. Permanece PENDENTE até a confirmação explícita."
            )
        }
    }

    // 4. Simular Adiamento (15 minutos)
    fun simulatePostpone(doseId: Long) {
        val current = _simulatedDoses.value.toMutableList()
        val index = current.indexOfFirst { it.id == doseId }
        if (index != -1) {
            val dose = current[index]
            val postponeUntil = LocalDateTime.now().plusMinutes(15)
            current[index] = dose.copy(
                status = DoseStatus.POSTPONED,
                postponedUntil = postponeUntil
            )
            _simulatedDoses.value = current
            logEvent(
                "Dose Adiada por 15 Minutos",
                "'${dose.medicationName}' adiada para ${postponeUntil.format(DateTimeFormatter.ofPattern("HH:mm"))}. Outras doses permanecem inalteradas."
            )
        }
    }

    // 5. Agendar Notificação Real para daqui a 30s
    fun scheduleRealTestAlarm(seconds: Int = 30) {
        try {
            alarmScheduler.scheduleTestAlarm(seconds)
            logEvent(
                "Alarme Real Agendado via AlarmManager",
                "Disparo agendado para daqui a $seconds segundos. Bloqueie a tela para testar o despertar do display e a notificação."
            )
        } catch (e: Exception) {
            logEvent("Falha no Agendamento", "Erro: ${e.message}", isSuccess = false)
        }
    }

    // 6. Testar Som e Vibração do Alarme
    fun testSoundAndVibration() {
        try {
            // Tocar Ringtone de Notificação/Alarme
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone?.play()

            // Disparar Vibração Háptica
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 400, 200, 400), -1)
                }
            }

            logEvent("Som e Vibração Testados", "Tom de alarme acionado e padrão háptico de vibração disparado com sucesso.")
        } catch (e: Exception) {
            logEvent("Falha no Teste de Áudio/Vibração", "Erro: ${e.message}", isSuccess = false)
        }
    }

    // 7. Informações de Diagnóstico de Permissões
    fun checkPermissionsStatus(): Map<String, Boolean> {
        val notifEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        val exactAlarm = alarmScheduler.canScheduleExact()
        val nextAlarm = alarmScheduler.getNextAlarmClockInfo() != null

        return mapOf(
            "POST_NOTIFICATIONS (Notificações Ativas)" to notifEnabled,
            "SCHEDULE_EXACT_ALARM (Alarmes Exatos Permitidos)" to exactAlarm,
            "Próximo Alarme Registrado no SO" to nextAlarm
        )
    }

    fun getNextAlarmFormatted(): String {
        val info = alarmScheduler.getNextAlarmClockInfo() ?: return "Nenhum alarme do sistema registrado"
        val triggerTime = info.triggerTime
        val dt = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(triggerTime),
            java.time.ZoneId.systemDefault()
        )
        return dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
    }
}
