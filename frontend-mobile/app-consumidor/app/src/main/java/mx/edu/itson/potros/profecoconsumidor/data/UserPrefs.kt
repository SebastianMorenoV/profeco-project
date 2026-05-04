package mx.edu.itson.potros.profecoconsumidor.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "profeco_prefs")

@Serializable
data class ItemCompra(
    val id: Int,
    val nombre: String,
    val marcado: Boolean
)

class UserPrefs(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    val state: Flow<UserPrefsState> = context.dataStore.data.map { p ->
        UserPrefsState(
            usuarioId = p[KEY_USUARIO_ID] ?: 1L,
            baseUrl = p[KEY_BASE_URL] ?: DEFAULT_BASE_URL,
            comerciosFavoritos = p[KEY_FAV_COMERCIOS]
                ?.split(",")
                ?.mapNotNull { it.trim().toLongOrNull() }
                ?.toSet()
                .orEmpty(),
            wishlist = p[KEY_WISHLIST]
                ?.split(",")
                ?.mapNotNull { it.trim().toLongOrNull() }
                ?.toSet()
                .orEmpty(),
            busquedasRecientes = p[KEY_BUSQUEDAS_RECIENTES]
                ?.split("\n")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                .orEmpty(),
            listaCompras = decodeListaCompras(p[KEY_LISTA_COMPRAS]),
            recibioPushes = p[KEY_RECIBIO_PUSHES] ?: false
        )
    }

    suspend fun setUsuarioId(id: Long) =
        context.dataStore.edit { it[KEY_USUARIO_ID] = id }

    suspend fun setBaseUrl(url: String) =
        context.dataStore.edit { it[KEY_BASE_URL] = url.ifBlank { DEFAULT_BASE_URL } }

    suspend fun toggleComercioFavorito(comercioId: Long) =
        context.dataStore.edit { p ->
            val actuales = p[KEY_FAV_COMERCIOS]
                ?.split(",")
                ?.mapNotNull { it.trim().toLongOrNull() }
                ?.toMutableSet()
                ?: mutableSetOf()
            if (!actuales.add(comercioId)) actuales.remove(comercioId)
            p[KEY_FAV_COMERCIOS] = actuales.joinToString(",")
        }

    suspend fun toggleProductoWishlist(productoId: Long) =
        context.dataStore.edit { p ->
            val actuales = p[KEY_WISHLIST]
                ?.split(",")
                ?.mapNotNull { it.trim().toLongOrNull() }
                ?.toMutableSet()
                ?: mutableSetOf()
            if (!actuales.add(productoId)) actuales.remove(productoId)
            p[KEY_WISHLIST] = actuales.joinToString(",")
        }

    suspend fun marcarPushRecibido() =
        context.dataStore.edit { it[KEY_RECIBIO_PUSHES] = true }

    suspend fun registrarBusqueda(query: String) {
        val limpia = query.trim()
        if (limpia.isEmpty()) return
        context.dataStore.edit { p ->
            val actuales = p[KEY_BUSQUEDAS_RECIENTES]
                ?.split("\n")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?.toMutableList()
                ?: mutableListOf()
            actuales.removeAll { it.equals(limpia, ignoreCase = true) }
            actuales.add(0, limpia)
            while (actuales.size > MAX_BUSQUEDAS) actuales.removeAt(actuales.lastIndex)
            p[KEY_BUSQUEDAS_RECIENTES] = actuales.joinToString("\n")
        }
    }

    suspend fun limpiarBusquedas() =
        context.dataStore.edit { it.remove(KEY_BUSQUEDAS_RECIENTES) }

    suspend fun agregarItemListaCompras(nombre: String) {
        val limpio = nombre.trim()
        if (limpio.isEmpty()) return
        context.dataStore.edit { p ->
            val items = decodeListaCompras(p[KEY_LISTA_COMPRAS]).toMutableList()
            val nuevoId = (items.maxOfOrNull { it.id } ?: 0) + 1
            items.add(ItemCompra(nuevoId, limpio, marcado = false))
            p[KEY_LISTA_COMPRAS] = encodeListaCompras(items)
        }
    }

    suspend fun toggleItemListaCompras(id: Int) =
        context.dataStore.edit { p ->
            val items = decodeListaCompras(p[KEY_LISTA_COMPRAS]).toMutableList()
            val idx = items.indexOfFirst { it.id == id }
            if (idx != -1) {
                items[idx] = items[idx].copy(marcado = !items[idx].marcado)
                p[KEY_LISTA_COMPRAS] = encodeListaCompras(items)
            }
        }

    suspend fun eliminarItemListaCompras(id: Int) =
        context.dataStore.edit { p ->
            val items = decodeListaCompras(p[KEY_LISTA_COMPRAS]).filter { it.id != id }
            p[KEY_LISTA_COMPRAS] = encodeListaCompras(items)
        }

    suspend fun limpiarListaCompras() =
        context.dataStore.edit { it.remove(KEY_LISTA_COMPRAS) }

    private fun encodeListaCompras(items: List<ItemCompra>): String =
        json.encodeToString(ListSerializer(ItemCompra.serializer()), items)

    private fun decodeListaCompras(raw: String?): List<ItemCompra> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(ItemCompra.serializer()), raw)
        }.getOrDefault(emptyList())
    }

    companion object {
        const val DEFAULT_BASE_URL = "http://10.0.2.2:8084"
        private const val MAX_BUSQUEDAS = 8
        private val KEY_USUARIO_ID = longPreferencesKey("usuario_id")
        private val KEY_BASE_URL = stringPreferencesKey("base_url")
        private val KEY_FAV_COMERCIOS = stringPreferencesKey("fav_comercios")
        private val KEY_WISHLIST = stringPreferencesKey("wishlist")
        private val KEY_BUSQUEDAS_RECIENTES = stringPreferencesKey("busquedas_recientes")
        private val KEY_LISTA_COMPRAS = stringPreferencesKey("lista_compras")
        private val KEY_RECIBIO_PUSHES = booleanPreferencesKey("recibio_pushes")
    }
}

data class UserPrefsState(
    val usuarioId: Long = 1L,
    val baseUrl: String = UserPrefs.DEFAULT_BASE_URL,
    val comerciosFavoritos: Set<Long> = emptySet(),
    val wishlist: Set<Long> = emptySet(),
    val busquedasRecientes: List<String> = emptyList(),
    val listaCompras: List<ItemCompra> = emptyList(),
    val recibioPushes: Boolean = false
)
