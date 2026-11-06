package com.solplus.app.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.solplus.app.MainActivity
import com.solplus.app.R
import com.solplus.app.core.alarm.AlarmActionReceiver

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "solplus_medications_channel_v1"
        const val NOTIFICATION_ID_MORNING = 1001
        const val NOTIFICATION_ID_NIGHT = 1002
        const val NOTIFICATION_ID_TEST = 1003

        const val EXTRA_PROMPT_TYPE = "extra_prompt_type"
        const val PROMPT_MORNING = "MORNING"
        const val PROMPT_NIGHT = "NIGHT"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_medication_name)
            val descriptionText = context.getString(R.string.channel_medication_desc)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
                setShowBadge(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createPublicNotification(title: String, text: String): android.app.Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .build()
    }

    fun showMorningGroupedNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_PROMPT_TYPE, PROMPT_MORNING)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_MORNING,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val publicVersion = createPublicNotification(
            title = context.getString(R.string.morning_alert_title),
            text = "Você possui cuidados da manhã programados. Desbloqueie para responder."
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.morning_alert_title))
            .setContentText("1. Remédio da pressão\n2. Colírio matinal nos dois olhos")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Hora dos cuidados da manhã:\n" +
                            "• Remédio da pressão (08:00)\n" +
                            "• Colírio nos dois olhos (08:00)\n\n" +
                            "Toque para responder se já tomou ou aplicou."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicVersion)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val postponeIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = AlarmActionReceiver.ACTION_POSTPONE_MORNING
        }
        val postponePendingIntent = PendingIntent.getBroadcast(
            context,
            2001,
            postponeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(R.drawable.ic_notification, "LEMBRAR EM 15 MIN", postponePendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_MORNING, builder.build())
        } catch (_: SecurityException) {
            // Permissão negada pelo sistema
        }
    }

    fun showNightNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_PROMPT_TYPE, PROMPT_NIGHT)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_NIGHT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val publicVersion = createPublicNotification(
            title = context.getString(R.string.night_alert_title),
            text = "Você possui cuidados da noite programados. Desbloqueie para responder."
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.night_alert_title))
            .setContentText(context.getString(R.string.night_question_eye))
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Hora do cuidado da noite:\n" +
                            "• Colírio nos dois olhos (20:00)\n\n" +
                            "Você já aplicou o colírio nos dois olhos esta noite?"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicVersion)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val postponeIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = AlarmActionReceiver.ACTION_POSTPONE_NIGHT
        }
        val postponePendingIntent = PendingIntent.getBroadcast(
            context,
            2002,
            postponeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(R.drawable.ic_notification, "LEMBRAR EM 15 MIN", postponePendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_NIGHT, builder.build())
        } catch (_: SecurityException) {
            // Permissão negada pelo sistema
        }
    }

    fun showTestNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_TEST,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("SOL+ Alerta de Teste")
            .setContentText("O sistema de notificações do SOL+ está funcionando perfeitamente no seu aparelho!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_TEST, builder.build())
        } catch (_: SecurityException) {}
    }
}
