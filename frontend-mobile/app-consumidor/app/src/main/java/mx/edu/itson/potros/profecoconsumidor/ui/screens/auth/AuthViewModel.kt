package mx.edu.itson.potros.profecoconsumidor.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.AuthRepository
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator
import mx.edu.itson.potros.profecoconsumidor.data.UserPrefs

data class AuthUiState(
    val procesando: Boolean = false,
    val errorMensaje: String? = null,
    val okMensaje: String? = null
)

class AuthViewModel : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui = _ui.asStateFlow()

    /** URL actual del gateway, observable desde las pantallas de login/registro. */
    val baseUrl = ServiceLocator.prefs.state
        .map { it.baseUrl }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPrefs.DEFAULT_BASE_URL)

    fun actualizarBaseUrl(url: String) {
        viewModelScope.launch { ServiceLocator.prefs.setBaseUrl(url.trim()) }
    }

    fun restaurarBaseUrl() {
        viewModelScope.launch { ServiceLocator.prefs.setBaseUrl(UserPrefs.DEFAULT_BASE_URL) }
    }

    fun login(email: String, password: String, onExito: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _ui.value = AuthUiState(errorMensaje = "Captura correo y contraseña.")
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
        _ui.value = AuthUiState(procesando = true)
        viewModelScope.launch {
            when (val r = ServiceLocator.auth.registrar(nombre, apellido, email, telefono, password, confirmar)) {
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
