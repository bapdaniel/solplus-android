package com.solplus.app.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.solplus.app.data.local.SolPlusDatabase
import com.solplus.app.data.repository.MedicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            val alarmScheduler = AlarmScheduler(context)
            alarmScheduler.scheduleDailyAlarms()

            val repo = MedicationRepository(SolPlusDatabase.getInstance(context))
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    repo.ensureOccurrencesForDate(LocalDate.now())
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
