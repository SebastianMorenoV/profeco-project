package mx.edu.itson.potros.profecoconsumidor.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.AuthRepository
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator

data class AuthUiState(
    val procesando: Boolean = false,
    val errorMensaje: String? = null,
    val okMensaje: String? = null
)

class AuthViewModel : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui = _ui.asStateFlow()

    fun login(email: String, password: String, onExito: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _ui.value = AuthUiState(errorMensaje = "Captura email y contraseña.")
            return
        }
        _ui.value = AuthUiState(procesando = true)
        viewModelScope.launch {
            when (val r = ServiceLocator.auth.login(email, password)) {
                is AuthRepository.Resultado.Ok -> {
                    val nombreCompleto = listOf(r.cuenta.nombre, r.cuenta.apellido)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .ifBlank { r.cuenta.email }
                    ServiceLocator.prefs.iniciarSesion(r.cuenta.usuarioId, nombreCompleto)
                    ServiceLocator.syncDownFromRemote(r.cuenta.usuarioId)
                    _ui.value = AuthUiState(okMensaje = "Bienvenido, $nombreCompleto.")
                    onExito()
                }
                is AuthRepository.Resultado.Error ->
                    _ui.value = AuthUiState(errorMensaje = r.mensaje)
            }
        }
    }

    fun registrar(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String,
        password: String,
        confirmar: String,
        onExito: () -> Unit
    ) {
        if (password != confirmar) {
            _ui.value = AuthUiState(errorMensaje = "Las contraseñas no coinciden.")
            return
        }
        _ui.value = AuthUiState(procesando = true)
        viewModelScope.launch {
            when (val r = ServiceLocator.auth.registrar(nombre, apellido, email, telefono, password)) {
                is AuthRepository.Resultado.Ok -> {
                    val nombreCompleto = listOf(r.cuenta.nombre, r.cuenta.apellido)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .ifBlank { r.cuenta.email }
                    ServiceLocator.prefs.iniciarSesion(r.cuenta.usuarioId, nombreCompleto)
                    ServiceLocator.syncDownFromRemote(r.cuenta.usuarioId)
                    _ui.value = AuthUiState(okMensaje = "Cuenta creada. Bienvenido, $nombreCompleto.")
                    onExito()
                }
                is AuthRepository.Resultado.Error ->
                    _ui.value = AuthUiState(errorMensaje = r.mensaje)
            }
        }
    }

    fun limpiarMensajes() {
        _ui.value = _ui.value.copy(errorMensaje = null, okMensaje = null)
    }
}
