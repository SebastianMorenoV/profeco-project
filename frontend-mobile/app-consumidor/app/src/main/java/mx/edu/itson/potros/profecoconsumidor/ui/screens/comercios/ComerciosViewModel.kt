package mx.edu.itson.potros.profecoconsumidor.ui.screens.comercios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ComercioDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState
import mx.edu.itson.potros.profecoconsumidor.util.ApiErrorMapper

class ComerciosViewModel : ViewModel() {

    private val _comercios = MutableStateFlow<UiState<List<ComercioDto>>>(UiState.Loading)
    val comercios: StateFlow<UiState<List<ComercioDto>>> = _comercios.asStateFlow()

    private var currentJob: Job? = null

    fun cargar() {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _comercios.value = UiState.Loading
            _comercios.value = try {
                UiState.Success(ServiceLocator.comercios.listar())
            } catch (t: Throwable) {
                UiState.Error(ApiErrorMapper.map(t, "No se pudieron cargar los comercios"))
            }
        }
    }

    fun buscar(query: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            delay(300)
            _comercios.value = UiState.Loading
            _comercios.value = try {
                if (query.isBlank()) UiState.Success(ServiceLocator.comercios.listar())
                else UiState.Success(ServiceLocator.comercios.buscar(query))
            } catch (t: Throwable) {
                UiState.Error(ApiErrorMapper.map(t, "No se pudieron cargar los comercios"))
            }
        }
    }
}
