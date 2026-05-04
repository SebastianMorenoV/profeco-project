package mx.edu.itson.potros.profecoconsumidor

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.messaging.FirebaseMessaging
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator

class ProfecoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        crearCanalNotificaciones()
        FirebaseMessaging.getInstance().subscribeToTopic("ofertas-nuevas")
        // Reenviar el token FCM actual al backend en cada arranque por si onNewToken
        // ya disparó antes de que el endpoint existiera o antes de que el usuario tuviera id.
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            if (!token.isNullOrBlank()) ServiceLocator.registrarFcmToken(token)
        }
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                "ofertas",
                "Ofertas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos de nuevas ofertas y promociones de comercios"
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm?.createNotificationChannel(canal)
        }
    }
}
