package com.solplus.app.ui.screens.diagnostic

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import com.solplus.app.core.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DiagnosticState(
    val notificationsEnabled: Boolean = false,
    val exactAlarmsEnabled: Boolean = false,
    val batteryOptimizationIgnored: Boolean = false,
    val isXiaomiOrHyperOs: Boolean = false
)

class DiagnosticViewModel(
    context: Context,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val appContext = context.applicationContext

    private val _state = MutableStateFlow(DiagnosticState())
    val state: StateFlow<DiagnosticState> = _state.asStateFlow()

    init {
        refreshStatus()
    }

    fun refreshStatus() {
        val notifsEnabled = NotificationManagerCompat.from(appContext).areNotificationsEnabled()

        val exactAlarmEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.canScheduleExactAlarms()
        } else {
            true
        }

        val powerManager = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val batteryIgnored = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager.isIgnoringBatteryOptimizations(appContext.packageName)
        } else {
            true
        }

        val manufacturer = Build.MANUFACTURER.lowercase()
        val isXiaomi = manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco")

        _state.value = DiagnosticState(
            notificationsEnabled = notifsEnabled,
            exactAlarmsEnabled = exactAlarmEnabled,
            batteryOptimizationIgnored = batteryIgnored,
            isXiaomiOrHyperOs = isXiaomi
        )
    }

    fun testAlertNow() {
        notificationHelper.showTestNotification()
    }

    fun openNotificationSettings(): Intent {
        return Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, appContext.packageName)
            } else {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", appContext.packageName, null)
            }
        }
    }

    fun openExactAlarmSettings(): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                data = Uri.fromParts("package", appContext.packageName, null)
            }
        } else null
    }

    fun openBatteryOptimizationSettings(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            Intent(Settings.ACTION_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        }
    }

    fun openXiaomiAutostartSettings(): Intent {
        val intent = Intent().apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        // Tenta abrir a tela de Início Automático do HyperOS / MIUI
        intent.setClassName(
            "com.miui.securitycenter",
            "com.miui.permcenter.autostart.AutoStartManagementActivity"
        )
        return intent
    }

    fun openXiaomiBatteryRestrictions(): Intent {
        val intent = Intent().apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        // Tenta abrir o gerenciador de economia de bateria de apps em segundo plano
        intent.setClassName(
            "com.miui.powerkeeper",
            "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
        )
        intent.putExtra("package_name", appContext.packageName)
        intent.putExtra("package_label", "SOL+")
        return intent
    }
}
