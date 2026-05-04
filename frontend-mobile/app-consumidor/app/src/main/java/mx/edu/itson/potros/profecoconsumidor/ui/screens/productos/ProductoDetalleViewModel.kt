package mx.edu.itson.potros.profecoconsumidor.ui.screens.productos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ComercioDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.PrecioDto
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ProductoDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState
import mx.edu.itson.potros.profecoconsumidor.util.ApiErrorMapper

data class ProductoDetalleData(
    val producto: ProductoDto?,
    val precios: List<PrecioDto>,
    val comercios: Map<Long, ComercioDto>
)

class ProductoDetalleViewModel : ViewModel() {

    private val _state = MutableStateFlow<UiState<ProductoDetalleData>>(UiState.Loading)
    val state: StateFlow<UiState<ProductoDetalleData>> = _state.asStateFlow()

    private val _productoActual = MutableStateFlow(0L)

    val esWishlist: StateFlow<Boolean> =
        combine(ServiceLocator.prefs.state, _productoActual) { prefs, id ->
            id != 0L && prefs.wishlist.contains(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun cargar(productoId: Long) {
        _productoActual.value = productoId
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                val producto = ServiceLocator.catalogo.obtenerProducto(productoId)
                val precios = ServiceLocator.catalogo.obtenerPrecios(productoId)
                val comerciosUnicos = precios.map { it.comercioId }.toSet()
                val mapa = comerciosUnicos.associateWith { id ->
                    runCatching { ServiceLocator.comercios.obtener(id) }.getOrNull()
                }.mapNotNull { (k, v) -> v?.let { k to it } }.toMap()
                UiState.Success(ProductoDetalleData(producto, precios, mapa))
            } catch (t: Throwable) {
                UiState.Error(ApiErrorMapper.map(t, "No se pudo cargar el producto"))
            }
        }
    }

    fun toggleWishlist() {
        val id = _productoActual.value
        if (id == 0L) return
        viewModelScope.launch { ServiceLocator.prefs.toggleProductoWishlist(id) }
    }

    fun agregarALista(nombre: String) {
        viewModelScope.launch { ServiceLocator.prefs.agregarItemListaCompras(nombre) }
    }
}
