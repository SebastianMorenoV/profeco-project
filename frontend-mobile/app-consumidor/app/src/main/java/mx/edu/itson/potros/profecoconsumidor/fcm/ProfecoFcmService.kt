package mx.edu.itson.potros.profecoconsumidor.fcm

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mx.edu.itson.potros.profecoconsumidor.R

class ProfecoFcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "FCM token = $token")
        // TODO: cuando exista POST /api/usuarios/{id}/fcm-token, mandar el token aquí.
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val titulo = message.notification?.title
            ?: message.data["titulo"]
            ?: "Nueva oferta disponible"
        val cuerpo = message.notification?.body
            ?: message.data["cuerpo"]
            ?: "Revisa las promociones publicadas en la app."
        mostrarNotificacion(titulo, cuerpo)
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String) {
        val builder = NotificationCompat.Builder(this, "ofertas")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cuerpo))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    companion object { private const val TAG = "ProfecoFcm" }
}
