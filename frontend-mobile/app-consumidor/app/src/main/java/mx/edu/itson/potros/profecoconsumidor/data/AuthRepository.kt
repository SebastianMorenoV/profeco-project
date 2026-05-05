package mx.edu.itson.potros.profecoconsumidor.data

import mx.edu.itson.potros.profecoconsumidor.data.network.ApiClient
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.LoginRequest
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.RegistrarUsuarioRequest
import retrofit2.HttpException

class AuthRepository(private val apiClient: ApiClient) {

    data class CuentaSesion(
        val usuarioId: Long,
        val nombre: String,
        val apellido: String,
        val email: String
    )

    sealed interface Resultado {
        data class Ok(val cuenta: CuentaSesion) : Resultado
        data class Error(val mensaje: String) : Resultado
    }

    suspend fun login(email: String, password: String): Resultado {
        if (email.isBlank() || password.isBlank()) {
            return Resultado.Error("Captura email y contraseña.")
        }
        return runCatching {
            val resp = apiClient.usuarios.login(LoginRequest(email.trim(), password))
            if (resp.exito && resp.usuario != null) {
                val u = resp.usuario
                Resultado.Ok(
                    CuentaSesion(
                        usuarioId = u.id,
                        nombre = u.nombre,
                        apellido = u.apellido,
                        email = u.email
                    )
                )
            } else {
                Resultado.Error(resp.mensaje.ifBlank { "Credenciales inválidas." })
            }
        }.getOrElse { mapearError(it) }
    }

    suspend fun registrar(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String,
        password: String
    ): Resultado {
        if (nombre.isBlank()) return Resultado.Error("Captura tu nombre.")
        if (email.isBlank() || !email.contains("@")) return Resultado.Error("Captura un email válido.")
        if (password.length < 6) return Resultado.Error("La contraseña debe tener al menos 6 caracteres.")

        return runCatching {
            val resp = apiClient.usuarios.registrar(
                RegistrarUsuarioRequest(
                    nombre = nombre.trim(),
                    apellido = apellido.trim(),
                    email = email.trim(),
                    telefono = telefono.trim(),
                    tipo_usuario = "CONSUMIDOR",
                    password = password
                )
            )
            val u = resp.usuario
            if (u != null) {
                Resultado.Ok(
                    CuentaSesion(
                        usuarioId = u.id,
                        nombre = u.nombre,
                        apellido = u.apellido,
                        email = u.email
                    )
                )
            } else {
                Resultado.Error("El servidor no devolvió la cuenta creada.")
            }
        }.getOrElse { mapearError(it) }
    }

    private fun mapearError(t: Throwable): Resultado.Error {
        if (t is HttpException) {
            return when (t.code()) {
                409 -> Resultado.Error("Ya existe una cuenta con ese email.")
                400 -> Resultado.Error("Datos inválidos. Revisa email y contraseña.")
                404 -> Resultado.Error("Endpoint no disponible. Verifica que el gateway esté actualizado.")
                else -> Resultado.Error("Error del servidor (${t.code()}).")
            }
        }
        return Resultado.Error("No se pudo conectar con el servidor: ${t.message ?: "error desconocido"}")
    }
}
