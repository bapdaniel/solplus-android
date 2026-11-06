package com.solplus.app.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.solplus.app.core.notification.NotificationHelper
import com.solplus.app.data.local.SolPlusDatabase
import com.solplus.app.data.repository.MedicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class AlarmActionReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_POSTPONE_MORNING = "com.solplus.app.ACTION_POSTPONE_MORNING"
        const val ACTION_POSTPONE_NIGHT = "com.solplus.app.ACTION_POSTPONE_NIGHT"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alarmScheduler = AlarmScheduler(context)
        val repo = MedicationRepository(SolPlusDatabase.getInstance(context))
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    ACTION_POSTPONE_MORNING -> {
                        NotificationManagerCompat.from(context).cancel(NotificationHelper.NOTIFICATION_ID_MORNING)
                        alarmScheduler.schedulePostpone(15, isMorning = true)
                        val doses = repo.getDosesForDateAndTime(LocalDate.now(), LocalTime.of(8, 0))
                        doses.forEach {
                            if (!it.occurrence.status.isResolved) {
                                repo.postponeDose(it.occurrence.id, 15)
                            }
                        }
                    }
                    ACTION_POSTPONE_NIGHT -> {
                        NotificationManagerCompat.from(context).cancel(NotificationHelper.NOTIFICATION_ID_NIGHT)
                        alarmScheduler.schedulePostpone(15, isMorning = false)
                        val doses = repo.getDosesForDateAndTime(LocalDate.now(), LocalTime.of(20, 0))
                        doses.forEach {
                            if (!it.occurrence.status.isResolved) {
                                repo.postponeDose(it.occurrence.id, 15)
                            }
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
