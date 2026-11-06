package com.solplus.app.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val RC_MORNING = 8000
        const val RC_NIGHT = 20000
        const val RC_POSTPONE_MORNING = 30001
        const val RC_POSTPONE_NIGHT = 30002

        const val EXTRA_ALARM_TYPE = "extra_alarm_type"
        const val TYPE_MORNING = "TYPE_MORNING"
        const val TYPE_NIGHT = "TYPE_NIGHT"
        const val TYPE_POSTPONE = "TYPE_POSTPONE"
    }

    fun canScheduleExact(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun scheduleDailyAlarms() {
        scheduleAlarmAt(LocalTime.of(8, 0), TYPE_MORNING, RC_MORNING)
        scheduleAlarmAt(LocalTime.of(20, 0), TYPE_NIGHT, RC_NIGHT)
    }

    private fun scheduleAlarmAt(targetTime: LocalTime, type: String, requestCode: Int) {
        val now = LocalDateTime.now()
        var targetDateTime = LocalDateTime.of(LocalDate.now(), targetTime)
        if (targetDateTime.isBefore(now)) {
            targetDateTime = targetDateTime.plusDays(1)
        }

        val triggerMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_TYPE, type)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExact(triggerMillis, pendingIntent)
    }

    fun schedulePostpone(minutes: Long = 15, isMorning: Boolean = true) {
        val triggerMillis = System.currentTimeMillis() + (minutes * 60 * 1000)
        val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_TYPE, if (isMorning) TYPE_MORNING else TYPE_NIGHT)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            if (isMorning) RC_POSTPONE_MORNING else RC_POSTPONE_NIGHT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        scheduleExact(triggerMillis, pendingIntent)
    }

    private fun scheduleExact(triggerMillis: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // setAlarmClock garante a maior prioridade de despertar mesmo em modo Doze
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerMillis, pendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            // Em caso de permissão de alarme exato não concedida, usa alarme inexato como fallback
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
        }
    }

    fun cancelAlarms() {
        val morningIntent = Intent(context, MedicationAlarmReceiver::class.java)
        val pMorning = PendingIntent.getBroadcast(
            context,
            RC_MORNING,
            morningIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pMorning?.let { alarmManager.cancel(it) }

        val nightIntent = Intent(context, MedicationAlarmReceiver::class.java)
        val pNight = PendingIntent.getBroadcast(
            context,
            RC_NIGHT,
            nightIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pNight?.let { alarmManager.cancel(it) }
    }

    /**
     * Agenda um alarme de teste real para daqui a X segundos (padrão 30s)
     * utilizando a API real do AlarmManager para validar notificações e tela de bloqueio.
     */
    fun scheduleTestAlarm(seconds: Int = 30) {
        val triggerMillis = System.currentTimeMillis() + (seconds * 1000L)
        val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_TYPE, TYPE_MORNING)
            putExtra("is_test_alarm", true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            39999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        scheduleExact(triggerMillis, pendingIntent)
    }

    /**
     * Retorna a informação do próximo alarme do tipo AlarmClock registrado no SO.
     */
    fun getNextAlarmClockInfo(): AlarmManager.AlarmClockInfo? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.nextAlarmClock
        } else {
            null
        }
    }
}
