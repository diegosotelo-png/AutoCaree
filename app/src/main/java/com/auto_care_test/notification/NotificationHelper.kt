package com.auto_care_test.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.auto_care_test.MainActivity
import com.auto_care_test.R

/**
 * Centraliza la creación del canal y el envío de notificaciones locales de AutoCare.
 * Usa NotificationCompat para compatibilidad con versiones antiguas de Android.
 */
object NotificationHelper {

    const val CHANNEL_ID = "autocare_channel"
    const val CHANNEL_NAME = "Recordatorios AutoCare"
    const val EXTRA_ID_MANTENIMIENTO = "extra_id_mantenimiento"

    /** Crea el canal de notificación (requerido en Android 8.0 / API 26+). */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios de mantenimientos programados de tus vehículos"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    /**
     * Muestra una notificación. Al tocarla abre la app en el detalle del mantenimiento
     * (MainActivity lee el extra [EXTRA_ID_MANTENIMIENTO] y navega).
     */
    fun mostrarNotificacion(
        context: Context,
        titulo: String,
        mensaje: String,
        idMantenimiento: Int
    ) {
        crearCanal(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_ID_MANTENIMIENTO, idMantenimiento)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            idMantenimiento, // requestCode único por mantenimiento
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Si falta el permiso POST_NOTIFICATIONS (Android 13+), notify se ignora sin lanzar excepción.
        NotificationManagerCompat.from(context).notify(idMantenimiento, notificacion)
    }
}
