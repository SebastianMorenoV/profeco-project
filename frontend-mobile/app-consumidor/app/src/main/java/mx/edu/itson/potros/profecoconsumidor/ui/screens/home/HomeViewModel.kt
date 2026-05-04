package mx.edu.itson.potros.profecoconsumidor.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.OfertaDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState

class HomeViewModel : ViewModel() {

    private val _ofertas = MutableStateFlow<UiState<List<OfertaDto>>>(UiState.Loading)
    val ofertas: StateFlow<UiState<List<OfertaDto>>> = _ofertas.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _ofertas.value = UiState.Loading
            _ofertas.value = try {
                UiState.Success(ServiceLocator.ofertas.listar(soloActivas = true))
            } catch (t: Throwable) {
                UiState.Error(t.message ?: "No se pudieron cargar las ofertas")
            }
        }
    }
}
