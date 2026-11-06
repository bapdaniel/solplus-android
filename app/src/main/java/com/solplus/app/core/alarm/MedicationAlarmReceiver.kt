package com.solplus.app.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.solplus.app.core.notification.NotificationHelper
import com.solplus.app.data.local.SolPlusDatabase
import com.solplus.app.data.repository.MedicationRepository
import com.solplus.app.domain.model.DoseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class MedicationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_TYPE) ?: AlarmScheduler.TYPE_MORNING
        val notificationHelper = NotificationHelper(context)
        val alarmScheduler = AlarmScheduler(context)

        val database = SolPlusDatabase.getInstance(context)
        val doseDao = database.doseOccurrenceDao()
        val repository = MedicationRepository(database)

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val today = LocalDate.now()
                repository.ensureOccurrencesForDate(today)
                when (type) {
                    AlarmScheduler.TYPE_MORNING -> {
                        // Alerta agrupado matinal
                        val morningDoses = doseDao.getByDateAndTime(today, LocalTime.of(8, 0))
                        morningDoses.forEach { item ->
                            if (item.occurrence.status == DoseStatus.SCHEDULED) {
                                doseDao.update(item.occurrence.copy(status = DoseStatus.PENDING))
                            }
                        }
                        notificationHelper.showMorningGroupedNotification()
                    }
                    AlarmScheduler.TYPE_NIGHT -> {
                        // Alerta noturno do colírio
                        val nightDoses = doseDao.getByDateAndTime(today, LocalTime.of(20, 0))
                        nightDoses.forEach { item ->
                            if (item.occurrence.status == DoseStatus.SCHEDULED) {
                                doseDao.update(item.occurrence.copy(status = DoseStatus.PENDING))
                            }
                        }
                        notificationHelper.showNightNotification()
                    }
                }
                // Reagenda o próximo ciclo diário para garantir perenidade
                alarmScheduler.scheduleDailyAlarms()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
