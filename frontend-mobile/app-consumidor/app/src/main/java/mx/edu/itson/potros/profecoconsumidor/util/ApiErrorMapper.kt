package mx.edu.itson.potros.profecoconsumidor.util

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import kotlinx.coroutines.CancellationException

/**
 * Mapea excepciones de red y HTTP a mensajes en español que tienen sentido para el usuario.
 *
 * Why: por defecto los ViewModels mostraban `Throwable.message` que es críptico
 * (ej. "Failed to connect to /10.0.2.2 (port 8084) after 15000ms"). Aquí se traduce
 * por categoría conservando un fallback corto cuando no se reconoce la causa.
 */
object ApiErrorMapper {

    fun map(t: Throwable, fallback: String = "No se pudo completar la operación"): String {
        // Las cancelaciones de coroutines no son errores reales — devolvemos algo neutro
        // pero el caller normalmente no los muestra.
        if (t is CancellationException) return fallback

        return when (t) {
            is UnknownHostException -> "Sin conexión a internet o el servidor no está disponible."
            is ConnectException     -> "No se pudo conectar con el servidor. Verifica que esté encendido."
            is SocketTimeoutException -> "El servidor tardó demasiado en responder. Intenta de nuevo."
            is SSLException         -> "Hubo un problema con la conexión segura del servidor."
            is HttpException        -> mapHttp(t)
            is IOException          -> "Hubo un problema de red. Revisa tu conexión."
            else                    -> t.message?.takeIf { it.isNotBlank() } ?: fallback
        }
    }

    private fun mapHttp(t: HttpException): String = when (t.code()) {
        400 -> "Los datos enviados no son válidos."
        401 -> "Necesitas iniciar sesión para hacer eso."
        403 -> "No tienes permiso para esta acción."
        404 -> "No encontramos el recurso solicitado."
        409 -> "El recurso ya existe o hay un conflicto con los datos."
        422 -> "Los datos no pasaron la validación del servidor."
        in 500..599 -> "El servidor está teniendo problemas. Intenta de nuevo en unos segundos."
        else -> "Error del servidor (HTTP ${t.code()})."
    }
}
