package mx.edu.itson.potros.profecoconsumidor.ui.screens.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ComercioDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState

class MisFavoritosViewModel : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<ComercioDto>>>(UiState.Loading)
    val state: StateFlow<UiState<List<ComercioDto>>> = _state.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                val ids = ServiceLocator.prefs.state.first().comerciosFavoritos
                val comercios = ids.mapNotNull { id ->
                    runCatching { ServiceLocator.comercios.obtener(id) }.getOrNull()
                }
                UiState.Success(comercios)
            } catch (t: Throwable) {
                UiState.Error(t.message ?: "No se pudieron cargar los favoritos")
            }
        }
    }

    fun quitar(comercioId: Long) {
        viewModelScope.launch {
            ServiceLocator.prefs.toggleComercioFavorito(comercioId)
            cargar()
        }
    }
}
