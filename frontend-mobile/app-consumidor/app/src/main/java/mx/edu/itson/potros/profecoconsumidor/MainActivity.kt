package mx.edu.itson.potros.profecoconsumidor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import mx.edu.itson.potros.profecoconsumidor.ui.ProfecoApp
import mx.edu.itson.potros.profecoconsumidor.ui.theme.ProfecoConsumidorTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Debe invocarse antes de super.onCreate para hacer el handoff al postSplashScreenTheme.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pedirPermisoNotificacionesSiAplica()
        setContent {
            ProfecoConsumidorTheme {
                ProfecoApp()
            }
        }
    }

    private fun pedirPermisoNotificacionesSiAplica() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val concedido = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!concedido) {
                requestNotificationsPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
