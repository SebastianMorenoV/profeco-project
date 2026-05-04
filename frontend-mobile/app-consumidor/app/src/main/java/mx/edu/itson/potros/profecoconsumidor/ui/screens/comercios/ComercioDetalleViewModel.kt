package mx.edu.itson.potros.profecoconsumidor.ui.screens.comercios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ComercioDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.OfertaDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.PromedioDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ReseniaDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState

data class ComercioDetalleData(
    val comercio: ComercioDto?,
    val promedio: PromedioDto?,
    val resenias: List<ReseniaDto>,
    val ofertas: List<OfertaDto>,
    val okMensaje: String? = null
)

class ComercioDetalleViewModel : ViewModel() {

    private val _state = MutableStateFlow<UiState<ComercioDetalleData>>(UiState.Loading)
    val state: StateFlow<UiState<ComercioDetalleData>> = _state.asStateFlow()

    private val _comercioActual = MutableStateFlow(0L)

    val esFavorito: StateFlow<Boolean> =
        kotlinx.coroutines.flow.combine(
            ServiceLocator.prefs.state,
            _comercioActual
        ) { prefs, id ->
            id != 0L && prefs.comerciosFavoritos.contains(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun cargar(id: Long) {
        _comercioActual.value = id
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                val comercio = ServiceLocator.comercios.obtener(id)
                val promedio = runCatching { ServiceLocator.resenias.obtenerPromedio(id) }.getOrNull()
                val resenias = ServiceLocator.resenias.listarPorComercio(id)
                val ofertas = ServiceLocator.ofertas.listarPorComercio(id)
                UiState.Success(ComercioDetalleData(comercio, promedio, resenias, ofertas))
            } catch (t: Throwable) {
                UiState.Error(t.message ?: "No se pudo cargar el comercio")
            }
        }
    }

    fun publicarResenia(calificacion: Int, comentario: String) {
        val id = _comercioActual.value
        if (id == 0L) return
        viewModelScope.launch {
            try {
                val usuarioId = ServiceLocator.prefs.state.first().usuarioId
                ServiceLocator.resenias.crear(usuarioId, id, calificacion, comentario)
                recargarConMensaje(id, "Reseña publicada. Gracias por tu aporte.")
            } catch (t: Throwable) {
                _state.value = UiState.Error(t.message ?: "No se pudo publicar la reseña")
            }
        }
    }

    fun toggleFavorito(id: Long) {
        viewModelScope.launch { ServiceLocator.prefs.toggleComercioFavorito(id) }
    }

    private suspend fun recargarConMensaje(id: Long, mensaje: String) {
        val comercio = ServiceLocator.comercios.obtener(id)
        val promedio = runCatching { ServiceLocator.resenias.obtenerPromedio(id) }.getOrNull()
        val resenias = ServiceLocator.resenias.listarPorComercio(id)
        val ofertas = ServiceLocator.ofertas.listarPorComercio(id)
        _state.value = UiState.Success(
            ComercioDetalleData(comercio, promedio, resenias, ofertas, okMensaje = mensaje)
        )
    }
}
