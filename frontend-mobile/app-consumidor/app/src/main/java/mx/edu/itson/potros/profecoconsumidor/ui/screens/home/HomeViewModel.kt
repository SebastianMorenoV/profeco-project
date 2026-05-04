package mx.edu.itson.potros.profecoconsumidor.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.OfertaDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState
import mx.edu.itson.potros.profecoconsumidor.util.ApiErrorMapper

class HomeViewModel : ViewModel() {

    private val _ofertas = MutableStateFlow<UiState<List<OfertaDto>>>(UiState.Loading)
    val ofertas: StateFlow<UiState<List<OfertaDto>>> = _ofertas.asStateFlow()

    val busquedasRecientes: Flow<List<String>> =
        ServiceLocator.prefs.state.map { it.busquedasRecientes }

    fun cargar() {
        viewModelScope.launch {
            _ofertas.value = UiState.Loading
            _ofertas.value = try {
                UiState.Success(ServiceLocator.ofertas.listar(soloActivas = true))
            } catch (t: Throwable) {
                UiState.Error(ApiErrorMapper.map(t, "No se pudieron cargar las ofertas"))
            }
        }
    }
}
