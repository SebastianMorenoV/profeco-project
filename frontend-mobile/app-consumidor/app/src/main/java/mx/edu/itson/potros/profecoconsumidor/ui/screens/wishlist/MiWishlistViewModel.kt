package mx.edu.itson.potros.profecoconsumidor.ui.screens.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator
import mx.edu.itson.potros.profecoconsumidor.data.network.dto.ProductoDto
import mx.edu.itson.potros.profecoconsumidor.ui.UiState
import mx.edu.itson.potros.profecoconsumidor.util.ApiErrorMapper

class MiWishlistViewModel : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<ProductoDto>>>(UiState.Loading)
    val state: StateFlow<UiState<List<ProductoDto>>> = _state.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                val ids = ServiceLocator.prefs.state.first().wishlist
                val productos = ids.mapNotNull { id ->
                    runCatching { ServiceLocator.catalogo.obtenerProducto(id) }.getOrNull()
                }
                UiState.Success(productos)
            } catch (t: Throwable) {
                UiState.Error(ApiErrorMapper.map(t, "No se pudo cargar la wishlist"))
            }
        }
    }

    fun quitar(productoId: Long) {
        viewModelScope.launch {
            ServiceLocator.prefs.toggleProductoWishlist(productoId)
            cargar()
        }
    }
}
