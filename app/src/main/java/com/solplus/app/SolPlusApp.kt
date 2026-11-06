package com.solplus.app

import android.app.Application
import com.solplus.app.core.alarm.AlarmScheduler
import com.solplus.app.core.notification.NotificationHelper
import com.solplus.app.data.local.SolPlusDatabase
import com.solplus.app.data.preferences.AppPreferences
import com.solplus.app.data.repository.MedicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class SolPlusApp : Application() {

    val database by lazy { SolPlusDatabase.getInstance(this) }
    val repository by lazy { MedicationRepository(database) }
    val preferences by lazy { AppPreferences(this) }
    val alarmScheduler by lazy { AlarmScheduler(this) }
    val notificationHelper by lazy { NotificationHelper(this) }

    override fun onCreate() {
        super.onCreate()

        // Garante agendamento dos alarmes diários das 08:00 e 20:00
        alarmScheduler.scheduleDailyAlarms()

        // Inicializa dados do dia de hoje caso ainda não existam
        CoroutineScope(Dispatchers.IO).launch {
            repository.ensureOccurrencesForDate(LocalDate.now())
        }
    }
}
