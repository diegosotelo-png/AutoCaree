package com.auto_care_test.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Servicio de Firebase Cloud Messaging.
 * - Loguea el token FCM para poder enviar mensajes de prueba desde la consola.
 * - Muestra las notificaciones recibidas (incluso en primer plano) con NotificationHelper.
 */
class AutoCareMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo token FCM: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Soporta tanto payload "notification" como "data".
        val titulo = message.notification?.title
            ?: message.data["titulo"]
            ?: "AutoCare"
        val mensaje = message.notification?.body
            ?: message.data["mensaje"]
            ?: "Tienes una nueva notificación"
        val idMantenimiento = message.data["idMantenimiento"]?.toIntOrNull() ?: 0

        Log.d(TAG, "Mensaje FCM recibido: $titulo / $mensaje")

        NotificationHelper.mostrarNotificacion(
            context = applicationContext,
            titulo = titulo,
            mensaje = mensaje,
            idMantenimiento = idMantenimiento
        )
    }

    companion object {
        private const val TAG = "AutoCareFCM"
    }
}
