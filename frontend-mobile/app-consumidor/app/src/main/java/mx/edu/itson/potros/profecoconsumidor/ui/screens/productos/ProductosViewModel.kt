package mx.edu.itson.potros.profecoconsumidor.ui.screens.productos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ProductoDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState

class ProductosViewModel : ViewModel() {

    private val _productos = MutableStateFlow<UiState<List<ProductoDto>>>(UiState.Loading)
    val productos: StateFlow<UiState<List<ProductoDto>>> = _productos.asStateFlow()

    private var currentJob: Job? = null

    fun buscar(query: String, categoria: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            delay(300)
            _productos.value = UiState.Loading
            _productos.value = try {
                UiState.Success(ServiceLocator.catalogo.buscarProductos(query, categoria))
            } catch (t: Throwable) {
                UiState.Error(t.message ?: "No se pudieron cargar los productos")
            }
        }
    }
}
