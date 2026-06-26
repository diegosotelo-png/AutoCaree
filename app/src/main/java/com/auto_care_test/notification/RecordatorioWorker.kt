package com.auto_care_test.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Worker que muestra el recordatorio de un mantenimiento programado.
 * No accede al ViewModel ni al Repository: solo usa los datos recibidos por inputData
 * y delega en [NotificationHelper] para mostrar la notificación.
 */
class RecordatorioWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_TITULO = "key_titulo"
        const val KEY_ID_MANTENIMIENTO = "key_id_mantenimiento"
    }

    override suspend fun doWork(): Result {
        val titulo = inputData.getString(KEY_TITULO) ?: "Mantenimiento"
        val idMantenimiento = inputData.getInt(KEY_ID_MANTENIMIENTO, 0)

        NotificationHelper.mostrarNotificacion(
            context = applicationContext,
            titulo = "Recordatorio AutoCare",
            mensaje = "Hoy tienes programado: $titulo",
            idMantenimiento = idMantenimiento
        )

        return Result.success()
    }
}
