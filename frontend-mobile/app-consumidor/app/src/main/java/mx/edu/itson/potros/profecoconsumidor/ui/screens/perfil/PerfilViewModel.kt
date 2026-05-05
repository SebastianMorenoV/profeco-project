package mx.edu.itson.potros.profecoconsumidor.ui.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator
import mx.edu.itson.potros.profecoconsumidor.data.UserPrefsState

class PerfilViewModel : ViewModel() {

    val prefs: Flow<UserPrefsState> = ServiceLocator.prefs.state

    fun actualizarUsuarioId(id: Long) {
        viewModelScope.launch {
            ServiceLocator.prefs.setUsuarioId(id)
            ServiceLocator.syncDownFromRemote(id)
        }
    }

    fun actualizarBaseUrl(url: String) {
        viewModelScope.launch { ServiceLocator.prefs.setBaseUrl(url) }
    }

    fun limpiarBusquedas() {
        viewModelScope.launch { ServiceLocator.prefs.limpiarBusquedas() }
    }
}
