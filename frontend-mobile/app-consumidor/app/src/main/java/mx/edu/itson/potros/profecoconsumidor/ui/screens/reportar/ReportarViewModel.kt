package mx.edu.itson.potros.profecoconsumidor.ui.screens.reportar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ReporteDto

data class ReportarUi(
    val enviando: Boolean = false,
    val okMensaje: String? = null,
    val errorMensaje: String? = null,
    val cargandoReportes: Boolean = false,
    val reportes: List<ReporteDto> = emptyList()
)

class ReportarViewModel : ViewModel() {

    private val _ui = MutableStateFlow(ReportarUi())
    val ui: StateFlow<ReportarUi> = _ui.asStateFlow()

    fun enviar(comercioId: Long, motivo: String, descripcion: String) {
        viewModelScope.launch {
            _ui.update { it.copy(enviando = true, okMensaje = null, errorMensaje = null) }
            try {
                val usuarioId = ServiceLocator.prefs.state.first().usuarioId
                ServiceLocator.multas.crearReporte(usuarioId, comercioId, motivo, descripcion)
                _ui.update { it.copy(enviando = false, okMensaje = "Reporte enviado a PROFECO. Gracias por tu aporte.") }
                cargarMisReportes()
            } catch (t: Throwable) {
                _ui.update {
                    it.copy(enviando = false, errorMensaje = t.message ?: "No se pudo enviar el reporte")
                }
            }
        }
    }

    fun cargarMisReportes() {
        viewModelScope.launch {
            _ui.update { it.copy(cargandoReportes = true) }
            try {
                val usuarioId = ServiceLocator.prefs.state.first().usuarioId
                val reportes = ServiceLocator.multas.listarReportesPorUsuario(usuarioId)
                _ui.update { it.copy(cargandoReportes = false, reportes = reportes) }
            } catch (t: Throwable) {
                _ui.update { it.copy(cargandoReportes = false) }
            }
        }
    }
}
