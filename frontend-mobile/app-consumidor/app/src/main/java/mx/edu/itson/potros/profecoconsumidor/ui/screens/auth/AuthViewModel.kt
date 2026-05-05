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

    fun login(usuario: String, password: String, onExito: () -> Unit) {
        if (usuario.isBlank() || password.isBlank()) {
            _ui.value = AuthUiState(errorMensaje = "Captura usuario y contraseña.")
            return
        }
        _ui.value = AuthUiState(procesando = true)
        viewModelScope.launch {
            val cuenta = AuthRepository.login(usuario, password)
            if (cuenta == null) {
                _ui.value = AuthUiState(errorMensaje = "Usuario o contraseña incorrectos.")
                return@launch
            }
            ServiceLocator.prefs.iniciarSesion(cuenta.usuarioId, cuenta.nombreCompleto)
            ServiceLocator.syncDownFromRemote(cuenta.usuarioId)
            _ui.value = AuthUiState(okMensaje = "Bienvenido, ${cuenta.nombreCompleto}.")
            onExito()
        }
    }

    fun registrar(
        usuario: String,
        password: String,
        confirmar: String,
        nombreCompleto: String,
        onExito: () -> Unit
    ) {
        if (password != confirmar) {
            _ui.value = AuthUiState(errorMensaje = "Las contraseñas no coinciden.")
            return
        }
        _ui.value = AuthUiState(procesando = true)
        viewModelScope.launch {
            when (val r = AuthRepository.registrar(usuario, password, nombreCompleto)) {
                is AuthRepository.Resultado.Ok -> {
                    ServiceLocator.prefs.iniciarSesion(r.usuarioId, r.nombre)
                    ServiceLocator.syncDownFromRemote(r.usuarioId)
                    _ui.value = AuthUiState(okMensaje = "Cuenta creada. Bienvenido, ${r.nombre}.")
                    onExito()
                }
                AuthRepository.Resultado.UsuarioYaExiste ->
                    _ui.value = AuthUiState(errorMensaje = "Ese nombre de usuario ya está registrado.")
                AuthRepository.Resultado.UsuarioInvalido ->
                    _ui.value = AuthUiState(errorMensaje = "El usuario debe tener al menos 4 caracteres.")
                AuthRepository.Resultado.PasswordCorto ->
                    _ui.value = AuthUiState(errorMensaje = "La contraseña debe tener al menos 6 caracteres.")
                AuthRepository.Resultado.NombreVacio ->
                    _ui.value = AuthUiState(errorMensaje = "Captura tu nombre completo.")
            }
        }
    }

    fun limpiarMensajes() {
        _ui.value = _ui.value.copy(errorMensaje = null, okMensaje = null)
    }
}
