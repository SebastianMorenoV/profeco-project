package mx.edu.itson.potros.profecoconsumidor.ui.screens.listacompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mx.edu.itson.potros.profecoconsumidor.data.ItemCompra
import mx.edu.itson.potros.profecoconsumidor.data.ServiceLocator

class ListaComprasViewModel : ViewModel() {

    val items: Flow<List<ItemCompra>> =
        ServiceLocator.prefs.state.map { it.listaCompras }

    fun agregar(nombre: String) {
        viewModelScope.launch { ServiceLocator.prefs.agregarItemListaCompras(nombre) }
    }

    fun toggle(id: Int) {
        viewModelScope.launch { ServiceLocator.prefs.toggleItemListaCompras(id) }
    }

    fun eliminar(id: Int) {
        viewModelScope.launch { ServiceLocator.prefs.eliminarItemListaCompras(id) }
    }

    fun limpiar() {
        viewModelScope.launch { ServiceLocator.prefs.limpiarListaCompras() }
    }
}
