package mx.edu.itson.potros.profecoconsumidor.data

object AuthRepository {

    data class CuentaMock(
        val usuarioId: Long,
        val usuario: String,
        val password: String,
        val nombreCompleto: String
    )

    private val cuentas: MutableMap<String, CuentaMock> = mutableMapOf(
        "juanperez" to CuentaMock(1L, "juanperez", "12345678", "Juan Pérez"),
        "marialopez" to CuentaMock(2L, "marialopez", "12345678", "María López"),
        "demo" to CuentaMock(99L, "demo", "demo1234", "Usuario Demo")
    )

    @Synchronized
    fun login(usuario: String, password: String): CuentaMock? {
        val key = usuario.trim().lowercase()
        val cuenta = cuentas[key] ?: return null
        return if (cuenta.password == password) cuenta else null
    }

    @Synchronized
    fun registrar(usuario: String, password: String, nombreCompleto: String): Resultado {
        val key = usuario.trim().lowercase()
        if (key.length < 4) return Resultado.UsuarioInvalido
        if (password.length < 6) return Resultado.PasswordCorto
        if (nombreCompleto.isBlank()) return Resultado.NombreVacio
        if (cuentas.containsKey(key)) return Resultado.UsuarioYaExiste
        val nuevoId = (cuentas.values.maxOfOrNull { it.usuarioId } ?: 0L) + 1L
        cuentas[key] = CuentaMock(nuevoId, key, password, nombreCompleto.trim())
        return Resultado.Ok(nuevoId, nombreCompleto.trim(), key)
    }

    sealed interface Resultado {
        data class Ok(val usuarioId: Long, val nombre: String, val usuario: String) : Resultado
        data object UsuarioYaExiste : Resultado
        data object UsuarioInvalido : Resultado
        data object PasswordCorto : Resultado
        data object NombreVacio : Resultado
    }
}
